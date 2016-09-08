package com.carecloud.carepaylibray.selectlanguage.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.selectlanguage.models.LanguageOptionModel;
import com.carecloud.carepaylibray.utils.StringFunctions;

import java.util.List;


public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.ViewHolder> {

    List<LanguageOptionModel> mLanguageListLanguageOptionModels;
    private OnItemClickListener itemClickListener;
    Context mContext;

    public LanguageListAdapter(List<LanguageOptionModel> mLanguageListLanguageOptionModels, OnItemClickListener itemClickListener, Context mContext) {
        this.mLanguageListLanguageOptionModels = mLanguageListLanguageOptionModels;
        this.itemClickListener = itemClickListener;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.languages_row_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LanguageOptionModel mLanguage = mLanguageListLanguageOptionModels.get(position);
        if (!StringFunctions.isNullOrEmpty(mLanguage.getValue())) {
            holder.mLanguageName.setText(mLanguage.getValue());
            if (mLanguage.isChecked()) {
                holder.mLanguageName.setTextColor(R.color.colorPrimary);
            }
        }
        holder.mRadioButton.setChecked(mLanguage.isChecked());
    }

    @Override
    public int getItemCount() {
        return mLanguageListLanguageOptionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mLanguageName;
        CardView mCardView;
        RadioButton mRadioButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mLanguageName = (TextView) itemView.findViewById(R.id.languageName);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            mRadioButton = (RadioButton) itemView.findViewById(R.id.languageRadioButton);

            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(view, getAdapterPosition(), mLanguageListLanguageOptionModels.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, LanguageOptionModel mLanguage);
    }
}
