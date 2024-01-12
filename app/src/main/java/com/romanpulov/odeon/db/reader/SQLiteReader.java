package com.romanpulov.odeon.db.reader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import com.romanpulov.odeon.R;
import com.romanpulov.odeon.db.Artifact;
import com.romanpulov.odeon.db.Artist;
import com.romanpulov.odeon.db.Composition;
import com.romanpulov.odeon.db.DBData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SQLiteReader implements DBReader {
    private static final String ARTIFACTS_TABLE_NAME = "artifacts";
    private static final String ARTF_ID_COLUMN_NAME = "artf_id";
    private static final String ATTP_ID_COLUMN_NAME = "attp_id";
    private static final String ARTS_ID_COLUMN_NAME = "arts_id";
    private static final String ARTF_TITLE_COLUMN_NAME = "artf_title";
    private static final String ARTF_YEAR_COLUMN_NAME = "artf_year";

    private static final String ARTISTS_TABLE_NAME = "artists";
    private static final String ARTS_NAME_COLUMN_NAME = "arts_name";

    private static final String TRACKS_TABLE_NAME = "tracks";
    private static final String TRCK_ID_COLUMN_NAME = "trck_id";
    private static final String TRCK_TITLE_COLUMN_NAME = "trck_title";
    private static final String TRCK_DURATION_COLUMN_NAME = "trck_duration";
    private static final String TRCK_DISK_NUM_COLUMN_NAME = "trck_disk_num";
    private static final String TRCK_NUM_COLUMN_NAME = "trck_num";

    private final Set<Integer> uniqueArtistIds = new HashSet<>();
    private final Set<Integer> uniqueArtifactIds = new HashSet<>();

    private final List<Artifact> artifacts = new ArrayList<>();
    private final List<Artist> artists = new ArrayList<>();
    private final List<Composition> compositions = new ArrayList<>();

    static class DBHelper extends SQLiteOpenHelper {
        public DBHelper(@Nullable Context context, @Nullable String name) {
            super(context, name, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private DBHelper dbHelper;

    private final String name;

    public SQLiteReader(String name) {
        this.name = name;
    }

    @Override
    public void close() {
        dbHelper.close();
    }

    @Override
    public DBData read(Context context, ProgressListener progressListener) {
        dbHelper = new DBHelper(context, this.name);
        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {

            progressListener.onProgress(context.getString(R.string.notification_extract_artifacts));
            readArtifacts(db);

            progressListener.onProgress(context.getString(R.string.notification_extract_artists));
            readArtists(db);

            progressListener.onProgress(context.getString(R.string.notification_extract_comp));
            readCompositions(db);
        }

        return DBData.from(artists, artifacts, compositions);
    }

    private void readArtifacts(SQLiteDatabase db) {
        try (
            Cursor cursor = db.query(
                    ARTIFACTS_TABLE_NAME,
                    new String[] {ARTF_ID_COLUMN_NAME, ATTP_ID_COLUMN_NAME, ARTS_ID_COLUMN_NAME, ARTF_TITLE_COLUMN_NAME, ARTF_YEAR_COLUMN_NAME},
                    ATTP_ID_COLUMN_NAME + " IN (?, ?)",
                    new String[] {"101", "102"},
                    null,
                    null,
                    null)
        ) {
            while (cursor.moveToNext()) {
                uniqueArtifactIds.add(cursor.getInt(0));
                uniqueArtistIds.add(cursor.getInt(2));

                Artifact artifact = new Artifact(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        null,
                        null
                );
                artifacts.add(artifact);
            }
        }
    }

    private void readArtists(SQLiteDatabase db) {
        try (
                Cursor cursor = db.query(
                        ARTISTS_TABLE_NAME,
                        new String[] {ARTS_ID_COLUMN_NAME, ARTS_NAME_COLUMN_NAME},
                        null,
                        null,
                        null,
                        null,
                        null)
        ) {
            while (cursor.moveToNext()) {
                if (uniqueArtistIds.contains(cursor.getInt(0))) {
                    Artist artist = new Artist(
                            cursor.getInt(0),
                            cursor.getString(1)
                    );
                    artists.add(artist);
                }
            }
        }
    }

    private void readCompositions(SQLiteDatabase db) {
        try (
                Cursor cursor = db.query(
                        TRACKS_TABLE_NAME,
                        new String[] {
                                TRCK_ID_COLUMN_NAME,
                                ARTF_ID_COLUMN_NAME,
                                TRCK_TITLE_COLUMN_NAME,
                                TRCK_DURATION_COLUMN_NAME,
                                TRCK_DISK_NUM_COLUMN_NAME,
                                TRCK_NUM_COLUMN_NAME
                        },
                        null,
                        null,
                        null,
                        null,
                        null)
        ) {
            while (cursor.moveToNext()) {
                if (uniqueArtifactIds.contains(cursor.getInt(1))) {
                    Composition composition = new Composition(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getString(2),
                            cursor.getInt(3),
                            cursor.getInt(4),
                            cursor.getInt(5)
                    );
                    compositions.add(composition);
                }
            }
        }
    }
}
