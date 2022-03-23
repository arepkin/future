package ru.arepkin.future.executor;

import ru.arepkin.future.old.SyncParser;
import ru.arepkin.future.service.CbrService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Repkin Andrey {@literal <arepkin@at-consulting.ru>}
 */
public class FutureApp {
    private static int HISTORY_DAYS = 100;

    public static void main(String[] args) {
        final LocalDate now = LocalDate.now();
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try (final CbrService.ServiceHolder serviceHolder = CbrService.create()) {
            final long startMs = System.currentTimeMillis();
            List<Future<Map<String, String>>> futures = IntStream.range(0, HISTORY_DAYS)
                    .mapToObj(it -> new ExecutorSyncParser(now.minusDays(it), serviceHolder.getLoader()))
                    .map(parser -> executorService.submit(parser))
                    .collect(Collectors.toList());

            for (int i = 0; i < HISTORY_DAYS; i++) {
                final Map<String, String> valuts = futures.get(i).get();
                System.out.println("EUR/RUB " + valuts.get("EUR") + " on " + now.minusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            System.out.println("Valuts was been loaded for " + (System.currentTimeMillis() - startMs) + "ms");
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
