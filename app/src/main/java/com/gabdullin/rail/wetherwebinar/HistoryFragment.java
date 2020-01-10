package com.gabdullin.rail.wetherwebinar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gabdullin.rail.wetherwebinar.db.DataSource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryFragment extends Fragment {

    HistoryAdapter historyAdapter;
    private DataSource dataSource;

//    public HistoryFragment(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.note_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        historyAdapter = new HistoryAdapter(DataSource.getDataSource(getActivity()).getReader());
        recyclerView.setAdapter(historyAdapter);
    }
}
