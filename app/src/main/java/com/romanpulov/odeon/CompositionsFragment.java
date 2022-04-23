package com.romanpulov.odeon;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.romanpulov.odeon.databinding.CompositionsFragmentBinding;
import com.romanpulov.odeon.db.Composition;

public class CompositionsFragment extends Fragment {

    private static void log(String message) {
        Log.d(CompositionsFragment.class.getSimpleName(), message);
    }

    private CompositionsFragmentBinding mBinding;
    private CompositionsRecyclerViewAdapter mAdapter;

    public CompositionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = CompositionsFragmentBinding.inflate(getLayoutInflater(), container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArtistsViewModel mViewModel = new ViewModelProvider(requireActivity()).get(ArtistsViewModel.class);

        // set actionbar title
        AppCompatActivity activity = (AppCompatActivity)requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mViewModel.getSelectedArtifact().getName() +
                    (mViewModel.getSelectedArtifact().getYear() == null ? "" :
                            " " + mViewModel.getSelectedArtifact().getYear())
                    );
        }

        // listview
        mAdapter = new CompositionsRecyclerViewAdapter();
        mBinding.compositionsRecyclerView.setAdapter(mAdapter);
        mBinding.compositionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        mBinding.compositionsRecyclerView.addItemDecoration(dividerItemDecoration);

        mViewModel.getCompositions().observe(getViewLifecycleOwner(), compositions -> {
            if (compositions != null) {
                log("Got compositions:" + compositions.size());
                boolean diskVisible = compositions.stream().map(Composition::getDiskNumber).distinct().count() > 1;
                mBinding.diskNumberHeaderTextView.setVisibility(diskVisible ? View.VISIBLE : View.GONE
                );

                mAdapter.setDiskVisible(diskVisible);
                mAdapter.submitList(compositions);
            }
        });

    }
}