


package com.carecloud.carepaylibray.appointments.createappointment.visittype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.VisitTypeQuestions;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

import java.util.List;

/**
 * Created by lmenendez on 5/30/17
 */

public class VisitTypeOptionsListAdapter extends RecyclerView.Adapter<VisitTypeOptionsListAdapter.ViewHolder> {

    private final boolean showSeparator;
    private Context context;
    private OptionSelectionListener callback;
    private List<VisitTypeQuestions> itemsList;
    private ViewHolder lastHighlightedView = null;

    private VisitTypeQuestions selectedItem;

    public VisitTypeOptionsListAdapter(Context context,
                                       List<VisitTypeQuestions> itemsList,
                                       OptionSelectionListener callback,
                                       boolean showSeparator) {
        this.context = context;
        this.callback = callback;
        this.itemsList = itemsList;
        this.showSeparator = showSeparator;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_visit_type_options, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final VisitTypeQuestions optionInfo = itemsList.get(position);
        holder.tvOption.setText(optionInfo.getName());

        if (isTheSameOption(optionInfo)) {
            lastHighlightedView = holder;
            highlightRow(holder, true, CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD);
        } else {
            highlightRow(holder, false, CustomAssetStyleable.PROXIMA_NOVA_REGULAR);
        }

        holder.itemView.setOnClickListener(view -> {
            if (callback != null) {
                selectedItem = optionInfo;
                if (lastHighlightedView != null) {
                    highlightRow(lastHighlightedView, false, CustomAssetStyleable.PROXIMA_NOVA_REGULAR);
                }
                highlightRow(holder, true, CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD);
                lastHighlightedView = holder;
                callback.onOptionSelected(optionInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    private boolean isTheSameOption(VisitTypeQuestions visitTypeQuestions) {
        return selectedItem != null
                && ((visitTypeQuestions.getId() != null
                && visitTypeQuestions.getId().equals(selectedItem.getId())));
    }

    private void highlightRow(VisitTypeOptionsListAdapter.ViewHolder holder, boolean selected, int proximaNovaSemiBold) {
        holder.tvOption.setSelected(selected);
        holder.tvOption.setFontAttribute(proximaNovaSemiBold);
        holder.itemCheck.setSelected(selected);
    }

    public void setSelectedCreditCard(VisitTypeQuestions visitTypeQuestion) {
        selectedItem = visitTypeQuestion;
    }

    public interface OptionSelectionListener {
        void onOptionSelected(VisitTypeQuestions visitTypeQuestions);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemCheck;
        CarePayTextView tvOption;
        View divider;

        ViewHolder(View itemView) {
            super(itemView);
            itemCheck = itemView.findViewById(R.id.item_check);
            tvOption = itemView.findViewById(R.id.visit_option_text);
            divider = itemView.findViewById(R.id.divider);
            if (showSeparator) {
                divider.setVisibility(View.VISIBLE);
            }
        }
    }

}
