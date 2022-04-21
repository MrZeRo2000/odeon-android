package com.romanpulov.odeon;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.romanpulov.odeon.db.Artist;
import com.romanpulov.odeon.db.DBManager;

import java.util.List;

public class ArtistsViewModel extends AndroidViewModel {
    private final DBManager mDbManager;

    public ArtistsViewModel(@NonNull Application application) {
        super(application);
        mDbManager = new DBManager(application.getApplicationContext());
    }

    LiveData<List<Artist>> mArtists;

    LiveData<List<Artist>> getArtists() {
        if (mArtists == null) {
            mArtists = mDbManager.getDatabase().artistDAO().getAllSorted();
        }
        return mArtists;
    }
}