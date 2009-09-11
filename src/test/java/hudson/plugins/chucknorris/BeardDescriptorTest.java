package hudson.plugins.chucknorris;

import static org.mockito.Mockito.mock;
import hudson.model.AbstractProject;
import junit.framework.TestCase;

public class BeardDescriptorTest extends TestCase {

	private BeardDescriptor descriptor;

	public void setUp() {
		descriptor = new BeardDescriptor();
	}

	public void testGetDisplayName() {
		assertEquals("Activate Chuck Norris", descriptor.getDisplayName());
	}

	public void testIsApplicableGivesTrue() {
		assertTrue(descriptor.isApplicable(mock(AbstractProject.class)
				.getClass()));
	}
}
