package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import io.jenkins.plugins.casc.misc.ConfiguredWithCode;
import io.jenkins.plugins.casc.misc.JenkinsConfiguredWithCodeRule;
import io.jenkins.plugins.casc.misc.junit.jupiter.WithJenkinsConfiguredWithCode;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Custom facts set through Configuration as Code.
 */
@WithJenkinsConfiguredWithCode
class ChuckNorrisConfigurationCascTest {

    @Test
    @ConfiguredWithCode("configuration-as-code.yml")
    void customFactsComeFromConfigurationAsCode(JenkinsConfiguredWithCodeRule j) {
        ChuckNorrisConfiguration config = ChuckNorrisConfiguration.get();

        assertEquals(
                List.of("Chuck Norris reviews his own pull requests.", "Chuck Norris's builds are never flaky."),
                config.getCustomFactList());
        assertTrue(config.isCustomFactsOnly());
    }
}
