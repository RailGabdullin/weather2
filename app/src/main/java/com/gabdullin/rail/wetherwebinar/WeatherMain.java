package com.gabdullin.rail.wetherwebinar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gabdullin.rail.wetherwebinar.db.LocationDataReader;
import com.gabdullin.rail.wetherwebinar.db.LocationsDataSource;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class WeatherMain extends AppCompatActivity {

    public static final String INIT_LOCATION = "initLocation";
    private Bundle savedInstanceStateBundle;
    private final int PERMISSION_REQUEST_CODE = 10;
    private LocationManager locationManager;
    private ArrayList<String> locationList = new ArrayList<>();

    private LocationsDataSource locationsDataSource;
    private LocationDataReader locationDataReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        savedInstanceStateBundle = savedInstanceState;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            start(savedInstanceStateBundle);
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET) &&
                !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int i : grantResults) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) allPermissionsGranted = false;
            }
            if (grantResults.length > 0 && allPermissionsGranted) {
                start(savedInstanceStateBundle);
            }
        }
    }

    private void start(Bundle savedInstanceState) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(savedInstanceState == null) {
            initLocationDataSource();
            locationsDataSource.deleteAll();
            for(int i = 0; i < locationDataReader.getCount(); i++){
                locationList.add(locationDataReader.getPosition(i).getCityName());
                Log.i("DB: ", locationDataReader.getPosition(i).getCityName());
            }
            if(locationList.size() == 0 ){
                locationsDataSource.addNote(INIT_LOCATION);
                locationList.add(INIT_LOCATION);
                for(int i = 0; i < locationDataReader.getCount(); i++){
                    Log.i("DB2: ", locationDataReader.getPosition(i).toString());
                }
            }
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new MainFragment(locationManager, locationList, locationsDataSource)).commitAllowingStateLoss();
        }
        Log.i("ONCREATE: ", locationList.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void initLocationDataSource(){
        locationsDataSource = new LocationsDataSource(getApplicationContext());
        locationsDataSource.open();
        locationDataReader = locationsDataSource.getLocationDataReader();
    }
}
