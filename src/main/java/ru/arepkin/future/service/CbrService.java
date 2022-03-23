package ru.arepkin.future.service;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.arepkin.future.service.data.ValCurs;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Repkin Andrey {@literal <arepkin@at-consulting.ru>}
 */
public interface CbrService {
    @GET("XML_daily.asp")
    Call<ValCurs> listCurs(@Query("date_req") String date);

    static ServiceHolder create() {
        final OkHttpClient client = new OkHttpClient.Builder()
//                .connectionPool(new ConnectionPool(100,5L, TimeUnit.MINUTES))
//                .dispatcher(new Dispatcher(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.cbr.ru/scripts/")
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        CbrService service = retrofit.create(CbrService.class);
        return new ServiceHolder(client, service);
    }

    class ServiceHolder implements Closeable {
        private static final DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        private final OkHttpClient client;
        private final CbrService service;

        public ServiceHolder(OkHttpClient client, CbrService service) {
            this.client = client;
            this.service = service;
        }

        @Override
        public void close() throws IOException {
            client.connectionPool().evictAll();
            client.dispatcher().executorService().shutdown();
        }

        public DataLoader getLoader() {
            return new DataLoader() {
                @Override
                public Map<String, String> loadValuts(LocalDate date) throws IOException {
                    final Call<ValCurs> valCursCall = service.listCurs(PATTERN.format(date));
                    final ValCurs body = valCursCall.execute().body();
                    return body.getValuts();
                }

                @Override
                public void loadValutsAsync(LocalDate date, Consumer<Map<String, String>> successCallback,
                                            Consumer<Throwable> errorCallback) {
                    final Call<ValCurs> valCursCall = service.listCurs(PATTERN.format(date));
                    valCursCall.enqueue(new Callback<ValCurs>() {
                        @Override
                        public void onResponse(Call<ValCurs> call, Response<ValCurs> response) {
                            successCallback.accept(response.body().getValuts());
                        }

                        @Override
                        public void onFailure(Call<ValCurs> call, Throwable throwable) {
                            errorCallback.accept(throwable);
                        }
                    });

                }
            };
        }
    }
}
