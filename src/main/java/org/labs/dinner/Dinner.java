package org.labs.dinner;

public class Dinner {

    private final StatisticService statisticService;
    private final Thread[] waiterThreads;
    private final Thread[] programmerThreads;

    public Dinner(int programmers, int waiters, int portions, int maxDeltaEatenPortions) {
        requireAtLeast("programmers", programmers, 2);
        requireAtLeast("waiters", waiters, 1);
        requireAtLeast("portions", portions, 0);
        requireAtLeast("maxDeltaEatenPortions", maxDeltaEatenPortions, 1);

        this.statisticService = new StatisticService(portions);
        WaiterService waiterService = new WaiterService(programmers, portions);
        this.waiterThreads = createWaiterThreads(waiters, waiterService);
        FairnessService fairnessService = new FairnessService(maxDeltaEatenPortions, programmers);
        Spoon[] spoons = createSpoons(programmers);
        this.programmerThreads = createProgrammerThreads(programmers, spoons, waiterService, fairnessService, statisticService);
    }

    public void run() throws InterruptedException {
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
        }
        for (Thread waiterThread : waiterThreads) {
            waiterThread.join();
        }
    }

    public StatisticService getStatisticService() {
        return statisticService;
    }

    private static void requireAtLeast(String name, int value, int min) {
        if (value < min) {
            throw new IllegalArgumentException(name + " must be at least " + min + ", but was " + value);
        }
    }

    private static Thread[] createWaiterThreads(int amount, WaiterService waiterService) {
        Thread[] waiterThreads = new Thread[amount];
        for (int i = 0; i < amount; i++) {
            Waiter waiter = new Waiter(waiterService);
            waiterThreads[i] = new Thread(waiter, "waiter-" + i);
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

    private static Thread[] createProgrammerThreads(
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
            programmerThreads[i] = new Thread(programmer, "programmer-" + i);
        }
        return programmerThreads;
    }
}
