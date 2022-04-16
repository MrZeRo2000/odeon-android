package com.romanpulov.odeon;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadViewModel = new ViewModelProvider(requireActivity()).get(LoadViewModel.class);

        mLoadViewModel.getLoadProgress().observe(getViewLifecycleOwner(), loadProgress ->  {
            LoadViewModel.LoadStep downloadLoadStep = loadProgress.getLoadSteps().get(LoadViewModel.StepType.DOWNLOAD);
            if (downloadLoadStep != null) {
                mBinding.downloadProgressBar.setMax((int)downloadLoadStep.params.getLong(LoadViewModel.PARAM_NAME_MAX_VALUE, 0));
                mBinding.downloadProgressBar.setProgress((int)downloadLoadStep.params.getLong(LoadViewModel.PARAM_NAME_VALUE, 0));
            }

            mBinding.cancelButton.setVisibility(loadProgress.isRunning() ? View.VISIBLE : View.GONE);
            mBinding.cancelProcessButton.setVisibility(loadProgress.isRunning() ? View.GONE : View.VISIBLE);
            mBinding.passwordTextField.setVisibility(loadProgress.isPasswordRequired()? View.VISIBLE : View.GONE);
        });

        mBinding.cancelButton.setOnClickListener(v -> {
            LoadManager.cancelAll(requireContext());
        });

        mBinding.cancelProcessButton.setOnClickListener(v -> {
            mLoadViewModel.getLoadProgress().setValue(new LoadViewModel.LoadProgress());
            NavHostFragment.findNavController(this).navigate(R.id.artistsFragment);
        });

        mBinding.passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                LoadManager.startProcessWithPassword(requireContext(), v.getText().toString());
            }
            return true;
        });
    }
}