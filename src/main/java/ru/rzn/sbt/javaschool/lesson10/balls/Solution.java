package ru.rzn.sbt.javaschool.lesson10.balls;

import javafx.util.Pair;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;

/**
 * 1. Измените метода {@link Solution#main(String[])} таким образом, чтобы вместо явного создания потоков использовался
 * какой-нибудь {@link java.util.concurrent.Executor}.
 * 2. Реализуйте последовательую "заморозку" потоков при попадании {@link Ball} на диагональ {@link BallWorld}
 * (где x == y). Попаданием считать пересечение описывающего прямоуголькника диагонали. При заморозке всех потоков
 * осуществляйте возобновление выполнения
 * 3. Введите в программу дополнительный поток, который уничтожает {@link Ball} в случайные моменты времени.
 * Начните выполнение этого потока c задержкой в 15 секунд после старта всех {@link Ball}. {@link Ball} должны
 * уничтожаться в случайном порядке. Под уничтожением {@link Ball} подразумевается
 * а) исключение из массива {@link BallWorld#balls} (нужно реализовать потокобезопасный вариант)
 * б) завершение потока, в котором выполняется соответствующая задача (следует использовать
 * {@link java.util.concurrent.Future}сформированный при запуске потока для прерывания
 * {@link java.util.concurrent.Future#cancel(boolean)})
 */
public class Solution {

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static final List<Ball> balls = new ArrayList<>();

    private static final List<Pair<Ball, Future<?>>> listTasks = new ArrayList<>();

    private static final Phaser phaser = new Phaser() {
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            return registeredParties == 0;
        }
    };

    public static void nap(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.err.println("Thread " + Thread.currentThread().getName() + " throwed exception " + e.getMessage());
        }
    }

    private static void exit(Future<?> future) {
        try {
            future.get();
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] a) {

        final BallWorld world = new BallWorld();
        final JFrame win = new JFrame();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                win.getContentPane().add(world);
                win.pack();
                win.setVisible(true);
            }
        });

        Thread.currentThread().setName("MyMainThread");

        balls.add(new Ball(world, 50, 80, 5, 10, Color.red, phaser));
        balls.add(new Ball(world, 70, 100, 8, 6, Color.blue, phaser));
        balls.add(new Ball(world, 150, 100, 9, 7, Color.green, phaser));
        balls.add(new Ball(world, 200, 130, 3, 8, Color.black, phaser));

        ScheduledExecutorService service = Executors.newScheduledThreadPool(balls.size());
        for (Ball ball : balls) {
            phaser.register();
            Future<?> future = service.schedule(ball, (int) (5 * Math.random()), TimeUnit.SECONDS);
            listTasks.add(new Pair<>(ball, future));
        }

        final ScheduledFuture<?> removerHandler = scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                int threshold = (int) (Math.random() * 11);
                System.out.println("threshold: " + threshold);
                if (threshold > 8 && world.countBoll() > 0) {
                    int order = (int) ((world.countBoll()) * Math.random());
                    System.out.println("order:" + order);
                    Pair<Ball, Future<?>> pair = listTasks.get(order);
                    pair.getValue().cancel(true);
                    System.out.println(pair.getKey().getCol());
                    world.remove(pair.getKey());
                    listTasks.remove(order);
                }
            }
        }, 15, 1, TimeUnit.SECONDS);

        final ExecutorService stopScheduler = Executors.newSingleThreadExecutor();
        Future<?> future = stopScheduler.submit(new Runnable() {
            @Override
            public void run() {
                while (world.countBoll() != 0) {
                    nap(100);
                }
                System.out.println("stop");
                removerHandler.cancel(true);
            }
        });

        exit(future);
    }

}


