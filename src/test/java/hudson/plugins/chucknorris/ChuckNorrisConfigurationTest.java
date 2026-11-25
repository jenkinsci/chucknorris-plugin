package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Run;
import java.util.List;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * Custom facts from the Manage Jenkins &gt; Appearance page, in a running Jenkins.
 */
@WithJenkins
class ChuckNorrisConfigurationTest {

    private JenkinsRule j;
    private ChuckNorrisConfiguration config;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        j = rule;
        config = ChuckNorrisConfiguration.get();
    }

    @Test
    void settingsSurviveASaveOfTheAppearancePage() throws Exception {
        config.setCustomFacts("Chuck Norris reviews his own pull requests.\nChuck Norris's builds are never flaky.");
        config.setCustomFactsOnly(true);

        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            HtmlPage page = wc.goTo("manage/appearance");
            assertNotNull(page.getElementByName("_.customFacts"), "the custom facts are on the Appearance page");
            j.submit(page.getFormByName("config"));
        }

        assertEquals(
                List.of("Chuck Norris reviews his own pull requests.", "Chuck Norris's builds are never flaky."),
                config.getCustomFactList());
        assertTrue(config.isCustomFactsOnly());
    }

    @Test
    void notOnTheSystemPage() throws Exception {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            assertTrue(wc.goTo("manage/configure")
                    .getElementsByName("_.customFacts")
                    .isEmpty());
        }
    }

    @Test
    void blankLinesAndSurroundingSpacesAreDropped() {
        config.setCustomFacts("\n  First fact.  \r\n\n   \nSecond fact.\n");

        assertEquals(List.of("First fact.", "Second fact."), config.getCustomFactList());
        assertEquals("First fact.\nSecond fact.", config.getCustomFacts());
    }

    @Test
    void theParsedFactsAreSavedAndLoaded() {
        config.setCustomFacts("First fact.\n\nSecond fact.");

        assertEquals(List.of("First fact.", "Second fact."), new ChuckNorrisConfiguration().getCustomFactList());
    }

    @Test
    void clearingTheFactsLeavesNone() {
        config.setCustomFacts("First fact.");
        config.setCustomFacts("   ");

        assertEquals(List.of(), config.getCustomFactList());
        assertNull(config.getCustomFacts());
    }

    @Test
    void customOnlyBuildsShowTheCustomFactEscaped() throws Exception {
        String fact = "<b>Chuck Norris</b> wrote this plugin & its tests.";
        config.setCustomFacts(fact);
        config.setCustomFactsOnly(true);

        FreeStyleBuild build = buildWithChuck();
        RoundhouseAction action = build.getAction(RoundhouseAction.class);

        assertEquals(fact, action.getFact());
        assertNull(action.getFactIndex(), "a custom fact has no index into the built-in facts");
        j.assertLogContains("You know what? " + fact, build);
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            HtmlPage page = wc.getPage(build);
            assertTrue(page.asNormalizedText().contains(fact), "the fact should be shown as written");
            assertTrue(
                    page.getByXPath("//td[@class='chuck-fact']//b").isEmpty(),
                    "the fact's HTML must be escaped, not rendered");
        }
    }

    @Test
    void aBuildKeepsItsCustomFactWhenTheListChanges() throws Exception {
        config.setCustomFacts("The original fact.");
        config.setCustomFactsOnly(true);
        FreeStyleBuild build = buildWithChuck();

        config.setCustomFacts("A replacement fact.");
        RoundhouseAction reloaded =
                (RoundhouseAction) Run.XSTREAM2.fromXML(Run.XSTREAM2.toXML(build.getAction(RoundhouseAction.class)));

        assertEquals("The original fact.", reloaded.getFact());
    }

    @Test
    void customOnlyWithoutCustomFactsUsesTheBuiltInFacts() throws Exception {
        config.setCustomFactsOnly(true);

        RoundhouseAction action = buildWithChuck().getAction(RoundhouseAction.class);

        assertNotNull(action.getFactIndex(), "with no custom facts, a built-in fact is picked");
    }

    private FreeStyleBuild buildWithChuck() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getPublishersList().add(new CordellWalkerRecorder());
        return j.buildAndAssertSuccess(project);
    }
}
