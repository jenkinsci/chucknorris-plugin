package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecondRoundhouseActionTest {

    private RoundhouseAction action;

    @BeforeEach
    void setUp() {
        action = new RoundhouseAction(Style.BAD_ASS, 0);
    }

    @Test
    void testAccessors() {
        assertEquals(Style.BAD_ASS, action.getStyle());
        assertNotNull(action.getFact());
        assertEquals("Chuck Norris", action.getDisplayName());
        assertNull(action.getIconFileName());
        assertEquals("chucknorris", action.getUrlName());
    }

    @Test
    void testGetProjectActions() {
        assertNotNull(action.getProjectActions());
        assertEquals(1, action.getProjectActions().size());
        assertSame(action, action.getProjectActions().iterator().next());
    }
}
