package com.example.locationsaver;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SavedLocationsViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnCreateContextMenuListener {

    private final TextView latLong;
    private final SavedLocationsOnClickListener onClickListener;
    private SavedLocationsViewModel savedLocationsViewModel;
    public SavedLocationsViewHolder(
            @NonNull View itemView,
            SavedLocationsOnClickListener onClickListener,
            SavedLocationsViewModel savedLocationsViewModel) {
        super(itemView);
        latLong = itemView.findViewById(R.id.tvLocationName);
        this.onClickListener = onClickListener;
        this.savedLocationsViewModel = savedLocationsViewModel;

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void bind(String location) {
        this.latLong.setText(location);
    }

    static SavedLocationsViewHolder create(
            ViewGroup parent, SavedLocationsOnClickListener onClickListener,
            SavedLocationsViewModel savedLocationsViewModel
            ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_items_saved_location, parent, false);
        return new SavedLocationsViewHolder(view, onClickListener, savedLocationsViewModel);
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
                Toast.makeText(
                        itemView.getContext(),
                        "Edit " + getAdapterPosition(),
                        Toast.LENGTH_SHORT
                ).show();
                return true;
            case CONTEXT_MENU_DELETE:
                Toast.makeText(
                        itemView.getContext(),
                        "Deleted " + location.locationName,
                        Toast.LENGTH_SHORT
                ).show();
                savedLocationsViewModel.deleteSelectedLocation(location);
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
