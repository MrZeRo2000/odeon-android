package com.romanpulov.odeon.db;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import java.io.File;
import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MDBReader {
    private static final String REC_ID_COLUMN_NAME = "RecID";
    private static final String MP3CDCONT_ID_COLUMN_NAME = "MP3CDContID";
    private static final String ARTISTLIST_ID_COLUMN_NAME = "ArtistListID";
    private static final String TITLE_COLUMN_NAME = "Title";
    private static final String YEAR_COLUMN_NAME = "Year";
    private static final String DURATION_COLUMN_NAME = "Duration";
    private static final String INS_DATE_COLUMN_NAME = "InsDate";

    private static final String LACONT_ID_COLUMN_NAME = "LAContID";

    private static final String MP3CDCOMP_ID_COLUMN_NAME = "MP3CDCompID";

    private static void log(String message) {
        Log.d(MDBReader.class.getSimpleName(), message);
    }

    private final File mFile;

    private final List<Artifact> artifacts = new ArrayList<>();

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    private int lastMP3CDContId = 0;
    private int lastLAContId = 0;
    private final Set<Integer> mp3CDContIds = new HashSet<>();

    private final Set<Integer> uniqueArtistIds = new HashSet<>();
    private final List<Artist> artists = new ArrayList<>();

    public List<Artist> getArtists() {
        return artists;
    }

    private final Map<Integer, List<Composition>> compositionMap = new HashMap<>();

    public Map<Integer, List<Composition>> getCompositionMap() {
        return compositionMap;
    }

    public MDBReader(@NonNull File mFile) {
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

    public void readArtifacts(@NonNull Database database) throws IOException {
        readArtifactsFromMP3CDCont(database, 0);
        readArtifactsFromLACont(database, lastMP3CDContId);

        readArtists(database);

        readCompositionsFromMP3CDCont(database);
    }

    public void readArtifactsFromMP3CDCont(@NonNull Database database, int startId) throws IOException {
        Table table = database.getTable("MP3CDCont");

        for(Row row : table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == 1173) {
                int id = row.getInt(MP3CDCONT_ID_COLUMN_NAME);
                if (id > lastMP3CDContId) {
                    lastMP3CDContId = id;
                }
                int artistListID = row.getInt(ARTISTLIST_ID_COLUMN_NAME);
                uniqueArtistIds.add(artistListID);
                mp3CDContIds.add(id);

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

    public void readArtifactsFromLACont(@NonNull Database database, int startId) throws IOException {
        Table table = database.getTable("LACont");

        for(Row row : table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == 1172) {
                int id = row.getInt(LACONT_ID_COLUMN_NAME);
                if (id > lastLAContId) {
                    lastLAContId = id;
                }
                int artistListID = row.getInt(ARTISTLIST_ID_COLUMN_NAME);
                uniqueArtistIds.add(artistListID);

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

    public void readArtists(@NonNull Database database) throws IOException {
        Table table = database.getTable("ArtistList");
        for (Row row : table) {
            int id = row.getInt(ARTISTLIST_ID_COLUMN_NAME);
            if (uniqueArtistIds.contains(id)) {
                Artist artist = new Artist(id, row.getString(TITLE_COLUMN_NAME));
                artists.add(artist);
            }
        }
    }

    public void readCompositionMaps(@NonNull Database database) throws IOException {
        Table table = database.getTable("MP3CDComp");
        for (Row row : table) {
            int contId = row.getInt(MP3CDCONT_ID_COLUMN_NAME);

            if (mp3CDContIds.contains(contId)) {
                List<Composition> compositions;

                //calc comp
                compositions = compositionMap.getOrDefault(contId, null);

                if (compositions == null) {
                    compositions = new ArrayList<>();
                }

                //create and add
                Composition composition = new Composition(
                        row.getInt(MP3CDCOMP_ID_COLUMN_NAME),
                        contId,
                        row.getString(TITLE_COLUMN_NAME),
                        row.getInt(DURATION_COLUMN_NAME),
                        null,
                        null
                );
                compositions.add(composition);

                compositionMap.put(contId, compositions);
            }
        }
    }

    public void readCompositionsFromMP3CDCont(@NonNull Database database) throws IOException {
        readCompositionMaps(database);
    }
}
