package com.vlkan.rfos;

import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemClockTest {

    private abstract static class GenericTest implements Runnable {

        abstract protected Map<String, List<String>> getCurrentInstantTextsByExpectedInstantText();

        abstract protected Instant getActualInstant(Clock clock);

        @Override
        public void run() {
            Map<String, List<String>> currentInstantTextsByExpectedInstantText = getCurrentInstantTextsByExpectedInstantText();
            for (String expectedInstantText : currentInstantTextsByExpectedInstantText.keySet()) {
                List<String> currentInstantTexts = currentInstantTextsByExpectedInstantText.get(expectedInstantText);
                for (String currentInstantText : currentInstantTexts) {
                    testInstant(currentInstantText, expectedInstantText);
                }
            }
        }

        private void testInstant(String currentInstantText, String expectedInstantText) {
            Instant currentInstant = Instant.parse(currentInstantText);
            SystemClock clock = new SystemClock() {
                @Override
                public Instant now() {
                    return currentInstant;
                }
            };
            Instant actualInstant = getActualInstant(clock);
            String actualInstantText = UtcHelper.INSTANT_FORMATTER.format(actualInstant);
            assertThat(actualInstantText)
                    .as("currentInstantText=%s", currentInstantText)
                    .isEqualTo(expectedInstantText);
        }

    }

    @Test
    public void test_midnight() {

        // Create test cases.
        Map<String, List<String>> currentInstantTextsByExpectedInstantText = new LinkedHashMap<>();
        currentInstantTextsByExpectedInstantText.put(
                "2017-01-02T00:00:00.000Z",
                Arrays.asList(
                        "2017-01-01T00:00:00.000Z",
                        "2017-01-01T01:00:00.000Z",
                        "2017-01-01T23:59:59.999Z"));
        currentInstantTextsByExpectedInstantText.put(
                "2017-12-30T00:00:00.000Z",
                Arrays.asList(
                        "2017-12-29T00:00:00.000Z",
                        "2017-12-29T01:00:00.000Z",
                        "2017-12-29T23:59:59.999Z"));
        currentInstantTextsByExpectedInstantText.put(
                "2018-01-01T00:00:00.000Z",
                Arrays.asList(
                        "2017-12-31T00:00:00.000Z",
                        "2017-12-31T01:00:00.000Z",
                        "2017-12-31T23:59:59.999Z"));

        // Execute tests.
        new GenericTest() {

            @Override
            protected Map<String, List<String>> getCurrentInstantTextsByExpectedInstantText() {
                return currentInstantTextsByExpectedInstantText;
            }

            @Override
            protected Instant getActualInstant(Clock clock) {
                return clock.midnight();
            }

        }.run();

    }

    @Test
    public void test_sundayMidnight() {

        // Create test cases.
        Map<String, List<String>> currentInstantTextsByExpectedInstantText = new LinkedHashMap<>();
        currentInstantTextsByExpectedInstantText.put(
                "2017-01-02T00:00:00.000Z",
                Arrays.asList(
                        "2017-01-01T00:00:00.000Z",
                        "2017-01-01T01:00:00.000Z",
                        "2017-01-01T23:59:59.999Z"));
        currentInstantTextsByExpectedInstantText.put(
                "2018-01-01T00:00:00.000Z",
                Arrays.asList(
                        "2017-12-25T00:00:00.000Z",
                        "2017-12-25T01:00:00.000Z",
                        "2017-12-25T01:59:59.999Z",
                        "2017-12-26T00:00:00.000Z",
                        "2017-12-26T01:00:00.000Z",
                        "2017-12-26T01:59:59.999Z",
                        "2017-12-31T00:00:00.000Z",
                        "2017-12-31T01:00:00.000Z",
                        "2017-12-31T01:59:59.999Z"));

        // Execute tests.
        new GenericTest() {

            @Override
            protected Map<String, List<String>> getCurrentInstantTextsByExpectedInstantText() {
                return currentInstantTextsByExpectedInstantText;
            }

            @Override
            protected Instant getActualInstant(Clock clock) {
                return clock.sundayMidnight();
            }

        }.run();

    }

}
