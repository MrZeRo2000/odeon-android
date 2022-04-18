package com.romanpulov.odeon.db;

import android.provider.ContactsContract;
import android.util.Log;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import java.io.File;
import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MDBReader {
    private static final String REC_ID_COLUMN_NAME = "RecID";
    private static final String MP3CDCONT_ID_COLUMN_NAME = "MP3CDContID";
    private static final String ARTISTLIST_ID_COLUMN_NAME = "ArtistListID";
    private static final String TITLE_COLUMN_NAME = "Title";
    private static final String YEAR_COLUMN_NAME = "Year";
    private static final String INS_DATE_COLUMN_NAME = "InsDate";

    private static void log(String message) {
        Log.d(MDBReader.class.getSimpleName(), message);
    }

    private final File mFile;

    private List<Artifact> artifacts = new ArrayList<>();
    private Set<Integer> uniqueArtists = new HashSet<>();

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public MDBReader(File mFile) {
        this.mFile = mFile;
    }

    public Database openDatabase() throws IOException{
        return DatabaseBuilder.open(mFile);
    }

    public void readAll() throws IOException {
        try (Database database = openDatabase()) {
            readArtifacts(database);

        }
    }

    public void readArtifacts(Database database) throws IOException {
        int lastId = readArtifactsFromMP3CDCont(database);
    }

    public int readArtifactsFromMP3CDCont(Database database) throws IOException {
        Table table = database.getTable("MP3CDCont");
        List<? extends Column> columns = table.getColumns();
        int lastId = 0;

        for(Row row : table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == 1173) {
                int id = row.getInt(MP3CDCONT_ID_COLUMN_NAME);
                if (id > lastId) {
                    lastId = id;
                }
                int artistListID = row.getInt(ARTISTLIST_ID_COLUMN_NAME);
                uniqueArtists.add(artistListID);

                Artifact artifact = new Artifact(
                        id,
                        DBManager.ARTIFACT_TYPE_ID_MUSIC_MP3,
                        artistListID,
                        row.getString(TITLE_COLUMN_NAME),
                        row.getInt(YEAR_COLUMN_NAME),
                        null,
                        Optional.ofNullable(row.getLocalDateTime(INS_DATE_COLUMN_NAME))
                                .map(d -> d.toEpochSecond(ZoneOffset.ofHours(0)))
                                .orElse(null)
                        );
                artifacts.add(artifact);
            }
        }

        return lastId;
    }
}
