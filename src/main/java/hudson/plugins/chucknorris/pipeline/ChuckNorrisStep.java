package hudson.plugins.chucknorris.pipeline;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.kohsuke.stapler.DataBoundConstructor;

public class ChuckNorrisStep extends AbstractStepImpl {

    @DataBoundConstructor
    public ChuckNorrisStep() {}

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {
        public DescriptorImpl() {
            super(ChuckNorrisStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "chuckNorris";
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Submit to Chuck Norris' will";
        }
    }
}
