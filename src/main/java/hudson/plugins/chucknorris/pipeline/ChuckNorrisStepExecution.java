package hudson.plugins.chucknorris.pipeline;

import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.chucknorris.CordellWalkerRecorder;
import hudson.plugins.chucknorris.RoundhouseAction;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

public class ChuckNorrisStepExecution extends SynchronousNonBlockingStepExecution<String> {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Whether to leave the fact out of the build log. */
    private final boolean quiet;

    /** Whether the build page and the job page show Chuck's image. */
    private final boolean showImage;

    ChuckNorrisStepExecution(StepContext context, boolean quiet, boolean showImage) {
        super(context);
        this.quiet = quiet;
        this.showImage = showImage;
    }

    /**
     * Attaches Chuck Norris to the run and, unless {@code quiet}, prints the fact to the build log.
     * @return the fact, so pipelines can use it, e.g. {@code def fact = chuckNorris()}
     */
    @Override
    protected String run() throws Exception {
        TaskListener listener = getContext().get(TaskListener.class);
        Run<?, ?> run = getContext().get(Run.class);

        if (!quiet) {
            listener.getLogger().println(SubmittingNote.encodeLine());
        }

        CordellWalkerRecorder recorder = new CordellWalkerRecorder();
        String fact = recorder.perform(run, quiet ? null : listener);
        RoundhouseAction action = run.getAction(RoundhouseAction.class);
        if (action != null) {
            action.setShowImage(showImage);
        }
        return fact;
    }
}
