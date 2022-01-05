package com.example.locationsaver;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditLocationDetailsActivity extends AppCompatActivity {
    private Button btSaveDetails;
    private EditText etLocationName;
    private final String LOCATION_NAME_KEY = "LOCATION_NAME_KEY";
    private final String SELECTION_POSITION = "LOCATION_ID_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_location_details);

        etLocationName = findViewById(R.id.etLocationName);
        etLocationName.setText(getIntent().getExtras().getString(LOCATION_NAME_KEY));

        btSaveDetails = findViewById(R.id.btSaveLocationDetails);
        btSaveDetails.setOnClickListener(
                view -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(LOCATION_NAME_KEY, etLocationName.getText().toString().trim());
                    resultIntent.putExtra(
                            SELECTION_POSITION,
                            getIntent().getExtras().getInt(SELECTION_POSITION)
                    );
                    setResult(RESULT_OK, resultIntent);
                    finish();
                });
    }
}
