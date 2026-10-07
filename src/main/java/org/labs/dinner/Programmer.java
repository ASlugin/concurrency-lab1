package org.labs.dinner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Programmer implements Runnable {

    private final int number;
    private final Spoon firstSpoon;
    private final Spoon secondSpoon;
    private final WaiterService waiterService;
    private final FairnessService fairnessService;
    private final StatisticService statisticService;

    private PortionFullness portionFullness = PortionFullness.EMPTY;
    private boolean finished = false;
    private long eatenPortions = 0;

    private enum PortionFullness { FULL, EMPTY }

    public Programmer(
            int number,
            Spoon leftSpoon,
            Spoon rightSpoon,
            WaiterService waiterService,
            FairnessService fairnessService,
            StatisticService statisticService
    ) {
        this.number = number;
        if (leftSpoon.getNumber() < rightSpoon.getNumber()) {
            this.firstSpoon = leftSpoon;
            this.secondSpoon = rightSpoon;
        } else {
            this.firstSpoon = rightSpoon;
            this.secondSpoon = leftSpoon;
        }
        this.waiterService = waiterService;
        this.fairnessService = fairnessService;
        this.statisticService = statisticService;
    }

    @Override
    public void run() {
        try {
            while (!finished) {
                if (portionFullness == PortionFullness.FULL) {
                    eat();
                } else {
                    callWaiter();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            statisticService.collectStatistic(this);
            fairnessService.removeProgrammer(this);
        }
    }

    private void eat() throws InterruptedException {
        fairnessService.checkBeforeDinner(this);

        // берем две ложки
        firstSpoon.acquire();
        secondSpoon.acquire();
        try {
            // едим от 0 до 10 мс
            // Thread.sleep(((int) (Math.random() * 10)));

            // доели
            portionFullness = PortionFullness.EMPTY;
            eatenPortions++;
        } finally {
            // возвращаем ложки
            secondSpoon.release();
            firstSpoon.release();
            fairnessService.checkAfterDinner(this);
        }

        // перерыв на поболтать от 0 до 2 мс
        // Thread.sleep(((int) (Math.random() * 2)));
    }

    private void callWaiter() throws InterruptedException, ExecutionException {
        Future<Boolean> served = waiterService.orderMeal(this);
        if (served.get()) {
            portionFullness = PortionFullness.FULL;
        } else {
            finished = true;
        }
    }

    public int getNumber() {
        return number;
    }

    public long getEatenPortions() {
        return eatenPortions;
    }
}
