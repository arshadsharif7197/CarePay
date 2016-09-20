package com.carecloud.carepaylibray.selectlanguage.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.selectlanguage.models.LanguageOptionModel;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

/**
 * Created Adapter and view holder for recycler view.
 */

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.ViewHolder> {

    List<LanguageOptionModel> languageListLanguageOptionModels;
    private OnItemClickListener itemClickListener;
    Context Context;

    public LanguageListAdapter(List<LanguageOptionModel> LanguageListLanguageOptionModels, OnItemClickListener itemClickListener, Context mContext) {
        this.languageListLanguageOptionModels = LanguageListLanguageOptionModels;
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
        LanguageOptionModel languageSelected = languageListLanguageOptionModels.get(position);
        if (!StringUtil.isNullOrEmpty(languageSelected.getValue())) {
            holder.textViewLanguageName.setText(languageSelected.getValue());
            if (languageSelected.isChecked()) {
                holder.textViewLanguageName.setTextColor(ContextCompat.getColor(Context, R.color.colorPrimary));
                SystemUtil.setProximaNovaSemiboldTypeface(Context, holder.textViewLanguageName);
                holder.radioImageLanguageSelect.setImageResource(R.drawable.cell_radio_on);
              //  holder.cardView.setCardBackgroundColor(ContextCompat.getColor(Context, R.color.white));
            }
        }
        holder.radioImageLanguageSelect.isSelected();
    }

    /**
     * @return number of languages
     */
    @Override
    public int getItemCount() {
        return languageListLanguageOptionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLanguageName;
        CardView cardView;
        ImageView radioImageLanguageSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewLanguageName = (TextView) itemView.findViewById(R.id.languageName);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            textViewLanguageName.setTextColor(ContextCompat.getColor(Context, R.color.slateGray));
            radioImageLanguageSelect = (ImageView) itemView.findViewById(R.id.languageRadioImage);
            radioImageLanguageSelect.setImageResource(R.drawable.cell_radio_off);
            SystemUtil.setProximaNovaRegularTypeface(Context, textViewLanguageName);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(view, getAdapterPosition(), languageListLanguageOptionModels.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, LanguageOptionModel languageSelected);
    }
}
