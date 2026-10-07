package org.labs.dinner;

import org.labs.dinner.service.FairnessService;
import org.labs.dinner.service.StatisticService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Programmer implements Runnable {

    private final Integer number;
    private final Spoon leftSpoon;
    private final Spoon rightSpoon;
    private final WaiterService waiterService;

    private PortionFullness portionFullness = PortionFullness.FULL;
    private boolean interrupted = false;

    private Long eatenPortionsCounter = 0L;
    private final FairnessService fairnessService;
    private final StatisticService statisticService;

    enum PortionFullness { FULL, EMPTY }

    public Programmer(
            Integer number,
            Spoon leftSpoon,
            Spoon rightSpoon,
            WaiterService waiterService,
            FairnessService fairnessService,
            StatisticService statisticService
    ) {
        this.number = number;
        this.leftSpoon = leftSpoon;
        this.rightSpoon = rightSpoon;
        this.waiterService = waiterService;
        this.fairnessService = fairnessService;
        this.statisticService = statisticService;
    }

    @Override
    public void run() {
        try{
            while (!interrupted) {
                if (portionFullness == PortionFullness.FULL) {

                    // берем две ложки
                    fairnessService.checkBeforeDinner(this);
                    if (leftSpoon.getNumber() < rightSpoon.getNumber()) {
                        leftSpoon.acquire();
                        rightSpoon.acquire();
                    } else {
                        rightSpoon.acquire();
                        leftSpoon.acquire();
                    }

                    try {
                        // едим от 0 до 10 мс
                        // Thread.sleep(((int) (Math.random() * 10)));

                        // доели
                        portionFullness = PortionFullness.EMPTY;
                        eatenPortionsCounter++;
                    } finally {
                        // возвращаем ложки
                        leftSpoon.release();
                        rightSpoon.release();
                        fairnessService.checkAfterDinner(this);
                    }

                    // перерыв на поболтать от 0 до 2 мс
                    // Thread.sleep(((int) (Math.random() * 2)));

                } else if (portionFullness == PortionFullness.EMPTY) {
                    // вызвать официанта
                    Future<Boolean> served = waiterService.orderMeal(this);
                    if (served.get()) {
                        portionFullness =  PortionFullness.FULL;
                    } else {
                        interrupted = true;
                    }
                }
            }


        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            statisticService.collectStatistic(this);
            fairnessService.removeProgrammer(this);
        }
    }

    public Integer getNumber() {
        return number;
    }

    public Long getEatenPortions() {
        return eatenPortionsCounter;
    }
}
