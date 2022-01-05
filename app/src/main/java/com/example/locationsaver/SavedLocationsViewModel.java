package com.example.locationsaver;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class SavedLocationsViewModel extends AndroidViewModel {

    private final SavedLocationsRepository savedLocationsRepository;
    public LiveData<List<SavedLocations>> savedLocations;

    public SavedLocationsViewModel(@NonNull Application application) {
        super(application);
        savedLocationsRepository = new SavedLocationsRepository(application);
//        savedLocations.postValue(savedLocationsRepository.getAllSavedLocations().getValue());
        savedLocations = savedLocationsRepository.getAllSavedLocations();
    }

    public LiveData<List<SavedLocations>> getAllSavedLocations() {
        return savedLocations;
    }

    public void addNewSavedLocation(SavedLocations location) {
        savedLocationsRepository.addNewSavedLocation(location);
    }

    public void deleteSelectedLocation(SavedLocations location) {
        savedLocationsRepository.deleteSelectedLocation(location);
    }

    public void updateLocation(SavedLocations location) {
        savedLocationsRepository.updateLocation(location);
    }

}
