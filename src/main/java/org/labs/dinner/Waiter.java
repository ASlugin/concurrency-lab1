package org.labs.dinner;

public class Waiter implements Runnable {

    private final WaiterService waiterService;

    public Waiter(WaiterService waiterService) {
        this.waiterService = waiterService;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                waiterService.serveMeal();
            }
        }  catch (InterruptedException e) {
        }
    }
}
