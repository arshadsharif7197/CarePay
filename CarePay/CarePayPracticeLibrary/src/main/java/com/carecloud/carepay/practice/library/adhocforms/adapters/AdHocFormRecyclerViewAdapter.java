package com.carecloud.carepay.practice.library.adhocforms.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.List;

/**
 * @author pjohnson on 3/07/17.
 */

public class AdHocFormRecyclerViewAdapter extends RecyclerView
        .Adapter<AdHocFormRecyclerViewAdapter.ViewHolder> {

    private final List<PracticeForm> forms;
    private AdHocFormsListInterface callback;

    public AdHocFormRecyclerViewAdapter(List<PracticeForm> forms) {
        this.forms = forms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_adhoc_form, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final PracticeForm practiceForm = forms.get(position);
        holder.formNameTextView.setText(practiceForm.getPayload().get("title").getAsString());
        if (practiceForm.getLastModifiedDate() != null) {
            holder.formLastEditDateTextView.setText(
                    String.format(Label.getLabel("adhoc_form_date_placeholder"), DateUtil.getInstance()
                            .setDateRaw(practiceForm.getLastModifiedDate())
                            .toStringWithFormatMmSlashDdSlashYyyy()));
            holder.formLastEditDateTextView.setTextColor(holder.formLastEditDateTextView
                    .getContext().getResources().getColor(R.color.pastel_blue));
        }
        holder.formCheckBox.setOnCheckedChangeListener(null);
        holder.formCheckBox.setChecked(practiceForm.isSelected());
        holder.formCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            practiceForm.setSelected(!practiceForm.isSelected());
            callback.onFormSelected(practiceForm, isChecked);
            if (practiceForm.isSelected()) {
                holder.formNameTextView.setTextColor(holder.formLastEditDateTextView
                        .getContext().getResources().getColor(R.color.colorPrimary));
            } else {
                holder.formNameTextView.setTextColor(holder.formLastEditDateTextView
                        .getContext().getResources().getColor(R.color.slateGray));
            }
        });

    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView formNameTextView;
        CheckBox formCheckBox;
        TextView formLastEditDateTextView;

        ViewHolder(View itemView) {
            super(itemView);
            formNameTextView = itemView.findViewById(R.id.formNameTextView);
            formLastEditDateTextView = itemView.findViewById(R.id.formLastEditDateTextView);
            formCheckBox = itemView.findViewById(R.id.formCheckBox);
        }
    }

    public void setCallback(AdHocFormsListInterface callback) {
        this.callback = callback;
    }

    public interface AdHocFormsListInterface {
        void onFormSelected(PracticeForm practiceForm, boolean selected);
    }
}
