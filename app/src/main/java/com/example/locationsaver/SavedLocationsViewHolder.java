package com.example.locationsaver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SavedLocationsViewHolder extends RecyclerView.ViewHolder {

    private final TextView latLong;
    public SavedLocationsViewHolder(@NonNull View itemView) {
        super(itemView);
        latLong = itemView.findViewById(R.id.tvLocationName);
    }

    public void bind(String location) {
        this.latLong.setText(location);
    }

    static SavedLocationsViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_items_saved_location, parent, false);
        return new SavedLocationsViewHolder(view);
    }

}
