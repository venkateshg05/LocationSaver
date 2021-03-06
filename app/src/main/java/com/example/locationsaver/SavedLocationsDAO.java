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
    void deleteLocations(ArrayList<SavedLocations> locations);

    @Query("SELECT * FROM saved_locations ORDER BY locationName")
    LiveData<List<SavedLocations>> getAllLocationsSortedAsc();

    @Query("SELECT * FROM saved_locations ORDER BY locationName DESC")
    LiveData<List<SavedLocations>> getAllLocationsSortedDesc();

    @Query("SELECT * FROM saved_locations WHERE locationName LIKE :searchTerm ORDER BY locationName ASC")
    LiveData<List<SavedLocations>> getFilteredSavedLocations(String searchTerm);
}
