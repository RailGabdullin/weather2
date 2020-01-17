package com.gabdullin.rail.wetherwebinar;

import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gabdullin.rail.wetherwebinar.db.LocationsDataSource;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainFragment extends Fragment implements TheCityFragment.ChangeLocationsDBListener, AddNewLocationFragment.AddNewLocationListener {


    private ViewPager viewPager;
    private LocationManager locationManager;
    private ArrayList <String> locationList;
    private LocationsDataSource locationsDataSource;
    private WeatherViewPagerAdapter weatherViewPagerAdapter;


    public MainFragment(LocationManager locationManager, ArrayList <String> locationList, LocationsDataSource locationsDataSource) {
        this.locationManager = locationManager;
        this.locationList = locationList;
        this. locationsDataSource = locationsDataSource;
        locationList.add(null);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pager_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.pager);
        weatherViewPagerAdapter = new WeatherViewPagerAdapter(getChildFragmentManager(), 0 , this, this);
        viewPager.setAdapter(weatherViewPagerAdapter);
    }

    @Override
    public void changeLocationsDB(String currentCity, String changingCity) {
        //Добавление новой локации
        if(currentCity == null && changingCity != null){
            locationList.add(locationList.size() - 1, changingCity);
            weatherViewPagerAdapter.notifyDataSetChanged();
            viewPager.setAdapter(weatherViewPagerAdapter);
            viewPager.setCurrentItem(locationList.size()-2, true);
            locationsDataSource.addNote(changingCity);
        }
        //Удаление локации
        if(currentCity != null && changingCity == null){
            int position = locationList.indexOf(currentCity) - 1;
            locationList.remove(currentCity);
            weatherViewPagerAdapter.notifyDataSetChanged();
            viewPager.setAdapter(weatherViewPagerAdapter);
            viewPager.setCurrentItem(position + 1, true);
            locationsDataSource.deleteNote(currentCity);
        }
        //Изменение локации
        if(!Objects.equals(currentCity, changingCity) && currentCity != null && changingCity != null){
            int position = locationList.indexOf(currentCity) - 1;
            if (position != -2) {
                locationList.set(locationList.indexOf(currentCity), changingCity);
                weatherViewPagerAdapter.notifyDataSetChanged();
                viewPager.setAdapter(weatherViewPagerAdapter);
                viewPager.setCurrentItem(position + 1, true);
                locationsDataSource.editNote(currentCity, changingCity);
            }
        }
    }

    @Override
    public void AddNewLocation(String newLocation) {
        changeLocationsDB(null, newLocation);
    }

    class WeatherViewPagerAdapter extends FragmentStatePagerAdapter {

        private TheCityFragment.ChangeLocationsDBListener changeLocationsDBListener;
        private AddNewLocationFragment.AddNewLocationListener addNewLocationListener;

        public WeatherViewPagerAdapter(@NonNull FragmentManager fm, int behavior, TheCityFragment.ChangeLocationsDBListener changeLocationsDBListener, AddNewLocationFragment.AddNewLocationListener addNewLocationListener) {
            super(fm, behavior);
            this.changeLocationsDBListener = changeLocationsDBListener;
            this.addNewLocationListener = addNewLocationListener;
        }

        @Override
        public int getCount() {
            return locationList.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if(position == locationList.size() - 1) return new AddNewLocationFragment(addNewLocationListener);
            return new TheCityFragment(locationManager, locationList.get(position), changeLocationsDBListener);
        }
    }
}
