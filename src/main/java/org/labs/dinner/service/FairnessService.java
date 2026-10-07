package org.labs.dinner.service;

import org.labs.dinner.Programmer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FairnessService {

    private final int maxDelta;
    private final ConcurrentHashMap<Integer, Long> eatenPortionsByProgrammer;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public FairnessService(int maxDelta, int programmerCount) {
        this.maxDelta = maxDelta;
        this.eatenPortionsByProgrammer = new ConcurrentHashMap<>(programmerCount);
        this.eatenPortionsByProgrammer.put(0, 0L);
    }

    public void checkBeforeDinner(Programmer programmer) throws InterruptedException {
        lock.lock();
        try {
            while (programmer.getEatenPortions() - minEatenPortions() > maxDelta) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    private Long minEatenPortions() {
        long min = Long.MAX_VALUE;
        for (Map.Entry<Integer, Long> entry : eatenPortionsByProgrammer.entrySet()) {
            Long eatenPortions = entry.getValue();
            min = Math.min(min, eatenPortions);
        }
        return min;
    }

    public void checkAfterDinner(Programmer programmer) {
        lock.lock();
        try {
            eatenPortionsByProgrammer.put(programmer.getNumber(), programmer.getEatenPortions());
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void removeProgrammer(Programmer programmer) {
        lock.lock();
        try {
            eatenPortionsByProgrammer.remove(programmer.getNumber());
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
