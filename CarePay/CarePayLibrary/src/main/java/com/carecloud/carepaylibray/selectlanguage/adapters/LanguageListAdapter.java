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

    List<LanguageOptionModel> LanguageListLanguageOptionModels;
    private OnItemClickListener itemClickListener;
    Context Context;

    public LanguageListAdapter(List<LanguageOptionModel> mLanguageListLanguageOptionModels, OnItemClickListener itemClickListener, Context mContext) {
        this.LanguageListLanguageOptionModels = mLanguageListLanguageOptionModels;
        this.itemClickListener = itemClickListener;
        this.Context = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.languages_row_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LanguageOptionModel mLanguage = LanguageListLanguageOptionModels.get(position);
        if (!StringFunctions.isNullOrEmpty(mLanguage.getValue())) {
            holder.TextViewLanguageName.setText(mLanguage.getValue());
            if (mLanguage.isChecked()) {
                holder.TextViewLanguageName.setTextColor(ContextCompat.getColor(Context, R.color.colorPrimary));
                Utility.setTypefaceFromAssets(Context,"fonts/proximanova_semibold.otf",holder.TextViewLanguageName);
            }
        }
        holder.RadioButtonLanguageSelect.setChecked(mLanguage.isChecked());
    }

    /**
     * @return  number of languages
     */
    @Override
    public int getItemCount() {
        return LanguageListLanguageOptionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView TextViewLanguageName;
        CardView CardView;
        RadioButton RadioButtonLanguageSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            TextViewLanguageName = (TextView) itemView.findViewById(R.id.languagename);
            CardView = (CardView) itemView.findViewById(R.id.cardView);
            RadioButtonLanguageSelect = (RadioButton) itemView.findViewById(R.id.languageRadioButton);
            Utility.setTypefaceFromAssets(Context,"fonts/proximanova_regular.otf", TextViewLanguageName);
            CardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(view, getAdapterPosition(), LanguageListLanguageOptionModels.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, LanguageOptionModel mLanguage);
    }
}
