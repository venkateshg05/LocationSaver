package com.example.locationsaver;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

class SavedLocationsRepository {

    private SavedLocationsDAO savedLocationsDAO;
    private LiveData<List<SavedLocations>> savedLocations;

    SavedLocationsRepository(Application application) {
        SavedLocationsDB savedLocationsDB = SavedLocationsDB.getDatabase(application);
        savedLocationsDAO = savedLocationsDB.savedLocationsDAO();
        savedLocations = savedLocationsDAO.getAllLocations();
    }

    LiveData<List<SavedLocations>> getAllSavedLocations() {
        return savedLocations;
    }

    void addNewSavedLocation(SavedLocations location) {
        SavedLocationsDB.databaseWriteExecutor.execute(
                () -> {
                    savedLocationsDAO.insertLocations(location);
                }
        );
    }

    void updateLocation(SavedLocations location) {
        SavedLocationsDB.databaseWriteExecutor.execute(
                () -> {
                    savedLocationsDAO.updateLocation(location);
                }
        );
    }

    void deleteSelectedLocation(SavedLocations location) {
        SavedLocationsDB.databaseWriteExecutor.execute(
                () -> {
                    savedLocationsDAO.deleteLocation(location);
                }
        );
    }

}
