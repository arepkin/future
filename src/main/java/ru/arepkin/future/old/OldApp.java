package ru.arepkin.future.old;

import ru.arepkin.future.service.CbrService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Пример старого многопоточного приложения на первых версиях Java.
 * @author Repkin Andrey {@literal <arepkin@at-consulting.ru>}
 */
public class OldApp {
    private static int HISTORY_DAYS = 100;

    public static void main(String[] args) throws InterruptedException {
        final LocalDate now = LocalDate.now();
        try (final CbrService.ServiceHolder serviceHolder = CbrService.create()) {
            final long startMs = System.currentTimeMillis();
            List<SyncParser> parsers = IntStream.range(0, HISTORY_DAYS)
                    .mapToObj(it -> new SyncParser(now.minusDays(it), serviceHolder.getLoader()))
                    .collect(Collectors.toList());
            for (int i = 0; i < HISTORY_DAYS; i++) {
                new Thread(parsers.get(i)).start();
            }
            for (int i = 0; i < HISTORY_DAYS; i++) {
                final Map<String, String> valuts = parsers.get(i).getValuts();
                System.out.println("EUR/RUB " + valuts.get("EUR") + " on " + now.minusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            System.out.println("Valuts was been loaded for " + (System.currentTimeMillis() - startMs) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
