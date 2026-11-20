package hudson.plugins.chucknorris;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.MarkupText;
import hudson.Util;
import hudson.console.ConsoleAnnotationDescriptor;
import hudson.console.ConsoleAnnotator;
import hudson.console.ConsoleNote;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest2;

/**
 * Marks the fact line in the build log so that it is shown in the viewer's language.
 *
 * <p>The log is written while the build runs, before anyone views it, so the line itself is in the controller's
 * default language. When the console is shown in a browser, this note swaps in the same fact in the browser's
 * language, matching the build page. The plain text log ({@code consoleText}) keeps the original line.
 */
public class FactNote extends ConsoleNote<Object> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(FactNote.class.getName());

    /** The fact index, looked up again in the viewer's language. */
    private final int factIndex;

    /** The length of the logged line, which this note covers. */
    private final int length;

    FactNote(int factIndex, int length) {
        this.factIndex = factIndex;
        this.length = length;
    }

    /**
     * Encodes the fact line with a note in front of it.
     * @param factIndex the fact index
     * @param line the fact line, in the controller's default language
     * @return the line to print to the build log
     */
    public static String encodeTo(int factIndex, String line) {
        // The note covers a single line: a newline would make its length run past the displayed line.
        line = line.replace('\n', ' ').replace('\r', ' ');
        try {
            return new FactNote(factIndex, line.length()).encode() + line;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to serialize " + FactNote.class, e);
            return line;
        }
    }

    @Override
    public ConsoleAnnotator<Object> annotate(Object context, MarkupText text, int charPos) {
        StaplerRequest2 req = Stapler.getCurrentRequest2();
        int end = charPos + length;
        if (req == null || end > text.length()) {
            return null;
        }
        Locale locale = req.getLocale();
        String localized = Messages._CordellWalkerRecorder_FactLog(FactGenerator.getFact(factIndex, locale))
                .toString(locale);
        if (!localized.equals(text.getText().substring(charPos, end))) {
            text.addMarkup(charPos, end, "<span hidden>", "</span>" + Util.escape(localized));
        }
        return null;
    }

    /**
     * Registers the note with Jenkins.
     */
    @Extension
    public static final class DescriptorImpl extends ConsoleAnnotationDescriptor {
        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.FactNote_DisplayName();
        }
    }
}
