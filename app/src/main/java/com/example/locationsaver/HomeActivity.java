package com.example.locationsaver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;

public class HomeActivity extends AppCompatActivity {

    private Button btSaveLocation, btShowSavedLocations;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Set the home page layout
        setContentView(R.layout.home_screen);

        // Get the save location button
        btSaveLocation = findViewById(R.id.btSaveLocation);
        btSaveLocation.setOnClickListener(v -> saveLocation());

        // Get the save location button
        btShowSavedLocations = findViewById(R.id.btShowSavedLocation);
        btShowSavedLocations.setOnClickListener(v -> showSavedLocations());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    private void saveLocation() {
        if (
                ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            CancellationTokenSource cts = new CancellationTokenSource();
            //TODO get the location & save it
            fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, cts.getToken()
            ).addOnSuccessListener(this, location -> {
                // Got current location. In some rare situations this can be null.
                if (location != null) {
                    Log.i("saveLocation", location.toString());
                    // Logic to handle location object
                }
            });
            Toast.makeText(
                    getApplicationContext(),
                    "Location saved",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
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
            saveLocation();
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
