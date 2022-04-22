package com.romanpulov.odeon;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.romanpulov.odeon.databinding.CompositionsRecyclerViewItemBinding;
import com.romanpulov.odeon.db.Composition;

public class CompositionsRecyclerViewAdapter extends ListAdapter<Composition, CompositionsRecyclerViewAdapter.ViewHolder> {

    private static class CompositionDiffCallback extends DiffUtil.ItemCallback<Composition> {
        @Override
        public boolean areItemsTheSame(@NonNull Composition oldItem, @NonNull Composition newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Composition oldItem, @NonNull Composition newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    ((oldItem.getDiskNumber() == null ? 0 : (int)oldItem.getDiskNumber()) ==
                            (newItem.getDiskNumber() == null ? 0 : (int)newItem.getDiskNumber())) &&
                    ((oldItem.getNumber() == null ? 0 : (int)oldItem.getNumber()) ==
                            (newItem.getNumber() == null ? 0 : (int)newItem.getNumber()))
                    ;
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final CompositionsRecyclerViewItemBinding mBinding;

        public ViewHolder(@NonNull CompositionsRecyclerViewItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    public CompositionsRecyclerViewAdapter() {
        super(new CompositionDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CompositionsRecyclerViewItemBinding binding = CompositionsRecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Composition composition = getCurrentList().get(position);

        // title
        holder.mBinding.titleTextView.setText(composition.getTitle());
    }
}
