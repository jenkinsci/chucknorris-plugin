package hudson.plugins.chucknorris;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;

import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import java.util.Arrays;
import junit.framework.TestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ThirdRoundhouseActionTest extends TestCase {

    private RoundhouseAction action;

    private Run<?, ?> run;
    private RoundhouseAction lastBuildAction;

    @Override
    @SuppressWarnings("rawtypes")
    public void setUp() {
        action = new RoundhouseAction(Style.BAD_ASS, "Chuck Norris can divide by zero.");

        run = mock(Run.class);
        given(run.getResult()).willReturn(Result.SUCCESS);

        lastBuildAction = new RoundhouseAction(Style.ALERT, "Chuck Norris went out of an infinite loop.");
        final Job job = mock(Job.class);
        Run<?, ?> lastRun = mock(Run.class);
    }

    public void testGetStyleFromRunResult() {
        action.onAttached(run);

        assertEquals(Style.THUMB_UP, action.getStyle());
    }
}
