package com;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Hello {

    public static int value = 0;
    public static AtomicInteger atomic = new AtomicInteger(0);

    public static int count = 0;

    public static void main(String[] args) throws InterruptedException {

        int threads = 3;
        CountDownLatch latch = new CountDownLatch(threads+1);

        Runnable task = () -> {
            try {
                latch.countDown();
                latch.await();
            } catch (Exception ex) {
                System.out.println();
            }

            for (int i = 0; i < 10000; i++) {
                value++;
                atomic.incrementAndGet();
            }
            System.out.println(value);
            System.out.println(atomic.get());

        };
        for (int i = 0; i < threads; i++) {
            new Thread(task).start();
            Thread.sleep(300);
        }

        while (latch.getCount()!=1) {
            Thread.sleep(100);
        }
        latch.countDown();

    }
}

