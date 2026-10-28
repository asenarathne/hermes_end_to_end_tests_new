package com.wiley.hermes.runners;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class HermesTestRunner {

    @Test
    void testParallel() {
        // Runs all features, generates Cucumber JSON & HTML reports, uses 5 parallel threads
        Results results = Runner.path("classpath:features")
                .outputCucumberJson(true)
                .parallel(5);
        
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
}