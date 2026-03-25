package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import hudson.model.Result;
import hudson.model.Run;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ThirdRoundhouseActionTest {

    private RoundhouseAction action;

    private Run<?, ?> run;

    @BeforeEach
    @SuppressWarnings("rawtypes")
    void setUp() {
        action = new RoundhouseAction(Style.BAD_ASS, 0);

        run = mock(Run.class);
        given(run.getResult()).willReturn(Result.SUCCESS);
    }

    @Test
    void testGetStyleFromRunResult() {
        action.onAttached(run);

        assertEquals(Style.THUMB_UP, action.getStyle());
    }
}
