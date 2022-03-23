package ru.arepkin.future;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Repkin Andrey {@literal <arepkin@at-consulting.ru>}
 */
public class CompletableFutureTest {

    @Test
    public void testException() {
        final CompletableFuture<Integer> completableFuture = new CompletableFuture();
        new Thread(() -> {
            try {
                completableFuture.complete(5 / 0);
            } catch (ArithmeticException e) {
                completableFuture.completeExceptionally(e);
            }
        }).start();
        try {
            final Integer result = completableFuture.get(1, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Assertions.assertEquals(e.getCause().getClass(), ArithmeticException.class);
        }
    }

    @Test
    public void testExceptionWithoutThread() {
        final CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            return 5 / 0;
        });
        try {
            final Integer result = completableFuture.get(1, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Assertions.assertEquals(e.getCause().getClass(), ArithmeticException.class);
        }
    }

    @Test
    public void combineFutures() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Integer> priceFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 400;
        });
        CompletableFuture<Map<String, Double>> rateFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Map.of("EUR", 150.5);
        });
        final CompletableFuture<Double> result = priceFuture.thenCombine(rateFuture, (price, ratesMap) -> price * ratesMap.get("EUR"));
        Assertions.assertEquals(result.get(1, TimeUnit.MINUTES), 150.5 * 400);
    }
}
