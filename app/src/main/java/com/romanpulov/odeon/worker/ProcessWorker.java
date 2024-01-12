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
import com.romanpulov.odeon.db.*;
import com.romanpulov.odeon.db.reader.DBReader;
import com.romanpulov.odeon.db.reader.MDBReader;
import com.romanpulov.odeon.db.reader.SQLiteReader;

import java.io.File;
import java.io.IOException;

public class ProcessWorker extends Worker {
    public static final String PARAM_NAME_PASSWORD = "password";
    public static final String PARAM_NAME_MESSAGE = "message";
    public static final String PARAM_NAME_EXTENSION = "extension";

    private static void log(String message) {
        Log.d(ProcessWorker.class.getSimpleName(), message);
    }

    public ProcessWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // get archive file
        File archiveFile = new File(
                getApplicationContext().getFilesDir(),
                DownloadWorker.DATA_FILE_NAME + "." + getInputData().getString(PARAM_NAME_EXTENSION));
        if (!archiveFile.exists()) {
            return Result.failure(createDataWithMessage(getApplicationContext().getString(R.string.error_loader_archive_file_not_exists)));
        }

        DBData dbData;
        String password = getInputData().getString(PARAM_NAME_PASSWORD);

        // read depending on file type
        if (password == null) {
            try (DBReader reader = new SQLiteReader(getApplicationContext(), archiveFile.getAbsolutePath())) {
                dbData = reader.read(getApplicationContext(), message -> setProgressAsync(createDataWithMessage(message)));
            } catch (Exception e) {
                log("Error reading from SQLite:" + e);
                return Result.failure(createDataWithMessage(getApplicationContext().getString(R.string.error_sqlite_read_failure)));
            }

            //return Result.failure(createDataWithMessage(getApplicationContext().getString(R.string.error_feature_not_implemented)));
        } else {
            setProgressAsync(createDataWithMessage(R.string.notification_extract));
            File file;
            try {
                Thread.sleep(10);
                file = Junrar.extract(archiveFile, getApplicationContext().getFilesDir(), getInputData().getString(PARAM_NAME_PASSWORD)).get(0);
                log("Extracted file:" + file.getName());
            } catch (IOException | RarException | InterruptedException e) {
                log("extract failure");
                return Result.failure(createDataWithMessage(getApplicationContext().getString(R.string.error_loader_extract)));
            }

            try (DBReader reader = new MDBReader(file)) {
                dbData = reader.read(getApplicationContext(), message -> setProgressAsync(createDataWithMessage(message)));
            } catch (IOException e) {
                return Result.failure(createDataWithMessage(getApplicationContext().getString(R.string.error_mdb_read_failure)));
            }
        }

        setProgressAsync(createDataWithMessage(R.string.notification_db_prepare));
        DBManager dbManager = new DBManager(getApplicationContext());
        dbManager.prepare();

        setProgressAsync(createDataWithMessage(R.string.notification_load_artists));
        dbManager.getDatabase().artistDAO().insertAll(dbData.getArtists());
        setProgressAsync(createDataWithMessage(R.string.notification_load_artifacts));
        dbManager.getDatabase().artifactDAO().insertAll(dbData.getArtifacts());
        setProgressAsync(createDataWithMessage(R.string.notification_load_comp));
        dbManager.getDatabase().compositionDAO().insertAll(dbData.getCompositions());

        setProgressAsync(createDataWithMessage(R.string.notification_successfully_completed));
        return Result.success();
    }

    private Data createDataWithMessage(String message) {
        return new Data.Builder().putString(PARAM_NAME_MESSAGE, message).build();
    }

    private Data createDataWithMessage(int resId) {
        return new Data.Builder().putString(PARAM_NAME_MESSAGE, getApplicationContext().getString(resId)).build();
    }
}
