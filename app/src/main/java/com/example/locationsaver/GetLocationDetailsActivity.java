package com.example.locationsaver;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetLocationDetailsActivity extends AppCompatActivity {
    private Button btSaveDetails, btGetPhoto;
    private ImageView thumbNail;
    private Uri photoURI = null;
    private boolean havePhoto = false;
    private EditText etLocationName;

    private final String LOCATION_NAME_KEY = "LOCATION_NAME_KEY";
    private final String LOCATION_LATITUDE_KEY = "LOCATION_LATITUDE_KEY";
    private final String LOCATION_LONGITUDE_KEY = "LOCATION_LONGITUDE_KEY";
    private final String PHOTO_URI_KEY = "PHOTO_URI_KEY";

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

        btGetPhoto = findViewById(R.id.btAddImage);
        btGetPhoto.setOnClickListener(v -> launchCamera());
        thumbNail = findViewById(R.id.ivThumbNail);

        etLocationName = findViewById(R.id.etLocationName);
        etLocationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btSaveDetails.setEnabled(!TextUtils.isEmpty(etLocationName.getText()));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        checkPermissionAndGetLocation();

        btSaveDetails.setOnClickListener(
                view -> {
                    if (havePhoto) {
                        try {
                            photoURI = savePhotoToFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    String photoURIString = "";
                    if (photoURI != null) {
                        photoURIString = photoURI.toString();
                    }
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(LOCATION_NAME_KEY, etLocationName.getText().toString().trim());
                    resultIntent.putExtra(LOCATION_LATITUDE_KEY, String.valueOf(currentLocation.getLatitude()));
                    resultIntent.putExtra(LOCATION_LONGITUDE_KEY, String.valueOf(currentLocation.getLongitude()));
                    resultIntent.putExtra(PHOTO_URI_KEY, photoURIString);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                });

    }

    private Uri savePhotoToFile() throws IOException {
        File photoFile = null;
        Uri photoURI = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Toast.makeText(
                    getApplicationContext(),
                    "Failed to save photo... please edit in location details",
                    Toast.LENGTH_SHORT
            ).show();
        }

        if (photoFile != null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) thumbNail.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            OutputStream fout = new FileOutputStream(photoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.close();
            photoURI = FileProvider.getUriForFile(
                    getApplicationContext(),
                    "com.example.locationsaver.fileprovider",
                    photoFile
            );
        }
        return photoURI;
    }

    ActivityResultLauncher<Intent> arlGetImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> getImage(result)
    );
    private void getImage(ActivityResult result) {

        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent camResult = result.getData();
            Bitmap imgBitmap = (Bitmap) camResult.getExtras().get("data");

            if (imgBitmap != null) {
                thumbNail.setImageBitmap(imgBitmap);
                btGetPhoto.setText("New photo");
                havePhoto = true;
            }
        }
        else if (result.getResultCode() == Activity.RESULT_CANCELED) {
            Toast.makeText(
                    getApplicationContext(),
                    "Cancelled",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );
        return image;
    }
    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            arlGetImage.launch(intent);
        } catch (ActivityNotFoundException e){
            Toast.makeText(
                    getApplicationContext(),
                    "Camera not found",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void checkPermissionAndGetLocation() {
        if (
                ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY, cts.getToken()
            ).addOnSuccessListener(this, location -> {
                // Got current location. In some rare situations this can be null.
                Log.i("saveLocation", "got location");
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



