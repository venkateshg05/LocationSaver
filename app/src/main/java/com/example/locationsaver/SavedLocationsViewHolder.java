package com.example.locationsaver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class SavedLocationsViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    public final TextView locationName;
    public final ImageView locationThumbNail;
    private final ImageButton ibEdit, ibDelete, ibOpenMaps;
    public final LinearLayout rvLinearLayout;

    private final SavedLocationsOnClickListener onClickListener;
    private SavedLocationsViewModel savedLocationsViewModel;
    ActivityResultLauncher<Intent> arlEditLocationDetail;

    private final String SELECTION_POSITION = "LOCATION_ID_KEY";
    private final String LOCATION_NAME_KEY = "LOCATION_NAME_KEY";
    private final String PHOTO_URI_KEY = "PHOTO_URI_KEY";

    public SavedLocationsViewHolder(
            @NonNull View itemView,
            SavedLocationsOnClickListener onClickListener,
            SavedLocationsViewModel savedLocationsViewModel,
            ActivityResultLauncher<Intent> arlEditLocationDetail) {

        super(itemView);

        locationName = itemView.findViewById(R.id.tvLocationName);
        locationThumbNail = itemView.findViewById(R.id.ivThumbNail);
        ibEdit = itemView.findViewById(R.id.ibEdit);
        ibDelete = itemView.findViewById(R.id.ibDelete);
        ibOpenMaps = itemView.findViewById(R.id.ibOpenMaps);
        rvLinearLayout = itemView.findViewById(R.id.linearLayout);

        this.onClickListener = onClickListener;
        this.savedLocationsViewModel = savedLocationsViewModel;
        this.arlEditLocationDetail = arlEditLocationDetail;

        ibOpenMaps.setOnClickListener(this);
    }

    public void bind(SavedLocations location, int position) {

        this.locationName.setText(location.locationName);

        if (location.photoURI.length() > 0) {
            Picasso
                    .get()
                    .load(location.photoURI)
                    .resize(0,75)
                    .centerInside()
                    .into(this.locationThumbNail)
            ;
        }

        this.ibEdit.setOnClickListener(v -> launchEditActivity(location, position));
        this.ibDelete.setOnClickListener(v -> deleteLocation(location));

    }

    private void launchEditActivity(SavedLocations location, int position) {
        Intent intentGetLocationDetails = new Intent(
                itemView.getContext(),
                EditLocationDetailsActivity.class
        );
        intentGetLocationDetails.putExtra(SELECTION_POSITION, position);
        intentGetLocationDetails.putExtra(LOCATION_NAME_KEY, location.locationName);
        intentGetLocationDetails.putExtra(PHOTO_URI_KEY, location.photoURI);
        arlEditLocationDetail.launch(intentGetLocationDetails);
    }

    private void deleteLocation(SavedLocations location) {
        new AlertDialog.Builder(itemView.getContext())
                .setTitle("Are you sure you want to delete this?")
                .setMessage("This action is not reversible")
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {
                            savedLocationsViewModel.deleteSelectedLocation(location);
                            Toast.makeText(
                                itemView.getContext(),
                                "Deleted " + location.locationName,
                                Toast.LENGTH_SHORT
                            ).show();
                        })
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) -> Toast.makeText(
                            itemView.getContext(),
                            "Canceled",
                            Toast.LENGTH_SHORT
                        ).show()
                )
                .show()
        ;
    }

    static SavedLocationsViewHolder create(
            ViewGroup parent, SavedLocationsOnClickListener onClickListener,
            SavedLocationsViewModel savedLocationsViewModel,
            ActivityResultLauncher<Intent> arlEditLocationDetail
            ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_items_saved_location, parent, false);
        return new SavedLocationsViewHolder(
                view,
                onClickListener,
                savedLocationsViewModel,
                arlEditLocationDetail
        );
    }

    @Override
    public void onClick(View v) {
        this.onClickListener.onClick(v, getAdapterPosition());
    }
}
