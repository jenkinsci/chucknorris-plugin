package hudson.plugins.chucknorris;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

public class SecondRoundhouseActionTest {

    private RoundhouseAction action;

    @Before
    public void setUp() {
        action = new RoundhouseAction(Style.BAD_ASS, "Chuck Norris can divide by zero.");
    }

    @Test
    public void testAccessors() {
        assertEquals(Style.BAD_ASS, action.getStyle());
        assertEquals("Chuck Norris can divide by zero.", action.getFact());
        assertEquals("Chuck Norris", action.getDisplayName());
        assertNull(action.getIconFileName());
        assertEquals("chucknorris", action.getUrlName());
    }

    @Test
    public void testGetProjectActions() {
        assertNotNull(action.getProjectActions());
        assertEquals(1, action.getProjectActions().size());
        assertSame(action, action.getProjectActions().iterator().next());
    }
}
