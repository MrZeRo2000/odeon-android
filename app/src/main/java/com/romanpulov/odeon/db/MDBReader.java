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

    private static final String LACONT_ID_COLUMN_NAME = "LAContID";

    private static void log(String message) {
        Log.d(MDBReader.class.getSimpleName(), message);
    }

    private final File mFile;

    private List<Artifact> artifacts = new ArrayList<>();
    private int lastMP3CDContId = 0;
    private int lastLAContId = 0;

    private Set<Integer> uniqueArtists = new HashSet<>();
    private List<Artist> artists = new ArrayList<>();

    public List<Artist> getArtists() {
        return artists;
    }

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
        readArtifactsFromMP3CDCont(database, 0);
        readArtifactsFromLACont(database, lastMP3CDContId);
        readArtists(database);
    }

    public void readArtifactsFromMP3CDCont(Database database, int startId) throws IOException {
        Table table = database.getTable("MP3CDCont");

        for(Row row : table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == 1173) {
                int id = row.getInt(MP3CDCONT_ID_COLUMN_NAME);
                if (id > lastMP3CDContId) {
                    lastMP3CDContId = id;
                }
                int artistListID = row.getInt(ARTISTLIST_ID_COLUMN_NAME);
                uniqueArtists.add(artistListID);

                Artifact artifact = new Artifact(
                        id + startId,
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
        lastMP3CDContId += startId;
    }

    public void readArtifactsFromLACont(Database database, int startId) throws IOException {
        Table table = database.getTable("LACont");

        for(Row row : table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == 1172) {
                int id = row.getInt(LACONT_ID_COLUMN_NAME);
                if (id > lastLAContId) {
                    lastLAContId = id;
                }
                int artistListID = row.getInt(ARTISTLIST_ID_COLUMN_NAME);
                uniqueArtists.add(artistListID);

                Artifact artifact = new Artifact(
                        id + startId,
                        DBManager.ARTIFACT_TYPE_ID_MUSIC_LA,
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

        lastLAContId += startId;
    }

    public void readArtists(Database database) throws IOException {
        Table table = database.getTable("ArtistList");
        for (Row row : table) {
            int id = row.getInt(ARTISTLIST_ID_COLUMN_NAME);
            if (uniqueArtists.contains(id)) {
                Artist artist = new Artist(id, row.getString(TITLE_COLUMN_NAME));
                artists.add(artist);
            }
        }


    }

}
