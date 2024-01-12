package com.romanpulov.odeon;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.romanpulov.odeon.databinding.LoadFragmentBinding;
import com.romanpulov.odeon.worker.LoadManager;

public class LoadFragment extends Fragment {

    private static void log(String message) {
        Log.d(LoadFragment.class.getSimpleName(), message);
    }

    private LoadViewModel mLoadViewModel;
    public LoadFragmentBinding mBinding;

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
            if (loadProgress.isEmpty()) {
                //NavHostFragment.findNavController(this).navigate(R.id.artistsFragment);
                NavHostFragment.findNavController(this).navigateUp();
            } else {
                LoadViewModel.LoadStep downloadLoadStep = loadProgress.getLoadSteps().get(LoadViewModel.StepType.DOWNLOAD);
                if (downloadLoadStep != null) {
                    mBinding.downloadProgressBar.setMax((int) downloadLoadStep.params.getLong(LoadViewModel.PARAM_NAME_MAX_VALUE, 0));
                    mBinding.downloadProgressBar.setProgress((int) downloadLoadStep.params.getLong(LoadViewModel.PARAM_NAME_VALUE, 0));
                }

                mBinding.cancelButton.setVisibility(loadProgress.isRunning() ? View.VISIBLE : View.GONE);
                mBinding.cancelProcessButton.setVisibility(loadProgress.isRunning() ? View.GONE : View.VISIBLE);
                mBinding.passwordTextField.setVisibility(loadProgress.isPasswordRequired() ? View.VISIBLE : View.GONE);
                mBinding.progressTextView.setVisibility(loadProgress.isProcessRunning() ? View.VISIBLE : View.GONE);
            }
        });

        mLoadViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            log("Observing message:" + message);
            if (mBinding.progressTextView.getVisibility() == View.VISIBLE) {
                log("Displaying message");
                mBinding.progressTextView.setText(message);
            }
        });

        mBinding.cancelButton.setOnClickListener(v -> LoadManager.cancelAll(requireContext()));

        mBinding.cancelProcessButton.setOnClickListener(v -> {
            mLoadViewModel.getLoadProgress().setValue(new LoadViewModel.LoadProgress());
            //NavHostFragment.findNavController(this).navigate(R.id.artistsFragment);
            NavHostFragment.findNavController(this).navigateUp();
        });

        mBinding.passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                LoadViewModel.LoadProgress loadProgress = mLoadViewModel.getLoadProgress().getValue();
                if (loadProgress != null) {
                    LoadViewModel.LoadStep passwordRequestLoadStep = new LoadViewModel.LoadStep(LoadViewModel.LoadStatus.COMPLETED, null);
                    loadProgress.getLoadSteps().put(LoadViewModel.StepType.PASSWORD_REQUEST, passwordRequestLoadStep);
                    loadProgress.getLoadSteps().put(LoadViewModel.StepType.PROCESS, new LoadViewModel.LoadStep(LoadViewModel.LoadStatus.RUNNING, null));
                    mLoadViewModel.getLoadProgress().postValue(loadProgress);
                    LoadManager.startProcessWithPassword(requireContext(), mLoadViewModel.getFileExtension(), v.getText().toString());
                }
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            return true;
        });
    }
}