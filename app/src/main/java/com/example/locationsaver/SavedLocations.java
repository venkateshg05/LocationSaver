package com.example.locationsaver;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_locations")
public class SavedLocations {

    public SavedLocations(String latitude, String longitude, String locationName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String latitude;
    public String longitude;
    public String locationName;
}
