package com.example.locationsaver;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SavedLocationsAdapter extends ListAdapter<SavedLocations, SavedLocationsViewHolder> {

    SavedLocationsOnClickListener onClickListener;
    SavedLocationsViewModel savedLocationsViewModel;
    ActivityResultLauncher<Intent> arlEditLocationDetail;
    ArrayList<SavedLocations> locationsToDelete = new ArrayList<>();
    boolean isEnable = false;

    protected SavedLocationsAdapter(
            @NonNull DiffUtil.ItemCallback<SavedLocations> diffCallback,
            SavedLocationsOnClickListener onClickListener,
            SavedLocationsViewModel savedLocationsViewModel,
            ActivityResultLauncher<Intent> arlEditLocationDetail
    ) {
        super(diffCallback);
        this.onClickListener = onClickListener;
        this.savedLocationsViewModel = savedLocationsViewModel;
        this.arlEditLocationDetail = arlEditLocationDetail;
    }

    @NonNull
    @Override
    public SavedLocationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SavedLocationsViewHolder.create(
                parent,
                this.onClickListener,
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
        holder.locationName.setOnLongClickListener(v -> {
            selectLocation(holder, location);
            return true;
        });

        holder.locationName.setOnClickListener(v -> {
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

                if (locationsToDelete.isEmpty()) {
                    isEnable = false;
                }
            } else if (isEnable) {
                selectLocation(holder, location);
            }
        });
    }

    private void selectLocation(SavedLocationsViewHolder holder, SavedLocations selectedLocation) {

        Log.i("selectedLocation", selectedLocation.locationName);
        isEnable = true;
        locationsToDelete.add(selectedLocation);
        holder.rvLinearLayout.setBackgroundColor(Color.LTGRAY);
        Picasso
                .get()
                .load(R.mipmap.check_mark)
                .resize(0, 75)
                .centerInside()
                .into(holder.locationThumbNail);

    }

    public void deleteSelectedLocations() {
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
