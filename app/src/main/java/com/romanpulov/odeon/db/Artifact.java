package com.romanpulov.odeon.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "artifacts", indices = {@Index(value = "arts_id", name = "idx_artifacts_arts_id")})
public class Artifact {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "artf_id")
    private Integer id;

    @NonNull
    public Integer getId() {
        return id;
    }

    @NonNull
    @ColumnInfo(name = "attp_id")
    private Integer artifactTypeId;

    @NonNull
    public Integer getArtifactTypeId() {
        return artifactTypeId;
    }

    @NonNull
    @ColumnInfo(name = "arts_id")
    private Integer artistId;

    @NonNull
    public Integer getArtistId() {
        return artistId;
    }

    @NonNull
    @ColumnInfo(name = "artf_name")
    private String name;

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    @ColumnInfo(name = "artf_year")
    private Integer year;

    @Nullable
    public Integer getYear() {
        return year;
    }

    @Nullable
    @ColumnInfo(name = "artf_duration")
    private Integer duration;

    @Nullable
    public Integer getDuration() {
        return duration;
    }

    @Nullable
    @ColumnInfo(name = "artf_ins_date")
    private Integer insert_date;

    @Nullable
    public Integer getInsert_date() {
        return insert_date;
    }

    public Artifact(@NonNull Integer id, @NonNull Integer artifactTypeId, @NonNull Integer artistId, @NonNull String name, @Nullable Integer year, @Nullable Integer duration, @Nullable Integer insert_date) {
        this.id = id;
        this.artifactTypeId = artifactTypeId;
        this.artistId = artistId;
        this.name = name;
        this.year = year;
        this.duration = duration;
        this.insert_date = insert_date;
    }
}
