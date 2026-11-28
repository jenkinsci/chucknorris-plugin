package hudson.plugins.chucknorris.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hudson.plugins.chucknorris.RoundhouseAction;
import org.htmlunit.html.HtmlPage;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.cps.Snippetizer;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * The {@code quiet} and {@code showImage} options of the {@code chuckNorris} step.
 */
@WithJenkins
class ChuckNorrisStepOptionsTest {

    @Test
    void quietLeavesTheFactOutOfTheLogButStillShowsAndReturnsIt(JenkinsRule j) throws Exception {
        WorkflowRun run = build(j, "echo \"returned: ${chuckNorris(quiet: true)}\"");
        RoundhouseAction action = run.getAction(RoundhouseAction.class);
        assertNotNull(action, "the build should still get its fact");

        String fact = action.getFact();
        String log = JenkinsRule.getLog(run);
        assertFalse(log.contains("You know what?"), "quiet should not print the fact line");
        assertFalse(log.contains("Submitting to Chuck's will"), "quiet should not print the step's line");
        assertTrue(log.contains("returned: " + fact), "the step should still return the fact");

        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            assertTrue(wc.getPage(run).asNormalizedText().contains(fact), "the build page should show the fact");
        }
    }

    @Test
    void showImageFalseShowsOnlyTheFact(JenkinsRule j) throws Exception {
        WorkflowRun run = build(j, "chuckNorris(showImage: false)");
        RoundhouseAction action = run.getAction(RoundhouseAction.class);
        assertFalse(action.isShowImage());
        assertTrue(JenkinsRule.getLog(run).contains("You know what?"), "the fact line is still printed");

        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            HtmlPage buildPage = wc.getPage(run);
            assertTrue(buildPage.asNormalizedText().contains(action.getFact()));
            assertTrue(
                    buildPage
                            .getByXPath("//img[contains(@class, 'chuck-image')]")
                            .isEmpty(),
                    "no image on the build page");

            HtmlPage jobPage = wc.getPage(run.getParent());
            assertTrue(
                    jobPage.getByXPath("//img[contains(@class, 'chuck-image')]").isEmpty(), "no image on the job page");
        }
    }

    @Test
    void theDefaultsKeepTodaysBehaviour(JenkinsRule j) throws Exception {
        WorkflowRun run = build(j, "chuckNorris()");
        RoundhouseAction action = run.getAction(RoundhouseAction.class);
        assertTrue(action.isShowImage());
        String log = JenkinsRule.getLog(run);
        assertTrue(log.contains("Submitting to Chuck's will"));
        assertTrue(log.contains("You know what?"));

        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            assertEquals(
                    1,
                    wc.getPage(run)
                            .getByXPath("//div[@class='chuck-build-image']/img[contains(@class, 'chuck-image')]")
                            .size());
        }
    }

    @Test
    void theSnippetGeneratorWritesOnlyTheOptionsThatDifferFromTheDefaults(JenkinsRule j) {
        assertEquals("chuckNorris()", Snippetizer.object2Groovy(new ChuckNorrisStep()));

        ChuckNorrisStep step = new ChuckNorrisStep();
        step.setQuiet(true);
        step.setShowImage(false);
        assertEquals("chuckNorris quiet: true, showImage: false", Snippetizer.object2Groovy(step));
    }

    @Test
    void aBuildSavedBeforeTheOptionExistedStillShowsTheImage(JenkinsRule j) {
        RoundhouseAction legacy = (RoundhouseAction) jenkins.model.Jenkins.XSTREAM2.fromXML(
                "<hudson.plugins.chucknorris.RoundhouseAction><mFactIndex>3</mFactIndex>"
                        + "</hudson.plugins.chucknorris.RoundhouseAction>");
        assertTrue(legacy.isShowImage());
        assertEquals(3, legacy.getFactIndex());
    }

    private static WorkflowRun build(JenkinsRule j, String script) throws Exception {
        WorkflowJob p = j.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition(script, true));
        return j.buildAndAssertSuccess(p);
    }
}
