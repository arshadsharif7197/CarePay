package com.carecloud.carepay.practice.library.adhocforms.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

import java.util.List;

/**
 * @author pjohnson on 10/07/17.
 */

public class AdHocRecyclerViewAdapter extends RecyclerView.Adapter<AdHocRecyclerViewAdapter.ViewHolder> {

    private final List<PracticeForm> forms;
    private int displayedFormsIndex;

    public AdHocRecyclerViewAdapter(List<PracticeForm> forms) {
        this.forms = forms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_adhoc_form_title, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.formNameTextView.setText(forms.get(position).getPayload().get("title").toString()
                .replace("\"", ""));
        if (displayedFormsIndex == position) {
            holder.formNameTextView.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_BOLD);
        } else {
            holder.formNameTextView.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_LIGHT);
        }
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public void highlightFormName(int displayedFormsIndex) {
        this.displayedFormsIndex = displayedFormsIndex;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CarePayTextView formNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            formNameTextView = itemView.findViewById(R.id.formNameTextView);
        }
    }
}
