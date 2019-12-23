package com.gabdullin.rail.wetherwebinar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gabdullin.rail.wetherwebinar.db.DataReader;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryNoteHolder> {


    private DataReader reader;

    public HistoryAdapter(DataReader dataReader) {
        this.reader = dataReader;
    }

    @NonNull
    @Override
    public HistoryNoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryNoteHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryNoteHolder holder, int position) {
        reader.refresh();
        holder.bind(reader.getPosition(position));
    }

    @Override
    public int getItemCount() {
        return reader.getCount();
    }

    class HistoryNoteHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView city;
        TextView temp;
        TextView desc;

        public HistoryNoteHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.item_date);
            city = itemView.findViewById(R.id.item_city);
            temp = itemView.findViewById(R.id.item_temperature);
            desc = itemView.findViewById(R.id.item_description);
        }

        public void bind(Note note) {
            date.setText(note.getDate());
            city.setText(note.getCity());
            temp.setText(note.getTemperature() + "");
            desc.setText(note.getDescription());
        }
    }
}
