package api;

import model.Post;
import model.SentimResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SentimApi {
    //https://sentim-api.herokuapp.com/api/v1/
    @POST("/api/v1/")
    Call<SentimResponse> createPost(@Body Post post);
}