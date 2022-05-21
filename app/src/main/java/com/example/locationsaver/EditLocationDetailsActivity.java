package com.example.locationsaver;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditLocationDetailsActivity extends AppCompatActivity {
    private Button btSaveDetails, btEditThumbnail;
    private EditText etLocationName;
    private ImageView ivThumbNail;
    private Uri photoURI = null;
    private boolean havePhoto = false;
    private final String LOCATION_NAME_KEY = "LOCATION_NAME_KEY";
    private final String SELECTION_POSITION = "LOCATION_ID_KEY";
    private final String PHOTO_URI_KEY = "PHOTO_URI_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle intentData = intent.getExtras();
        setContentView(R.layout.get_location_details);

        etLocationName = findViewById(R.id.etLocationName);
        etLocationName.setText(intentData.getString(LOCATION_NAME_KEY));

        ivThumbNail = findViewById(R.id.ivThumbNail);
        String thumbnailURI = intentData.getString(PHOTO_URI_KEY);
        if (thumbnailURI.length() > 0) {
            Picasso
                    .get()
                    .load(thumbnailURI)
                    .fit()
                    .centerCrop()
                    .into(this.ivThumbNail)
            ;
        }

        btEditThumbnail = findViewById(R.id.btAddImage);
        if (thumbnailURI.length() > 0) {
            btEditThumbnail.setText("New Photo");
        }
        btEditThumbnail.setOnClickListener(v -> launchCamera());

        btSaveDetails = findViewById(R.id.btSaveLocationDetails);
        btSaveDetails.setOnClickListener(
                view -> {
                    if (havePhoto) {
                        try {
                            photoURI = savePhotoToFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    String photoURIString = thumbnailURI;
                    if (photoURI != null) {
                        photoURIString = photoURI.toString();
                    }
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(LOCATION_NAME_KEY, etLocationName.getText().toString().trim());
                    resultIntent.putExtra(PHOTO_URI_KEY, photoURIString);
                    resultIntent.putExtra(
                            SELECTION_POSITION,
                            getIntent().getExtras().getInt(SELECTION_POSITION)
                    );
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
                    "Failed to save photo... please edit again",
                    Toast.LENGTH_SHORT
            ).show();
        }

        if (photoFile != null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ivThumbNail.getDrawable();
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
                ivThumbNail.setImageBitmap(imgBitmap);
                btEditThumbnail.setText("New photo");
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
}
