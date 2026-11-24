package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FactGeneratorTest {

    private static final int EXPECTED_FACT_COUNT = 102;

    private FactGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new FactGenerator();
    }

    @Test
    void testRandomIndexGivesAtLeast2Indices() {
        int lastIndex = -1;
        for (int i = 0; i < 1000000; i++) {
            int currIndex = generator.randomIndex();
            if (lastIndex >= 0 && lastIndex != currIndex) {
                return;
            }
            lastIndex = currIndex;
        }
        fail("randomIndex should give at least 2 different indices in 1000000 tries.");
    }

    @Test
    void testGetFactReturnsNonEmpty() {
        String fact = FactGenerator.getFact(0, Locale.ENGLISH);
        assertNotNull(fact, "Fact should not be null");
        assertFalse(fact.isEmpty(), "Fact should not be empty");
    }

    @Test
    void testGetFactReturnsItalian() {
        String englishFact = FactGenerator.getFact(0, Locale.ENGLISH);
        String italianFact = FactGenerator.getFact(0, Locale.ITALIAN);
        assertNotEquals(englishFact, italianFact, "Italian fact should differ from English");
    }

    @Test
    void testGetFactReturnsGerman() {
        String englishFact = FactGenerator.getFact(0, Locale.ENGLISH);
        String germanFact = FactGenerator.getFact(0, Locale.GERMAN);
        assertNotEquals(englishFact, germanFact, "German fact should differ from English");
    }

    @Test
    void testGetFactReturnsBrazilianPortuguese() {
        String englishFact = FactGenerator.getFact(0, Locale.ENGLISH);
        String brazilianFact = FactGenerator.getFact(0, Locale.forLanguageTag("pt-BR"));
        assertNotEquals(englishFact, brazilianFact, "Brazilian Portuguese fact should differ from English");
    }

    @Test
    void testGetFactReturnsChinese() {
        String englishFact = FactGenerator.getFact(0, Locale.ENGLISH);
        String chineseFact = FactGenerator.getFact(0, Locale.SIMPLIFIED_CHINESE);
        assertNotEquals(englishFact, chineseFact, "Chinese fact should differ from English");
    }

    @Test
    void testGetFactReturnsTraditionalChinese() {
        String englishFact = FactGenerator.getFact(0, Locale.ENGLISH);
        String traditionalChineseFact = FactGenerator.getFact(0, Locale.TRADITIONAL_CHINESE);
        assertNotEquals(englishFact, traditionalChineseFact, "Traditional Chinese fact should differ from English");
    }

    @Test
    void testGetFactReturnsRussian() {
        String englishFact = FactGenerator.getFact(0, Locale.ENGLISH);
        String russianFact = FactGenerator.getFact(0, Locale.forLanguageTag("ru"));
        assertNotEquals(englishFact, russianFact, "Russian fact should differ from English");
    }

    @Test
    void testGetFactReturnsSpanish() {
        String englishFact = FactGenerator.getFact(0, Locale.ENGLISH);
        String localizedFact = FactGenerator.getFact(0, Locale.forLanguageTag("es"));
        assertNotEquals(englishFact, localizedFact, "Spanish fact should differ from English");
    }

    @Test
    void testGetFactReturnsFrench() {
        String englishFact = FactGenerator.getFact(0, Locale.ENGLISH);
        String localizedFact = FactGenerator.getFact(0, Locale.FRENCH);
        assertNotEquals(englishFact, localizedFact, "French fact should differ from English");
    }

    @Test
    void testGetFactReturnsJapanese() {
        String englishFact = FactGenerator.getFact(0, Locale.ENGLISH);
        String localizedFact = FactGenerator.getFact(0, Locale.JAPANESE);
        assertNotEquals(englishFact, localizedFact, "Japanese fact should differ from English");
    }

    @Test
    void testGetFactReturnsDutch() {
        String englishFact = FactGenerator.getFact(0, Locale.ENGLISH);
        String localizedFact = FactGenerator.getFact(0, Locale.forLanguageTag("nl"));
        assertNotEquals(englishFact, localizedFact, "Dutch fact should differ from English");
    }

    @Test
    void testMissingFactFallsBackToEnglishFallback() {
        assertEquals("Chuck Norris can divide by zero.", FactGenerator.getFact(999, Locale.ENGLISH));
        assertEquals("Chuck Norris can divide by zero.", FactGenerator.getFact(-1, Locale.ENGLISH));
    }

    @ParameterizedTest
    @ValueSource(strings = {"de", "it", "pt-BR", "ru", "zh-CN", "zh-TW", "es", "ja", "fr", "nl"})
    void testMissingFactFallsBackToLocalizedFallback(String languageTag) {
        Locale locale = Locale.forLanguageTag(languageTag);
        String fallback = FactGenerator.getFact(999, locale);
        assertNotEquals(
                FactGenerator.getFact(999, Locale.ENGLISH),
                fallback,
                "Fallback fact for " + languageTag + " should not be the English text");
    }

    @Test
    void testAllFactsLoaded() {
        java.util.Set<Integer> uniqueIndices = new java.util.HashSet<>();
        for (int i = 0; i < 10000000; i++) {
            uniqueIndices.add(generator.randomIndex());
            if (uniqueIndices.size() >= EXPECTED_FACT_COUNT) {
                return;
            }
        }
        fail("Expected " + EXPECTED_FACT_COUNT + " unique fact indices but only found " + uniqueIndices.size());
    }

    /**
     * Reads each properties file directly (not via ResourceBundle, which would silently fall back to English)
     * and checks that it declares the right fact.count and defines every fact.N key, written in ASCII only.
     */
    @ParameterizedTest
    @ValueSource(
            strings = {
                "FactGenerator.properties",
                "FactGenerator_de.properties",
                "FactGenerator_it.properties",
                "FactGenerator_pt_BR.properties",
                "FactGenerator_zh_CN.properties",
                "FactGenerator_zh_TW.properties",
                "FactGenerator_ru.properties",
                "FactGenerator_es.properties",
                "FactGenerator_ja.properties",
                "FactGenerator_fr.properties",
                "FactGenerator_nl.properties"
            })
    void testEveryLocaleFileIsComplete(String fileName) throws IOException {
        byte[] raw;
        try (InputStream in = FactGenerator.class.getResourceAsStream(fileName)) {
            assertNotNull(in, fileName + " should exist");
            raw = in.readAllBytes();
        }
        for (byte b : raw) {
            assertTrue(b >= 0, fileName + " must be ASCII; escape other characters as \\uXXXX");
        }

        Properties props = new Properties();
        props.load(new StringReader(new String(raw, StandardCharsets.US_ASCII)));
        assertEquals(String.valueOf(EXPECTED_FACT_COUNT), props.getProperty("fact.count"), fileName + " fact.count");
        for (int i = 0; i < EXPECTED_FACT_COUNT; i++) {
            String fact = props.getProperty("fact." + i);
            assertNotNull(fact, fileName + " is missing fact." + i);
            assertFalse(fact.isBlank(), fileName + " has an empty fact." + i);
        }
        assertNull(props.getProperty("fact." + EXPECTED_FACT_COUNT), fileName + " has facts beyond fact.count");
    }

    @Test
    void noCustomFactsMeansABuiltInFact() {
        assertNull(FactGenerator.randomCustomFact(List.of(), true, fixed(0)));
    }

    @Test
    void customOnlyAlwaysPicksACustomFact() {
        List<String> custom = List.of("First.", "Second.");

        assertEquals("First.", FactGenerator.randomCustomFact(custom, true, fixed(0)));
        assertEquals("Second.", FactGenerator.randomCustomFact(custom, true, fixed(1)));
    }

    @Test
    void mixedPicksComeFromOnePoolOfBuiltInAndCustomFacts() {
        List<String> custom = List.of("First.", "Second.");
        int[] bound = new int[1];
        Random last = new Random() {
            @Override
            public int nextInt(int b) {
                bound[0] = b;
                return b - 1;
            }
        };

        assertNull(FactGenerator.randomCustomFact(custom, false, fixed(EXPECTED_FACT_COUNT - 1)), "a built-in pick");
        assertEquals("First.", FactGenerator.randomCustomFact(custom, false, fixed(EXPECTED_FACT_COUNT)));
        assertEquals("Second.", FactGenerator.randomCustomFact(custom, false, last));
        assertEquals(EXPECTED_FACT_COUNT + 2, bound[0], "every built-in and custom fact is equally likely");
    }

    private static Random fixed(int value) {
        return new Random() {
            @Override
            public int nextInt(int bound) {
                assertTrue(value < bound, value + " is outside the pool of " + bound);
                return value;
            }
        };
    }
}
