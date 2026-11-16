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
    }

    @Test
    void testIsApplicableGivesTrue() {
        assertTrue(descriptor.isApplicable(mock(AbstractProject.class).getClass()));
    }
}
