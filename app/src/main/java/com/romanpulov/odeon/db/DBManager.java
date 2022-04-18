package com.romanpulov.odeon.db;

import android.content.Context;

public class DBManager {
    private final Context mContext;
    private final AppDatabase mDb;

    public DBManager(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        mDb = obtainDatabase(mContext);
    }

    private void prePopulate(AppDatabase db) {
        db.artifactTypeDAO().insertAll(
                new ArtifactType(100, "Music", null),
                new ArtifactType(101, "MP3", 100),
                new ArtifactType(102, "LA", 100)
        );
    }

    protected AppDatabase obtainDatabase(Context context) {
        return AppDatabase.getDatabase(mContext);
    }

    public AppDatabase getDatabase() {
        return mDb;
    }

    public void prepare() {
        mDb.clearAllTables();
        prePopulate(mDb);
    }

    public void close() {
        mDb.close();
    }
}
