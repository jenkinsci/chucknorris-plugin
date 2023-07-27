package hudson.plugins.chucknorris.pipeline;

import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.chucknorris.CordellWalkerRecorder;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

public class ChuckNorrisStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @StepContextParameter
    private transient TaskListener listener;

    @StepContextParameter
    private transient Run<?, ?> run;

    @Override
    protected Void run() throws Exception {
        listener.getLogger().println("Submitting to Chuck's will");

        CordellWalkerRecorder recorder = new CordellWalkerRecorder();
        recorder.perform(run);

        return null;
    }
}
