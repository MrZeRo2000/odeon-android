package com.romanpulov.odeon.db;

import android.content.Context;

import java.io.Closeable;
import java.io.IOException;

public interface DBReader extends Closeable {
    DBData read(Context context, ProgressListener progressListener) throws IOException;

    interface ProgressListener {
        void onProgress(String message);
    }
}
