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
    private ArtistsViewModel mViewModel;

    public CompositionsFragment() {
        // Required empty public constructor
    }

    public static CompositionsFragment newInstance(String param1, String param2) {
        return new CompositionsFragment();
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

        mViewModel = new ViewModelProvider(requireActivity()).get(ArtistsViewModel.class);

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
                mBinding.diskNumberHeaderTextView.setVisibility(
                        compositions.stream().map(Composition::getDiskNumber).distinct().count() > 1 ? View.VISIBLE : View.GONE
                );
                mAdapter.submitList(compositions);
            }
        });

    }
}