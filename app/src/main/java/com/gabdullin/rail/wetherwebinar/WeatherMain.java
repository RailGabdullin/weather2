package com.gabdullin.rail.wetherwebinar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.gabdullin.rail.wetherwebinar.db.LocationDataReader;
import com.gabdullin.rail.wetherwebinar.db.LocationsDataSource;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class WeatherMain extends AppCompatActivity {

    private Bundle savedInstanceStateBundle;
    private final int PERMISSION_REQUEST_CODE = 10;
    private LocationManager locationManager;
    private ArrayList<String> locationList = new ArrayList<>();

    private LocationsDataSource locationsDataSource;
    private LocationDataReader locationDataReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            for(int i = 0; i < locationDataReader.getCount(); i++){
                locationList.add(locationDataReader.getPosition(i).getcityName());
            }
            if(locationList.size() == 0 ){locationList.add("moscow");}
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, new MainFragment(locationManager, locationList, locationsDataSource)).commitAllowingStateLoss();
        }
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
