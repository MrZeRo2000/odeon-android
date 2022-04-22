package com.romanpulov.odeon;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.romanpulov.odeon.databinding.ArtifactsFragmentBinding;

public class ArtifactsFragment extends Fragment {

    private static void log(String message) {
        Log.d(ArtifactsFragment.class.getSimpleName(), message);
    }

    private ArtifactsFragmentBinding mBinding;
    private ArtifactsRecyclerViewAdapter mAdapter;
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

        // listview
        mAdapter = new ArtifactsRecyclerViewAdapter();
        mBinding.artifactsRecyclerView.setAdapter(mAdapter);
        mBinding.artifactsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        mBinding.artifactsRecyclerView.addItemDecoration(dividerItemDecoration);

        mViewModel.getArtifacts().observe(getViewLifecycleOwner(), artifacts -> {
            if (artifacts != null) {
                log("Got artifacts:" + artifacts.size());
                mAdapter.submitList(artifacts);
            }
        });

        mViewModel.getSelectedArtifactIndex().observe(getViewLifecycleOwner(), artifactIndex -> {
            if (artifactIndex != null) {
                mViewModel.getSelectedArtifactIndex().postValue(null);
                if (mViewModel.getCompositions() != null) {
                    mViewModel.getCompositions().postValue(null);
                }

                mViewModel.setSelectedArtifact(mAdapter.getCurrentList().get(artifactIndex));
                mViewModel.loadCompositions();
                NavHostFragment.findNavController(this).navigate(R.id.action_artifactsFragment_to_compositionsFragment);
            }
        });

    }
}