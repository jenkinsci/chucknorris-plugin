package hudson.plugins.chucknorris.pipeline;

import org.jenkinsci.plugins.workflow.steps.StepContext;

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
	private transient TaskListener listener = getContext().get(TaskListener.class);
	private transient Run<?, ?> run = getContext().get(Run.class);

    @Inject
	protected ChuckNorrisStepExecution(@Nonnull StepContext context) throws IOException, InterruptedException {
		super(context);
	}

	@Override
	protected Void run() throws Exception {
    	listener.getLogger().println("Submitting to Chuck's will");
		CordellWalkerRecorder recorder = new CordellWalkerRecorder();
		recorder.perform(run);
		return null;
	}

}