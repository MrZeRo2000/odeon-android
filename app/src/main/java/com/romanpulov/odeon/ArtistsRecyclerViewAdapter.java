package com.romanpulov.odeon;

import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.romanpulov.odeon.databinding.ArtistsRecyclerViewItemBinding;
import com.romanpulov.odeon.db.Artist;

import java.util.Locale;

public class ArtistsRecyclerViewAdapter extends ListAdapter<Artist, ArtistsRecyclerViewAdapter.ViewHolder> {

    private static void log(String message) {
        Log.d(ArtistsRecyclerViewAdapter.class.getSimpleName(), message);
    }

    private int mHighlightedPosition = -1;
    private int mPrevHighlightedPosition = -1;

    public void setHighlightedPosition(int highlightedPosition) {
        this.mPrevHighlightedPosition = this.mHighlightedPosition;
        this.mHighlightedPosition = highlightedPosition;
    }

    public void notifyHightlightedItemsChanged() {
        if (mPrevHighlightedPosition != -1) {
            log("Notifying previous:" + mPrevHighlightedPosition);
            this.notifyItemChanged(mPrevHighlightedPosition);
        }
        if ((mHighlightedPosition != -1) && (mPrevHighlightedPosition != mHighlightedPosition)) {
            log("Notifying current:" + mHighlightedPosition);
            this.notifyItemChanged(mHighlightedPosition);
        }
    }

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

        if (position == mHighlightedPosition) {
            holder.mBinding.titleTextView.setTypeface(null, Typeface.BOLD);
        } else {
            holder.mBinding.titleTextView.setTypeface(null, Typeface.NORMAL);
        }
        /*

        TypedValue outValue = new TypedValue();
        holder.mBinding.titleTextView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);

        if (position == mHighlightedPosition) {
            holder.mBinding.getRoot().setBackgroundResource(R.color.cyan_light);
        } else {
            holder.mBinding.getRoot().setBackgroundResource(outValue.resourceId);
        }
        *
         */
    }

    public int findNearestAdapterPosition(@NonNull String searchText) {
        String searchTextLower = searchText.toLowerCase(Locale.ROOT);

        int position = 0;
        for (Artist artist: getCurrentList()) {
            if (artist.getName().toLowerCase(Locale.ROOT).contains(searchTextLower)) {
                return position;
            }
            position ++;
        }

        return -1;
    }
}
