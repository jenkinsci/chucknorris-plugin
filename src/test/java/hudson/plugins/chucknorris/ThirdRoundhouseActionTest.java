package hudson.plugins.chucknorris;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import hudson.model.Result;
import hudson.model.Run;
import org.junit.Before;
import org.junit.Test;

public class ThirdRoundhouseActionTest {

    private RoundhouseAction action;

    private Run<?, ?> run;

    @Before
    @SuppressWarnings("rawtypes")
    public void setUp() {
        action = new RoundhouseAction(Style.BAD_ASS, "Chuck Norris can divide by zero.");

        run = mock(Run.class);
        given(run.getResult()).willReturn(Result.SUCCESS);
    }

    @Test
    public void testGetStyleFromRunResult() {
        action.onAttached(run);

        assertEquals(Style.THUMB_UP, action.getStyle());
    }
}
