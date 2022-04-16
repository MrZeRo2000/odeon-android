package com.romanpulov.odeon.worker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadWorker extends Worker {
    public static final String PARAM_NAME_URI = "uri";
    public static final String PARAM_NAME_PROGRESS_TOTAL = "progress_total";
    public static final String PARAM_NAME_PROGRESS_CURRENT = "progress_current";

    public static final String DATA_FILE_NAME = "data.rar";

    private static final int FILE_BUF_LEN = 1024;

    private static void log(String message) {
        Log.d(DownloadWorker.class.getSimpleName(), message);
    }

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private long getContentSize(Uri uri) {
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                log("Moved to first cursor line");

                int sizeColumnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
                if (sizeColumnIndex > -1) {
                    log("Found size column");
                    return cursor.getLong(sizeColumnIndex);
                } else {
                    log("Size column not found, can't determine the size");
                    return 0;
                }
            } else {
                log("Media query returns no data, can't determine size");
                return 0;
            }
        } finally {
            if ((cursor != null) && !cursor.isClosed())
            cursor.close();
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Uri uri = Uri.parse(getInputData().getString(PARAM_NAME_URI));
            log("Starting work with uri = " + uri.toString());

            long contentSize = getContentSize(uri);
            log("Content size:" + contentSize);

            setProgressAsync(new Data.Builder()
                    .putLong(PARAM_NAME_PROGRESS_TOTAL, contentSize)
                    .putLong(PARAM_NAME_PROGRESS_CURRENT, 0)
                    .build()
            );

            try (
                    InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(uri);
                    OutputStream outputStream = new FileOutputStream(new File(getApplicationContext().getFilesDir(), DATA_FILE_NAME));
                    )
            {
                byte[] buf = new byte[FILE_BUF_LEN];
                int len;
                long position = 0;
                long sectionSize = contentSize / 10;
                long sectionPosition = 0;

                while ((len = inputStream.read(buf)) > 0) {
                    if (isStopped()) {
                        log("Stopping");
                        break;
                    }
                    //Thread.sleep(10);
                    position += len;

                    if (position > sectionPosition) {
                        sectionPosition += sectionSize;
                        setProgressAsync(new Data.Builder()
                                .putLong(PARAM_NAME_PROGRESS_TOTAL, contentSize)
                                .putLong(PARAM_NAME_PROGRESS_CURRENT, position)
                                .build()
                        );
                    }

                    outputStream.write(buf, 0, len);
                }
            }

            setProgressAsync(new Data.Builder()
                    .putLong(PARAM_NAME_PROGRESS_TOTAL, contentSize)
                    .putLong(PARAM_NAME_PROGRESS_CURRENT, contentSize)
                    .build()
            );

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }

    }
}
