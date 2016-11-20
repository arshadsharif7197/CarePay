package com.carecloud.carepay.patient.selectlanguage.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;


import com.carecloud.carepay.patient.selectlanguage.models.LanguageOptionModel;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

/**
 * Created Adapter and view holder for recycler view.
 */

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.ViewHolder> {

    List<LanguageOptionModel> languageListLanguageOptionModels;
    Context context;
    RadioButton selectedLanguage;
    private  String languageID="";
    private OnItemClickListener itemClickListener;

    /**
     *
     * @param languageListLanguageOptionModels  option model
     * @param itemClickListener onclicklistner
     * @param context context
     */
    public LanguageListAdapter(List<LanguageOptionModel> languageListLanguageOptionModels, OnItemClickListener itemClickListener, Context context) {
        this.languageListLanguageOptionModels = languageListLanguageOptionModels;
        this.itemClickListener = itemClickListener;
        this.context = context;
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

        if (ApplicationPreferences.Instance.getUserLanguage().equals(languageSelected.getLanguageId())) {
            selectedLanguage = holder.languageNameRadioButton;
            selectedLanguage.setChecked(true);
            selectedLanguage.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
    }

    /**
     * @return number of languages
     */
    @Override
    public int getItemCount() {
        return languageListLanguageOptionModels.size();
    }

    public interface OnItemClickListener {
        void onLanguageChange(String selectedLanguage,String languagecode);
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

            languageNameRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RadioButton radioButton = (RadioButton) view;
                    if (selectedLanguage != null) {
                        selectedLanguage.setChecked(false);
                        selectedLanguage.setTextColor(ContextCompat.getColor(context, R.color.slateGray));
                        SystemUtil.setProximaNovaRegularTypeface(context, selectedLanguage);
                    }
                    selectedLanguage = radioButton;
                    selectedLanguage.setChecked(true);
                    selectedLanguage.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    SystemUtil.setProximaNovaSemiboldTypeface(context, selectedLanguage);
                    if (itemClickListener != null) {
                        String selLangValue = selectedLanguage.getText().toString();
                        // search sel lang id
                        languageID = languageListLanguageOptionModels.get(0).getLanguageId();
                        for (LanguageOptionModel langOpt : languageListLanguageOptionModels) {
                            if (langOpt.getValue().equals(selLangValue)) {
                                languageID = langOpt.getLanguageId();
                            }
                        }
                        itemClickListener.onLanguageChange(selLangValue, languageID);

                    }
                }
            });
        }
    }
}
