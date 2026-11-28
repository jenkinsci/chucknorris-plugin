package hudson.plugins.chucknorris.pipeline;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class ChuckNorrisStep extends Step {

    /** Whether to leave the fact out of the build log. */
    private boolean quiet;

    /** Whether the build page and the job page show Chuck's image. */
    private boolean showImage = true;

    @DataBoundConstructor
    public ChuckNorrisStep() {}

    /**
     * Whether the step leaves the fact out of the build log. The fact is still shown on the build page and returned.
     * @return true to print nothing to the build log
     */
    public boolean isQuiet() {
        return quiet;
    }

    /**
     * Sets whether the step leaves the fact out of the build log.
     * @param quiet true to print nothing to the build log
     */
    @DataBoundSetter
    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    /**
     * Whether the build page and the job page show Chuck's image next to the fact.
     * @return false to show only the fact
     */
    public boolean isShowImage() {
        return showImage;
    }

    /**
     * Sets whether the build page and the job page show Chuck's image next to the fact.
     * @param showImage false to show only the fact
     */
    @DataBoundSetter
    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new ChuckNorrisStepExecution(context, quiet, showImage);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "chuckNorris";
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.ChuckNorrisStep_DisplayName();
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            Set<Class<?>> context = new HashSet<>();
            context.add(Run.class);
            context.add(TaskListener.class);
            return Collections.unmodifiableSet(context);
        }
    }
}
