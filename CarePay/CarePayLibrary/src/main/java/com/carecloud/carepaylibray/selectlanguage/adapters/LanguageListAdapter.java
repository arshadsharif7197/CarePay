package com.carecloud.carepaylibray.selectlanguage.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.selectlanguage.models.LanguageOptionModel;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;

import java.util.List;

/**
 * Created Adapter and view holder for recycler view.
 */

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.ViewHolder> {

    List<LanguageOptionModel> languageListLanguageOptionModels;
    private OnItemClickListener itemClickListener;
    Context context;

    RadioButton selectedLanguage;

    public LanguageListAdapter(List<LanguageOptionModel> LanguageListLanguageOptionModels, OnItemClickListener itemClickListener, Context mContext) {
        this.languageListLanguageOptionModels = LanguageListLanguageOptionModels;
        this.itemClickListener = itemClickListener;
        this.context = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.languages_row_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LanguageOptionModel languageSelected = languageListLanguageOptionModels.get(position);
        String languageName = languageSelected.getValue();
        holder.languageNameRadioButton.setText(languageName);
        if (ApplicationPreferences.Instance.getUserLanguage().equals(languageName)) {
            selectedLanguage =holder.languageNameRadioButton;
            selectedLanguage.performClick();
        }
    }

    /**
     * @return number of languages
     */
    @Override
    public int getItemCount() {
        return languageListLanguageOptionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //TextView textViewLanguageName;
        CardView cardView;
        //ImageView radioImageLanguageSelect;
        RadioButton languageNameRadioButton;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            languageNameRadioButton = (RadioButton) itemView.findViewById(R.id.languageNameRadioButton);
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/proximanova_regular.otf");
            languageNameRadioButton.setTypeface(typeface);
            languageNameRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RadioButton radioButton= (RadioButton) view;
                    if(selectedLanguage !=null){
                        selectedLanguage.setChecked(false);
                        selectedLanguage.setTextColor(ContextCompat.getColor(context, R.color.light_gray));
                    }
                    selectedLanguage = radioButton;
                    selectedLanguage.setChecked(true);
                    selectedLanguage.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    if (itemClickListener != null) {
                        itemClickListener.onLanguageChange(selectedLanguage.getText().toString());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onLanguageChange(String selectedLanguage);
    }
}
