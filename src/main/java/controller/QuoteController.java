package controller;

import api.QuoteApi;
import model.Quote;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class QuoteController {

    private QuoteCallbackListener quoteCallbackListener;
    private List<Quote> quoteList = new ArrayList<>();

    public QuoteController(QuoteCallbackListener listener) {
        quoteCallbackListener = listener;
    }

    public void fetchQuoteApi() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.kanye.rest")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        QuoteApi messageApi = retrofit.create(QuoteApi.class);

        Call<Quote> call = messageApi.getQuote();
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.code() == 200) { //HTTP Code 200 equals to OK
                    Quote quote = response.body();
                    if (!quoteList.contains(quote)) {
                        quoteList.add(quote);
                        System.out.println("\"" + response.body().getQuote() + "\"");
                    }
                    quoteCallbackListener.onFetchProgress(quote);
                    shutdownAndAwaitTermination(okHttpClient);
                } else System.out.println("Kanye Api declined to respond");
            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                System.out.println("Data call to API failed: " + t);
            }
        });
    }

    /**
     * https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
     */
    void shutdownAndAwaitTermination(OkHttpClient okHttpClient) {
        ExecutorService pool = okHttpClient.dispatcher().executorService();
        ConnectionPool connectionPool = okHttpClient.connectionPool();
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(2, TimeUnit.SECONDS)) {

                pool.shutdownNow(); // Cancel currently executing tasks
                connectionPool.evictAll();
                // Wait a while for tasks to respond to being cancelled
                pool.awaitTermination(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdown();
            connectionPool.evictAll();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public interface QuoteCallbackListener {
        void onFetchProgress(Quote quote);
    }
}
