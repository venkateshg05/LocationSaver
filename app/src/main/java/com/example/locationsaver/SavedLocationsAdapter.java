package com.example.locationsaver;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SavedLocationsAdapter extends ListAdapter<SavedLocations, SavedLocationsViewHolder> {

    MultiSelectDelete multiSelectDelete;
    SavedLocationsViewModel savedLocationsViewModel;
    ActivityResultLauncher<Intent> arlEditLocationDetail;
    ArrayList<SavedLocations> locationsToDelete = new ArrayList<>();
    boolean isEnable = false;

    protected SavedLocationsAdapter(
            @NonNull DiffUtil.ItemCallback<SavedLocations> diffCallback,
            SavedLocationsViewModel savedLocationsViewModel,
            ActivityResultLauncher<Intent> arlEditLocationDetail,
            MultiSelectDelete multiSelectDelete
    ) {
        super(diffCallback);
        this.savedLocationsViewModel = savedLocationsViewModel;
        this.arlEditLocationDetail = arlEditLocationDetail;
        this.multiSelectDelete = multiSelectDelete;
    }

    @NonNull
    @Override
    public SavedLocationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SavedLocationsViewHolder.create(
                parent,
                this.savedLocationsViewModel,
                this.arlEditLocationDetail
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SavedLocationsViewHolder holder, int position) {
        SavedLocations location = getItem(position);
        holder.bind(location, position);

        setupMultiselection(holder, location);
    }

    private void setupMultiselection(SavedLocationsViewHolder holder, SavedLocations location) {
        holder.rvLinearLayout.setOnLongClickListener(v -> {
            selectLocation(holder, location);
            isEnable = true;
            multiSelectDelete.showDeleteButton(true);
            return true;
        });

        holder.rvLinearLayout.setOnClickListener(v -> {
            if (locationsToDelete.contains(location)) {
                locationsToDelete.remove(location);

                if (location.photoURI.length() > 0) {
                    Picasso
                            .get()
                            .load(location.photoURI)
                            .resize(0, 75)
                            .centerInside()
                            .into(holder.locationThumbNail);
                } else {
                    Picasso
                            .get()
                            .load(R.mipmap.location_pin)
                            .resize(0, 75)
                            .centerInside()
                            .into(holder.locationThumbNail);
                }

                holder.rvLinearLayout.setBackgroundColor(Color.TRANSPARENT);
                holder.ibOpenMaps.setVisibility(View.VISIBLE);
                holder.ibEdit.setVisibility(View.VISIBLE);
                holder.ibDelete.setVisibility(View.VISIBLE);

                if (locationsToDelete.isEmpty()) {
                    isEnable = false;
                    multiSelectDelete.showDeleteButton(false);
                }
            } else if (isEnable) {
                selectLocation(holder, location);
            }
        });
    }

    private void selectLocation(SavedLocationsViewHolder holder, SavedLocations selectedLocation) {

        Log.i("selectedLocation", selectedLocation.locationName);
        locationsToDelete.add(selectedLocation);

        holder.rvLinearLayout.setBackgroundColor(Color.LTGRAY);
        Picasso
                .get()
                .load(R.mipmap.check_mark)
                .resize(0, 75)
                .centerInside()
                .into(holder.locationThumbNail);
        holder.ibOpenMaps.setVisibility(View.INVISIBLE);
        holder.ibEdit.setVisibility(View.INVISIBLE);
        holder.ibDelete.setVisibility(View.INVISIBLE);

    }

    public void deleteSelectedLocations() {
        for (SavedLocations location : locationsToDelete) {
            Log.i("locationsToDelete", location.locationName);
        }
        savedLocationsViewModel.deleteSelectedLocations(locationsToDelete);
    }

    static class SavedLocationDiff extends DiffUtil.ItemCallback<SavedLocations> {
        @Override
        public boolean areItemsTheSame(@NonNull SavedLocations oldItem, @NonNull SavedLocations newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull SavedLocations oldItem, @NonNull SavedLocations newItem) {
            return oldItem.locationName.equals(newItem.locationName)
                    && oldItem.photoURI.equals(newItem.photoURI);
        }
    }

}
