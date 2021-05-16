package controller;

import api.SentimApi;
import model.Post;
import model.Sentences;
import model.SentimResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SentimController {

    private SentimCallbackListener sentimCallbackListener;
    private SentimApi sentimApi;
    private List<Sentences> sentences;
    private OkHttpClient okHttpClient = null;

    public SentimController(SentimCallbackListener listener) {
        this.sentimCallbackListener = listener;
    }

    public void analyseQuotes(String textToAnalyse) {
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sentim-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        sentimApi = retrofit.create(SentimApi.class);

        createPost(textToAnalyse);
    }

    private void createPost(String textToAnalyse) {
        Post post = new Post(textToAnalyse);

        Call<SentimResponse> call = sentimApi.createPost(post);

        call.enqueue(new Callback<SentimResponse>() {
            @Override
            public void onResponse(Call<SentimResponse> call, Response<SentimResponse> response) {
                if (response.code() == 200) { //HTTP Code 200 equals to OK
                    SentimResponse sentimResponse = response.body();

                    sentences = new ArrayList<>(Arrays.asList(sentimResponse.getSentences())); //get sentences array from sentim-api
                    sentimCallbackListener.onFetchCompleted(sentences);

                    okHttpClient.dispatcher().executorService().shutdown();
                    okHttpClient.connectionPool().evictAll();
                }
            }

            @Override
            public void onFailure(Call<SentimResponse> call, Throwable t) {
                System.out.println("Data call to API failed: " + t);
            }
        });
    }

    public interface SentimCallbackListener {
        void onFetchCompleted(List<Sentences> sentences);
    }
}
