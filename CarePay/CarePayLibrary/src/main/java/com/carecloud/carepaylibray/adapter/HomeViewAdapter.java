package com.carecloud.carepaylibray.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.models.Option;

import java.util.List;


public class HomeViewAdapter extends RecyclerView.Adapter<HomeViewAdapter.ViewHolder> {
    List<Option> mHomeListOptions;
    private OnItemClickListener itemClickListener;
    Context mContext;

    public HomeViewAdapter(List<Option> mHomeListOptions, OnItemClickListener itemClickListener, Context mContext) {
        this.mHomeListOptions = mHomeListOptions;
        this.itemClickListener = itemClickListener;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = (LayoutInflater.from(parent.getContext())).inflate(R.layout.home_grid,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Option mOption = mHomeListOptions.get(position);
        holder.mTextView.setText(mOption.getValue());

    }

    @Override
    public int getItemCount() {
        return mHomeListOptions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mImageView;
        CardView mCardView;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.home_name);
            mImageView = (ImageView) itemView.findViewById(R.id.home_image);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, Option mLanguage);
    }
}
