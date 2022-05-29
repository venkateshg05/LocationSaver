package com.example.locationsaver;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private SavedLocationsViewModel savedLocationsViewModel;
    private SavedLocationsAdapter savedLocationsAdapter;
    private Menu mainMenu;

    private FloatingActionButton fabSaveLocation;

    private final String mapsURIBase = "http://maps.google.com/maps?q=loc:";
    private final String LOCATION_NAME_KEY = "LOCATION_NAME_KEY";
    private final String LOCATION_LATITUDE_KEY = "LOCATION_LATITUDE_KEY";
    private final String LOCATION_LONGITUDE_KEY = "LOCATION_LONGITUDE_KEY";
    private final String PHOTO_URI_KEY = "PHOTO_URI_KEY";

    private boolean asc = true;

    ActivityResultLauncher<Intent> arlGetLocationDetail = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> addNewLocation(result)
    );
    private void addNewLocation(ActivityResult result) {

        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent locationDetails = result.getData();
            String locationName = locationDetails.getStringExtra(LOCATION_NAME_KEY);
            String locationLatitude = locationDetails.getStringExtra(LOCATION_LATITUDE_KEY);
            String locationLongitude = locationDetails.getStringExtra(LOCATION_LONGITUDE_KEY);
            String locationPhotoURI = locationDetails.getStringExtra(PHOTO_URI_KEY);
            Log.i("addNewLoc", ": " + locationPhotoURI);
            SavedLocations newLocation = new SavedLocations(
                    locationLatitude, locationLongitude, locationName, locationPhotoURI
            );
            savedLocationsViewModel.addNewSavedLocation(newLocation);
            Toast.makeText(
                    getApplicationContext(),
                    "Location saved",
                    Toast.LENGTH_SHORT
            ).show();
        }
        else if (result.getResultCode() == Activity.RESULT_CANCELED) {
            Toast.makeText(
                    HomeActivity.this,
                    "Cancelled",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private final String SELECTION_POSITION = "LOCATION_ID_KEY";
    ActivityResultLauncher<Intent> arlEditLocationDetail = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> editLocation(result)
    );
    private void editLocation(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent locationDetails = result.getData();
            String locationName = locationDetails.getStringExtra(LOCATION_NAME_KEY);
            String locationPhotoURI = locationDetails.getStringExtra(PHOTO_URI_KEY);
            int position = locationDetails.getIntExtra(SELECTION_POSITION, -1);
            SavedLocations selectedLocation = savedLocationsViewModel.savedLocations.getValue().get(position);
            SavedLocations updateLocation = new SavedLocations(
                    selectedLocation.latitude,
                    selectedLocation.longitude,
                    locationName,
                    locationPhotoURI
                    );
            updateLocation.id = selectedLocation.id;
            savedLocationsViewModel.updateLocation(updateLocation);
            Toast.makeText(
                    getApplicationContext(),
                    "Changes saved",
                    Toast.LENGTH_SHORT
            ).show();
        }
        else if (result.getResultCode() == Activity.RESULT_CANCELED) {
            Toast.makeText(
                    HomeActivity.this,
                    "Cancelled",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        savedLocationsViewModel = new ViewModelProvider(this)
                                    .get(SavedLocationsViewModel.class);

        SavedLocationsOnClickListener oclLaunchMaps =
                (view, position) -> setupLaunchMaps(view, position);

        savedLocationsAdapter =
                new SavedLocationsAdapter(
                        new SavedLocationsAdapter.SavedLocationDiff(),
                        oclLaunchMaps,
                        savedLocationsViewModel,
                        arlEditLocationDetail
                );

        setupHomePage(savedLocationsAdapter);

        savedLocationsViewModel.getAllSavedLocationsSortedAsc()
                .observe(this, locations -> {
                    savedLocationsAdapter.submitList(locations);
                    }
                );
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_bar, menu);

        showDeleteButton(false);

        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        setupSearchView(searchView);
        return true;
    }

    private void showDeleteButton(boolean show) {
        mainMenu.findItem(R.id.delete).setVisible(show);
    }

    private void setupSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private void searchQuery(String query) {
                if (query.length() > 0) {
                    searchLocations(query);
                }
                else if (asc){
                    sortLocationsAlphanumericallyAsc();
                }
                else {
                    sortLocationsAlphanumericallyDesc();
                }
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery(newText);
                return true;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("A -> Z")) {
            sortLocationsAlphanumericallyAsc();
            return true;
        } else if (item.getTitle().equals("Z -> A")) {
            sortLocationsAlphanumericallyDesc();
            return true;
        } else if (item.getTitle().equals("Delete")) {
            deleteSelectedLocations();
        }
        return false;
    }

    private void deleteSelectedLocations() {
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Are you sure you want to delete this?")
                .setMessage("This action is not reversible")
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {
                            savedLocationsAdapter.deleteSelectedLocations();
                            showDeleteButton(false);
                        })
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) -> Toast.makeText(
                                getApplicationContext(),
                                "Canceled",
                                Toast.LENGTH_SHORT
                        ).show()
                )
                .show();
    }

    public void searchLocations(String searchTerm) {
        searchTerm = "%" + searchTerm + "%";
        savedLocationsViewModel.getFilteredSavedLocations(searchTerm).observe(
                this,
                    locations -> {
                        savedLocationsAdapter.submitList(locations);
                    }
        );
    }

    private void sortLocationsAlphanumericallyAsc() {
        savedLocationsViewModel.getAllSavedLocationsSortedAsc().observe(
                    this,
                    locations -> {
                        savedLocationsAdapter.submitList(locations);
                    }
            );
        asc = true;
    }

    private void sortLocationsAlphanumericallyDesc() {
        savedLocationsViewModel.getAllSavedLocationsSortedDesc().observe(
                    this,
                    locations -> {
                        savedLocationsAdapter.submitList(locations);
                    }
            );
        asc = false;
    }

    private void setupHomePage(SavedLocationsAdapter savedLocationsAdapter) {
        // Set the home page layout
        setContentView(R.layout.home_screen);
        setSupportActionBar(findViewById(R.id.toolBar));

        // Get the save location button
        fabSaveLocation = findViewById(R.id.fabSaveMyLocation);
        fabSaveLocation.setOnClickListener(v -> getLocationDetailsAndSave());

        // Saved locations RV
        setupRecyclerView(savedLocationsAdapter);
    }

    private void setupRecyclerView(SavedLocationsAdapter savedLocationsAdapter) {
        RecyclerView rvSavedLocations = findViewById(R.id.rvSavedLocations);
        rvSavedLocations.setAdapter(savedLocationsAdapter);
        rvSavedLocations.setLayoutManager(new LinearLayoutManager(this));
        addSwipeGestures(rvSavedLocations);
    }

    private void addSwipeGestures(RecyclerView rvSavedLocations) {
        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(
                        @NonNull RecyclerView recyclerView,
                        @NonNull RecyclerView.ViewHolder viewHolder,
                        @NonNull RecyclerView.ViewHolder target
                ) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    showDeletionAlertBox(viewHolder);
                }
            }
        ).attachToRecyclerView(rvSavedLocations);
    }

    private void showDeletionAlertBox(RecyclerView.ViewHolder viewHolder) {
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Are you sure you want to delete this?")
                .setMessage("This action is not reversible")
                .setPositiveButton(
                        "Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSwipedLocation(viewHolder);
                            }
                        })
                .setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Canceled",
                                        Toast.LENGTH_SHORT
                                ).show();
                                savedLocationsAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        }
                )
                .show();
    }

    private void deleteSwipedLocation(RecyclerView.ViewHolder viewHolder) {
        SavedLocations locationToDelete = savedLocationsViewModel
                                            .getAllSavedLocations()
                                            .getValue()
                                            .get(viewHolder.getAdapterPosition());
        savedLocationsViewModel.deleteSelectedLocation(locationToDelete);
        Toast.makeText(
            getApplicationContext(),
            "Deleted " + locationToDelete.locationName,
            Toast.LENGTH_SHORT
        ).show();
    }

    private void setupLaunchMaps(View view, int position) {
        Log.i("launch maps", "len: "+savedLocationsViewModel.savedLocations.getValue());
        SavedLocations selectedLocation = savedLocationsViewModel.savedLocations.getValue().get(position);
        String lat_long = selectedLocation.latitude + "," + selectedLocation.longitude +
                "(" + selectedLocation.locationName + ")";

        Intent mapIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(mapsURIBase + lat_long)
                );
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
          startActivity(mapIntent);
        }
    }

    private void getLocationDetailsAndSave() {
        Intent intentGetLocationDetails = new Intent(HomeActivity.this, GetLocationDetailsActivity.class);
        if (
                ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            arlGetLocationDetail.launch(intentGetLocationDetails);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Need permission to save precise location",
                    Toast.LENGTH_SHORT
            ).show();
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    0
            );
        }
    }

    public void onRequestPermissionsResult(int code, String[] permissions, int[] grantResults) {
        // get the user's response
        super.onRequestPermissionsResult(code, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Permission granted, go ahead and save location
            Log.i("HomeActivity", "Permission granted");
            getLocationDetailsAndSave();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

}
