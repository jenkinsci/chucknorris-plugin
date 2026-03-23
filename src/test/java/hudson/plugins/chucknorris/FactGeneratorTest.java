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
}
