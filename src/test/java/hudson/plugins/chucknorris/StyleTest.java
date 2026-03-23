package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.Result;
import org.junit.jupiter.api.Test;

class StyleTest {

    @Test
    void testGetWithFailureResultGivesBadAssStyle() {
        assertEquals(Style.BAD_ASS, Style.get(Result.FAILURE));
    }

    @Test
    void testGetWithSuccessResultGivesSuitupStyle() {
        assertEquals(Style.THUMB_UP, Style.get(Result.SUCCESS));
    }

    @Test
    void testGetWithAbortedResultGivesAlertStyle() {
        assertEquals(Style.ALERT, Style.get(Result.ABORTED));
    }

    @Test
    void testGetWithNotBuiltResultGivesAlertStyle() {
        assertEquals(Style.ALERT, Style.get(Result.NOT_BUILT));
    }

    @Test
    void testGetWithUnstableResultGivesAlertStyle() {
        assertEquals(Style.ALERT, Style.get(Result.UNSTABLE));
    }
}
