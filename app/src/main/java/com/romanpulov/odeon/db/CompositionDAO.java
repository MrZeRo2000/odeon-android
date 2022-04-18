package com.romanpulov.odeon.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CompositionDAO {
    @Insert
    void insertAll(Composition... compositions);

    @Query("SELECT * FROM compositions WHERE artf_id = :artifactId ORDER BY comp_disk_num, comp_num, comp_id")
    List<Composition> getByArtifactId(int artifactId);
}
