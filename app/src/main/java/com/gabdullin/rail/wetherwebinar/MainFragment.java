package com.gabdullin.rail.wetherwebinar;

import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gabdullin.rail.wetherwebinar.db.LocationsDataSource;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private LocationManager locationManager;
    private ArrayList <String> locationList;
    private LocationsDataSource locationsDataSource;

    public MainFragment(LocationManager locationManager, ArrayList <String> locationList, LocationsDataSource locationsDataSource) {
        this.locationManager = locationManager;
        this.locationList = locationList;
        this. locationsDataSource = locationsDataSource;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(TheCityRecyclerAdapter.initAdapter(locationList, locationManager, locationsDataSource));

        super.onViewCreated(view, savedInstanceState);
    }
}
