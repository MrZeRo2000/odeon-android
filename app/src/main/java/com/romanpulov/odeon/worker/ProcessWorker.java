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
import com.romanpulov.odeon.db.AppDatabase;
import com.romanpulov.odeon.db.DBManager;
import com.romanpulov.odeon.db.MDBReader;

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

        setProgressAsync(createDataWithMessage(R.string.notification_extract));
        List<File> files = null;
        try {
            Thread.sleep(10);
            files = Junrar.extract(archiveFile, getApplicationContext().getFilesDir(), getInputData().getString(PARAM_NAME_PASSWORD));
            log("Extracted files:" + files.stream().map(File::getName).collect(Collectors.toList()));
        } catch (IOException | RarException |InterruptedException e) {
            log("extract failure");
            return Result.failure(createDataWithMessage(getApplicationContext().getString(R.string.error_loader_extract)));
        }

        MDBReader reader;
        try {
            reader = readMDB(files.get(0));

        } catch (IOException e) {
            return Result.failure(createDataWithMessage(getApplicationContext().getString(R.string.error_mdb_read_failure)));
        }

        setProgressAsync(createDataWithMessage(R.string.notification_db_prepare));
        DBManager dbManager = new DBManager(getApplicationContext());
        dbManager.prepare();

        setProgressAsync(createDataWithMessage(R.string.notification_successfully_completed));
        return Result.success();
    }

    private MDBReader readMDB(File file) throws IOException {
        try (MDBReader reader = new MDBReader(file)) {
            setProgressAsync(createDataWithMessage(R.string.notification_extract_artifacts));
            reader.readArtifactsFromMP3CDCont(0);
            reader.readArtifactsFromLACont(reader.getLastMP3CDContId());

            setProgressAsync(createDataWithMessage(R.string.notification_extract_artists));
            reader.readArtists();

            setProgressAsync(createDataWithMessage(R.string.notification_extract_comp));
            reader.readCompositionsFromMP3CDComp(0);
            reader.readCompositionsFromLAComp(reader.getLastMP3CDContId());

            return reader;
        }
    }

    private Data createDataWithMessage(String message) {
        return new Data.Builder().putString(PARAM_NAME_MESSAGE, message).build();
    }

    private Data createDataWithMessage(int resId) {
        return new Data.Builder().putString(PARAM_NAME_MESSAGE, getApplicationContext().getString(resId)).build();
    }


}
