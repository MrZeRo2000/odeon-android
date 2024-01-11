package com.romanpulov.odeon.db;

import android.content.Context;

import java.io.IOException;

public interface DBReader {
    DBData read(Context context, ProgressListener progressListener) throws IOException;

    interface ProgressListener {
        void onProgress(String message);
    }
}
