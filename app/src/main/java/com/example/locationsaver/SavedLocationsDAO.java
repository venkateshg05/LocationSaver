package com.example.locationsaver;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SavedLocationsDAO {
    @Query("SELECT * FROM saved_locations")
    LiveData<List<SavedLocations>> getAll();

    @Insert()
    public void insertLocations(SavedLocations... locations);

    @Delete
    public void deleteLocations(SavedLocations... locations);

}
