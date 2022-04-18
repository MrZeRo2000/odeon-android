package com.romanpulov.odeon.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ArtifactDAO {
    @Insert
    void insertAll(Artifact... artifacts);

    @Query("SELECT * FROM artifacts WHERE arts_id = :artistId ORDER BY artf_year, arts_id")
    LiveData<List<Artifact>> getByArtist(int artistId);
}
