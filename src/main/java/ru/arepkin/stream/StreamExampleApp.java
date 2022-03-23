package ru.arepkin.stream;

import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import ru.arepkin.future.service.CbrService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Repkin Andrey {@literal <arepkin@at-consulting.ru>}
 */
public class StreamExampleApp {
    private static int HISTORY_DAYS = 100;

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("stream");
        final LocalDate now = LocalDate.now();
        try (final CbrService.ServiceHolder serviceHolder = CbrService.create()) {
            final long startMs = System.currentTimeMillis();
            final int parallelism = Runtime.getRuntime().availableProcessors();
            Source.range(1, HISTORY_DAYS)
                    .map(now::minusDays)
                    .mapAsync(parallelism, day -> serviceHolder.getLoader().loadValutsFuture(day).thenApply(f -> Pair.create(f, day)))
                    .runWith(Sink.foreach(pair -> {
                        System.out.println("EUR/RUB " + pair.first().get("EUR") + " on " + pair.second().format(DateTimeFormatter.ISO_LOCAL_DATE));
                    }), system)
                    .toCompletableFuture().get(1, TimeUnit.MINUTES);
            System.out.println("Valuts was been loaded for " + (System.currentTimeMillis() - startMs) + "ms");
        } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            system.terminate();
        }

    }
}
