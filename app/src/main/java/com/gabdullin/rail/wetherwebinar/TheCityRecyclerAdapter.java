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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gabdullin.rail.wetherwebinar.db.LocationDataReader;
import com.gabdullin.rail.wetherwebinar.db.LocationsDataSource;
import com.gabdullin.rail.wetherwebinar.model.Datum;
import com.gabdullin.rail.wetherwebinar.model.WeatherModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TheCityRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static ArrayList<String> locationsList;

    private static WeatherModel weatherDataModel;
    private static LocationManager locationManager;
    private static LocationsDataSource locationsDataSource;
    private static LocationDataReader locationDataReader;

    private TextView temperature;
    private TextView weatherIcon;
    private TextView city;
    private TextView humidity;
    private TextView pressure;
    private TextView windForce;
    private TextView weatherType;
    private TextView lastUpdate;
    private TextView location;

    private TheCityRecyclerAdapter(){}

    public static TheCityRecyclerAdapter initAdapter(ArrayList<String> locationsList, LocationManager locationManager, LocationsDataSource locationsDataSource){
        TheCityRecyclerAdapter.locationsList = locationsList;
        TheCityRecyclerAdapter.locationManager = locationManager;
        TheCityRecyclerAdapter.locationsDataSource = locationsDataSource;
        TheCityRecyclerAdapter.locationDataReader = locationsDataSource.getLocationDataReader();

        return new TheCityRecyclerAdapter();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == locationsList.size() - 1){ return 10;}
        else { return super.getItemViewType(position);}
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(locationsList.size() == 0 ){locationsList.add("moscow");}
        if(viewType == 10){ return new AddNewLocationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_location_layout, parent, false));}
        return new TheCityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.the_city_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            TheCityViewHolder theCityViewHolder = (TheCityViewHolder) holder;
            theCityViewHolder.updateWeatherDataByCityName(locationsList.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return locationsList.size();
    }

    class TheCityViewHolder extends RecyclerView.ViewHolder{

        private View itemView;
        private String currentCity;

        public TheCityViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            initUI(itemView);
        }

        private void initUI(final View view){
            city = view.findViewById(R.id.city);
            if (city != null) {
                city.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeCity(view);
                    }
                });
            }

            lastUpdate = view.findViewById(R.id.updated);

            if(lastUpdate != null) {
                lastUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateWeatherDataByCityName(currentCity);
                    }
                });
            }

            weatherIcon = view.findViewById(R.id.weatherIcon);
            temperature = view.findViewById(R.id.temperature);
            weatherType = view.findViewById(R.id.weatherType);
            pressure = view.findViewById(R.id.pressure);
            windForce = view.findViewById(R.id.wind);
            humidity = view.findViewById(R.id.humidity);
            location = view.findViewById(R.id.location);

            if(location != null) {
                location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateLocation();
                    }
                });
            }
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
                    updateWeatherDataByCityName(enterCityField.getText().toString());
                }
            });
            changeCityDialogBuilder.show();
        }

        public void updateWeatherDataByCityName(final String city) {
            currentCity = city;
            new AsyncTask<Boolean, String, Boolean>() {
                @Override
                protected Boolean doInBackground(Boolean... booleans) {
                    weatherDataModel = WeatherDataLoader.requestRetrofitByCity(itemView.getContext(), currentCity, "ru");
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(weatherDataModel == null){
                        Toast.makeText(itemView.getContext(), "Нет такого города", Toast.LENGTH_SHORT).show();
                        changeCity(itemView);
                    } else {
                        updateUIByNewWeatherData(itemView);
                    }
                    super.onPostExecute(result);
                }
            }.execute();
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

        private void updateLocation() {
            try {
                locationManager.requestSingleUpdate(locationManager.getBestProvider(new Criteria(), true),  new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        updateWeatherDataByLocation(location);
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

        private void updateWeatherDataByLocation(final Location location){
            new AsyncTask<Boolean, Location, Boolean>() {
                @Override
                protected Boolean doInBackground(Boolean... booleans) {
                    weatherDataModel = WeatherDataLoader.requestRetrofitByLocation(itemView.getContext(), location, "ru");
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(weatherDataModel == null){
                        Toast.makeText(itemView.getContext(), "Нет такого города", Toast.LENGTH_SHORT).show();
                        changeCity(itemView);
                    } else {
                        updateUIByNewWeatherData(itemView);
                    }
                    super.onPostExecute(result);
                }
            }.execute();
        }
    }

    class AddNewLocationViewHolder extends RecyclerView.ViewHolder{

        public AddNewLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            initUI(itemView);
        }

        private void initUI(final View itemView) {
            itemView.findViewById(R.id.add_new_location_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder changeCityDialogBuilder = new AlertDialog.Builder(itemView.getContext());
                    changeCityDialogBuilder.setTitle("Добавить локацию");
                    final EditText enterCityField = new EditText(itemView.getContext());
                    enterCityField.setInputType(InputType.TYPE_CLASS_TEXT);
                    changeCityDialogBuilder.setView(enterCityField);
                    changeCityDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            locationsList.add(locationsList.size() - 1, enterCityField.getText().toString());
                            locationsDataSource.addNote(enterCityField.getText().toString());
//                            updateWeatherDataByCityName(enterCityField.getText().toString());
//                    preferences.edit().putString(CURRENT_CITY, enterCityField.getText().toString()).apply();
                        }
                    });
                    changeCityDialogBuilder.show();
                }
            });
        }
    }
}
