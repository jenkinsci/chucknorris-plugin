package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.Result;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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

    @Test
    void testGetImageFile() {
        assertEquals("thumb_up.png", Style.THUMB_UP.getImageFile());
        assertEquals("bad_ass.png", Style.BAD_ASS.getImageFile());
    }

    /**
     * Every style's image must be bundled with the plugin, so a missing or misspelled image file fails the build
     * instead of showing a broken image.
     */
    @ParameterizedTest
    @EnumSource(Style.class)
    void testEveryStyleHasAnExistingImage(Style style) {
        Path image = Path.of("src", "main", "webapp", "images", style.getImageFile());
        assertTrue(Files.isRegularFile(image), "missing image " + image);
    }
}
