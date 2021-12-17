package com.example.locationsaver;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class SavedLocationsAdapter extends ListAdapter<SavedLocations, SavedLocationsViewHolder> {

    SavedLocationsOnClickListener onClickListener;
    SavedLocationsViewModel savedLocationsViewModel;
    protected SavedLocationsAdapter(
            @NonNull DiffUtil.ItemCallback<SavedLocations> diffCallback,
            SavedLocationsOnClickListener onClickListener,
            SavedLocationsViewModel savedLocationsViewModel
    ) {
        super(diffCallback);
        this.onClickListener = onClickListener;
        this.savedLocationsViewModel = savedLocationsViewModel;
    }

    @NonNull
    @Override
    public SavedLocationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SavedLocationsViewHolder.create(parent, this.onClickListener, this.savedLocationsViewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedLocationsViewHolder holder, int position) {
        SavedLocations location = getItem(position);
        holder.bind(location.locationName);
    }

    static class SavedLocationDiff extends DiffUtil.ItemCallback<SavedLocations> {
        @Override
        public boolean areItemsTheSame(@NonNull SavedLocations oldItem, @NonNull SavedLocations newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull SavedLocations oldItem, @NonNull SavedLocations newItem) {
            return oldItem.id == newItem.id;
        }
    }

}
