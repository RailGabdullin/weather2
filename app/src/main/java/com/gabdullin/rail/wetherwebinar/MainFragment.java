package com.gabdullin.rail.wetherwebinar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {

    private static JSONObject weatherDataJSON;
    private TextView temperature;
    private TextView weatherIcon;
    private TextView city;
    private TextView humidity;
    private TextView pressure;
    private TextView windForce;
    private TextView weatherType;
    private TextView lastUpdate;

    private static SharedPreferences preferences;
    private final String CURRENT_CITY = "CURRENT_CITY";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        updateWeatherData(container, preferences.getString(CURRENT_CITY, "Moscow"));
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    private void updateWeatherData(final View view, final String city) {
        new AsyncTask<Boolean, String, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... booleans) {
                weatherDataJSON = WeatherDataLoader.getJSONObject(getContext(), city);
//                Log.i("WEATHER_PARSE", weatherDataJSON.toString());
                return weatherDataJSON != null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(weatherDataJSON == null){
                    Toast.makeText(getActivity(), "Нет такого города", Toast.LENGTH_SHORT).show();
                    changeCity(view);
                } else {
                    updateUIByNewWeatherData(view);
                }
                super.onPostExecute(result);
            }
        }.execute();
    }

    private void updateUIByNewWeatherData(View view) {
        try {
            initUI(view);
            lastUpdate.setText("\uF04C " + DateFormat.getDateTimeInstance().format(new Date()));

            JSONObject weatherDataJSONParse = weatherDataJSON.getJSONArray("data").getJSONObject(0);

            city.setText("\uf0b1 " + weatherDataJSONParse.getString("city_name"));
            updateWeatherIcon(weatherDataJSONParse.getJSONObject("weather").getInt("code"));
            temperature.setText((int) weatherDataJSONParse.getDouble("temp") + "°C");
            weatherType.setText(weatherDataJSONParse.getJSONObject("weather").getString("description"));
            humidity.setText(weatherDataJSONParse.getString("rh"));
            pressure.setText( String.valueOf((int) weatherDataJSONParse.getDouble("slp")));
            windForce.setText( String.valueOf((int) weatherDataJSONParse.getDouble("wind_spd")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateWeatherIcon(int weatherCode) {
        if (weatherCode == 800) {
            weatherIcon.setText(R.string.clear);
        } else {
            int weatherCodeGroup = weatherCode / 100;
            switch (weatherCodeGroup) {
                case 2:
                    weatherIcon.setText(R.string.thunderstorm);
                    break;
                case 3:
                    weatherIcon.setText(R.string.drizzle);
                    break;
                case 5:
                    weatherIcon.setText(R.string.rain);
                    break;
                case 6:
                    weatherIcon.setText(R.string.snow);
                    break;
                case 7:
                    weatherIcon.setText(R.string.fog);
                    break;
                case 8:
                    weatherIcon.setText(R.string.cloudy);
                    break;
            }
        }
    }

    private void initUI(final View view) {
        city = view.findViewById(R.id.city);
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCity(view);
            }
        });

        lastUpdate = view.findViewById(R.id.updated);

        lastUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWeatherData(view, preferences.getString(CURRENT_CITY, "Moscow"));
            }
        });

        weatherIcon = view.findViewById(R.id.weatherIcon);
        temperature = view.findViewById(R.id.temperature);
        weatherType = view.findViewById(R.id.weatherType);
        pressure = view.findViewById(R.id.pressure);
        windForce = view.findViewById(R.id.wind);
        humidity = view.findViewById(R.id.humidity);
    }

    private void changeCity(final View view) {
        AlertDialog.Builder changeCityDialogBuilder = new AlertDialog.Builder(getContext());
        changeCityDialogBuilder.setTitle("Изменить город");
        final EditText enterCityField = new EditText(getContext());
        enterCityField.setInputType(InputType.TYPE_CLASS_TEXT);
        changeCityDialogBuilder.setView(enterCityField);
        changeCityDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateWeatherData(view, enterCityField.getText().toString());
                preferences.edit().putString(CURRENT_CITY, enterCityField.getText().toString()).apply();
            }
        });
        changeCityDialogBuilder.show();
    }
}
