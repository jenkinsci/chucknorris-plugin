package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import java.util.List;
import java.util.Locale;
import org.htmlunit.html.HtmlImage;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.FailureBuilder;
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
    void buildPageShowsFactAndImage() throws Exception {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            HtmlPage page = wc.getPage(build);

            String fact = FactGenerator.getFact(factIndex(), Locale.ENGLISH);
            assertTrue(page.asNormalizedText().contains(fact), "fact should be on the build page");
            assertChuckImage(page, Style.THUMB_UP);
        }
    }

    @Test
    void projectPageShowsFactAndImage() throws Exception {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            HtmlPage page = wc.getPage(project);

            String fact = FactGenerator.getFact(factIndex(), Locale.ENGLISH);
            assertTrue(page.asNormalizedText().contains(fact), "fact should be on the project page");
            assertChuckImage(page, Style.THUMB_UP);
        }
    }

    /**
     * On the project page the fact stays in the right-hand column, and the image sits in the main area (next to the
     * test results and permalinks), not under the fact.
     */
    @Test
    void projectPageShowsImageInMainArea() throws Exception {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            HtmlPage page = wc.getPage(project);

            assertEquals(
                    1,
                    page.getByXPath("//div[@class='chuck-job-image']/img[contains(@class, 'chuck-image')]")
                            .size(),
                    "image should be in the main area");
            assertEquals(
                    0,
                    page.getByXPath("//div[@class='chuck-floating']//img[contains(@class, 'chuck-image')]")
                            .size(),
                    "image should not be under the fact in the right-hand column");
        }
    }

    /**
     * The image is part of the plugin's own markup, so it must be there without any script and without relying on
     * Jenkins' page layout (such as the {@code #main-panel} element).
     */
    @Test
    void imageIsRenderedWithoutJavaScript() throws Exception {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            wc.setJavaScriptEnabled(false);
            HtmlPage page = wc.getPage(build);

            assertChuckImage(page, Style.THUMB_UP);
        }
    }

    @Test
    void imageFollowsBuildResult() throws Exception {
        project.getBuildersList().add(new FailureBuilder());
        FreeStyleBuild failed = j.buildAndAssertStatus(Result.FAILURE, project);
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            assertChuckImage(wc.getPage(failed), Style.BAD_ASS);
        }
    }

    private void assertChuckImage(HtmlPage page, Style style) throws Exception {
        List<HtmlImage> images = page.getByXPath("//img[contains(@class, 'chuck-image')]");
        assertEquals(1, images.size(), "expected exactly one Chuck image");
        HtmlImage image = images.get(0);
        assertTrue(image.getAttribute("class").contains(style.getCssClass()), image.getAttribute("class"));
        assertTrue(
                image.getSrcAttribute().endsWith("/plugin/chucknorris/images/" + style.getImageFile()),
                image.getSrcAttribute());
        assertEquals(
                200,
                page.getWebClient()
                        .getPage(page.getFullyQualifiedUrl(image.getSrcAttribute()))
                        .getWebResponse()
                        .getStatusCode());
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

            assertFalse(html.contains(".chuck-image {"), "styles must come from chucknorris.css, not inline");
            assertTrue(html.contains("/plugin/chucknorris/css/chucknorris.css"), "stylesheet should be linked");
        }
    }

    private int factIndex() {
        return build.getAction(RoundhouseAction.class).getFactIndex();
    }
}
