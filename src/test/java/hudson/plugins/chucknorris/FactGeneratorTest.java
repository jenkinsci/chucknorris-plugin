package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;
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
    @ValueSource(strings = {"FactGenerator.properties", "FactGenerator_de.properties", "FactGenerator_it.properties"})
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
}
