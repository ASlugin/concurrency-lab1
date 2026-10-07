package org.labs.dinner.service;

import org.labs.dinner.Programmer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticService {

    private final Integer programmers;
    private final Integer portions;

    private final ConcurrentHashMap<Integer, Long> eatenPortionsByProgrammer = new ConcurrentHashMap<>();

    public StatisticService(Integer programmers, Integer portions) {
        this.programmers = programmers;
        this.portions = portions;
    }

    public void collectStatistic(Programmer programmer) {
        eatenPortionsByProgrammer.put(programmer.getNumber(), programmer.getEatenPortions());
    }

    public void printStatistics() {
        System.out.println("EATEN PORTIONS: ");

        long totalEatenPortions = 0L;
        long minEatenPortion = Long.MAX_VALUE;
        long maxEatenPortion = -1L;

        for(Map.Entry<Integer, Long> entry : eatenPortionsByProgrammer.entrySet()) {
            Integer programmerNumber = entry.getKey();
            Long eatenPortions = entry.getValue();

            totalEatenPortions += eatenPortions;
            minEatenPortion = Math.min(minEatenPortion, eatenPortions);
            maxEatenPortion = Math.max(maxEatenPortion, eatenPortions);

            System.out.printf("Programmer %d ate: %d\n", programmerNumber, eatenPortions);
        }

        System.out.printf("\nTotal eaten portions: %d\n", totalEatenPortions);
        System.out.printf("Expected eaten portions: %d\n", (programmers +  portions));

        System.out.printf("\nmin: %d | max: %d | delta: %d \n", minEatenPortion, maxEatenPortion, (maxEatenPortion - minEatenPortion));
    }
}
