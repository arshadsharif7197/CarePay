package com.carecloud.carepay.patient.consentforms.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormsFormsInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.List;

/**
 * @author pjohnson on 6/04/18.
 */
public class PracticeConsentPracticeFormsAdapter extends RecyclerView.Adapter<PracticeConsentPracticeFormsAdapter.ViewHolder> {

    private final List<PracticeForm> forms;
    private final List<String> pendingForms;
    private ConsentFormsFormsInterface callback;

    public PracticeConsentPracticeFormsAdapter(List<PracticeForm> forms, List<String> pendingForms) {
        this.forms = forms;
        this.pendingForms = pendingForms;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_provider_forms, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PracticeForm form = forms.get(position);
        holder.container.setOnClickListener(null);
        holder.formNameTextView.setText(form.getPayload().get("title").getAsString());
        StringBuilder sb = new StringBuilder();
        if (form.getLastModifiedDate() != null) {
            holder.formCheckBox.setVisibility(View.GONE);
            sb.append(String.format(Label.getLabel("adhoc_form_date_placeholder"),
                    DateUtil.getInstance().setDateRaw(form.getLastModifiedDate()).toStringWithFormatMmSlashDdSlashYyyy()));
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onFilledFormSelected(form);
                }
            });
        }

        if (pendingForms.contains(form.getPayload().get("uuid").getAsString())) {
            holder.formCheckBox.setVisibility(View.VISIBLE);
            holder.formDateTextView.setTextColor(holder.formDateTextView.getContext().getResources()
                    .getColor(R.color.lightning_yellow));
            if (sb.length() > 0) {
                sb.insert(0, " - ");
            }
            sb.insert(0, Label.getLabel("consentForms.providersFormList.item.status.pendingStatus"));
        }
        holder.formDateTextView.setText(sb.toString());
        holder.formCheckBox.setOnCheckedChangeListener(null);
        holder.formCheckBox.setChecked(form.isSelected());
        holder.formCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                form.setSelected(!form.isSelected());
                callback.onPendingFormSelected(form, isChecked);
                if (form.isSelected()) {
                    holder.formNameTextView.setTextColor(holder.formNameTextView.getContext()
                            .getResources().getColor(R.color.colorPrimary));
                    holder.formNameTextView.setFontAttribute(CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD);
                } else {
                    holder.formNameTextView.setTextColor(holder.formNameTextView.getContext()
                            .getResources().getColor(R.color.myHealthTextColor));
                    holder.formNameTextView.setFontAttribute(CustomAssetStyleable.PROXIMA_NOVA_REGULAR);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public void setCallback(ConsentFormsFormsInterface callback) {
        this.callback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox formCheckBox;
        CarePayTextView formNameTextView;
        TextView formDateTextView;
        View container;

        public ViewHolder(View itemView) {
            super(itemView);
            formCheckBox = (CheckBox) itemView.findViewById(R.id.formCheckBox);
            formNameTextView = (CarePayTextView) itemView.findViewById(R.id.formNameTextView);
            formDateTextView = (TextView) itemView.findViewById(R.id.formDateTextView);
            container = itemView.findViewById(R.id.container);
        }
    }
}
