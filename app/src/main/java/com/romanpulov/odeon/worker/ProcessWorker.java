package com.romanpulov.odeon.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;
import com.romanpulov.odeon.R;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessWorker extends Worker {
    public static final String PARAM_NAME_PASSWORD = "password";
    public static final String PARAM_NAME_MESSAGE = "message";

    private static void log(String message) {
        Log.d(ProcessWorker.class.getSimpleName(), message);
    }

    public ProcessWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        File archiveFile = new File(getApplicationContext().getFilesDir(), DownloadWorker.DATA_FILE_NAME);
        if (!archiveFile.exists()) {
            return Result.failure(createDataWithMessage(getApplicationContext().getString(R.string.error_loader_archive_file_not_exists)));
        }

        List<File> files = null;
        try {
            files = Junrar.extract(archiveFile, getApplicationContext().getFilesDir(), getInputData().getString(PARAM_NAME_PASSWORD));
            log("Extracted files:" + files.stream().map(File::getName).collect(Collectors.toList()));
        } catch (IOException | RarException e) {
            log("extract failure");
            return Result.failure(createDataWithMessage(getApplicationContext().getString(R.string.error_loader_extract)));
        }

        return Result.success();
    }

    private Data createDataWithMessage(String message) {
        return new Data.Builder().putString(PARAM_NAME_MESSAGE, message).build();
    }

}
