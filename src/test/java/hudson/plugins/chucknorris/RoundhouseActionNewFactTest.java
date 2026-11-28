package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.Result;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import jenkins.model.Jenkins;
import org.htmlunit.HttpMethod;
import org.htmlunit.WebRequest;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.FailureBuilder;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * The New fact button on the job page: a POST to the build's {@code chucknorris/newFact} picks another fact for the
 * build and saves it, for users who can configure the job.
 */
@WithJenkins
class RoundhouseActionNewFactTest {

    private JenkinsRule j;
    private FreeStyleProject project;

    @BeforeEach
    void setUp(JenkinsRule rule) throws Exception {
        j = rule;
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy()
                .grant(Jenkins.READ, Item.READ, Item.CONFIGURE)
                .everywhere()
                .to("configurer")
                .grant(Jenkins.READ, Item.READ)
                .everywhere()
                .to("reader"));
        project = j.createFreeStyleProject("p");
        project.getPublishersList().add(new CordellWalkerRecorder());
    }

    @Test
    void theButtonPicksANewFactAndSavesItWithTheBuild() throws Exception {
        FreeStyleBuild build = j.buildAndAssertSuccess(project);
        Integer before = action(build).getFactIndex();
        Integer picked;

        try (JenkinsRule.WebClient wc = j.createWebClient().login("configurer")) {
            HtmlPage page = wc.getPage(project);
            HtmlButton button = page.querySelector("form.chuck-floating__new-fact button");
            assertNotNull(button, "a user who can configure the job should see the New fact button");
            HtmlPage after = button.click();
            assertEquals(j.getURL() + project.getUrl(), after.getUrl().toString(), "back on the job page");

            picked = action(build).getFactIndex();
            assertNotNull(picked);
            assertNotEquals(before, picked, "the fact should change");
            String fact = FactGenerator.getFact(picked, Locale.ENGLISH);
            assertTrue(after.asNormalizedText().contains(fact), "the job page should show the new fact");
        }

        // saved with the build, so it is the same after a reload
        build.reload();
        assertEquals(picked, action(build).getFactIndex());
    }

    @Test
    void theMoodStillFollowsTheBuildResult() throws Exception {
        project.getBuildersList().add(new FailureBuilder());
        FreeStyleBuild build = j.buildAndAssertStatus(Result.FAILURE, project);

        try (JenkinsRule.WebClient wc = j.createWebClient().login("configurer")) {
            wc.getPage(post(wc, build));
        }
        assertEquals(Style.BAD_ASS, action(build).getStyle());
    }

    @Test
    void aUserWhoCannotConfigureTheJobSeesNoButtonAndIsRefused() throws Exception {
        FreeStyleBuild build = j.buildAndAssertSuccess(project);
        Integer before = action(build).getFactIndex();

        try (JenkinsRule.WebClient wc = j.createWebClient().login("reader")) {
            HtmlPage page = wc.getPage(project);
            assertNull(page.querySelector("form.chuck-floating__new-fact"), "no button without Job/Configure");

            wc.setThrowExceptionOnFailingStatusCode(false);
            assertEquals(
                    HttpURLConnection.HTTP_FORBIDDEN,
                    wc.getPage(post(wc, build)).getWebResponse().getStatusCode());
        }
        assertEquals(before, action(build).getFactIndex(), "the fact must not change");
    }

    @Test
    void aGetOrAPostWithoutTheCrumbChangesNothing() throws Exception {
        FreeStyleBuild build = j.buildAndAssertSuccess(project);
        Integer before = action(build).getFactIndex();
        URL url = new URL(j.getURL(), build.getUrl() + "chucknorris/newFact");

        try (JenkinsRule.WebClient wc = j.createWebClient().login("configurer")) {
            wc.setThrowExceptionOnFailingStatusCode(false);
            assertEquals(
                    HttpURLConnection.HTTP_BAD_METHOD,
                    wc.getPage(new WebRequest(url, HttpMethod.GET))
                            .getWebResponse()
                            .getStatusCode());
            assertEquals(
                    HttpURLConnection.HTTP_FORBIDDEN,
                    wc.getPage(new WebRequest(url, HttpMethod.POST))
                            .getWebResponse()
                            .getStatusCode(),
                    "a POST without the CSRF crumb should be refused");
        }
        assertEquals(before, action(build).getFactIndex(), "the fact must not change");
    }

    @Test
    void pickNewFactDrawsAgainWhenItGetsTheSameFact() {
        RoundhouseAction action = new RoundhouseAction(Style.THUMB_UP, 3);
        action.pickNewFact(new Scripted(3, 3, 7), null);
        assertEquals(7, action.getFactIndex());
    }

    @Test
    void pickNewFactCanPickACustomFact() throws Exception {
        ChuckNorrisConfiguration config = ChuckNorrisConfiguration.get();
        config.setCustomFacts("Chuck Norris reviews his own pull requests.");
        config.setCustomFactsOnly(true);
        RoundhouseAction action = new RoundhouseAction(Style.THUMB_UP, 3);

        action.pickNewFact(new FactGenerator(), config);

        assertNull(action.getFactIndex());
        assertEquals("Chuck Norris reviews his own pull requests.", action.getFact());
    }

    private static RoundhouseAction action(FreeStyleBuild build) {
        return build.getAction(RoundhouseAction.class);
    }

    private WebRequest post(JenkinsRule.WebClient wc, FreeStyleBuild build) throws Exception {
        return wc.addCrumb(
                new WebRequest(new URL(j.getURL(), build.getUrl() + "chucknorris/newFact"), HttpMethod.POST));
    }

    /** Hands out the given fact indexes in turn, and no custom facts. */
    private static final class Scripted extends FactGenerator {
        private final Deque<Integer> indexes;

        Scripted(Integer... indexes) {
            this.indexes = new ArrayDeque<>(Arrays.asList(indexes));
        }

        @Override
        public int randomIndex() {
            return indexes.size() > 1 ? indexes.pop() : indexes.peek();
        }

        @Override
        public String randomCustomFact(List<String> customFacts, boolean customFactsOnly) {
            return null;
        }
    }
}
