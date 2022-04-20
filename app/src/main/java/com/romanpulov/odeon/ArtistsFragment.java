package com.romanpulov.odeon;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.romanpulov.odeon.databinding.ArtistsFragmentBinding;
import com.romanpulov.odeon.db.Artist;

public class ArtistsFragment extends Fragment {

    private static void log(String message) {
        Log.d(ArtistsFragment.class.getSimpleName(), message);
    }

    private ArtistsViewModel mViewModel;
    private ArtistsFragmentBinding mBinding;

    public static ArtistsFragment newInstance() {
        return new ArtistsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = ArtistsFragmentBinding.inflate(getLayoutInflater(), container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ArtistsRecyclerViewAdapter adapter = new ArtistsRecyclerViewAdapter(new DiffUtil.ItemCallback<Artist>() {
            @Override
            public boolean areItemsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
                return oldItem.getName().equals(newItem.getName());
            }
        });

        mBinding.artistsRecyclerView.setAdapter(adapter);
        mBinding.artistsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        mViewModel = new ViewModelProvider(this).get(ArtistsViewModel.class);

        mViewModel.getArtists().observe(getViewLifecycleOwner(), artists -> {
            log("Got some artists:" + artists.size());
            adapter.submitList(artists);
        });
    }

    /*
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ArtistsViewModel.class);
     }

     */

}