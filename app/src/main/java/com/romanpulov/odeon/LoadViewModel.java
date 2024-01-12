package com.romanpulov.odeon;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoadViewModel extends ViewModel {
    public enum LoadType {
        RAR,
        SQLITE,
        UNKNOWN;

        private static final String STRING_RAR = "rar";
        private static final String STRING_SQLITE = "db";

        public static LoadType fromString(String s) {
            if (s.equals(STRING_RAR)) {
                return LoadType.RAR;
            } else if (s.equals(STRING_SQLITE)) {
                return LoadType.SQLITE;
            } else {
                return LoadType.UNKNOWN;
            }
        }

        public static String getExtension(LoadType loadType) {
            switch (loadType) {
                case RAR:
                    return STRING_RAR;
                case SQLITE:
                    return STRING_SQLITE;
                default:
                    return "unknown";
            }
        }
    }

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

    private String mFileName;

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String mFileName) {
        this.mFileName = mFileName;
        String extension = mFileName.substring(mFileName.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
        setLoadType(LoadType.fromString(extension));
    }

    private LoadType mLoadType = LoadType.UNKNOWN;

    public LoadType getLoadType() {
        return mLoadType;
    }

    public void setLoadType(LoadType mLoadType) {
        this.mLoadType = mLoadType;
    }

    public String getFileExtension() {
        return LoadType.getExtension(mLoadType);
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