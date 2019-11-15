package ru.rzn.sbt.javaschool.lesson10.cars;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class TrafficController {

//    private volatile AtomicInteger countLeft = new AtomicInteger(0);
//
//    private volatile AtomicInteger countRight = new AtomicInteger(0);
//
//    public void enterLeft() {
//        while (countRight.get() != 0) {
//        }
//        countLeft.incrementAndGet();
//    }
//
//    public void enterRight() {
//        while (countLeft.get() != 0) {
//        }
//        countRight.incrementAndGet();
//    }
//
//    public void leaveLeft() {
//        countLeft.decrementAndGet();
//    }
//
//    public void leaveRight() {
//        countRight.decrementAndGet();
//    }

    private Semaphore semaphore = new Semaphore(1);

    public void enterLeft() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void enterRight() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void leaveLeft() {
        semaphore.release();
    }

    public void leaveRight() {
        semaphore.release();
    }
}