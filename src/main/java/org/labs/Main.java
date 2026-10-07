package org.labs;

import org.labs.dinner.Dinner;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        int programmers = 7;
        int waiters = 2;
        int portions = 1_000_000;

        int maxDeltaEatenPortions = 2;

        System.out.println("================== PROGRAMMERS DINNER ==================");
        System.out.printf("Programmers: %d\n", programmers);
        System.out.printf("Waiters: %d\n", waiters);
        System.out.printf("Portions: %d\n\n", portions);

        Dinner dinner = new Dinner(programmers, waiters, portions, maxDeltaEatenPortions);

        long startTime = System.currentTimeMillis();
        dinner.run();
        long stopTime = System.currentTimeMillis();

        dinner.getStatisticService().printStatistics();
        System.out.printf("Time: %d ms\n", stopTime - startTime);
        System.out.println("========================================================");
    }
}
