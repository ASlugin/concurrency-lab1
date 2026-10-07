package org.labs;

import org.labs.dinner.service.FairnessService;
import org.labs.dinner.service.StatisticService;
import org.labs.dinner.service.WaiterServiceImpl;
import org.labs.dinner.Programmer;
import org.labs.dinner.Spoon;
import org.labs.dinner.Waiter;
import org.labs.dinner.WaiterService;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        int programmers = 7;
        int waiters = 2;
        int portions = 1_000_000;

        int maxDeltaEatenPortions = 2;

        System.out.println("================== PROGRAMMERS DINNER ==================");
        System.out.println("Programmers: " + programmers);
        System.out.println("Waiters: " + waiters);
        System.out.println("Portions: " + portions);
        System.out.println();

        StatisticService statisticService = new StatisticService(programmers, portions);

        WaiterService waiterService = new WaiterServiceImpl(programmers, portions);
        Thread[] waiterThreads = createWaitersThreads(waiters, waiterService);
        FairnessService fairnessService = new FairnessService(maxDeltaEatenPortions, programmers);
        Spoon[] spoons = createSpoons(programmers);
        Thread[] programmerThreads = createProgrammerThreads(programmers, spoons, waiterService, fairnessService, statisticService);

        long startTime = System.currentTimeMillis();
        for (Thread thread : waiterThreads) {
            thread.start();
        }
        for (Thread thread : programmerThreads) {
            thread.start();
        }

        for (Thread programmerThread : programmerThreads) {
            programmerThread.join();
        }
        for (Thread waiterThread : waiterThreads) {
            waiterThread.interrupt();
            waiterThread.join();
        }
        long stopTime = System.currentTimeMillis();

        statisticService.printStatistics();
        System.out.printf("Time: %d ms\n", stopTime - startTime);
        System.out.println("========================================================");
    }

    private static Thread[] createWaitersThreads(int amount, WaiterService waiterService) {
        Thread[] waiterThreads = new Thread[amount];
        for (int i = 0; i < amount; i++) {
            Waiter waiter = new Waiter(waiterService);
            waiterThreads[i] = new Thread(waiter);
        }
        return waiterThreads;
    }

    private static Spoon[] createSpoons(int amount) {
        Spoon[] spoons = new Spoon[amount];
        for (int i = 0; i < amount; i++) {
            spoons[i] = new Spoon(i);
        }
        return spoons;
    }

    public static Thread[] createProgrammerThreads(
            int amount,
            Spoon[] spoons,
            WaiterService waiterService,
            FairnessService fairnessService,
            StatisticService statisticService
    ) {
        Thread[] programmerThreads = new Thread[amount];
        for (int i = 0; i < amount; i++) {
            var leftSpoon = spoons[i];
            var rightSpoon = spoons[(i + 1) % amount];
            Programmer programmer = new Programmer(i, leftSpoon, rightSpoon, waiterService, fairnessService, statisticService);
            programmerThreads[i] = new Thread(programmer);
        }
        return  programmerThreads;
    }
}