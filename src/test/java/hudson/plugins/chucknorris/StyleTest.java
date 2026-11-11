package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.Result;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    /**
     * Every style needs a CSS rule in chucknorris.css whose background image exists, so a missing or misspelled
     * image file fails the build instead of showing a blank page.
     */
    @ParameterizedTest
    @EnumSource(Style.class)
    void testEveryStyleHasAnExistingImage(Style style) throws IOException {
        Path webapp = Path.of("src", "main", "webapp");
        String css = Files.readString(webapp.resolve("css/chucknorris.css"), StandardCharsets.UTF_8);

        Matcher rule = Pattern.compile(
                        "\\." + Pattern.quote(style.getCssClass()) + "\\s*\\{[^}]*url\\(\"\\.\\./([^\"]+)\"\\)")
                .matcher(css);
        assertTrue(rule.find(), "chucknorris.css has no background-image rule for ." + style.getCssClass());
        assertTrue(Files.isRegularFile(webapp.resolve(rule.group(1))), "missing image " + rule.group(1));
    }
}
