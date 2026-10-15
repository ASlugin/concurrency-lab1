package org.labs.dinner;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Timeout(10)
class DinnerTest {

    @ParameterizedTest(name = "programmers={0}, waiters={1}, portions={2}, maxDelta={3}")
    @CsvSource({
            "7, 2, 10000, 2",
            "2, 1, 1000, 1",
            "3, 2, 10000, 1",
            "10, 3, 10000, 2",
            "50, 2, 10000, 3",
            "7, 1, 10000, 2",
            "5, 10, 10000, 2",
            "7, 2, 10000, 1000",
            "7, 2, 3, 1",
            "7, 2, 1, 1",
            "7, 2, 0, 1",
    })
    void dinnerFinishesAndAllPortionsAreEaten(int programmers, int waiters, int portions, int maxDelta)
            throws InterruptedException {
        Dinner dinner = new Dinner(programmers, waiters, portions, maxDelta);
        dinner.run();
        assertEquals(portions, dinner.getStatisticService().getStatistics().totalEatenPortions());
    }

    @ParameterizedTest(name = "programmers={0}, waiters={1}, portions={2}, maxDelta={3} -> {4}")
    @CsvSource({
            "1, 2, 100, 1, programmers",
            "0, 2, 100, 1, programmers",
            "-1, 2, 100, 1, programmers",
            "7, 0, 100, 1, waiters",
            "7, -1, 100, 1, waiters",
            "7, 2, -1, 1, portions",
            "7, 2, 100, 0, maxDeltaEatenPortions",
            "7, 2, 100, -1, maxDeltaEatenPortions",
    })
    void invalidParametersAreRejected(int programmers, int waiters, int portions, int maxDelta, String invalidParameter) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Dinner(programmers, waiters, portions, maxDelta));
        assertTrue(exception.getMessage().startsWith(invalidParameter + " "), exception.getMessage());
    }

    @RepeatedTest(200)
    void smallDinnerNeverHangs() throws InterruptedException {
        Dinner dinner = new Dinner(2, 1, 2, 1);
        dinner.run();
        assertEquals(2, dinner.getStatisticService().getStatistics().totalEatenPortions());
    }

    @RepeatedTest(100)
    void dinnerWithPortionPerProgrammerNeverHangs() throws InterruptedException {
        Dinner dinner = new Dinner(7, 2, 7, 1);
        dinner.run();
        assertEquals(7, dinner.getStatisticService().getStatistics().totalEatenPortions());
    }
}
