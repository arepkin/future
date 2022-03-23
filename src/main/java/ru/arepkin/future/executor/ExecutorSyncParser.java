package ru.arepkin.future.executor;

import ru.arepkin.future.service.DataLoader;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Новая версия для executor.
 * Обратите внимание, насколько стало меньше кода!
 */
public class ExecutorSyncParser implements Callable<Map<String, String>> {

    private LocalDate date;
    private DataLoader dataLoader;

    private Map<String, String> valuts;

    public ExecutorSyncParser(LocalDate date, DataLoader dataLoader) {
        this.date = date == null ? LocalDate.now() : date;
        this.dataLoader = dataLoader;
    }

    @Override
    public Map<String, String> call() throws Exception {
//        Thread.sleep(10); // для имитации задержки;
        return dataLoader.loadValuts(date);
    }
}
