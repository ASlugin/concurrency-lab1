package org.labs.dinner;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Spoon {

    private final int number;
    private final Lock lock = new ReentrantLock();

    public Spoon(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void acquire() {
        lock.lock();
    }

    public void release() {
        lock.unlock();
    }
}
