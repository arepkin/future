package ru.arepkin.future.async;

import ru.arepkin.future.executor.ExecutorSyncParser;
import ru.arepkin.future.service.CbrService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Repkin Andrey {@literal <arepkin@at-consulting.ru>}
 */
public class AsyncApp {
    private static int HISTORY_DAYS = 100;

    public static void main(String[] args) {
        final LocalDate now = LocalDate.now();
        try (final CbrService.ServiceHolder serviceHolder = CbrService.create()) {
            final long startMs = System.currentTimeMillis();
            List<CompletableFuture<Map<String, String>>> futures = IntStream.range(0, HISTORY_DAYS)
                    .mapToObj(it -> serviceHolder.getLoader().loadValutsFuture(now.minusDays(it)))
                    .collect(Collectors.toList());
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).get(1, TimeUnit.MINUTES);
            for (int i = 0; i < HISTORY_DAYS; i++) {
                final Map<String, String> valuts = futures.get(i).get();
                System.out.println("EUR/RUB " + valuts.get("EUR") + " on " + now.minusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            System.out.println("Valuts was been loaded for " + (System.currentTimeMillis() - startMs) + "ms");
        } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static class TwoDaysValuts {
        private final Map<String, String> todayValuts;
        private final Map<String, String> yesterdayValuts;

        public TwoDaysValuts(Map<String, String> todayValuts, Map<String, String> yesterdayValuts) {
            this.todayValuts = todayValuts;
            this.yesterdayValuts = yesterdayValuts;
        }
    }
}
