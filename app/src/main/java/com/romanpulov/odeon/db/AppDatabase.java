package com.romanpulov.odeon.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ArtifactType.class, Artist.class, Artifact.class, Composition.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract ArtifactTypeDAO artifactTypeDAO();
    public abstract ArtistDAO artistDAO();
    public abstract ArtifactDAO artifactDAO();
    public abstract CompositionDAO compositionDAO();

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "odeon-db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
