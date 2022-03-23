package ru.arepkin.future.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author Repkin Andrey {@literal <arepkin@at-consulting.ru>}
 */
public interface DataLoader {
    Map<String, String> loadValuts(LocalDate date) throws IOException;

    void loadValutsAsync(LocalDate date, Consumer<Map<String, String>> successCallback, Consumer<Throwable> errorCallback);

    default CompletableFuture<Map<String, String>> loadValutsFuture(LocalDate date) {
        CompletableFuture<Map<String, String>> future = new CompletableFuture<>();
        loadValutsAsync(date, result -> future.complete(result), throwable -> future.completeExceptionally(throwable));
        return future;
    }

}
