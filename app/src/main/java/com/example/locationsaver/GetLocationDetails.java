package com.example.locationsaver;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class GetLocationDetails extends AppCompatActivity {
    private Button btSaveDetails;
    private EditText etLocationName;
    private final String LOCATION_NAME_KEY = "LOCATION_NAME_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_location_details);

        etLocationName = findViewById(R.id.etLocationName);

        btSaveDetails = findViewById(R.id.btSaveLocationDetails);
        btSaveDetails.setOnClickListener(
                view -> {
                    Intent resultIntent = new Intent();
                    if (TextUtils.isEmpty(etLocationName.getText())) {
                        setResult(RESULT_CANCELED, resultIntent);
                    } else {
                        resultIntent.putExtra(LOCATION_NAME_KEY, etLocationName.getText().toString());
                        setResult(RESULT_OK, resultIntent);
                    }
                    finish();
                });

    }

}
