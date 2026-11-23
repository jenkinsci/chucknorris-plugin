package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.FreeStyleProject;
import hudson.model.Run;
import java.util.List;
import java.util.Locale;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlPage;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * The fact in the console must be in the same language as on the build page, i.e. the viewer's language.
 */
@WithJenkins
class FactNoteTest {

    private JenkinsRule j;

    /** A language that differs from the controller's, so the test shows a real switch. */
    private Locale viewerLocale;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        j = rule;
        viewerLocale = Locale.getDefault().getLanguage().equals("de") ? Locale.ITALIAN : Locale.GERMAN;
    }

    @Test
    void freestyleConsoleShowsFactInViewerLanguage() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("p");
        project.getPublishersList().add(new CordellWalkerRecorder());

        assertConsoleMatchesBuildPage(j.buildAndAssertSuccess(project));
    }

    @Test
    void pipelineConsoleShowsFactInViewerLanguage() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class, "p");
        project.setDefinition(new CpsFlowDefinition("chuckNorris()", true));

        Run<?, ?> build = j.buildAndAssertSuccess(project);
        assertConsoleMatchesBuildPage(build);

        // The step's "Submitting to Chuck's will" line follows the viewer's language too.
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            wc.addRequestHeader("Accept-Language", viewerLocale.toLanguageTag());
            String shown = visibleConsoleText(wc.getPage(build, "console"));
            assertTrue(shown.contains(submitting(viewerLocale)), shown);
            assertFalse(shown.contains(submitting(Locale.getDefault())), shown);
        }
        j.assertLogContains(submitting(Locale.getDefault()), build);
    }

    @Test
    void plainTextLogKeepsControllerLanguage() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("p");
        project.getPublishersList().add(new CordellWalkerRecorder());
        Run<?, ?> build = j.buildAndAssertSuccess(project);

        j.assertLogContains(line(build, Locale.getDefault()), build);
    }

    private void assertConsoleMatchesBuildPage(Run<?, ?> build) throws Exception {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            wc.addRequestHeader("Accept-Language", viewerLocale.toLanguageTag());

            String buildPage = wc.getPage(build).asNormalizedText();
            String fact = FactGenerator.getFact(
                    build.getAction(RoundhouseAction.class).getFactIndex(), viewerLocale);
            assertTrue(buildPage.contains(fact), "build page should show the fact in the viewer's language");

            HtmlPage console = wc.getPage(build, "console");
            String original = line(build, Locale.getDefault());
            List<DomNode> hidden = console.getByXPath("//pre[@id='out']//span[@hidden]");
            assertEquals(
                    1,
                    hidden.stream()
                            .filter(n -> n.getTextContent().equals(original))
                            .count(),
                    "the original line should be kept, hidden");

            String shown = visibleConsoleText(console);
            assertTrue(shown.contains(line(build, viewerLocale)), shown);
            assertFalse(shown.contains(line(build, Locale.getDefault())), shown);
        }
    }

    @Test
    void consoleIsUnchangedWhenViewerUsesControllerLanguage() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("p");
        project.getPublishersList().add(new CordellWalkerRecorder());
        Run<?, ?> build = j.buildAndAssertSuccess(project);

        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            wc.addRequestHeader("Accept-Language", Locale.getDefault().toLanguageTag());
            HtmlPage console = wc.getPage(build, "console");

            assertTrue(console.getByXPath("//pre[@id='out']//span[@hidden]").isEmpty());
            assertTrue(console.asNormalizedText().contains(line(build, Locale.getDefault())));
        }
    }

    /**
     * The console text as a browser shows it. HtmlUnit does not apply the {@code hidden} attribute, so the hidden
     * elements are removed first.
     */
    private static String visibleConsoleText(HtmlPage console) {
        for (Object hidden : console.getByXPath("//pre[@id='out']//*[@hidden]")) {
            ((DomNode) hidden).remove();
        }
        return console.getElementById("out").asNormalizedText();
    }

    private static String submitting(Locale locale) {
        return hudson.plugins.chucknorris.pipeline.Messages._ChuckNorrisStepExecution_Submitting()
                .toString(locale);
    }

    private static String line(Run<?, ?> build, Locale locale) {
        int index = build.getAction(RoundhouseAction.class).getFactIndex();
        return Messages._CordellWalkerRecorder_FactLog(FactGenerator.getFact(index, locale))
                .toString(locale);
    }
}
