package org.labs.dinner;

import java.util.concurrent.Future;

public interface WaiterService {

    Future<Boolean> orderMeal(Programmer programmer);

    void serveMeal() throws InterruptedException;

}
