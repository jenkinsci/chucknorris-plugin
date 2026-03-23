package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import hudson.model.AbstractProject;
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
        assertEquals("Activate Chuck Norris", descriptor.getDisplayName());
    }

    @Test
    void testIsApplicableGivesTrue() {
        assertTrue(descriptor.isApplicable(mock(AbstractProject.class).getClass()));
    }
}
