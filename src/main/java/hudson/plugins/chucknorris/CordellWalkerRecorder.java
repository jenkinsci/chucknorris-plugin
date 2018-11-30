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

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.util.logging.Logger;

import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

/**
 * This class associates a RoundhouseAction to a job or a build. For more info
 * on Cordell Walker, check out <a
 * href="http://www.imdb.com/character/ch0038386/"
 * >http://www.imdb.com/character/ch0038386/</a>.
 * @author cliffano
 */
public class CordellWalkerRecorder extends Recorder implements SimpleBuildStep {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger
            .getLogger(CordellWalkerRecorder.class.getName());

    /**
     * Fact generator.
     */
    private FactGenerator factGenerator;

    /**
     * Constructs a {@link CordellWalkerRecorder} with default
     * {@link FactGenerator}.
     */
    @DataBoundConstructor
    public CordellWalkerRecorder() {
        this(new FactGenerator());
    }

    /**
     * Constructs a {@link CordellWalkerRecorder} with specified
     * {@link FactGenerator}.
     * @param factGenerator
     *            the fact generator
     */
    public CordellWalkerRecorder(final FactGenerator factGenerator) {
        this.factGenerator = factGenerator;
        LOGGER.fine("Star Trek is activated");
    }

    /**
     * Gets the RoundhouseAction as the project action. This is applicable for
     * each job and only when there's at least one build in the job.
     * @param project
     *            the project
     * @return the project action
     */
    @Override
    public final Action getProjectAction(final AbstractProject<?, ?> project) {
        Action action = null;
        AbstractBuild<?, ?> build = project.getLastBuild();
        if (build != null) {
            Result result = build.getResult();
            if (result != null) {
                Style style = Style.get(result);
                String fact = factGenerator.random();
                action = new RoundhouseAction(style, fact);
            }
        }
        return action;
    }

    /**
     * Adds RoundhouseAction to the build actions. This is applicable for each
     * build.
     * @param build
     *            the build
     * @param launcher
     *            the launcher
     * @param listener
     *            the listener
     * @return true
     * @throws InterruptedException
     *             when there's an interruption
     * @throws IOException
     *             when there's an IO error
     */
    @Override
    public final boolean perform(final AbstractBuild<?, ?> build,
            final Launcher launcher, final BuildListener listener)
            throws InterruptedException, IOException {
        perform(build);
        return true;
    }

    /**
     * Adds RoundhouseAction to the run actions. This is applicable for each
     * run.
     * @param run
     *            the run
     * @param workspace
     *            the workspace
     * @param launcher
     *            the launcher
     * @param listener
     *            the listener
     * @throws InterruptedException
     *             when there's an interruption
     * @throws IOException
     *             when there's an IO error
     */
    @Override
    public final void perform(
            @Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace,
            @Nonnull final Launcher launcher, @Nonnull final TaskListener listener)
            throws InterruptedException, IOException {
        perform(run);
    }

    /**
     * Gets the required monitor service.
     * @return the BuildStepMonitor
     */
    public final BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    /**
     * Adds RoundhouseAction to the run actions. This is applicable for each
     * run.
     * @param run
     *            the run
     */
    public void perform(final Run<?, ?> run) {
        Style style = Style.get(run.getResult());
        String fact = factGenerator.random();
        run.addAction(new RoundhouseAction(style, fact));
    }

}
