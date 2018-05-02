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
public class ProviderConsentFormsAdapter extends RecyclerView.Adapter<ProviderConsentFormsAdapter.ViewHolder> {

    private final List<PracticeForm> forms;
    private ConsentFormsFormsInterface callback;

    public ProviderConsentFormsAdapter(List<PracticeForm> forms) {
        this.forms = forms;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_provider_forms, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PracticeForm form = forms.get(position);
        holder.formNameTextView.setText(form.getPayload().get("title").getAsString());
        if (form.getLastModifiedDate() == null) {
            holder.formCheckBox.setVisibility(View.VISIBLE);
            holder.formDateTextView.setText(Label.getLabel("consentForms.providersFormList.item.status.pendingStatus"));
            holder.formDateTextView.setTextColor(holder.formDateTextView.getContext().getResources()
                    .getColor(R.color.lightning_yellow));
        } else {
            holder.formCheckBox.setVisibility(View.GONE);
            holder.formDateTextView.setText(String.format(Label.getLabel("adhoc_form_date_placeholder"),
                    DateUtil.getInstance().setDateRaw(form.getLastModifiedDate()).toStringWithFormatMmSlashDdSlashYyyy()));
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onFilledFormSelected(form);
                }
            });
        }
        holder.formCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                callback.onPendingFormSelected(form, isChecked);
                if (holder.formCheckBox.isChecked()) {
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
