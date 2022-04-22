package com.romanpulov.odeon;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.romanpulov.odeon.db.Artifact;
import com.romanpulov.odeon.db.Artist;
import com.romanpulov.odeon.db.DBManager;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ArtistsViewModel extends AndroidViewModel {

    private static void log(String message) {
        Log.d(ArtistsViewModel.class.getSimpleName(), message);
    }

    private final DBManager mDbManager;

    private final ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    private LiveData<List<Artist>> mArtists;

    LiveData<List<Artist>> getArtists() {
        if (mArtists == null) {
            mArtists = mDbManager.getDatabase().artistDAO().getAllSorted();
        }
        return mArtists;
    }

    private final MutableLiveData<Integer> mSelectedArtistId = new MutableLiveData<>();

    public MutableLiveData<Integer> getSelectedArtistId() {
        return mSelectedArtistId;
    }

    private Artist mSelectedArtist;

    public Artist getSelectedArtist() {
        return mSelectedArtist;
    }

    public void setSelectedArtist(Artist selectedArtist) {
        this.mSelectedArtist = selectedArtist;
    }

    MutableLiveData<List<Artifact>> mArtifacts = new MutableLiveData<>();

    public MutableLiveData<List<Artifact>> getArtifacts() {
        return mArtifacts;
    }

    public void loadArtifacts() {
        Future<?> future = mExecutorService.submit(() -> {
            mArtifacts.postValue(mDbManager
                    .getDatabase()
                    .artifactDAO()
                    .getByArtist(getSelectedArtist().getId())
            );
        });
        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public ArtistsViewModel(@NonNull Application application) {
        super(application);
        mDbManager = new DBManager(application.getApplicationContext());
    }

    @Override
    protected void onCleared() {
        mExecutorService.shutdownNow();
        super.onCleared();
    }
}