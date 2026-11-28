/**
 * Copyright (c) 2009 Cliffano Subagio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.chucknorris;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.model.Action;
import hudson.model.Item;
import hudson.model.Result;
import hudson.model.Run;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import jenkins.model.RunAction2;
import jenkins.tasks.SimpleBuildStep.LastBuildAction;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import org.kohsuke.stapler.interceptor.RequirePOST;

/**
 * {@link RoundhouseAction} keeps the style and fact associated with the action.
 * For more info, please watch <a
 * href="https://www.youtube.com/watch?v=Vb7lnpk3tRY"
 * >https://www.youtube.com/watch?v=Vb7lnpk3tRY</a>
 * <p>The style, fact and fact index are exported to the remote API, so a build's
 * {@code api/json} carries them for dashboards, chat bots and build monitors.
 * @author cliffano
 */
@ExportedBean
public final class RoundhouseAction implements RunAction2, LastBuildAction {

    /**
     * The style - for backward compatibility to version 0.2.
     */
    private Style style;

    /**
     * The style.
     */
    private Style mStyle;

    /**
     * The fact - for backward compatibility to version 0.2.
     */
    private String fact;

    /**
     * The fact.
     */
    private String mFact;

    /**
     * The fact index for locale-aware lookup. {@code null} means legacy (no index stored).
     */
    private Integer mFactIndex;

    /**
     * Whether the build page and the job page hide Chuck's image (the step's {@code showImage: false}). Stored the
     * negative way round so that builds saved before this field existed still show the image.
     */
    private boolean mHideImage;

    /**
     * The run
     */
    private transient Run<?, ?> mRun;

    /**
     * Constructs a RoundhouseAction with specified style and fact.
     * @param style
     *            the style
     * @param factIndex
     *            the fact index for locale-aware lookup
     */
    public RoundhouseAction(final Style style, final int factIndex) {
        super();
        this.mStyle = style;
        this.mFactIndex = factIndex;
    }

    /**
     * Constructs a RoundhouseAction with a custom fact. The fact's text is stored with the build, so the build keeps
     * it when the custom facts change, and it is shown as written in every language.
     * @param style
     *            the style
     * @param customFact
     *            the custom fact
     */
    public RoundhouseAction(final Style style, final String customFact) {
        super();
        this.mStyle = style;
        this.mFact = customFact;
    }

    /**
     * Gets the action display name.
     * @return the display name
     */
    @Override
    public String getDisplayName() {
        return "Chuck Norris";
    }

    /**
     * This action doesn't provide any icon file.
     * @return null
     */
    @Override
    public String getIconFileName() {
        return null;
    }

    /**
     * Gets the URL name for this action.
     * @return the URL name
     */
    @Override
    public String getUrlName() {
        return "chucknorris";
    }

    /**
     * Gets the Chuck Norris style.
     * @return the style
     */
    // TODO : check infra statistics to see if someone still has chucknorris in 0.2...
    @SuppressFBWarnings(
            value = "UWF_UNWRITTEN_FIELD",
            justification = "that field could have been deserialized from old <= 0.2")
    @Exported(visibility = 2)
    public Style getStyle() {
        Style theStyle;
        if (mRun != null) {
            Result result = mRun.getResult();
            if (result == null && mRun.isBuilding()) {
                theStyle = Style.THUMB_UP;
            } else {
                theStyle = Style.get(result);
            }
        } else if (mStyle != null) {
            theStyle = mStyle;
        } else {
            theStyle = style;
        }
        return theStyle;
    }
    /**
     * Whether the build page and the job page show Chuck's image next to the fact. Used by {@code summary.jelly} and
     * {@code jobMain.jelly}.
     * @return false if the step was called with {@code showImage: false}
     */
    public boolean isShowImage() {
        return !mHideImage;
    }

    /**
     * Sets whether the build page and the job page show Chuck's image.
     * @param showImage false to show only the fact
     */
    public void setShowImage(boolean showImage) {
        this.mHideImage = !showImage;
    }

    /**
     * Gets the fact index for locale-aware lookup in Jelly templates.
     * @return the fact index, or null for legacy builds
     */
    @Exported(visibility = 2)
    public Integer getFactIndex() {
        return mFactIndex;
    }

    /**
     * Gets the Chuck Norris fact.
     * @return the fact
     */
    @Exported(visibility = 2)
    public String getFact() {
        if (mFactIndex != null) {
            Locale locale = Locale.getDefault();
            StaplerRequest2 req = Stapler.getCurrentRequest2();
            if (req != null) {
                locale = req.getLocale();
            }
            return FactGenerator.getFact(mFactIndex, locale);
        }
        if (mFact != null) {
            return mFact;
        }
        return fact;
    }

    /**
     * Returns this action as a collection of all project actions.
     *
     * Default jenkins behavior is to get the action on the last successful build (Stable or Unstable)
     * while we want the last completed build (Stable, Unstable or Failure).
     *
     * @return the project actions
     */
    @Override
    public Collection<? extends Action> getProjectActions() {
        if (mRun != null) {
            Run<?, ?> lastBuild = mRun.getParent().getLastCompletedBuild();
            if (lastBuild != null) {
                return lastBuild.getActions(RoundhouseAction.class);
            }
        }
        return Collections.singletonList(this);
    }

    /**
     * How many times {@link #pickNewFact} draws again when it gets the fact the build already shows.
     */
    private static final int NEW_FACT_DRAWS = 10;

    /**
     * Whether the current user may pick a new fact for this build: they need to be able to configure the job.
     * Used by {@code floatingBox.jelly}.
     * @return true if the New fact button should be shown
     */
    @Restricted(NoExternalUse.class)
    public boolean isNewFactAllowed() {
        return mRun != null && mRun.getParent().hasPermission(Item.CONFIGURE);
    }

    /**
     * The URL of this action, relative to the Jenkins root. Used by {@code floatingBox.jelly}, where the action is
     * shown on the job page.
     * @return the URL of this action
     */
    @Restricted(NoExternalUse.class)
    public String getActionUrl() {
        return mRun == null ? null : mRun.getUrl() + getUrlName() + "/";
    }

    /**
     * Picks another random fact for this build and saves it with the build, so everyone sees the same fact
     * afterwards. The style still follows the build result. Needs a POST (with the CSRF crumb) and permission to
     * configure the job.
     * @param req the request
     * @return a redirect to the job page
     * @throws IOException if the build cannot be saved
     */
    @RequirePOST
    @Restricted(NoExternalUse.class)
    public HttpResponse doNewFact(StaplerRequest2 req) throws IOException {
        if (mRun == null) {
            return HttpResponses.notFound();
        }
        mRun.getParent().checkPermission(Item.CONFIGURE);
        pickNewFact(new FactGenerator(), ChuckNorrisConfiguration.getOrNull());
        mRun.save();
        return HttpResponses.redirectTo(
                req.getContextPath() + "/" + mRun.getParent().getUrl());
    }

    /**
     * Replaces the fact with another one picked as {@link CordellWalkerRecorder} picks it (custom facts included),
     * drawing again a few times if it gets the fact the build already shows.
     * @param factGenerator the generator to pick from
     * @param config the custom facts, or null if there is no Jenkins instance
     */
    void pickNewFact(FactGenerator factGenerator, ChuckNorrisConfiguration config) {
        Integer oldIndex = mFactIndex;
        String oldFact = oldIndex == null ? (mFact != null ? mFact : fact) : null;
        Integer index = null;
        String customFact = null;
        for (int draw = 0; draw < NEW_FACT_DRAWS; draw++) {
            customFact = config == null
                    ? null
                    : factGenerator.randomCustomFact(config.getCustomFactList(), config.isCustomFactsOnly());
            index = customFact == null ? factGenerator.randomIndex() : null;
            boolean same = index != null ? index.equals(oldIndex) : Objects.equals(customFact, oldFact);
            if (!same) {
                break;
            }
        }
        mFactIndex = index;
        mFact = customFact;
    }

    @Override
    public void onAttached(Run<?, ?> r) {
        this.mRun = r;
    }

    @Override
    public void onLoad(Run<?, ?> r) {
        this.mRun = r;
    }
}
