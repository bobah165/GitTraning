package com;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class BookTest extends TestCase {
    @Test
    public void testAddsAndRetrieves() {
        Book books = new Book();
        String title = "Elegant Objects";
        int id = books.add(title);
        Assertions.assertEquals(books.title(id),title);
    }

    @Test
    public void testMultithreading() throws InterruptedException, ExecutionException {
        Book books = new Book();
        int threads = 10;
        ExecutorService service = Executors.newFixedThreadPool(threads);
        Collection<Future<Integer>> futures = new ArrayList<>(threads);

        for (int t = 0; t < threads; ++t) {
            final String title = String.format("Book #%d", t);
            futures.add(service.submit(() -> books.add(title)));
        }
        Set<Integer> ids = new HashSet<>();
        for (Future<Integer> f : futures) {
            ids.add(f.get());
        }
        Assertions.assertEquals(ids.size(), threads);
    }

    @Test
    public void testNotSafaty() throws InterruptedException, ExecutionException {
        int threads = 10;
        Book books = new Book();
        ExecutorService service = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean running = new AtomicBoolean();
        AtomicInteger overlaps = new AtomicInteger();
        Collection<Future<Integer>> futures = new ArrayList<>(threads);

        for (int t = 0; t < threads; ++t) {
            final String title = String.format("Book #%d", t);
            futures.add(
                    service.submit(
                            () -> {
                                latch.await();
                                if (running.get()) {
                                    overlaps.incrementAndGet();
                                }
                                running.set(true);
                                int id = books.add(title);
                                running.set(false);
                                return id;
                            }
                    )
            );
        }

        latch.countDown();
        Set<Integer> ids = new HashSet<>();
        for (Future<Integer> f : futures) {
            ids.add(f.get());
        }

       // Assertions.assertEquals(ids.size(), threads);
        Assertions.assertEquals(threads,ids.size());
        Assertions.assertEquals(0, overlaps.get());
    }

}