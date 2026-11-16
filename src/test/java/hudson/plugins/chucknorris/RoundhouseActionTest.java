package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.util.XStream2;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoundhouseActionTest {

    private RoundhouseAction action;

    @BeforeEach
    void setUp() {
        action = new RoundhouseAction(Style.BAD_ASS, 0);
    }

    @Test
    void testAccessors() {
        assertEquals(Style.BAD_ASS, action.getStyle());
        assertEquals(Integer.valueOf(0), action.getFactIndex());
        assertNotNull(action.getFact());
        assertEquals("Chuck Norris", action.getDisplayName());
        assertNull(action.getIconFileName());
        assertEquals("chucknorris", action.getUrlName());
    }

    @Test
    void testGetStyleFollowsTheRunResultOnceAttached() {
        Run<?, ?> run = mock(Run.class);
        when(run.getResult()).thenReturn(Result.SUCCESS);

        action.onAttached(run);

        assertEquals(Style.THUMB_UP, action.getStyle());
    }

    @Test
    void testGetProjectActionsWithoutRunReturnsItself() {
        assertEquals(List.of(action), List.copyOf(action.getProjectActions()));
    }

    @Test
    void testGetProjectActionsComeFromTheLastCompletedBuild() {
        RoundhouseAction lastBuildAction = new RoundhouseAction(Style.ALERT, 1);
        Run<?, ?> lastRun = mock(Run.class);
        doReturn(List.of(lastBuildAction)).when(lastRun).getActions(RoundhouseAction.class);

        action.onAttached(runOfJobWithLastCompletedBuild(lastRun));

        assertEquals(List.of(lastBuildAction), List.copyOf(action.getProjectActions()));
    }

    @Test
    void testGetProjectActionsWhenNoCompletedBuildReturnsItself() {
        action.onAttached(runOfJobWithLastCompletedBuild(null));

        assertEquals(List.of(action), List.copyOf(action.getProjectActions()));
    }

    /** Builds saved by version 0.2 stored the style and the fact text in the {@code style} and {@code fact} fields. */
    @Test
    void testLoadsBuildsSavedByVersion02() {
        RoundhouseAction legacy = load("<style>BAD_ASS</style><fact>Chuck Norris can divide by zero.</fact>");

        assertEquals(Style.BAD_ASS, legacy.getStyle());
        assertEquals("Chuck Norris can divide by zero.", legacy.getFact());
        assertNull(legacy.getFactIndex());
    }

    /** Builds saved before facts were translated stored the fact text in {@code mFact}. */
    @Test
    void testLoadsBuildsSavedWithTheFactText() {
        RoundhouseAction legacy =
                load("<mStyle>THUMB_UP</mStyle><mFact>Chuck Norris writes code that optimizes itself.</mFact>");

        assertEquals(Style.THUMB_UP, legacy.getStyle());
        assertEquals("Chuck Norris writes code that optimizes itself.", legacy.getFact());
        assertNull(legacy.getFactIndex());
    }

    @Test
    void testLoadsBuildsSavedWithAFactIndex() {
        RoundhouseAction current = load("<mStyle>ALERT</mStyle><mFactIndex>3</mFactIndex>");

        assertEquals(Style.ALERT, current.getStyle());
        assertEquals(Integer.valueOf(3), current.getFactIndex());
        assertEquals(FactGenerator.getFact(3, java.util.Locale.getDefault()), current.getFact());
    }

    @Test
    void testCurrentFormatSurvivesASaveAndLoad() {
        XStream2 xstream = new XStream2();

        RoundhouseAction reloaded = (RoundhouseAction) xstream.fromXML(xstream.toXML(action));

        assertEquals(Style.BAD_ASS, reloaded.getStyle());
        assertEquals(Integer.valueOf(0), reloaded.getFactIndex());
    }

    private static RoundhouseAction load(String fields) {
        return (RoundhouseAction) new XStream2()
                .fromXML("<hudson.plugins.chucknorris.RoundhouseAction>" + fields
                        + "</hudson.plugins.chucknorris.RoundhouseAction>");
    }

    private static Run<?, ?> runOfJobWithLastCompletedBuild(Run<?, ?> lastCompletedBuild) {
        Run<?, ?> run = mock(Run.class);
        Job<?, ?> job = mock(Job.class);
        doReturn(job).when(run).getParent();
        doReturn(lastCompletedBuild).when(job).getLastCompletedBuild();
        return run;
    }
}
