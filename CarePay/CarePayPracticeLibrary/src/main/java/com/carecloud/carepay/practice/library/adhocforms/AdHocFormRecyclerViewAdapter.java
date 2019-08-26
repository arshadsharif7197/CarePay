package com.carecloud.carepay.practice.library.adhocforms;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_adhoc_form, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
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
        holder.formCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                practiceForm.setSelected(!practiceForm.isSelected());
                callback.onFormSelected(practiceForm, isChecked);
                if (practiceForm.isSelected()) {
                    holder.formNameTextView.setTextColor(holder.formLastEditDateTextView
                            .getContext().getResources().getColor(R.color.colorPrimary));
                } else {
                    holder.formNameTextView.setTextColor(holder.formLastEditDateTextView
                            .getContext().getResources().getColor(R.color.slateGray));
                }
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
            formNameTextView = (TextView) itemView.findViewById(R.id.formNameTextView);
            formLastEditDateTextView = (TextView) itemView.findViewById(R.id.formLastEditDateTextView);
            formCheckBox = (CheckBox) itemView.findViewById(R.id.formCheckBox);
        }
    }

    public void setCallback(AdHocFormsListInterface callback) {
        this.callback = callback;
    }

    public interface AdHocFormsListInterface {
        void onFormSelected(PracticeForm practiceForm, boolean selected);
    }
}
