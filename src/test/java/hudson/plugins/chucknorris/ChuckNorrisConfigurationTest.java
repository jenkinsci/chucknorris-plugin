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
 * Custom facts from the Manage Jenkins &gt; System page, in a running Jenkins.
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
    void settingsSurviveASaveOfTheSystemPage() throws Exception {
        config.setCustomFacts("Chuck Norris reviews his own pull requests.\nChuck Norris's builds are never flaky.");
        config.setCustomFactsOnly(true);

        j.configRoundtrip();

        assertEquals(
                List.of("Chuck Norris reviews his own pull requests.", "Chuck Norris's builds are never flaky."),
                config.getCustomFactList());
        assertTrue(config.isCustomFactsOnly());
    }

    @Test
    void blankLinesAndSurroundingSpacesAreDropped() {
        config.setCustomFacts("\n  First fact.  \r\n\n   \nSecond fact.\n");

        assertEquals(List.of("First fact.", "Second fact."), config.getCustomFactList());
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
