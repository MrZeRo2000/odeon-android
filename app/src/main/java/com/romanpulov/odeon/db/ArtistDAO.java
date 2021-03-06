package com.romanpulov.odeon.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Collection;
import java.util.List;

@Dao
public interface ArtistDAO {
    @Insert
    List<Long> insertAll(Collection<Artist> artists);

    @Query("SELECT * FROM artists")
    List<Artist> getAll();

    @Query("SELECT * FROM artists ORDER BY arts_name")
    LiveData<List<Artist>> getAllSorted();
}
