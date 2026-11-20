package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import hudson.model.AbstractProject;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeardDescriptorTest {

    private BeardDescriptor descriptor;

    @BeforeEach
    void setUp() {
        descriptor = new BeardDescriptor();
    }

    @Test
    void testGetDisplayName() {
        assertEquals(
                "Activate Chuck Norris", Messages._BeardDescriptor_DisplayName().toString(Locale.ENGLISH));
    }

    @Test
    void testDisplayNameIsTranslated() {
        assertEquals(
                "Chuck Norris aktivieren",
                Messages._BeardDescriptor_DisplayName().toString(Locale.GERMAN));
        assertEquals(
                "Attiva Chuck Norris", Messages._BeardDescriptor_DisplayName().toString(Locale.ITALIAN));
        assertEquals(
                "Activar Chuck Norris", Messages._BeardDescriptor_DisplayName().toString(Locale.forLanguageTag("es")));
        assertEquals(
                "\u30c1\u30e3\u30c3\u30af\u30fb\u30ce\u30ea\u30b9\u3092\u6709\u52b9\u306b\u3059\u308b",
                Messages._BeardDescriptor_DisplayName().toString(Locale.JAPANESE));
    }

    @Test
    void testIsApplicableGivesTrue() {
        assertTrue(descriptor.isApplicable(mock(AbstractProject.class).getClass()));
    }

    @Test
    void testHasChuckNorrisSymbolForJobDslAndConfigurationAsCode() {
        org.jenkinsci.Symbol symbol = BeardDescriptor.class.getAnnotation(org.jenkinsci.Symbol.class);
        assertNotNull(symbol);
        assertArrayEquals(new String[] {"chuckNorris"}, symbol.value());
    }
}
