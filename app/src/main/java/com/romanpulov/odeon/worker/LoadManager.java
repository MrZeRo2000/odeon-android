package com.romanpulov.odeon.worker;

import android.content.Context;
import android.net.Uri;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;

public class LoadManager {
    public static final String WORK_TAG_DOWNLOAD = "work_tag_download";
    public static final String WORK_NAME_DOWNLOAD = "work_name_download";

    public static Operation startDownloadFromUri(Context context, Uri uri) {
        Data inputData = new Data.Builder()
                .putString(DownloadWorker.PARAM_NAME_URI, uri.toString())
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

        return WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME_DOWNLOAD, ExistingWorkPolicy.REPLACE, request);
    }
}
