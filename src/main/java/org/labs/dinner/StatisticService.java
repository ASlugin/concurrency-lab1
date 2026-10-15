package org.labs.dinner;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticService {

    private final int expectedEatenPortions;
    private final Map<Integer, Long> eatenPortionsByProgrammer = new ConcurrentHashMap<>();

    public StatisticService(int expectedEatenPortions) {
        this.expectedEatenPortions = expectedEatenPortions;
    }

    public void collectStatistic(Programmer programmer) {
        eatenPortionsByProgrammer.put(programmer.getNumber(), programmer.getEatenPortions());
    }

    public DinnerStatistics getStatistics() {
        return new DinnerStatistics(eatenPortionsByProgrammer, expectedEatenPortions);
    }

    public void printStatistics() {
        DinnerStatistics statistics = getStatistics();

        System.out.println("EATEN PORTIONS: ");
        statistics.eatenPortionsByProgrammer().forEach((programmerNumber, eatenPortions) ->
                System.out.printf("Programmer %d ate: %d\n", programmerNumber, eatenPortions)
        );

        System.out.printf("\nTotal eaten portions: %d\n", statistics.totalEatenPortions());
        System.out.printf("Expected eaten portions: %d\n", statistics.expectedEatenPortions());

        System.out.printf("\nmin: %d | max: %d | delta: %d \n",
                statistics.minEatenPortions(), statistics.maxEatenPortions(), statistics.delta());
    }

    public record DinnerStatistics(
            Map<Integer, Long> eatenPortionsByProgrammer,
            long expectedEatenPortions
    ) {

        public DinnerStatistics {
            eatenPortionsByProgrammer = Collections.unmodifiableMap(new TreeMap<>(eatenPortionsByProgrammer));
        }

        public long totalEatenPortions() {
            return eatenPortionsByProgrammer.values().stream()
                    .mapToLong(Long::longValue)
                    .sum();
        }

        public long minEatenPortions() {
            return eatenPortionsByProgrammer.values().stream()
                    .mapToLong(Long::longValue)
                    .min()
                    .orElse(0);
        }

        public long maxEatenPortions() {
            return eatenPortionsByProgrammer.values().stream()
                    .mapToLong(Long::longValue)
                    .max()
                    .orElse(0);
        }

        public long delta() {
            return maxEatenPortions() - minEatenPortions();
        }
    }
}
