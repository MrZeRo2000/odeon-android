package com.romanpulov.odeon.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;

@Dao
public interface ArtifactTypeDAO {
    @Insert
    List<Long> insertAll(Collection<ArtifactType> artifactTypes);

    @Insert
    void insertAll(ArtifactType... artifactTypes);

    @Query("SELECT * FROM artifact_types")
    List<ArtifactType> getAll();
}
