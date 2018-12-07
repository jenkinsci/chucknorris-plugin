package hudson.plugins.startrek.pipeline;

import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.startrek.FederationRecorder;

public class StarTrekStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@StepContextParameter
	private transient TaskListener listener;

    @StepContextParameter
    private transient Run<?, ?> run;

	@Override
	protected Void run() throws Exception {
		listener.getLogger().println("Submitting to Star Trek will");
		
		FederationRecorder recorder = new FederationRecorder();
		recorder.perform(run);
		
		return null;
	}

}
