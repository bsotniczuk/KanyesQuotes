package api;

import model.Quote;
import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApi {
    //https://api.kanye.rest/
    @GET("/")
    Call<Quote> getQuote();
}