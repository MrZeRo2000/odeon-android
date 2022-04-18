package com.romanpulov.odeon.db;

import android.content.Context;

public class DBManager {
    private final Context mContext;

    public DBManager(Context mContext) {
        this.mContext = mContext;
    }

    private void prePopulate(AppDatabase db) {
        db.artifactTypeDAO().insertAll(
                new ArtifactType(100, "Music", null),
                new ArtifactType(101, "MP3", 100),
                new ArtifactType(102, "LA", 100)
        );
    }

    public void prepare() {
        AppDatabase db = AppDatabase.getDatabase(mContext);
        db.clearAllTables();
        prePopulate(db);
    }

    public void close() {
        AppDatabase.getDatabase(mContext).close();
    }
}
