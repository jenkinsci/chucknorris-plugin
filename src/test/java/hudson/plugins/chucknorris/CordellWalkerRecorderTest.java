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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
        String expectedLog = Messages.CordellWalkerRecorder_FactLog(fact);
        assertTrue(log.toString(StandardCharsets.UTF_8).contains(expectedLog));
    }

    @Test
    void testFactLogLineIsTranslated() {
        assertEquals(
                "You know what? x", Messages._CordellWalkerRecorder_FactLog("x").toString(Locale.ENGLISH));
        assertEquals(
                "Wei\u00dft du was? x",
                Messages._CordellWalkerRecorder_FactLog("x").toString(Locale.GERMAN));
        assertEquals(
                "Sai una cosa? x", Messages._CordellWalkerRecorder_FactLog("x").toString(Locale.ITALIAN));
        assertEquals(
                "¿Sabes qué? x", Messages._CordellWalkerRecorder_FactLog("x").toString(Locale.forLanguageTag("es")));
        assertEquals(
                "Tu sais quoi ? x", Messages._CordellWalkerRecorder_FactLog("x").toString(Locale.FRENCH));
        assertEquals(
                "\u77e5\u3063\u3066\u308b\u304b\u3044\uff1f x",
                Messages._CordellWalkerRecorder_FactLog("x").toString(Locale.JAPANESE));
    }

    /**
     * Every translated locale must translate the build log line, the post-build action name and the console note
     * name, or the build log falls back to the English "You know what?".
     */
    @ParameterizedTest
    @ValueSource(strings = {"de", "es", "fr", "it", "ja", "nl", "pt-BR", "ru", "zh-CN", "zh-TW"})
    void testEveryLocaleTranslatesLogLineAndActionName(String tag) {
        Locale locale = Locale.forLanguageTag(tag);
        assertNotEquals(
                Messages._CordellWalkerRecorder_FactLog("x").toString(Locale.ENGLISH),
                Messages._CordellWalkerRecorder_FactLog("x").toString(locale),
                tag);
        assertNotEquals(
                Messages._BeardDescriptor_DisplayName().toString(Locale.ENGLISH),
                Messages._BeardDescriptor_DisplayName().toString(locale),
                tag);
        assertNotEquals(
                Messages._FactNote_DisplayName().toString(Locale.ENGLISH),
                Messages._FactNote_DisplayName().toString(locale),
                tag);
    }
}
