package ru.arepkin.future.old;

import ru.arepkin.future.service.DataLoader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

/**
 * Обратите внимание, как должен выглядеть класс, который реализует логику предметной области приложения.
 * Нет лишних зависимостей от слоя ввода/вывода (только IOException), библиотеки загрузки данных.
 * Такие классы наиболее часто подвержены изменениям и не должны зависеть от других слоев приложения. Подробнее в книге Р. Мартина.
 * Вышеперечисленные правила сильно упрощают модульное тестирование ядра приложения.
 *
 * @author Repkin Andrey {@literal <arepkin@at-consulting.ru>}
 */
public class SyncParser implements Runnable {

    private LocalDate date;
    private DataLoader dataLoader;

    private Map<String, String> valuts;

    public SyncParser(LocalDate date, DataLoader dataLoader) {
        this.date = date == null ? LocalDate.now() : date;
        this.dataLoader = dataLoader;
    }

    public void run() {
        synchronized (this) {
            try {
                valuts = dataLoader.loadValuts(date);
//                Thread.sleep(10); // для имитации задержки
//                System.out.println("usd/rub=" + valuts.get("USD"));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.notifyAll();
            }
        }
    }

    public Map<String, String> getValuts() throws InterruptedException {
        synchronized (this) {
            if (valuts == null)
                this.wait();
            return valuts;
        }
    }
}
