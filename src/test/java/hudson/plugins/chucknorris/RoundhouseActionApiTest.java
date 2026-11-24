package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import java.util.Locale;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.FailureBuilder;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * Reads a build's remote API to check that the fact, fact index and style are exported.
 */
@WithJenkins
class RoundhouseActionApiTest {

    private JenkinsRule j;
    private FreeStyleProject project;

    @BeforeEach
    void setUp(JenkinsRule rule) throws Exception {
        j = rule;
        project = j.createFreeStyleProject("p");
        project.getPublishersList().add(new CordellWalkerRecorder());
    }

    @Test
    void buildApiJsonHasFactIndexAndStyle() throws Exception {
        FreeStyleBuild build = j.buildAndAssertSuccess(project);
        int factIndex = build.getAction(RoundhouseAction.class).getFactIndex();

        JSONObject action = chuckAction(build);

        assertEquals(FactGenerator.getFact(factIndex, Locale.ENGLISH), action.getString("fact"));
        assertEquals(factIndex, action.getInt("factIndex"));
        assertEquals("THUMB_UP", action.getString("style"));
    }

    @Test
    void buildApiJsonStyleFollowsTheResult() throws Exception {
        project.getBuildersList().add(new FailureBuilder());
        FreeStyleBuild failed = j.buildAndAssertStatus(Result.FAILURE, project);

        assertEquals("BAD_ASS", chuckAction(failed).getString("style"));
    }

    private JSONObject chuckAction(FreeStyleBuild build) throws Exception {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            JSONArray actions =
                    wc.getJSON(build.getUrl() + "api/json").getJSONObject().getJSONArray("actions");
            for (Object o : actions) {
                JSONObject action = (JSONObject) o;
                if (RoundhouseAction.class.getName().equals(action.optString("_class"))) {
                    return action;
                }
            }
            fail("no RoundhouseAction in the build's api/json actions: " + actions);
            return null;
        }
    }
}
