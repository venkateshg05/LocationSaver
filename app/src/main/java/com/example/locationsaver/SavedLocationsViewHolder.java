package com.example.locationsaver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SavedLocationsViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private final TextView latLong;
    private final SavedLocationsOnClickListener onClickListener;
    public SavedLocationsViewHolder(
            @NonNull View itemView,
            SavedLocationsOnClickListener onClickListener
    ) {
        super(itemView);
        latLong = itemView.findViewById(R.id.tvLocationName);
        this.onClickListener = onClickListener;

        itemView.setOnClickListener(this);
    }

    public void bind(String location) {
        this.latLong.setText(location);
    }

    static SavedLocationsViewHolder create(
            ViewGroup parent, SavedLocationsOnClickListener onClickListener
            ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_items_saved_location, parent, false);
        return new SavedLocationsViewHolder(view, onClickListener);
    }

    @Override
    public void onClick(View v) {
        this.onClickListener.onClick(v, getAdapterPosition());
    }
}
