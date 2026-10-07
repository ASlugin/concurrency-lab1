package org.labs.dinner.service;

import org.labs.dinner.Programmer;
import org.labs.dinner.WaiterService;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class WaiterServiceImpl implements WaiterService {

    private final AtomicLong portions;

    private final BlockingQueue<Order> programmersWantToEatQueue;
    private record Order(
            Long eatenPortions,
            CompletableFuture<Boolean> order
    ) {}

    public WaiterServiceImpl(Integer programmers, Integer portions) {
        this.portions = new AtomicLong(portions);
        this.programmersWantToEatQueue = new PriorityBlockingQueue<>(
                programmers,
                Comparator.comparingLong(Order::eatenPortions)
        );
    }

    @Override
    public Future<Boolean> orderMeal(Programmer programmer) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        programmersWantToEatQueue.add(new Order(
                programmer.getEatenPortions(),
                future
        ));
        return future;
    }

    @Override
    public void serveMeal() throws InterruptedException {
        Order order = programmersWantToEatQueue.take();
        CompletableFuture<Boolean> future = order.order;

        long remainingPortions = portions.decrementAndGet();
        if (remainingPortions >= 0) {
            future.complete(true);
        } else {
            future.complete(false);
        }
    }
}