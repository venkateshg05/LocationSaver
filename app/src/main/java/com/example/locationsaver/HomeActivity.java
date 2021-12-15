package com.example.locationsaver;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

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

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;

    private String locationName;
    private final String LOCATION_NAME_KEY = "LOCATION_NAME_KEY";
    ActivityResultLauncher<Intent> arlGetLocationName = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // check if the user has a entered a legal name
                if (result.getResultCode() == Activity.RESULT_OK) {

                    // Get the legal name entered by the user
                    Intent locationDetails = result.getData();
                    locationName = locationDetails.getStringExtra(LOCATION_NAME_KEY);
                    if (currentLocation != null) {
                        SavedLocations newLocation = new SavedLocations(
                                String.valueOf(currentLocation.getLatitude()),
                                String.valueOf(currentLocation.getLongitude()),
                                locationName
                        );
                        savedLocationsViewModel.addNewSavedLocation(newLocation);
                        Toast.makeText(
                                getApplicationContext(),
                                "Location saved",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
                // If the user has entered an illegal legal name
                else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    Toast.makeText(
                            HomeActivity.this,
                            "No Name",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Set the home page layout
        setContentView(R.layout.home_screen);

        // Get the save location button
        fabSaveLocation = findViewById(R.id.fabSaveMyLocation);
        fabSaveLocation.setOnClickListener(v -> checkPermissionAndGetLocation());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        savedLocationsViewModel = new ViewModelProvider(this).get(SavedLocationsViewModel.class);

        SavedLocationsOnClickListener onClickListener = (view, position) -> {
            Toast.makeText(
                    getApplicationContext(), "Clicked " + position, Toast.LENGTH_SHORT
            ).show();
        };
        SavedLocationsAdapter savedLocationsAdapter =
                new SavedLocationsAdapter(
                        new SavedLocationsAdapter.SavedLocationDiff(),
                        onClickListener
                );

        RecyclerView rvSavedLocations = findViewById(R.id.rvSavedLocations);
        rvSavedLocations.setAdapter(savedLocationsAdapter);
        rvSavedLocations.setLayoutManager(new LinearLayoutManager(this));

        savedLocationsViewModel.getAllSavedLocations().observe(this, locations -> {
            savedLocationsAdapter.submitList(locations);
        });

    }

    private void checkPermissionAndGetLocation() {
        if (
                ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            CancellationTokenSource cts = new CancellationTokenSource();
            fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, cts.getToken()
            ).addOnSuccessListener(this, location -> {
                // Got current location. In some rare situations this can be null.
                currentLocation = location;
                Log.i("saveLocation", currentLocation.toString());
                getLocationDetailsAndSave();
            });
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    0
            );
        }
    }

    private void getLocationDetailsAndSave() {
        Intent intentGetLocationDetails = new Intent(HomeActivity.this, GetLocationDetails.class);
        arlGetLocationName.launch(intentGetLocationDetails);
    }

    public void onRequestPermissionsResult(int code, String[] permissions, int[] grantResults) {
        // get the user's response
        super.onRequestPermissionsResult(code, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Permission granted, go ahead and save location
            Log.i("HomeActivity", "Permission granted");
            checkPermissionAndGetLocation();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSavedLocations() {
        Toast.makeText(
                getApplicationContext(),
                "Coming soon... ",
                Toast.LENGTH_SHORT
        ).show();
    }

}
