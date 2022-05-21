package com.example.locationsaver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class SavedLocationsViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private final TextView locationName;
    private final ImageView locationThumbNail;
    private final ImageButton ibEdit, ibDelete, ibOpenMaps;

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

        this.onClickListener = onClickListener;
        this.savedLocationsViewModel = savedLocationsViewModel;
        this.arlEditLocationDetail = arlEditLocationDetail;

        ibOpenMaps.setOnClickListener(this);
    }

    public void bind(SavedLocations location) {

        this.locationName.setText(location.locationName);

        if (location.photoURI.length() > 0) {
            Picasso
                    .get()
                    .load(location.photoURI)
                    .fit()
                    .centerCrop()
                    .into(this.locationThumbNail)
            ;
        }

        this.ibEdit.setOnClickListener(v -> launchEditActivity(location));
        this.ibDelete.setOnClickListener(v -> deleteLocation(location));

    }


    private void launchEditActivity(SavedLocations location) {
        Intent intentGetLocationDetails = new Intent(
                itemView.getContext(),
                EditLocationDetailsActivity.class
        );
        intentGetLocationDetails.putExtra(SELECTION_POSITION, getAdapterPosition());
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
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                savedLocationsViewModel.deleteSelectedLocation(location);
                                Toast.makeText(
                                    itemView.getContext(),
                                    "Deleted " + location.locationName,
                                    Toast.LENGTH_SHORT
                                ).show();
                            }
                })
                .setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(
                                itemView.getContext(),
                                "Canceled",
                                Toast.LENGTH_SHORT
                            ).show();
                            }
                        }
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
