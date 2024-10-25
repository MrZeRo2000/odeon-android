package com.romanpulov.odeon.worker;

import android.content.Context;
import android.net.Uri;
import androidx.work.*;

public class LoadManager {
    public static final String WORK_TAG_DOWNLOAD = "work_tag_download";
    public static final String WORK_NAME_DOWNLOAD = "work_name_download";
    public static final String WORK_TAG_PROCESS = "work_tag_process";
    public static final String WORK_NAME_PROCESS = "work_name_process";

    public static void startDownloadFromUri(Context context, Uri uri, long size, String extension) {
        Data inputData = new Data.Builder()
                .putString(DownloadWorker.PARAM_NAME_URI, uri.toString())
                .putLong(DownloadWorker.PARAM_NAME_SIZE, size)
                .putString(DownloadWorker.PARAM_NAME_EXTENSION, extension)
                .build();

        Constraints constraints = (new Constraints.Builder())
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .addTag(WORK_TAG_DOWNLOAD)
                .setInputData(inputData)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME_DOWNLOAD, ExistingWorkPolicy.REPLACE, request);
    }

    public static void startProcessWithPassword(Context context, String extension, String password) {
        Data inputData = new Data.Builder()
                .putString(ProcessWorker.PARAM_NAME_EXTENSION, extension)
                .putString(ProcessWorker.PARAM_NAME_PASSWORD, password)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(ProcessWorker.class)
                .addTag(WORK_TAG_PROCESS)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME_PROCESS, ExistingWorkPolicy.REPLACE, request);
    }

    public static void cancelAll(Context context) {
        WorkManager.getInstance(context).cancelAllWork();
    }
}
