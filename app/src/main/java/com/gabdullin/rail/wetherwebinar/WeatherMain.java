package com.gabdullin.rail.wetherwebinar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class WeatherMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) getSupportFragmentManager().beginTransaction().add(android.R.id.content, new MainFragment()).commit();
    }
}
