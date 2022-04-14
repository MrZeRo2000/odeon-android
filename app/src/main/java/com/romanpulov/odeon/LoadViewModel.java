package com.romanpulov.odeon;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

public class LoadViewModel extends ViewModel {
    private Uri mUri;

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }

}