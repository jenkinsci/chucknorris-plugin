package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.StreamBuildListener;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

class CordellWalkerRecorderTest {

    private FactGenerator mockGenerator;
    private CordellWalkerRecorder recorder;
    private AbstractBuild<?, ?> mockBuild;
    private ArgumentCaptor<RoundhouseAction> actionCaptor;

    @BeforeEach
    void setUp() {
        mockGenerator = mock(FactGenerator.class);
        recorder = new CordellWalkerRecorder(mockGenerator);
        mockBuild = mock(AbstractBuild.class);
        actionCaptor = ArgumentCaptor.forClass(RoundhouseAction.class);
        doNothing().when(mockBuild).addAction(actionCaptor.capture());
        when(mockGenerator.randomIndex()).thenReturn(5);
    }

    @Test
    void testPerformWithFailureResultAddsRoundHouseActionWithBadAssStyleAndExpectedFact() throws Exception {
        when(mockBuild.getResult()).thenReturn(Result.FAILURE);

        recorder.perform(
                mockBuild,
                mock(Launcher.class),
                new StreamBuildListener(OutputStream.nullOutputStream(), StandardCharsets.UTF_8));

        RoundhouseAction action = actionCaptor.getValue();
        assertEquals(Style.BAD_ASS, action.getStyle());
        assertEquals(Integer.valueOf(5), action.getFactIndex());
        assertNotNull(action.getFact());
    }

    @Test
    void testPerformReplacesEarlierActionInsteadOfAddingASecondOne() {
        recorder.perform(mockBuild, null);

        InOrder order = inOrder(mockBuild);
        order.verify(mockBuild).removeActions(RoundhouseAction.class);
        order.verify(mockBuild).addAction(actionCaptor.getValue());
    }

    @Test
    void testPerformPrintsFactToBuildLogAndReturnsIt() {
        ByteArrayOutputStream log = new ByteArrayOutputStream();
        BuildListener listener = mock(BuildListener.class);
        when(listener.getLogger()).thenReturn(new PrintStream(log, true, StandardCharsets.UTF_8));

        String fact = recorder.perform(mockBuild, listener);

        assertEquals(FactGenerator.getFact(5, Locale.getDefault()), fact);
        assertTrue(log.toString(StandardCharsets.UTF_8).contains("Chuck Norris says: " + fact));
    }
}
