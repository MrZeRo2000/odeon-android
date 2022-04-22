package com.romanpulov.odeon;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class LoadViewModel extends ViewModel {

    public enum LoadStatus {
        COMPLETED,
        RUNNING,
        WAITING,
        CANCELLED,
        ERROR
    };

    public enum StepType {
        DOWNLOAD,
        PASSWORD_REQUEST,
        PROCESS
    }

    public static final int LOAD_STATUS_COMPLETED = 0;
    public static final int LOAD_STATUS_RUNNING = 1;
    public static final int LOAD_STATUS_CANCELLED = 2;
    public static final int LOAD_STATUS_ERROR = 3;

    public static final int STEP_TYPE_DOWNLOAD = 1;
    public static final int STEP_TYPE_PASSWORD_REQUEST = 2;
    public static final int STEP_TYPE_PROCESS = 3;

    public static final String PARAM_NAME_VALUE = "param_name_value";
    public static final String PARAM_NAME_MAX_VALUE = "param_name_max_value";
    public static final String PARAM_NAME_PASSWORD = "param_name_password";

    public static class LoadStep {
        public final LoadStatus status;
        public final Bundle params;

        public LoadStep(LoadStatus status, Bundle params) {
            this.status = status;
            this.params = params;
        }
    }

    public static class LoadProgress {
        private final Map<StepType, LoadStep> mLoadSteps = new HashMap<>();

        @NonNull
        public Map<StepType, LoadStep> getLoadSteps() {
            return mLoadSteps;
        }

        public boolean isCompleted() {
            LoadStep lastLoadStep = mLoadSteps.get(StepType.PROCESS);
            if (lastLoadStep == null) {
                return false;
            } else {
                return lastLoadStep.status == LoadStatus.COMPLETED;
            }
        }

        public boolean isEmpty() {
            return mLoadSteps.isEmpty();
        }

        public boolean isRunning() {
            return mLoadSteps.values().stream().anyMatch(p -> p.status == LoadStatus.RUNNING);
        }

        public boolean isPasswordRequired() {
            LoadStep passwordLoadStep = mLoadSteps.get(StepType.PASSWORD_REQUEST);
            return passwordLoadStep !=null && passwordLoadStep.status == LoadStatus.WAITING;
            /*
            LoadStep downloadLoadStep = mLoadSteps.get(StepType.DOWNLOAD);
            LoadStep passwordLoadStep = mLoadSteps.get(StepType.PASSWORD_REQUEST);
            return downloadLoadStep != null && downloadLoadStep.status == LoadStatus.COMPLETED &&
                    (passwordLoadStep == null || passwordLoadStep.status != LoadStatus.COMPLETED);

             */
        }

        public boolean isProcessRunning() {
            LoadStep processLoadStep = mLoadSteps.get(StepType.PROCESS);
            return processLoadStep != null && processLoadStep.status == LoadStatus.RUNNING;
        }

        public boolean processExists() {
            return mLoadSteps.containsKey(StepType.PROCESS);
        }
    }

    private Uri mUri;

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }

    private MutableLiveData<LoadProgress> mLoadProgress;

    @NonNull
    public MutableLiveData<LoadProgress> getLoadProgress() {
        if (mLoadProgress == null) {
            mLoadProgress = new MutableLiveData<>(new LoadProgress());
        }

        return mLoadProgress;
    }

    private MutableLiveData<String> mMessage;

    public MutableLiveData<String> getMessage() {
        if (mMessage == null) {
            mMessage = new MutableLiveData<>();
        }
        return mMessage;
    }
}