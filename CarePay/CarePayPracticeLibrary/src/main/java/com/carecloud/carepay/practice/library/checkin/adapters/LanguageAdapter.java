package com.carecloud.carepay.practice.library.checkin.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;

import java.util.List;

/**
 * @author pjohnson on 9/01/18.
 */

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    private final List<OptionDTO> languages;
    private OptionDTO selectedLanguage;
    private LanguageInterface callback;
    private ViewHolder selectedHolder;

    public LanguageAdapter(List<OptionDTO> languages, OptionDTO selectedLanguage) {
        this.languages = languages;
        this.selectedLanguage = selectedLanguage;
        selectedLanguage.setSelected(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_language, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OptionDTO language = languages.get(position);
        holder.languageTitle.setText(language.getLabel());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLanguage.setSelected(false);
                selectedHolder.languageTitle.setTextColor(holder.languageTitle.getContext()
                        .getResources().getColor(R.color.slateGray));
                selectedHolder.checkImage.setVisibility(View.GONE);

                language.setSelected(true);
                holder.languageTitle.setTextColor(holder.languageTitle.getContext()
                        .getResources().getColor(R.color.colorPrimary));
                holder.checkImage.setVisibility(View.VISIBLE);
                selectedLanguage = language;
                selectedHolder = holder;
                callback.onLanguageSelected(language);
            }
        });
        if (language.isSelected()) {
            selectedHolder = holder;
            holder.languageTitle.setTextColor(holder.languageTitle.getContext()
                    .getResources().getColor(R.color.colorPrimary));
            holder.checkImage.setVisibility(View.VISIBLE);
        }else{
            holder.languageTitle.setTextColor(holder.languageTitle.getContext()
                    .getResources().getColor(R.color.slateGray));
            holder.checkImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public void setCallback(LanguageInterface languageInterface) {
        callback = languageInterface;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView languageTitle;
        ImageView checkImage;

        public ViewHolder(View itemView) {
            super(itemView);
            languageTitle = (TextView) itemView.findViewById(R.id.languageTitle);
            checkImage = (ImageView) itemView.findViewById(R.id.checkImage);
        }
    }

    public interface LanguageInterface {
        void onLanguageSelected(OptionDTO language);
    }
}
