package com.romanpulov.odeon;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.romanpulov.odeon.databinding.ArtifactsRecyclerViewItemBinding;
import com.romanpulov.odeon.db.Artifact;
import com.romanpulov.odeon.db.DBManager;

import java.util.HashMap;
import java.util.Map;

public class ArtifactsRecyclerViewAdapter extends ListAdapter<Artifact, ArtifactsRecyclerViewAdapter.ViewHolder> {

    private static final Map<Integer, Integer> IMAGE_RESOURCES = new HashMap<>();

    static {
        IMAGE_RESOURCES.put(DBManager.ARTIFACT_TYPE_ID_MUSIC_MP3, R.drawable.ic_artifact_mp3);
        IMAGE_RESOURCES.put(DBManager.ARTIFACT_TYPE_ID_MUSIC_LA, R.drawable.ic_artifact_la);
    }

    private static final int DEFAULT_IMAGE_RESOURCE = android.R.drawable.ic_menu_search;

    private static void log(String message) {
        Log.d(ArtifactsRecyclerViewAdapter.class.getSimpleName(), message);
    }

    private static class ArtifactDiffCallback extends DiffUtil.ItemCallback<Artifact> {
        @Override
        public boolean areItemsTheSame(@NonNull Artifact oldItem, @NonNull Artifact newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Artifact oldItem, @NonNull Artifact newItem) {
            return oldItem.getArtistId().equals(newItem.getArtistId()) &&
                    oldItem.getName().equals(newItem.getName()) &&
                    ((oldItem.getYear() == null ? 0 : (int)oldItem.getYear()) ==
                            (newItem.getYear() == null ? 0 : (int)newItem.getYear()));
        }
    }



    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ArtifactsRecyclerViewItemBinding mBinding;

        public ViewHolder(@NonNull ArtifactsRecyclerViewItemBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setOnClickListener(v -> {
                if (v.getContext() instanceof AppCompatActivity) {
                    AppCompatActivity a = (AppCompatActivity)v.getContext();
                    ArtistsViewModel viewModel = new ViewModelProvider(a).get(ArtistsViewModel.class);
                    viewModel.getSelectedArtifactId().postValue(getAdapterPosition());
                }
            });

            mBinding = binding;
        }
    }

    public ArtifactsRecyclerViewAdapter() {
        super(new ArtifactDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ArtifactsRecyclerViewItemBinding binding = ArtifactsRecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artifact artifact = getCurrentList().get(position);

        // title
        holder.mBinding.titleTextView.setText(artifact.getName());

        // year
        if (artifact.getYear() == null) {
            holder.mBinding.yearTextView.setVisibility(View.INVISIBLE);
        } else {
            holder.mBinding.yearTextView.setVisibility(View.VISIBLE);
            holder.mBinding.yearTextView.setText(String.valueOf(artifact.getYear()));
        }

        // artifact type image
        Integer resId = IMAGE_RESOURCES.get(artifact.getArtifactTypeId());
        if (resId == null) {
            holder.mBinding.artifactTypeImageView.setImageResource(DEFAULT_IMAGE_RESOURCE);
        } else {
            holder.mBinding.artifactTypeImageView.setImageResource(resId);
        }
    }
}
