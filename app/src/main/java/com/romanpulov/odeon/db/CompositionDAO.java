package com.romanpulov.odeon.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;

@Dao
public interface CompositionDAO {
    @Insert
    List<Long> insertAll(Collection<Composition> compositions);

    @Query("SELECT * FROM compositions WHERE artf_id = :artifactId ORDER BY comp_disk_num, comp_num, comp_id")
    List<Composition> getByArtifactId(int artifactId);
}
