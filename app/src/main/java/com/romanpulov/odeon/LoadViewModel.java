package com.romanpulov.odeon;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class LoadViewModel extends ViewModel {

    public enum LoadStatus {
        COMPLETED,
        RUNNING,
        WAITING,
        CANCELLED,
        ERROR
    }

    public enum StepType {
        DOWNLOAD,
        PASSWORD_REQUEST,
        PROCESS
    }

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

        public boolean isEmpty() {
            return mLoadSteps.isEmpty();
        }

        public boolean isRunning() {
            return mLoadSteps.values().stream().anyMatch(p -> p.status == LoadStatus.RUNNING);
        }

        public boolean isPasswordRequired() {
            LoadStep passwordLoadStep = mLoadSteps.get(StepType.PASSWORD_REQUEST);
            return passwordLoadStep !=null && passwordLoadStep.status == LoadStatus.WAITING;
        }

        public boolean isProcessRunning() {
            LoadStep processLoadStep = mLoadSteps.get(StepType.PROCESS);
            return processLoadStep != null && processLoadStep.status == LoadStatus.RUNNING;
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