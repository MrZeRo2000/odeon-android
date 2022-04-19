package com.romanpulov.odeon.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * Reads data from MDB to memory data structures
 */
public class MDBReader implements Closeable {
    private static final String REC_ID_COLUMN_NAME = "RecID";
    private static final String MP3CDCONT_ID_COLUMN_NAME = "MP3CDContID";
    private static final String ARTISTLIST_ID_COLUMN_NAME = "ArtistListID";
    private static final String TITLE_COLUMN_NAME = "Title";
    private static final String YEAR_COLUMN_NAME = "Year";
    private static final String DURATION_COLUMN_NAME = "Duration";
    private static final String INS_DATE_COLUMN_NAME = "InsDate";
    private static final String CDNUM_COLUMN_NAME = "CDNum";
    private static final String SNUM_COLUMN_NAME = "SNum";

    private static final String LACONT_ID_COLUMN_NAME = "LAContID";
    private static final String LACOMP_ID_COLUMN_NAME = "LACompID";

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

    public int getLastMP3CDContId() {
        return lastMP3CDContId;
    }

    private int lastMP3CDCompId = 0;

    public int getLastMP3CDCompId() {
        return lastMP3CDCompId;
    }

    private int lastLAContId = 0;

    public int getLastLAContId() {
        return lastLAContId;
    }

    private int lastLACompId = 0;

    public int getLastLACompId() {
        return lastLACompId;
    }

    private final Set<Integer> mp3CDContIds = new HashSet<>();
    private final Set<Integer> laContIds = new HashSet<>();
    private final Set<Integer> uniqueArtistIds = new HashSet<>();

    private final List<Artist> artists = new ArrayList<>();

    public List<Artist> getArtists() {
        return artists;
    }

    private final Map<Integer, List<Composition>> compositionMap = new HashMap<>();

    private final List<Composition> compositions = new ArrayList<>();

    public List<Composition> getCompositions() {
        return compositions;
    }

    private Database database;

    public MDBReader (@NonNull File mFile) throws IOException {
        this.mFile = mFile;
        this.database = DatabaseBuilder.open(mFile);
    }
    @Override
    public void close() throws IOException {
        this.database.close();
    }

    public void readAll() throws IOException {
        readArtifactsFromMP3CDCont(0);
        readArtifactsFromLACont(lastMP3CDContId);

        readArtists();

        readCompositionsFromMP3CDComp(0);
        readCompositionsFromLAComp(lastMP3CDContId);
    }

    public void readArtifactsFromMP3CDCont(int startId) throws IOException {
        Table table = database.getTable("MP3CDCont");

        for (Row row : table) {
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

    public void readArtifactsFromLACont(int startId) throws IOException {
        Table table = database.getTable("LACont");

        for(Row row : table) {
            if (row.getInt(REC_ID_COLUMN_NAME) == 1172) {
                int id = row.getInt(LACONT_ID_COLUMN_NAME);
                if (id > lastLAContId) {
                    lastLAContId = id;
                }
                int artistListID = row.getInt(ARTISTLIST_ID_COLUMN_NAME);
                uniqueArtistIds.add(artistListID);
                laContIds.add(id);

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

    public void readArtists() throws IOException {
        Table table = database.getTable("ArtistList");
        for (Row row : table) {
            int id = row.getInt(ARTISTLIST_ID_COLUMN_NAME);
            if (uniqueArtistIds.contains(id)) {
                Artist artist = new Artist(id, row.getString(TITLE_COLUMN_NAME));
                artists.add(artist);
            }
        }
    }

    public void readCompositionMaps(int startId) throws IOException {
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

                // calc last Id
                int id = row.getInt(MP3CDCOMP_ID_COLUMN_NAME);
                if (id > lastMP3CDCompId) {
                    lastMP3CDCompId = id;
                }

                //create and add
                Composition composition = new Composition(
                        id + startId,
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
        lastMP3CDCompId += startId;
    }

    public void sortCompositionMaps() {
        for (int key: compositionMap.keySet()) {
            List<Composition> mappedCompositions = compositionMap.get(key);
            if (mappedCompositions != null) {
                mappedCompositions.sort(Comparator.comparing(Composition::getId));

                int index = 1;
                for (Composition composition: mappedCompositions) {
                    Composition numberedComposition = new Composition(
                            composition.getId(),
                            composition.getArtifactId(),
                            composition.getTitle(),
                            composition.getDuration(),
                            1,
                            index
                    );

                    compositions.add(numberedComposition);
                    index ++;
                }
            }
        }
    }

    public void readCompositionsFromMP3CDComp(int startId) throws IOException {
        readCompositionMaps(startId);
        sortCompositionMaps();
    }

    public void readCompositionsFromLAComp(int startId) throws IOException {
        Table table = database.getTable("LAComp");

        for (Row row : table) {
            int contId = row.getInt(LACONT_ID_COLUMN_NAME);

            if (laContIds.contains(contId)) {
                int id = row.getInt(LACOMP_ID_COLUMN_NAME);
                if (id > lastLACompId) {
                    lastLACompId = id;
                }

                Composition composition = new Composition(
                        id + startId,
                        contId,
                        row.getString(TITLE_COLUMN_NAME),
                        row.getInt(DURATION_COLUMN_NAME),
                        row.getInt(CDNUM_COLUMN_NAME),
                        row.getInt(SNUM_COLUMN_NAME)
                );
                compositions.add(composition);
            }
        }
        lastLACompId += startId;
    }
}
