package com.gabdullin.rail.wetherwebinar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddNewLocationFragment extends Fragment {

    private AddNewLocationListener addNewLocationListener;

    AddNewLocationFragment (AddNewLocationListener addNewLocationListener){
        this.addNewLocationListener = addNewLocationListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_location_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    private void initUI(final View view) {
        view.findViewById(R.id.add_new_location_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCityDialog(v, view);
            }
        });
    }

    private void changeCityDialog(View v, View view) {
        AlertDialog.Builder changeCityDialogBuilder = new AlertDialog.Builder(view.getContext());
        changeCityDialogBuilder.setTitle("Добавить локацию");
        final EditText enterCityField = new EditText(v.getContext());
        enterCityField.setInputType(InputType.TYPE_CLASS_TEXT);
        changeCityDialogBuilder.setView(enterCityField);
        changeCityDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("TEXT FIELD: ", enterCityField.getText().toString());
                if (enterCityField.getText().length() != 0 ) {
                    addNewLocation(enterCityField.getText().toString());
                }
            }
        });
        changeCityDialogBuilder.show();
    }

    private void addNewLocation(String newLocation) {
        addNewLocationListener.AddNewLocation(newLocation);
    }

    interface AddNewLocationListener {
        void AddNewLocation(String newLocation);
    }
}
