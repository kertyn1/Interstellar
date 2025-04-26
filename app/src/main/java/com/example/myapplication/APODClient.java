package com.example.myapplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class APODClient {

    private static final String BASE_URL = "https://api.nasa.gov/";
    private static final String API_KEY = "WKoHswAoxzrriIV5CAcYugcErtycamDN2OrJOepU";

    private final APODService apodService;

    public APODClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apodService = retrofit.create(APODService.class);
    }

    public interface APODService {
        @GET("planetary/apod")
        Call<APODResponse> getAPOD(
                @Query("api_key") String apiKey,
                @Query("date") String date
        );
    }

    public void getAstronomyPictureOfTheDay(String date, final APODResponseListener listener) {
        Call<APODResponse> call = apodService.getAPOD(API_KEY, date);
        call.enqueue(new Callback<APODResponse>() {
            @Override
            public void onResponse(Call<APODResponse> call, Response<APODResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("API response not successful.");
                }
            }

            @Override
            public void onFailure(Call<APODResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public interface APODResponseListener {
        void onSuccess(APODResponse apodResponse);
        void onError(String error);
    }
}
