package hudson.plugins.chucknorris.pipeline;

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.chucknorris.CordellWalkerRecorder;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;

public class ChuckNorrisStepExecution extends SynchronousNonBlockingStepExecution {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@StepContextParameter
	private TaskListener listener;

    @StepContextParameter
    private transient Run<?, ?> run;

    @Inject
	protected ChuckNorrisStepExecution(@Nonnull StepContext context) {
		super(context);
	}

	@Override
	protected Object run() throws IOException, InterruptedException {

		listener.getLogger().println("Submitting to Chuck's will");
				CordellWalkerRecorder recorder = new CordellWalkerRecorder();
		recorder.perform(run);
		
		return null;

	}

}
