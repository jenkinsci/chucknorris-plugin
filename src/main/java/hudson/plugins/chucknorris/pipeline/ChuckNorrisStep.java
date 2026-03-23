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

public class ChuckNorrisStep extends Step {

    @DataBoundConstructor
    public ChuckNorrisStep() {}

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new ChuckNorrisStepExecution(context);
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
            return "Submit to Chuck Norris' will";
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
