package com.romanpulov.odeon.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "compositions", indices = {@Index(value = "artf_id", name = "idx_compositions_artf_id")})
public class Composition {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "comp_id")
    private Integer id;

    @NonNull
    public Integer getId() {
        return id;
    }

    @NonNull
    @ColumnInfo(name = "artf_id")
    private Integer artifactId;

    @NonNull
    public Integer getArtifactId() {
        return artifactId;
    }

    @NonNull
    @ColumnInfo(name = "comp_title")
    private String title;

    @NonNull
    public String getTitle() {
        return title;
    }

    @Nullable
    @ColumnInfo(name = "comp_duration")
    private Integer duration;

    @Nullable
    public Integer getDuration() {
        return duration;
    }

    @Nullable
    @ColumnInfo(name = "comp_disk_num")
    private Integer diskNumber;

    @Nullable
    public Integer getDiskNumber() {
        return diskNumber;
    }

    @Nullable
    @ColumnInfo(name = "comp_num")
    private Integer number;

    @Nullable
    public Integer getNumber() {
        return number;
    }

    public Composition(@NonNull Integer id, @NonNull Integer artifactId, @NonNull String title, @Nullable Integer duration, @Nullable Integer diskNumber, @Nullable Integer number) {
        this.id = id;
        this.artifactId = artifactId;
        this.title = title;
        this.duration = duration;
        this.diskNumber = diskNumber;
        this.number = number;
    }
}
