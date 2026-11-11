package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import java.util.Locale;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * Renders the real build and project pages to check the Jelly views.
 */
@WithJenkins
class RoundhouseActionPageTest {

    private JenkinsRule j;
    private FreeStyleProject project;
    private FreeStyleBuild build;

    @BeforeEach
    void setUp(JenkinsRule rule) throws Exception {
        j = rule;
        project = j.createFreeStyleProject("p");
        project.getPublishersList().add(new CordellWalkerRecorder());
        build = j.buildAndAssertSuccess(project);
    }

    @Test
    void buildPageShowsFactAndAppliesStyleClass() throws Exception {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            HtmlPage page = wc.getPage(build);

            String fact = FactGenerator.getFact(factIndex(), Locale.ENGLISH);
            assertTrue(page.asNormalizedText().contains(fact), "fact should be on the build page");

            String panelClass = page.getElementById("main-panel").getAttribute("class");
            assertTrue(panelClass.contains("chuck-style"), panelClass);
            assertTrue(panelClass.contains(Style.THUMB_UP.getCssClass()), panelClass);
        }
    }

    @Test
    void projectPageShowsFact() throws Exception {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            HtmlPage page = wc.getPage(project);

            String fact = FactGenerator.getFact(factIndex(), Locale.ENGLISH);
            assertTrue(page.asNormalizedText().contains(fact), "fact should be on the project page");
        }
    }

    @Test
    void buildPageUsesBrowserLocale() throws Exception {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            wc.addRequestHeader("Accept-Language", "de");
            HtmlPage page = wc.getPage(build);

            String fact = FactGenerator.getFact(factIndex(), Locale.GERMAN);
            assertTrue(page.asNormalizedText().contains(fact), "German fact should be on the build page");
        }
    }

    @Test
    void viewsHaveNoInlineStyleBlocks() throws Exception {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            String html = wc.getPage(build).getWebResponse().getContentAsString();

            assertFalse(html.contains(".chuck-style {"), "styles must come from chucknorris.css, not inline");
            assertTrue(html.contains("/plugin/chucknorris/css/chucknorris.css"), "stylesheet should be linked");
        }
    }

    private int factIndex() {
        return build.getAction(RoundhouseAction.class).getFactIndex();
    }
}
