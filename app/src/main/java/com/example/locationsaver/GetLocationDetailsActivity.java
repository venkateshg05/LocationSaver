package com.example.locationsaver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;

public class GetLocationDetailsActivity extends AppCompatActivity {
    private Button btSaveDetails;
    private EditText etLocationName;
    private final String LOCATION_NAME_KEY = "LOCATION_NAME_KEY";
    private final String LOCATION_LATITUDE_KEY = "LOCATION_LATITUDE_KEY";
    private final String LOCATION_LONGITUDE_KEY = "LOCATION_LONGITUDE_KEY";

    private FusedLocationProviderClient fusedLocationProviderClient;
    private CancellationTokenSource cts = new CancellationTokenSource();
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_location_details);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btSaveDetails = findViewById(R.id.btSaveLocationDetails);
        btSaveDetails.setEnabled(false);

        etLocationName = findViewById(R.id.etLocationName);
        etLocationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btSaveDetails.setEnabled(!TextUtils.isEmpty(etLocationName.getText()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        checkPermissionAndGetLocation();

        btSaveDetails.setOnClickListener(
                view -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(LOCATION_NAME_KEY, etLocationName.getText().toString().trim());
                        resultIntent.putExtra(LOCATION_LATITUDE_KEY, String.valueOf(currentLocation.getLatitude()));
                        resultIntent.putExtra(LOCATION_LONGITUDE_KEY, String.valueOf(currentLocation.getLongitude()));
                        setResult(RESULT_OK, resultIntent);
                    finish();
                });

    }

    private void checkPermissionAndGetLocation() {
        if (
                ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, cts.getToken()
            ).addOnSuccessListener(this, location -> {
                // Got current location. In some rare situations this can be null.
                currentLocation = location;
                Log.i("saveLocation", currentLocation.toString());
            });
        } else {
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED, resultIntent);
            finish();
        }
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

}



