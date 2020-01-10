package com.gabdullin.rail.wetherwebinar;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.gabdullin.rail.wetherwebinar.model.WeatherModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class WeatherDataLoader {

    private static final String WEATHERBIT_API = "https://api.weatherbit.io/v2.0/current?lang=ru&city=%s&key=%s";
    private static final String NEW_LINE = "\n";
    private static final String WEATHERBIT_URL = "https://api.weatherbit.io/";
    private static Weatherbit weatherbit;

    private static WeatherModel responseWeatherModel;

    static JSONObject getJSONObject (Context context, String city){
        try {
            URL url = new URL(String.format(WEATHERBIT_API, city, context.getString(R.string.API_KEY)));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder rawData = new StringBuilder();

            String tempVariable;
            while ((tempVariable = reader.readLine()) != null){
                rawData.append(tempVariable).append(NEW_LINE);
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(rawData.toString());

            Log.i("RESP", jsonObject.toString());
            return jsonObject;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    static WeatherModel requestRetrofitByCity(Context context, String city, String language){
        initRetrofit();
        Log.i("RETROFIT", "New retrofit");
        try {
            responseWeatherModel = weatherbit.loadWeatherByCity(language, city, context.getString(R.string.API_KEY))
            .execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseWeatherModel;
    }

    static WeatherModel requestRetrofitByLocation(Context context, Location location, String language){
        initRetrofit();
        Log.i("RETROFIT", "New retrofit");
        String longitude = String.valueOf(location.getLongitude());
        String latitude = String.valueOf(location.getLatitude());
        try {
            responseWeatherModel = weatherbit.loadWeatherByLocation(language, latitude, longitude, context.getString(R.string.API_KEY))
                    .execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseWeatherModel;
    }

    private static void initRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(WEATHERBIT_URL).
                addConverterFactory(GsonConverterFactory.create()).
                client(client.build()).
                build();
        weatherbit = retrofit.create(Weatherbit.class);
        Log.i("RETROFIT", weatherbit.toString());
    }
}
