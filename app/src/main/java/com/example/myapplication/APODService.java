package com.example.myapplication;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APODService {

    @GET("planetary/apod")
    Call<APODResponse> getAstronomyPictureOfTheDay(@Query("api_key") String apiKey);
}