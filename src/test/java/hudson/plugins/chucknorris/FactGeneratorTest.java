package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FactGeneratorTest {

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
    void testAllFactsLoaded() {
        java.util.Set<Integer> uniqueIndices = new java.util.HashSet<>();
        for (int i = 0; i < 10000000; i++) {
            uniqueIndices.add(generator.randomIndex());
            if (uniqueIndices.size() >= 82) {
                return;
            }
        }
        fail("Expected 82 unique fact indices but only found " + uniqueIndices.size());
    }
}
