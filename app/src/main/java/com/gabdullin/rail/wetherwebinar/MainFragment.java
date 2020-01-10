package com.gabdullin.rail.wetherwebinar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gabdullin.rail.wetherwebinar.db.DataSource;
import com.gabdullin.rail.wetherwebinar.model.Datum;
import com.gabdullin.rail.wetherwebinar.model.WeatherModel;

import java.text.DateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {

    private static WeatherModel weatherDataModel;

    private TextView temperature;
    private TextView weatherIcon;
    private TextView city;
    private TextView humidity;
    private TextView pressure;
    private TextView windForce;
    private TextView weatherType;
    private TextView lastUpdate;
    private TextView location;

    private static SharedPreferences preferences;
    private final String CURRENT_CITY = "CURRENT_CITY";
    private DataSource dataSource;
    private LocationManager locationManager;

    public MainFragment(DataSource dataSource, LocationManager locationManager) {
        this.dataSource = dataSource;
        this.locationManager = locationManager;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        updateWeatherDataByCityName(container, preferences.getString(CURRENT_CITY, "moscow"));
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        initUI(view);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation(view);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void updateLocation(final View view) {
        try {
            locationManager.requestSingleUpdate(locationManager.getBestProvider(new Criteria(), true),  new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateWeatherDataByLocation(view, location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            }, null);
        } catch (SecurityException e) { e.printStackTrace();}
    }

    private void updateWeatherDataByLocation(final View view, final Location location){
        new AsyncTask<Boolean, Location, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... booleans) {
                weatherDataModel = WeatherDataLoader.requestRetrofitByLocation(getContext(), location, "ru");
                return weatherDataJSON != null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(weatherDataModel == null){
                    Toast.makeText(getActivity(), "Нет такого города", Toast.LENGTH_SHORT).show();
                    changeCity(view);
                } else {
                    updateUIByNewWeatherData(view);
                    saveDataToHistory();
                }
                super.onPostExecute(result);
            }
        }.execute();
    }

    private void updateWeatherDataByCityName(final View view, final String city) {

        new AsyncTask<Boolean, String, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... booleans) {
                weatherDataModel = WeatherDataLoader.requestRetrofitByCity(getContext(), city, "ru");
                return weatherDataJSON != null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(weatherDataModel == null){
                    Toast.makeText(getActivity(), "Нет такого города", Toast.LENGTH_SHORT).show();
                    changeCity(view);
                } else {
                    updateUIByNewWeatherData(view);
                    saveDataToHistory();
                }
                super.onPostExecute(result);
            }
        }.execute();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void updateUIByNewWeatherData(View view) {

            initUI(view);
            lastUpdate.setText("\uF04C " + DateFormat.getDateTimeInstance().format(new Date()));

            Datum weatherDataModelParse = weatherDataModel.getData().get(0);
            city.setText("  " + weatherDataModelParse.getCityName());
            updateWeatherIcon(weatherDataModelParse.getWeather().getCode());
            temperature.setText(Math.round(weatherDataModelParse.getTemp()) + "°C");
            weatherType.setText(weatherDataModelParse.getWeather().getDescription());
            humidity.setText(String.valueOf(weatherDataModelParse.getRh()));
            pressure.setText(String.valueOf(Math.round(weatherDataModelParse.getSlp())));
            windForce.setText(String.valueOf(Math.round(weatherDataModelParse.getWindSpd())));
    }

    private void saveDataToHistory() {
        Datum weatherDataModelParse = weatherDataModel.getData().get(0);
        dataSource.add(DateFormat.getDateTimeInstance().format(new Date()),
                weatherDataModelParse.getCityName(),
                (int) Math.round(weatherDataModelParse.getTemp()),
                weatherDataModelParse.getWeather().getDescription());
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
                updateWeatherDataByCityName(view, preferences.getString(CURRENT_CITY, "Moscow"));
            }
        });

        weatherIcon = view.findViewById(R.id.weatherIcon);
        temperature = view.findViewById(R.id.temperature);
        weatherType = view.findViewById(R.id.weatherType);
        pressure = view.findViewById(R.id.pressure);
        windForce = view.findViewById(R.id.wind);
        humidity = view.findViewById(R.id.humidity);
        location = view.findViewById(R.id.location);
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
                updateWeatherDataByCityName(view, enterCityField.getText().toString());
                preferences.edit().putString(CURRENT_CITY, enterCityField.getText().toString()).apply();
            }
        });
        changeCityDialogBuilder.show();
    }
}
