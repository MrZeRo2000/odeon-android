package com.romanpulov.odeon;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.romanpulov.odeon.databinding.ArtifactsFragmentBinding;

public class ArtifactsFragment extends Fragment {

    private static void log(String message) {
        Log.d(ArtifactsFragment.class.getSimpleName(), message);
    }

    ArtifactsFragmentBinding mBinding;
    private ArtistsViewModel mViewModel;

    public ArtifactsFragment() {
        // Required empty public constructor
    }

    public static ArtifactsFragment newInstance() {
        return new ArtifactsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = ArtifactsFragmentBinding.inflate(getLayoutInflater(), container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(ArtistsViewModel.class);

        // set actionbar title
        AppCompatActivity activity = (AppCompatActivity)requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mViewModel.getSelectedArtist().getName());
        }

        mViewModel.getArtifacts().observe(getViewLifecycleOwner(), artifacts -> {
            if (artifacts != null) {
                log("Got artifacts:" + artifacts.size());
            }
        });

    }
}