package com.carecloud.carepaylibray.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.models.OptionModel;

import java.util.List;


public class HomeViewAdapter extends RecyclerView.Adapter<HomeViewAdapter.ViewHolder> {
    List<OptionModel> mHomeListOptionModels;
    private OnItemClickListener itemClickListener;
    Context mContext;


    DisplayMetrics displayMetrics ;



    public HomeViewAdapter(List<OptionModel> mHomeListOptionModels, OnItemClickListener itemClickListener, Context mContext) {
        this.mHomeListOptionModels = mHomeListOptionModels;
        this.itemClickListener = itemClickListener;
        this.mContext = mContext;

        displayMetrics=mContext.getResources().getDisplayMetrics();


    }

    public int dpToPx(int dp) {
        return Math.round(dp * (displayMetrics.ydpi / displayMetrics.densityDpi));
    }

    public int pxToDp(int px) {
        return Math.round(px / (displayMetrics.ydpi / displayMetrics.densityDpi));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = (LayoutInflater.from(parent.getContext())).inflate(R.layout.home_grid,parent,false);
        int pxForReduce=dpToPx(400);

        int px=(displayMetrics.heightPixels-pxForReduce)/3;

        int dp = pxToDp(px);

        view.getLayoutParams().height=dp;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        OptionModel mOptionModel = mHomeListOptionModels.get(position);
        holder.mTextView.setText(mOptionModel.getValue());
    }

    @Override
    public int getItemCount() {
        return mHomeListOptionModels.size();
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
        void onItemClick(View view, int position, OptionModel mLanguage);
    }
}
