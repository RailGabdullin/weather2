package com.gabdullin.rail.wetherwebinar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gabdullin.rail.wetherwebinar.model.Datum;
import com.gabdullin.rail.wetherwebinar.model.WeatherModel;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TheCityFragment extends Fragment {

    private WeatherModel weatherDataModel;
    private LocationManager locationManager;
    private ChangeLocationsDBListener changeLocationsDBListener;

    private TextView temperature;
    private TextView weatherIcon;
    private TextView city;
    private TextView humidity;
    private TextView pressure;
    private TextView windForce;
    private TextView weatherType;
    private TextView lastUpdate;
    private TextView location;
    private TextView removeLocation;

    private String currentCity;

    private String API_KEY;

    TheCityFragment(LocationManager locationManager, String currentCity, ChangeLocationsDBListener changeLocationsDBListener){
        this.locationManager = locationManager;
        this.currentCity = currentCity;
        this.changeLocationsDBListener = changeLocationsDBListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.the_city_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        API_KEY = view.getContext().getString(R.string.API_KEY);
        initUI(view);
        if(Objects.equals(currentCity, "initLocation")){
            updateLocation(view);
        } else {
            updateWeatherDataByCityName(currentCity, view);
        }

    }

    private void initUI(View view) {
        location = view.findViewById(R.id.location);
        city = view.findViewById(R.id.city);
        removeLocation = view.findViewById(R.id.remove_location);
        lastUpdate = view.findViewById(R.id.updated);
        weatherIcon = view.findViewById(R.id.weatherIcon);
        temperature = view.findViewById(R.id.temperature);
        weatherType = view.findViewById(R.id.weatherType);
        pressure = view.findViewById(R.id.pressure);
        windForce = view.findViewById(R.id.wind);
        humidity = view.findViewById(R.id.humidity);
        initButtons(view);
    }

    private void initButtons(final View view) {
        if (city != null) {
            city.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeCity(view);
                }
            });
        }
        if(lastUpdate != null) {
            lastUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateWeatherDataByCityName(currentCity, view);
                }
            });
        }
        if(location != null) {
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateLocation(view);
                }
            });
        }
        if(removeLocation != null){
            removeLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeLocation();
                }
            });
        }
    }

    private void removeLocation() {
        changeLocationSettings(null);
    }

    private void changeCity(final View view) {
        AlertDialog.Builder changeCityDialogBuilder = new AlertDialog.Builder(view.getContext());
        changeCityDialogBuilder.setTitle("Изменить город");
        final EditText enterCityField = new EditText(view.getContext());
        enterCityField.setInputType(InputType.TYPE_CLASS_TEXT);
        changeCityDialogBuilder.setView(enterCityField);
        changeCityDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    updateWeatherDataByCityName(enterCityField.getText().toString(), view);
            }
        });
        changeCityDialogBuilder.show();
    }

    private void updateWeatherDataByCityName(final String city, final View view) {
        if(city.length() > 0) {
            requestDataUpdate(city, view);
        } else {
            Toast.makeText(view.getContext(), "Поле ввода пустое", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestDataUpdate(final String city, final View view) {
        new AsyncTask<Boolean, String, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... booleans) {
                weatherDataModel = WeatherDataLoader.requestRetrofitByCity(API_KEY, city, "ru");
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (weatherDataModel != null){
                    updateUIByNewWeatherData(view);
                    changeLocationSettings(weatherDataModel.getData().get(0).getCityName());
                } else {
                    Toast.makeText(view.getContext(), "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
                    changeLocationsDBListener.changeLocationsDB(city, null);
                }
                super.onPostExecute(result);
            }
        }.execute();
    }

    private void requestDataUpdate(final Location location, final View view) {
        new AsyncTask<Boolean, String, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... booleans) {
                weatherDataModel = WeatherDataLoader.requestRetrofitByLocation(API_KEY, location, "ru");
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (weatherDataModel != null){
                    updateUIByNewWeatherData(view);
                    changeLocationSettings(weatherDataModel.getData().get(0).getCityName());
                } else {
                    Toast.makeText(view.getContext(), "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void updateUIByNewWeatherData(View view) {
        initUI(view);
        lastUpdate.setText("\uF04C " + DateFormat.getDateTimeInstance().format(new Date()));
        Datum weatherDataModelParse = weatherDataModel.getData().get(0);
        city.setText(" " + weatherDataModelParse.getCityName());
        updateWeatherIcon(weatherDataModelParse.getWeather().getCode());
        temperature.setText(Math.round(weatherDataModelParse.getTemp()) + "°C");
        weatherType.setText(weatherDataModelParse.getWeather().getDescription());
        humidity.setText(String.valueOf(weatherDataModelParse.getRh()));
        pressure.setText(String.valueOf(Math.round(weatherDataModelParse.getSlp())));
        windForce.setText(String.valueOf(Math.round(weatherDataModelParse.getWindSpd())));
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

    private void updateLocation(final View view) {
        try {
            locationManager.requestSingleUpdate(locationManager.getBestProvider(new Criteria(), true),  new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateWeatherDataByLocation(location, view);
                    Log.i("LOCATION: ", location.toString());
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
        if(Objects.equals(currentCity, "initLocation")){
            Toast.makeText(view.getContext(), "Ошибка геолокации", Toast.LENGTH_SHORT).show();
            changeCity(view);
        }
    }

    private void updateWeatherDataByLocation(final Location location, final View view){
        if(location != null) {
            requestDataUpdate(location, view);
        } else {
            Toast.makeText(view.getContext(), "Ошибка геолокации", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeLocationSettings(String newLocation) {
        changeLocationsDBListener.changeLocationsDB(currentCity, newLocation);
        currentCity = newLocation;
    }

    interface ChangeLocationsDBListener {
        void changeLocationsDB(String currentCity, String changingCity);
    }
}
