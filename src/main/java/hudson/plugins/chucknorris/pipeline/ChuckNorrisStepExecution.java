package hudson.plugins.chucknorris.pipeline;

import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.chucknorris.CordellWalkerRecorder;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

public class ChuckNorrisStepExecution extends SynchronousNonBlockingStepExecution<String> {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    ChuckNorrisStepExecution(StepContext context) {
        super(context);
    }

    /**
     * Attaches Chuck Norris to the run and prints the fact to the build log.
     * @return the fact, so pipelines can use it, e.g. {@code def fact = chuckNorris()}
     */
    @Override
    protected String run() throws Exception {
        TaskListener listener = getContext().get(TaskListener.class);
        Run<?, ?> run = getContext().get(Run.class);

        listener.getLogger().println("Submitting to Chuck's will");

        CordellWalkerRecorder recorder = new CordellWalkerRecorder();
        return recorder.perform(run, listener);
    }
}
