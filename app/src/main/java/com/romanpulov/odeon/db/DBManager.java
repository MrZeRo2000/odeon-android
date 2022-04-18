package com.romanpulov.odeon.db;

import android.content.Context;

public class DBManager {
    public final static int ARTIFACT_TYPE_ID_MUSIC = 100;
    public final static int ARTIFACT_TYPE_ID_MUSIC_MP3 = 101;
    public final static int ARTIFACT_TYPE_ID_MUSIC_LA = 102;

    private final Context mContext;
    private final AppDatabase mDb;

    public DBManager(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        mDb = obtainDatabase(mContext);
    }

    private void prePopulate(AppDatabase db) {
        db.artifactTypeDAO().insertAll(
                new ArtifactType(ARTIFACT_TYPE_ID_MUSIC, "Music", null),
                new ArtifactType(ARTIFACT_TYPE_ID_MUSIC_MP3, "MP3", ARTIFACT_TYPE_ID_MUSIC),
                new ArtifactType(ARTIFACT_TYPE_ID_MUSIC_LA, "LA", ARTIFACT_TYPE_ID_MUSIC)
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
