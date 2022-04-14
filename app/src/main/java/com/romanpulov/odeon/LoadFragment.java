package com.romanpulov.odeon;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.romanpulov.odeon.databinding.LoadFragmentBinding;
import com.romanpulov.odeon.worker.DownloadWorker;
import com.romanpulov.odeon.worker.LoadManager;

public class LoadFragment extends Fragment {

    private static void log(String message) {
        Log.d(LoadFragment.class.getSimpleName(), message);
    }

    private LoadViewModel mLoadViewModel;
    public LoadFragmentBinding mBinding;

    public static LoadFragment newInstance() {
        return new LoadFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = LoadFragmentBinding.inflate(getLayoutInflater(), container, false);
        return mBinding.getRoot();
        //return inflater.inflate(R.layout.load_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadViewModel = new ViewModelProvider(requireActivity()).get(LoadViewModel.class);

        WorkManager
                .getInstance(requireContext())
                .getWorkInfosByTagLiveData(LoadManager.WORK_TAG_DOWNLOAD)
                .observe(getViewLifecycleOwner(), workInfos -> {
                    log("WorkInfos:" + workInfos);
                    if (workInfos.size() > 0) {
                        WorkInfo workInfo = workInfos.get(0);
                        if (workInfo.getState() == WorkInfo.State.RUNNING) {
                            long currentValue = workInfo.getProgress().getLong(DownloadWorker.PARAM_NAME_PROGRESS_CURRENT, 0);
                            long totalValue = workInfo.getProgress().getLong(DownloadWorker.PARAM_NAME_PROGRESS_TOTAL, 0);
                            if (totalValue > 0) {
                                log("Updating progress with Max = " + totalValue + ", Progress = " + currentValue);
                                mBinding.downloadProgressBar.setMax((int) totalValue);
                                mBinding.downloadProgressBar.setProgress((int) currentValue);
                            }
                        } else if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            int max = mBinding.downloadProgressBar.getMax();
                            log("Updating progress with Max = " + max);
                            mBinding.downloadProgressBar.setProgress(max);
                        }
                    }
                });
    }
}