package com.romanpulov.odeon;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.romanpulov.odeon.databinding.ArtistsFragmentBinding;
import com.romanpulov.odeon.db.Artist;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ArtistsFragment extends Fragment {

    private ExecutorService mExecutorService;

    private static void log(String message) {
        Log.d(ArtistsFragment.class.getSimpleName(), message);
    }

    private ArtistsViewModel mViewModel;
    private ArtistsFragmentBinding mBinding;
    ArtistsRecyclerViewAdapter mAdapter;

    public static ArtistsFragment newInstance() {
        return new ArtistsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.searchAction).getActionView();

        searchView.setOnCloseListener(() -> {
            mAdapter.setHighlightedPosition(-1);
            mAdapter.notifyHightlightedItemsChanged();
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchView.setIconified(true);
                mAdapter.setHighlightedPosition(-1);
                mAdapter.notifyHightlightedItemsChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mExecutorService != null) {
                    mExecutorService.shutdownNow();
                    try {
                        if (!mExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                            log("Error terminating");
                        }
                    } catch (InterruptedException e) {
                        mExecutorService.shutdownNow();
                        Thread.currentThread().interrupt();
                    }
                }
                mExecutorService = Executors.newSingleThreadExecutor();

                if (newText.length() > 2) {
                    log("Text changed:" + newText);
                    mExecutorService.submit(() -> {
                       try {
                           Thread.sleep(500);
                            log("Ready to work with:" + newText);
                           int position = mAdapter.findNearestAdapterPosition(newText);
                           if (position > -1) {
                               log("Found something:" + position);
                               requireActivity().runOnUiThread(() -> {
                                   mBinding.artistsRecyclerView.scrollToPosition(position);
                                   mAdapter.setHighlightedPosition(position);
                                   mAdapter.notifyHightlightedItemsChanged();
                               });
                           } else {
                               log("Nothing found");
                               mAdapter.setHighlightedPosition(-1);
                               requireActivity().runOnUiThread(mAdapter::notifyHightlightedItemsChanged);
                           }

                       } catch (InterruptedException e) {
                           log("Thread task interrupted");
                       }
                    });
                    mExecutorService.shutdown();
                }
                return false;
            }
        });
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

        mAdapter = new ArtistsRecyclerViewAdapter(new DiffUtil.ItemCallback<Artist>() {
            @Override
            public boolean areItemsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Artist oldItem, @NonNull Artist newItem) {
                return oldItem.getName().equals(newItem.getName());
            }
        });

        mBinding.artistsRecyclerView.setAdapter(mAdapter);
        mBinding.artistsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        mViewModel = new ViewModelProvider(this).get(ArtistsViewModel.class);

        mViewModel.getArtists().observe(getViewLifecycleOwner(), artists -> {
            log("Got some artists:" + artists.size());
            mAdapter.submitList(artists);
        });
    }
}