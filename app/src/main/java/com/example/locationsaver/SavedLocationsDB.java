package com.example.locationsaver;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = SavedLocations.class, version = 3, exportSchema = false)
public abstract class SavedLocationsDB extends RoomDatabase{

    public abstract SavedLocationsDAO savedLocationsDAO();

    private static volatile SavedLocationsDB INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE saved_locations ADD COLUMN photoURI TEXT");
        }
    };


    static SavedLocationsDB getDatabase(final Context context) {
        try {
            if (INSTANCE == null) {
                synchronized (SavedLocationsDB.class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                                context.getApplicationContext(),
                                SavedLocationsDB.class,
                                "saved_locations_db"
                                )
                                .addMigrations(MIGRATION_2_3)
                                .build();
                    }
                }
            }

            return INSTANCE;
        } catch (ExceptionInInitializerError exception) {
            throw exception;
        }

    }

}
