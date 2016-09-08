package com.carecloud.carepaylibray.select_language.language_adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.select_language.language_model.OptionModel;
import com.carecloud.carepaylibray.utils.StringFunctions;

import java.util.List;


public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.ViewHolder> {

    List<OptionModel> mLanguageListOptionModels;
    private OnItemClickListener itemClickListener;
    Context mContext;

    public LanguageListAdapter(List<OptionModel> mLanguageListOptionModels, OnItemClickListener itemClickListener, Context mContext) {
        this.mLanguageListOptionModels = mLanguageListOptionModels;
        this.itemClickListener = itemClickListener;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.languagess_row_view, parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OptionModel mLanguage = mLanguageListOptionModels.get(position);
        if(!StringFunctions.isNullOrEmpty(mLanguage.getValue())){
            holder.mTextView.setText(mLanguage.getValue());
            if(mLanguage.isChecked()){
                holder.mTextView.setTextColor(Color.parseColor("#1f9bde"));
            }
        }
        holder.mRadioButton.setChecked(mLanguage.isChecked());
        // holder.mImageView.setImageResource(R.drawable.us);
        //   holder.mImageView.setImageResource(mContext.getResources().getIdentifier(mLanguage.getLabel(), "drawable", mContext.getPackageName()));
    }
    @Override
    public int getItemCount() {
        return mLanguageListOptionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mImageView;
        CardView mCardView;
        RadioButton mRadioButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.language_name);
            //  mImageView = (ImageView) itemView.findViewById(R.id.icon_image);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            mRadioButton = (RadioButton)itemView.findViewById(R.id.radio_btn);

            mCardView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(itemClickListener!=null)
                    {
                        itemClickListener.onItemClick(view, getAdapterPosition(), mLanguageListOptionModels.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position, OptionModel mLanguage);
    }
}
