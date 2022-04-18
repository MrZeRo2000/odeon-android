package com.romanpulov.odeon.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "artifact_types")
public class ArtifactType {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "attp_id")
    private Integer id;

    @NonNull
    public Integer getId() {
        return id;
    }

    @NonNull
    @ColumnInfo(name = "attp_name")
    private String name;

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    @ColumnInfo(name = "attp_parent_id")
    private Integer parentId;

    @Nullable
    public Integer getParentId() {
        return parentId;
    }

    public ArtifactType(@NonNull Integer id, @NonNull String name, @Nullable Integer parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }
}
