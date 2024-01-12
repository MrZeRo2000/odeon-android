package com.romanpulov.odeon.db.reader;

import android.content.Context;
import com.romanpulov.odeon.db.DBData;

import java.io.Closeable;
import java.io.IOException;

public interface DBReader extends Closeable {
    DBData read(Context context, ProgressListener progressListener) throws IOException;

    interface ProgressListener {
        void onProgress(String message);
    }
}
