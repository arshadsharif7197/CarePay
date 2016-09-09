package com.carecloud.carepaylibray.selectlanguage.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import com.carecloud.carepaylibray.utils.Utility;

import java.util.List;

/**
 * Created Adapter and view holder for recycler view.
 */

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
            holder.mTextViewLanguageName.setText(mLanguage.getValue());
            if (mLanguage.isChecked()) {
                holder.mTextViewLanguageName.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                Utility.setTypefaceFromAssets(mContext,"fonts/proximanova_semibold.otf",holder.mTextViewLanguageName);
            }
        }
        holder.mRadioButtonLanguageSelect.setChecked(mLanguage.isChecked());
    }

    /**
     * @return  number of languages
     */
    @Override
    public int getItemCount() {
        return mLanguageListLanguageOptionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewLanguageName;
        CardView mCardView;
        RadioButton mRadioButtonLanguageSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextViewLanguageName = (TextView) itemView.findViewById(R.id.languagename);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            mRadioButtonLanguageSelect = (RadioButton) itemView.findViewById(R.id.languageRadioButton);
            Utility.setTypefaceFromAssets(mContext,"fonts/proximanova_regular.otf", mTextViewLanguageName);
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
