package com.romanpulov.odeon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.romanpulov.odeon.databinding.ArtistsRecyclerViewItemBinding;
import com.romanpulov.odeon.db.Artist;

import java.util.List;

public class ArtistsRecyclerViewAdapter extends ListAdapter<Artist, ArtistsRecyclerViewAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ArtistsRecyclerViewItemBinding mBinding;

        public ViewHolder(@NonNull ArtistsRecyclerViewItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    public ArtistsRecyclerViewAdapter(@NonNull DiffUtil.ItemCallback<Artist> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ArtistsRecyclerViewItemBinding binding = ArtistsRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mBinding.titleTextView.setText(getItem(position).getName());
    }
}
