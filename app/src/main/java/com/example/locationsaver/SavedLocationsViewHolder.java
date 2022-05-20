package com.example.locationsaver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class SavedLocationsViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnCreateContextMenuListener {

    private final TextView locationName;
    private final ImageView locationThumbNail;
    private final SavedLocationsOnClickListener onClickListener;
    private SavedLocationsViewModel savedLocationsViewModel;
    ActivityResultLauncher<Intent> arlEditLocationDetail;
    private final String SELECTION_POSITION = "LOCATION_ID_KEY";
    private final String LOCATION_NAME_KEY = "LOCATION_NAME_KEY";
    private final String LOCATION_IMAGE_KEY = "LOCATION_IMAGE_KEY";

    public SavedLocationsViewHolder(
            @NonNull View itemView,
            SavedLocationsOnClickListener onClickListener,
            SavedLocationsViewModel savedLocationsViewModel,
            ActivityResultLauncher<Intent> arlEditLocationDetail) {
        super(itemView);
        locationName = itemView.findViewById(R.id.tvLocationName);
        locationThumbNail = itemView.findViewById(R.id.ivThumbNail);
        this.onClickListener = onClickListener;
        this.savedLocationsViewModel = savedLocationsViewModel;
        this.arlEditLocationDetail = arlEditLocationDetail;

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void bind(String locationName, String thumbnailURI) {
        this.locationName.setText(locationName);
        Log.i("onBind", ": " + thumbnailURI);
        if (thumbnailURI.length() > 0) {
            Picasso
                    .get()
                    .load(thumbnailURI)
                    .fit()
                    .centerCrop()
                    .into(this.locationThumbNail)
            ;
        }
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

    // Context menu ids
    private final int CONTEXT_MENU_EDIT = 1;
    private final int CONTEXT_MENU_DELETE = 2;
    private MenuItem.OnMenuItemClickListener contextMenu = item -> {
        SavedLocations location =
                savedLocationsViewModel.getAllSavedLocations().getValue().get(getAdapterPosition());
        switch (item.getItemId()) {
            case CONTEXT_MENU_EDIT:
                Intent intentGetLocationDetails = new Intent(
                        itemView.getContext(),
                        EditLocationDetailsActivity.class
                );
                intentGetLocationDetails.putExtra(SELECTION_POSITION, getAdapterPosition());
                intentGetLocationDetails.putExtra(LOCATION_NAME_KEY, location.locationName);
                intentGetLocationDetails.putExtra(LOCATION_IMAGE_KEY, location.photoURI);
                arlEditLocationDetail.launch(intentGetLocationDetails);
                return true;
            case CONTEXT_MENU_DELETE:
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
                return true;
        }
        return false;
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, CONTEXT_MENU_EDIT, 0, "Edit")
                .setOnMenuItemClickListener(contextMenu);
        menu.add(0, CONTEXT_MENU_DELETE, 0, "Delete")
                .setOnMenuItemClickListener(contextMenu);
    }
}
