package hudson.plugins.chucknorris.pipeline;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.chucknorris.RoundhouseAction;
import hudson.plugins.chucknorris.Style;
import java.util.Set;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.test.steps.SemaphoreStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class ChuckNorrisStepTest {

    private JenkinsRule j;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        j = rule;
    }

    @Test
    void descriptorFunctionName() {
        ChuckNorrisStep.DescriptorImpl descriptor = new ChuckNorrisStep.DescriptorImpl();
        assertEquals("chuckNorris", descriptor.getFunctionName());
    }

    @Test
    void descriptorDisplayName() {
        ChuckNorrisStep.DescriptorImpl descriptor = new ChuckNorrisStep.DescriptorImpl();
        assertEquals("Submit to Chuck Norris' will", descriptor.getDisplayName());
    }

    @Test
    void descriptorRequiredContext() {
        ChuckNorrisStep.DescriptorImpl descriptor = new ChuckNorrisStep.DescriptorImpl();
        Set<? extends Class<?>> required = descriptor.getRequiredContext();
        assertTrue(required.contains(Run.class));
        assertTrue(required.contains(TaskListener.class));
        assertEquals(2, required.size());
    }

    @Test
    void stepStartReturnsExecution() throws Exception {
        ChuckNorrisStep step = new ChuckNorrisStep();
        StepContext mockContext = mock(StepContext.class);
        StepExecution execution = step.start(mockContext);
        assertNotNull(execution);
        assertInstanceOf(ChuckNorrisStepExecution.class, execution);
    }

    @Test
    void badAssChuckNorris() throws Exception {
        WorkflowJob p = j.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition("chuckNorris()\n" + "semaphore 'wait'\n", true));
        WorkflowRun b1 = p.scheduleBuild2(0).waitForStart();
        SemaphoreStep.waitForStart("wait/1", b1);
        SemaphoreStep.failure("wait/1", new Exception());

        j.waitForCompletion(b1);
        j.assertBuildStatus(Result.FAILURE, b1);

        RoundhouseAction action = b1.getAction(RoundhouseAction.class);
        assertEquals(Style.BAD_ASS, action.getStyle());
        assertNotNull(action.getFact());
    }

    @Test
    void alertChuckNorris() throws Exception {
        WorkflowJob p = j.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition("chuckNorris()\n" + "semaphore 'wait'\n", true));
        WorkflowRun b1 = p.scheduleBuild2(0).waitForStart();
        SemaphoreStep.waitForStart("wait/1", b1);
        b1.setResult(Result.UNSTABLE);
        SemaphoreStep.success("wait/1", null);

        j.waitForCompletion(b1);
        j.assertBuildStatus(Result.UNSTABLE, b1);

        RoundhouseAction action = b1.getAction(RoundhouseAction.class);
        assertEquals(Style.ALERT, action.getStyle());
        assertNotNull(action.getFact());
    }

    @Test
    void thumbsUpChuckNorris() throws Exception {
        WorkflowJob p = j.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition("chuckNorris()\n" + "semaphore 'wait'\n", true));
        WorkflowRun b1 = p.scheduleBuild2(0).waitForStart();
        SemaphoreStep.waitForStart("wait/1", b1);
        SemaphoreStep.success("wait/1", null);

        j.waitForCompletion(b1);
        j.assertBuildStatus(Result.SUCCESS, b1);

        RoundhouseAction action = b1.getAction(RoundhouseAction.class);
        assertEquals(Style.THUMB_UP, action.getStyle());
        assertNotNull(action.getFact());
    }

    @Test
    void projectPageChuckNorris() throws Exception {
        WorkflowJob p = j.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition("chuckNorris()\n" + "semaphore 'wait'\n", true));
        WorkflowRun b1 = p.scheduleBuild2(0).waitForStart();
        SemaphoreStep.waitForStart("wait/1", b1);

        WorkflowRun b2 = p.scheduleBuild2(0).waitForStart();
        SemaphoreStep.waitForStart("wait/2", b2);

        SemaphoreStep.success("wait/1", null);
        SemaphoreStep.failure("wait/2", new Exception());

        j.waitForCompletion(b1);
        j.assertBuildStatus(Result.SUCCESS, b1);

        j.waitForCompletion(b2);
        j.assertBuildStatus(Result.FAILURE, b2);

        RoundhouseAction action1 = b1.getAction(RoundhouseAction.class);
        assertEquals(Style.THUMB_UP, action1.getStyle());
        assertNotNull(action1.getFact());

        RoundhouseAction action2 = b2.getAction(RoundhouseAction.class);
        assertEquals(Style.BAD_ASS, action2.getStyle());
        assertNotNull(action2.getFact());

        // Make sure we get the action from the last completed build (jenkins default behaviour is last successful
        // build)
        RoundhouseAction projectAction = p.getAction(RoundhouseAction.class);
        assertEquals(Style.BAD_ASS, projectAction.getStyle());
        assertNotNull(projectAction.getFact());
    }
}
