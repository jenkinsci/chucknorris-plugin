package hudson.plugins.chucknorris;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;

import hudson.model.Job;
import hudson.model.Run;
import java.util.Arrays;
import junit.framework.TestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class RoundhouseActionTest extends TestCase {

    private RoundhouseAction action;

    private Run<?, ?> run;
    private RoundhouseAction lastBuildAction;

    @Override
    @SuppressWarnings("rawtypes")
    public void setUp() {
        action = new RoundhouseAction(Style.BAD_ASS, "Chuck Norris can divide by zero.");

        run = mock(Run.class);

        lastBuildAction = new RoundhouseAction(Style.ALERT, "Chuck Norris went out of an infinite loop.");
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

    public void testGetProjectActionsFromLastProjectBuild() {
        action.onAttached(run);

        assertNotNull(action.getProjectActions());
        assertEquals(1, action.getProjectActions().size());
        assertSame(lastBuildAction, action.getProjectActions().iterator().next());
    }
}
