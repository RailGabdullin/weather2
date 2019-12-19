package com.gabdullin.rail.wetherwebinar;

import com.gabdullin.rail.wetherwebinar.model.WeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Weatherbit{
    @GET("v2.0/current")
    Call<WeatherModel> loadWeather(@Query ("lang") String language, @Query("city") String city, @Query("key") String key);
}