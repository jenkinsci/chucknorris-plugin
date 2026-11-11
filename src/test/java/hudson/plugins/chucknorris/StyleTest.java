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
    void testGetWithAbortedResultGivesDisappointedStyle() {
        assertEquals(Style.DISAPPOINTED, Style.get(Result.ABORTED));
    }

    @Test
    void testGetWithNotBuiltResultGivesDisappointedStyle() {
        assertEquals(Style.DISAPPOINTED, Style.get(Result.NOT_BUILT));
    }

    @Test
    void testGetWithNoResultYetGivesAlertStyle() {
        assertEquals(Style.ALERT, Style.get(null));
    }

    @Test
    void testCssClass() {
        assertEquals("chuck-style--bad_ass", Style.BAD_ASS.getCssClass());
        assertEquals("chuck-style--disappointed", Style.DISAPPOINTED.getCssClass());
    }

    @Test
    void testGetWithUnstableResultGivesAlertStyle() {
        assertEquals(Style.ALERT, Style.get(Result.UNSTABLE));
    }
}
