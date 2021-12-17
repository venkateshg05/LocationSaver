package com.example.locationsaver;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private SavedLocationsViewModel savedLocationsViewModel;

    private FloatingActionButton fabSaveLocation;

    private final String mapsURIBase = "http://maps.google.com/maps?q=loc:";
    private final String LOCATION_NAME_KEY = "LOCATION_NAME_KEY";
    private final String LOCATION_LATITUDE_KEY = "LOCATION_LATITUDE_KEY";
    private final String LOCATION_LONGITUDE_KEY = "LOCATION_LONGITUDE_KEY";
    ActivityResultLauncher<Intent> arlGetLocationName = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> processResult(result)
    );

    private void processResult(ActivityResult result) {

        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent locationDetails = result.getData();
            String locationName = locationDetails.getStringExtra(LOCATION_NAME_KEY);
            String locationLatitude = locationDetails.getStringExtra(LOCATION_LATITUDE_KEY);
            String locationLongitude = locationDetails.getStringExtra(LOCATION_LONGITUDE_KEY);
            SavedLocations newLocation = new SavedLocations(
                    locationLatitude, locationLongitude, locationName
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        savedLocationsViewModel = new ViewModelProvider(this).get(SavedLocationsViewModel.class);

        SavedLocationsOnClickListener onClickListener =
                (view, position) -> setupOnClickListener(view, position);

        SavedLocationsAdapter savedLocationsAdapter =
                new SavedLocationsAdapter(
                        new SavedLocationsAdapter.SavedLocationDiff(),
                        onClickListener,
                        savedLocationsViewModel
                );

        setupHomePage(savedLocationsAdapter);

        savedLocationsViewModel.getAllSavedLocations().observe(this, locations -> {
            savedLocationsAdapter.submitList(locations);
        });

    }

    private void setupHomePage(SavedLocationsAdapter savedLocationsAdapter) {
        // Set the home page layout
        setContentView(R.layout.home_screen);

        // Get the save location button
        fabSaveLocation = findViewById(R.id.fabSaveMyLocation);
        fabSaveLocation.setOnClickListener(v -> getLocationDetailsAndSave());

        // Saved locations RV
        RecyclerView rvSavedLocations = findViewById(R.id.rvSavedLocations);
        rvSavedLocations.setAdapter(savedLocationsAdapter);
        rvSavedLocations.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupOnClickListener(View view, int position) {
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
        Intent intentGetLocationDetails = new Intent(HomeActivity.this, GetLocationDetails.class);
        if (
                ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            arlGetLocationName.launch(intentGetLocationDetails);
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
