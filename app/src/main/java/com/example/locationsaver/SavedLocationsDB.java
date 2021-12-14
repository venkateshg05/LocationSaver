package com.example.locationsaver;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = SavedLocations.class, version = 2, exportSchema = false)
public abstract class SavedLocationsDB extends RoomDatabase{

    public abstract SavedLocationsDAO savedLocationsDAO();

    private static volatile SavedLocationsDB INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    static SavedLocationsDB getDatabase(final Context context) {
        try {
            if (INSTANCE == null) {
                synchronized (SavedLocationsDB.class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                                context.getApplicationContext(),
                                SavedLocationsDB.class,
                                "saved_locations_db"
                                ).build();
                    }
                }
            }

            return INSTANCE;
        } catch (ExceptionInInitializerError exception) {
            throw exception;
        }

    }

}
