package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CordellWalkerRecorderTest {

    private FactGenerator mockGenerator;
    private CordellWalkerRecorder recorder;

    @BeforeEach
    void setUp() {
        mockGenerator = mock(FactGenerator.class);
        recorder = new CordellWalkerRecorder(mockGenerator);
    }

    @Test
    void testGetProjectActionWithNoLastBuildGivesNullAction() {
        AbstractProject mockProject = mock(AbstractProject.class);
        when(mockProject.getLastBuild()).thenReturn(null);
        assertNull(recorder.getProjectAction(mockProject));
    }

    @Test
    void testGetProjectActionHavingLastBuildGivesRoundhouseAction() {
        AbstractProject mockProject = mock(AbstractProject.class);
        Build mockBuild = mock(Build.class);

        when(mockProject.getLastBuild()).thenReturn(mockBuild);
        when(mockBuild.getResult()).thenReturn(Result.SUCCESS);
        when(mockGenerator.random()).thenReturn("Chuck Norris burst the dot com bubble.");

        Action action = recorder.getProjectAction(mockProject);

        assertInstanceOf(RoundhouseAction.class, action);
        assertEquals(Style.THUMB_UP, ((RoundhouseAction) action).getStyle());
        assertNotNull(((RoundhouseAction) action).getFact());
    }

    @Test
    void testPerformWithFailureResultAddsRoundHouseActionWithBadAssStyleAndExpectedFact() throws Exception {
        AbstractBuild mockBuild = mock(AbstractBuild.class);
        when(mockBuild.getResult()).thenReturn(Result.FAILURE);

        ArgumentCaptor<RoundhouseAction> actionCaptor = ArgumentCaptor.forClass(RoundhouseAction.class);
        doNothing().when(mockBuild).addAction(actionCaptor.capture());

        when(mockGenerator.random()).thenReturn("Chuck Norris burst the dot com bubble.");

        recorder.perform(mockBuild, mock(Launcher.class), mock(BuildListener.class));

        RoundhouseAction action = actionCaptor.getValue();

        verify(mockBuild, times(1)).addAction(same(action));
        assertEquals(Style.BAD_ASS, (action).getStyle());
        assertEquals("Chuck Norris burst the dot com bubble.", action.getFact());
    }
}
