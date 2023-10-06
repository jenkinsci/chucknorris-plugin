package hudson.plugins.chucknorris;

import static org.mockito.Mockito.mock;

import hudson.model.Job;
import hudson.model.Run;
import junit.framework.TestCase;

public class SecondRoundhouseActionTest extends TestCase {

    private RoundhouseAction action;

    private Run<?, ?> run;
    private RoundhouseAction lastBuildAction;

    @Override
    @SuppressWarnings("rawtypes")
    public void setUp() {
        action = new RoundhouseAction(Style.BAD_ASS, "Chuck Norris can divide by zero.");

        run = mock(Run.class);
        lastBuildAction = new RoundhouseAction(Style.ALERT, "Chuck Norris went out of an infinite loop.");
        final Job job = mock(Job.class);
        Run<?, ?> lastRun = mock(Run.class);
    }

    public void testAccessors() {
        assertEquals(Style.BAD_ASS, action.getStyle());
        assertEquals("Chuck Norris can divide by zero.", action.getFact());
        assertEquals("Chuck Norris", action.getDisplayName());
        assertNull(action.getIconFileName());
        assertEquals("chucknorris", action.getUrlName());
    }

    public void testGetProjectActions() {
        assertNotNull(action.getProjectActions());
        assertEquals(1, action.getProjectActions().size());
        assertSame(action, action.getProjectActions().iterator().next());
    }
}
