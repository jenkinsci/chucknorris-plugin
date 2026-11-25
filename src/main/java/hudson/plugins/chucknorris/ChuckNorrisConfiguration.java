package hudson.plugins.chucknorris;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jenkins.appearance.AppearanceCategory;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalConfigurationCategory;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Facts that administrators add on top of the built-in ones, on the Manage Jenkins &gt; Appearance page or through
 * Configuration as Code ({@code appearance: chuckNorris:}).
 */
@Extension
@Symbol("chuckNorris")
public final class ChuckNorrisConfiguration extends GlobalConfiguration {

    /** The custom facts, trimmed and without blank lines, so that builds don't parse them each time. */
    private List<String> customFactList = new ArrayList<>();

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
     * Shows this configuration on the Manage Jenkins &gt; Appearance page, since it only changes what builds show.
     * @return the appearance category
     */
    @Override
    public GlobalConfigurationCategory getCategory() {
        return GlobalConfigurationCategory.get(AppearanceCategory.class);
    }

    /**
     * Gets the custom facts, one per line.
     * @return the custom facts, or null if there are none
     */
    public String getCustomFacts() {
        List<String> facts = getCustomFactList();
        return facts.isEmpty() ? null : String.join("\n", facts);
    }

    /**
     * Sets the custom facts, one per line.
     * @param customFacts the custom facts
     */
    @DataBoundSetter
    public void setCustomFacts(String customFacts) {
        this.customFactList = parse(customFacts);
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
        return customFactList == null ? Collections.emptyList() : Collections.unmodifiableList(customFactList);
    }

    private static List<String> parse(String customFacts) {
        String text = Util.fixEmptyAndTrim(customFacts);
        List<String> facts = new ArrayList<>();
        if (text == null) {
            return facts;
        }
        for (String line : text.split("\\R")) {
            String fact = line.trim();
            if (!fact.isEmpty()) {
                facts.add(fact);
            }
        }
        return facts;
    }
}
