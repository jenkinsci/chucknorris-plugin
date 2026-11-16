package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.transform.stream.StreamSource;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * Checks how {@link CordellWalkerRecorder} is saved to and loaded from a job's config.xml.
 */
@WithJenkins
class CordellWalkerRecorderConfigTest {

    @Test
    void testSavedConfigDoesNotContainFactGenerator(JenkinsRule j) throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getPublishersList().add(new CordellWalkerRecorder());
        project.save();

        String xml = project.getConfigFile().asString();

        assertTrue(xml.contains("<hudson.plugins.chucknorris.CordellWalkerRecorder"), xml);
        assertFalse(xml.contains("factGenerator"), xml);
    }

    @Test
    void testRecorderReloadedFromConfigStillShowsAFact(JenkinsRule j) throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getPublishersList().add(new CordellWalkerRecorder());
        project.save();

        // Reload from disk so the recorder is rebuilt by XStream, not the constructor.
        project.updateByXml(new StreamSource(
                new ByteArrayInputStream(project.getConfigFile().asString().getBytes(StandardCharsets.UTF_8))));

        FreeStyleBuild build = j.buildAndAssertSuccess(project);

        RoundhouseAction action = build.getAction(RoundhouseAction.class);
        assertNotNull(action);
        assertNotNull(action.getFact());
    }

    @Test
    void testOldConfigWithFactGeneratorElementStillLoads(JenkinsRule j) throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        String oldXml = "<?xml version='1.1' encoding='UTF-8'?>\n"
                + "<project>\n"
                + "  <builders/>\n"
                + "  <publishers>\n"
                + "    <hudson.plugins.chucknorris.CordellWalkerRecorder>\n"
                + "      <factGenerator/>\n"
                + "    </hudson.plugins.chucknorris.CordellWalkerRecorder>\n"
                + "  </publishers>\n"
                + "  <buildWrappers/>\n"
                + "</project>";

        project.updateByXml(new StreamSource(new ByteArrayInputStream(oldXml.getBytes(StandardCharsets.UTF_8))));

        assertNotNull(project.getPublishersList().get(CordellWalkerRecorder.class));
        FreeStyleBuild build = j.buildAndAssertSuccess(project);
        assertNotNull(build.getAction(RoundhouseAction.class));

        project.save();
        assertFalse(project.getConfigFile().asString().contains("factGenerator"));
    }
}
