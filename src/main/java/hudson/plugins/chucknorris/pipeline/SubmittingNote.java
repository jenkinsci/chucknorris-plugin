package hudson.plugins.chucknorris.pipeline;

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
 * Marks the "Submitting to Chuck's will" line of the {@code chuckNorris} step so that it is shown in the viewer's
 * language, like the fact line after it.
 *
 * <p>The log is written while the build runs, so the line itself is in the controller's default language. When the
 * console is shown in a browser, this note swaps in the same line in the browser's language. The plain text log
 * ({@code consoleText}) keeps the original line.
 */
public class SubmittingNote extends ConsoleNote<Object> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(SubmittingNote.class.getName());

    /** The length of the logged line, which this note covers. */
    private final int length;

    SubmittingNote(int length) {
        this.length = length;
    }

    /**
     * Encodes the "Submitting to Chuck's will" line, in the controller's default language, with a note in front.
     * @return the line to print to the build log
     */
    public static String encodeLine() {
        String line = Messages._ChuckNorrisStepExecution_Submitting().toString(Locale.getDefault());
        try {
            return new SubmittingNote(line.length()).encode() + line;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to serialize " + SubmittingNote.class, e);
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
        String localized = Messages._ChuckNorrisStepExecution_Submitting().toString(req.getLocale());
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
            return Messages.SubmittingNote_DisplayName();
        }
    }
}
