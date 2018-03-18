package com.carecloud.carepay.patient.selectlanguage.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;

import java.util.List;

/**
 * Created Adapter and view holder for recycler view.
 */

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.ViewHolder> {

    private List<OptionDTO> languageListLanguageOptionModels;
    private RadioButton selectedLanguage;
    private OnItemClickListener itemClickListener;

    /**
     * @param languageListLanguageOptionModels option model
     * @param itemClickListener                onclicklistner
     */
    public LanguageListAdapter(List<OptionDTO> languageListLanguageOptionModels,
                               OnItemClickListener itemClickListener) {
        this.languageListLanguageOptionModels = languageListLanguageOptionModels;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.languages_row_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OptionDTO languageSelected = languageListLanguageOptionModels.get(position);
        String languageName = languageSelected.getLabel();
        holder.languageNameRadioButton.setText(languageName);
        holder.languageNameRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.languageNameRadioButton.equals(selectedLanguage)) {
                    selectedLanguage.setTextColor(ContextCompat.getColor(selectedLanguage.getContext(), R.color.slateGray));
                    selectedLanguage.setChecked(false);
                    holder.languageNameRadioButton.setTextColor(ContextCompat
                            .getColor(holder.languageNameRadioButton.getContext(), R.color.colorPrimary));
                    holder.languageNameRadioButton.setChecked(true);
                    selectedLanguage = holder.languageNameRadioButton;
                    itemClickListener.onLanguageSelected(languageSelected);

                }
            }
        });
        if (holder.languageNameRadioButton.isChecked()) {
            holder.languageNameRadioButton.setTextColor(ContextCompat
                    .getColor(holder.languageNameRadioButton.getContext(), R.color.colorPrimary));
        } else {
            holder.languageNameRadioButton.setTextColor(ContextCompat
                    .getColor(holder.languageNameRadioButton.getContext(), R.color.slateGray));
        }

        if (((ISession) holder.languageNameRadioButton.getContext()).getApplicationPreferences()
                .getUserLanguage().equals(languageSelected.getCode())) {
            selectedLanguage = holder.languageNameRadioButton;
            selectedLanguage.setChecked(true);
            selectedLanguage.setTextColor(ContextCompat.getColor(selectedLanguage.getContext(), R.color.colorPrimary));
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
        void onLanguageSelected(OptionDTO language);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton languageNameRadioButton;

        public ViewHolder(View itemView) {
            super(itemView);
            languageNameRadioButton = (RadioButton) itemView.findViewById(R.id.languageNameRadioButton);
        }
    }
}
