package com.example.locationsaver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class SavedLocationsViewHolder
        extends RecyclerView.ViewHolder {

    private final Context appContext;
    public final TextView locationName;
    public final ImageView locationThumbNail;
    public final ImageButton ibEdit, ibDelete, ibOpenMaps;
    public final LinearLayout rvLinearLayout;

    private SavedLocationsViewModel savedLocationsViewModel;
    ActivityResultLauncher<Intent> arlEditLocationDetail;

    private final String mapsURIBase = "http://maps.google.com/maps?q=loc:";
    private final String SELECTION_POSITION = "LOCATION_ID_KEY";
    private final String LOCATION_NAME_KEY = "LOCATION_NAME_KEY";
    private final String PHOTO_URI_KEY = "PHOTO_URI_KEY";

    public SavedLocationsViewHolder(
            @NonNull View itemView,
            SavedLocationsViewModel savedLocationsViewModel,
            ActivityResultLauncher<Intent> arlEditLocationDetail) {

        super(itemView);
        this.appContext = itemView.getContext();
        locationName = itemView.findViewById(R.id.tvLocationName);
        locationThumbNail = itemView.findViewById(R.id.ivThumbNail);
        ibEdit = itemView.findViewById(R.id.ibEdit);
        ibDelete = itemView.findViewById(R.id.ibDelete);
        ibOpenMaps = itemView.findViewById(R.id.ibOpenMaps);
        rvLinearLayout = itemView.findViewById(R.id.linearLayout);

        this.savedLocationsViewModel = savedLocationsViewModel;
        this.arlEditLocationDetail = arlEditLocationDetail;

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

        this.ibOpenMaps.setOnClickListener(v -> launchGoogleMaps(location));
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

    private void launchGoogleMaps(SavedLocations location) {

        String lat_long = location.latitude + "," + location.longitude +
                "(" + location.locationName + ")";

        Intent mapIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(mapsURIBase + lat_long)
                );
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(this.appContext.getPackageManager()) != null) {
          this.appContext.startActivity(mapIntent);
        }
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
            ViewGroup parent,
            SavedLocationsViewModel savedLocationsViewModel,
            ActivityResultLauncher<Intent> arlEditLocationDetail
            ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_items_saved_location, parent, false);
        return new SavedLocationsViewHolder(
                view,
                savedLocationsViewModel,
                arlEditLocationDetail
        );
    }
}
