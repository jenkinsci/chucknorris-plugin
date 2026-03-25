package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;

import hudson.model.Job;
import hudson.model.Run;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

class RoundhouseActionTest {

    private RoundhouseAction action;

    private Run<?, ?> run;
    private RoundhouseAction lastBuildAction;

    @BeforeEach
    @SuppressWarnings("rawtypes")
    void setUp() {
        action = new RoundhouseAction(Style.BAD_ASS, 0);

        run = mock(Run.class);

        lastBuildAction = new RoundhouseAction(Style.ALERT, 1);
        final Job job = mock(Job.class);
        Run<?, ?> lastRun = mock(Run.class);

        given(run.getParent()).willAnswer(new Answer<Job>() {
            @Override
            public Job answer(InvocationOnMock invocation) throws Throwable {
                return job;
            }
        });
        given(job.getLastCompletedBuild()).willReturn(lastRun);
        given(lastRun.getActions(eq(RoundhouseAction.class))).willReturn(Arrays.asList(lastBuildAction));
    }

    @Test
    void testGetProjectActionsFromLastProjectBuild() {
        action.onAttached(run);

        assertNotNull(action.getProjectActions());
        assertEquals(1, action.getProjectActions().size());
        assertSame(lastBuildAction, action.getProjectActions().iterator().next());
    }

    @Test
    @SuppressWarnings("rawtypes")
    void testGetProjectActionsWhenNoCompletedBuild() {
        RoundhouseAction actionWithNoCompletedBuild = new RoundhouseAction(Style.BAD_ASS, 0);
        Run<?, ?> runWithNoCompletedBuild = mock(Run.class);
        final Job jobWithNoCompletedBuild = mock(Job.class);
        given(runWithNoCompletedBuild.getParent()).willAnswer(new Answer<Job>() {
            @Override
            public Job answer(InvocationOnMock invocation) throws Throwable {
                return jobWithNoCompletedBuild;
            }
        });
        given(jobWithNoCompletedBuild.getLastCompletedBuild()).willReturn(null);

        actionWithNoCompletedBuild.onAttached(runWithNoCompletedBuild);

        assertNotNull(actionWithNoCompletedBuild.getProjectActions());
        assertEquals(1, actionWithNoCompletedBuild.getProjectActions().size());
        assertSame(
                actionWithNoCompletedBuild,
                actionWithNoCompletedBuild.getProjectActions().iterator().next());
    }
}
