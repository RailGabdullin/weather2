package com.gabdullin.rail.wetherwebinar;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class WeatherDataLoader {

    private static final String WEATHERBIT_API = "https://api.weatherbit.io/v2.0/current?lang=ru&city=%s&key=%s";
    private static final String NEW_LINE = "\n";

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
}
