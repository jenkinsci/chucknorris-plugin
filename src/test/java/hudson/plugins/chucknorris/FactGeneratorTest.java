package hudson.plugins.chucknorris;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FactGeneratorTest {

    private FactGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new FactGenerator();
    }

    @Test
    void testRandomGivesAtLeast2Facts() {
        String lastFact = null;
        for (int i = 0; i < 1000000; i++) {
            String currFact = generator.random();
            if (lastFact != null && !lastFact.equals(currFact)) {
                return;
            }
            lastFact = currFact;
        }
        fail("Random should give at least 2 different facts in 1000000 tries.");
    }

    @Test
    void testFactsLoadedFromBundle() {
        String fact = generator.random();
        assertNotNull(fact, "Fact should not be null");
        assertFalse(fact.isEmpty(), "Fact should not be empty");
    }

    @Test
    void testAllFactsLoaded() {
        // Collect unique facts over many iterations to verify multiple facts are loaded
        java.util.Set<String> uniqueFacts = new java.util.HashSet<>();
        for (int i = 0; i < 10000000; i++) {
            uniqueFacts.add(generator.random());
            if (uniqueFacts.size() >= 82) {
                return;
            }
        }
        fail("Expected 82 unique facts but only found " + uniqueFacts.size());
    }
}
