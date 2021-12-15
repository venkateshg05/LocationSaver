package com.example.locationsaver;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class SavedLocationsViewModel extends AndroidViewModel {

    private SavedLocationsRepository savedLocationsRepository;
    public final LiveData<List<SavedLocations>> savedLocations;

    public SavedLocationsViewModel(@NonNull Application application) {
        super(application);
        savedLocationsRepository = new SavedLocationsRepository(application);
        savedLocations = savedLocationsRepository.getAllSavedLocations();
    }

    LiveData<List<SavedLocations>> getAllSavedLocations() {
        return savedLocations;
    }

    public void addNewSavedLocation(SavedLocations location) {
        savedLocationsRepository.addNewSavedLocation(location);
    }

    public SavedLocations getSelectedLocation(int id) {
        savedLocationsRepository.getSelectedLocation(id);
        return null;
    }

}
