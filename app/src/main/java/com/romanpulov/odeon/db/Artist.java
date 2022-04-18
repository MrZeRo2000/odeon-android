package com.romanpulov.odeon.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "artists", indices = {@Index(value = "arts_name", unique = true, name = "idx_artists_arts_name")})
public class Artist {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "arts_id")
    private Integer id;

    @NonNull
    public Integer getId() {
        return id;
    }

    @NonNull
    @ColumnInfo(name = "arts_name")
    private String name;

    @NonNull
    public String getName() {
        return name;
    }

    public Artist(@NonNull Integer id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }
}
