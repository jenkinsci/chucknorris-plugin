package hudson.plugins.chucknorris;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Facts that administrators add on top of the built-in ones, on the Manage Jenkins &gt; System page or through
 * Configuration as Code ({@code unclassified: chuckNorris:}).
 */
@Extension
@Symbol("chuckNorris")
public final class ChuckNorrisConfiguration extends GlobalConfiguration {

    /** The custom facts, one per line. */
    private String customFacts;

    /** Whether builds pick only from the custom facts. */
    private boolean customFactsOnly;

    public ChuckNorrisConfiguration() {
        load();
    }

    /**
     * Gets the configuration.
     * @return the configuration
     */
    public static ChuckNorrisConfiguration get() {
        return ExtensionList.lookupSingleton(ChuckNorrisConfiguration.class);
    }

    /**
     * Gets the configuration when Jenkins is running.
     * @return the configuration, or null outside a running Jenkins
     */
    static ChuckNorrisConfiguration getOrNull() {
        return Jenkins.getInstanceOrNull() == null ? null : get();
    }

    /**
     * Gets the custom facts as entered, one per line.
     * @return the custom facts, or null if there are none
     */
    public String getCustomFacts() {
        return customFacts;
    }

    /**
     * Sets the custom facts, one per line.
     * @param customFacts the custom facts
     */
    @DataBoundSetter
    public void setCustomFacts(String customFacts) {
        this.customFacts = Util.fixEmptyAndTrim(customFacts);
        save();
    }

    /**
     * Whether builds pick only from the custom facts. Has no effect while there are no custom facts.
     * @return true to use only the custom facts
     */
    public boolean isCustomFactsOnly() {
        return customFactsOnly;
    }

    /**
     * Sets whether builds pick only from the custom facts.
     * @param customFactsOnly true to use only the custom facts
     */
    @DataBoundSetter
    public void setCustomFactsOnly(boolean customFactsOnly) {
        this.customFactsOnly = customFactsOnly;
        save();
    }

    /**
     * Gets the custom facts, one per non-blank line, trimmed.
     * @return the custom facts
     */
    public List<String> getCustomFactList() {
        if (customFacts == null) {
            return Collections.emptyList();
        }
        List<String> facts = new ArrayList<>();
        for (String line : customFacts.split("\\R")) {
            String fact = line.trim();
            if (!fact.isEmpty()) {
                facts.add(fact);
            }
        }
        return facts;
    }
}
