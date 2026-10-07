package org.labs.dinner;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class WaiterService {

    private final AtomicLong portions;
    private final BlockingQueue<Order> orders;

    private record Order(
            long eatenPortions,
            CompletableFuture<Boolean> reply
    ) {}

    public WaiterService(int programmers, int portions) {
        this.portions = new AtomicLong(portions);
        this.orders = new PriorityBlockingQueue<>(programmers, Comparator.comparingLong(Order::eatenPortions));
    }

    public Future<Boolean> orderMeal(Programmer programmer) {
        CompletableFuture<Boolean> reply = new CompletableFuture<>();
        orders.add(new Order(
                programmer.getEatenPortions(),
                reply
        ));
        return reply;
    }

    public void serveMeal() throws InterruptedException {
        Order order = orders.take();
        order.reply().complete(portions.decrementAndGet() >= 0);
    }
}
