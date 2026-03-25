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
import hudson.model.Run;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import jenkins.model.RunAction2;
import jenkins.tasks.SimpleBuildStep.LastBuildAction;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest2;

/**
 * {@link RoundhouseAction} keeps the style and fact associated with the action.
 * For more info, please watch <a
 * href="http://www.youtube.com/watch?v=Vb7lnpk3tRY"
 * >http://www.youtube.com/watch?v=Vb7lnpk3tRY</a>
 * @author cliffano
 */
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
     * The fact index for locale-aware lookup. -1 means legacy (no index stored).
     */
    private Integer mFactIndex;

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
    public Style getStyle() {
        Style theStyle;
        if (mRun != null) {
            theStyle = Style.get(mRun.getResult());
        } else if (mStyle != null) {
            theStyle = mStyle;
        } else {
            theStyle = style;
        }
        return theStyle;
    }

    /**
     * Gets the fact index for locale-aware lookup in Jelly templates.
     * @return the fact index, or null for legacy builds
     */
    public Integer getFactIndex() {
        return mFactIndex;
    }

    /**
     * Gets the Chuck Norris fact.
     * @return the fact
     */
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

    @Override
    public void onAttached(Run<?, ?> r) {
        this.mRun = r;
    }

    @Override
    public void onLoad(Run<?, ?> r) {
        this.mRun = r;
    }
}
