package com.example.locationsaver;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SavedLocationsDAO {
    @Insert()
    void insertLocations(SavedLocations... location);

    @Query("SELECT * FROM saved_locations")
    LiveData<List<SavedLocations>> getAllLocations();

    @Update
    void updateLocation(SavedLocations... location);

    @Delete
    void deleteLocation(SavedLocations... location);

    @Delete
    void deleteLocations(SavedLocations... locations);

}
