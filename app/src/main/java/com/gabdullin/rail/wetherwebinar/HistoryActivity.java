package com.gabdullin.rail.wetherwebinar;

import android.os.Bundle;

import com.gabdullin.rail.wetherwebinar.db.DataSource;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    DataSource dataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        dataSource = (DataSource) new Bundle(getIntent().getExtras()).getSerializable("DATA_SOURCE");
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, new HistoryFragment()).commit();
    }
}
