package com.carecloud.carepaylibray.adapter;

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
import com.carecloud.carepaylibray.StringFunctions;
import com.carecloud.carepaylibray.models.Option;

import java.util.List;



public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.ViewHolder> {
    Context mContext;
 //   LanguageListAdapter(param1,param2,Context mcontext){this.mcontext = mcontext}
   List<Option> mLanguageListOptions;
    private OnItemClickListener itemClickListener;

    public LanguageListAdapter(List<Option> mLanguageListOptions,
                               OnItemClickListener itemClickListener, Context context) {
        this.mLanguageListOptions = mLanguageListOptions;
        this.itemClickListener = itemClickListener;
        this.mContext = context;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.languagess_row_view, parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Option mLanguage = mLanguageListOptions.get(position);
        if(!StringFunctions.isNullOrEmpty(mLanguage.getValue())){
            holder.mTextView.setText(mLanguage.getValue());
            if(mLanguage.isChecked()){
                holder.mTextView.setTextColor(Color.parseColor("#1f9bde"));
            }
        }
        holder.mRadioButton.setChecked(mLanguage.isChecked());
        holder.mImageView.setImageResource(mContext.getResources().getIdentifier(mLanguage.getLabel(), "drawable", mContext.getPackageName()));

    }
    @Override
    public int getItemCount() {
        return mLanguageListOptions.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
       ImageView mImageView;
        CardView mCardView;
        RadioButton mRadioButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.language_name);
            mImageView = (ImageView) itemView.findViewById(R.id.icon_image);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            mRadioButton = (RadioButton)itemView.findViewById(R.id.radio_btn);

            mCardView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(itemClickListener!=null)
                    {
                        itemClickListener.onItemClick(view, getAdapterPosition(), mLanguageListOptions.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position, Option mLanguage);
    }
}
