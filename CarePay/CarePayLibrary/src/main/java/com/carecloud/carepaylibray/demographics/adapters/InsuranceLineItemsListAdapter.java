package com.carecloud.carepaylibray.demographics.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class InsuranceLineItemsListAdapter extends
        RecyclerView.Adapter<InsuranceLineItemsListAdapter.InsuranceDetailsListViewHolder> {

    private Context context;
    private ApplicationMode.ApplicationType applicationType;
    private OnInsuranceEditClickListener listener;
    private List<DemographicInsurancePayloadDTO> insuranceList = new ArrayList<>();

    /**
     * @param context         context
     * @param insuranceList   list of insurances
     * @param listener        the listener
     * @param applicationType the application type
     */
    public InsuranceLineItemsListAdapter(Context context, List<DemographicInsurancePayloadDTO> insuranceList,
                                         OnInsuranceEditClickListener listener,
                                         ApplicationMode.ApplicationType applicationType) {

        this.context = context;
        this.listener = listener;
        this.applicationType = applicationType;
        this.insuranceList = insuranceList;
    }

    @Override
    public InsuranceDetailsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentDetailsListItemView = LayoutInflater.from(context).inflate(
                R.layout.insurance_line_item, parent, false);
        return new InsuranceDetailsListViewHolder(paymentDetailsListItemView);
    }

    @Override
    public int getItemCount() {
        return insuranceList.size();
    }

    @Override
    public void onBindViewHolder(final InsuranceDetailsListViewHolder holder, int position) {
        final DemographicInsurancePayloadDTO lineItem = insuranceList.get(position);
        String plan = lineItem.getInsurancePlan();
        String provider = lineItem.getInsuranceProvider();
        holder.name.setText(provider + " " + (plan != null ? plan : ""));
        if (applicationType == ApplicationMode.ApplicationType.PATIENT) {
            if (lineItem.getInsurancePhotos().size() == 0) {
                holder.separator.setBackgroundColor(context.getResources().getColor(R.color.lightning_yellow));
                holder.name.setTextColor(context.getResources().getColor(R.color.lightning_yellow));
                holder.edit.setText(Label.getLabel("demographics_insurance_add_photos_button"));
            } else {
                holder.separator.setBackgroundColor(context.getResources().getColor(R.color.gray_divider));
                holder.name.setTextColor(context.getResources().getColor(R.color.textview_default_textcolor));
                holder.edit.setText(Label.getLabel("practice_checin_edit_clickable_label"));
            }
            if (lineItem.getInsuranceType().toLowerCase().equals("primary")) {
                holder.type.setText(Label.getLabel("demographics_insurance_primary_type"));
            } else if (lineItem.getInsuranceType().toLowerCase().equals("secondary")) {
                holder.type.setText(Label.getLabel("demographics_insurance_secondary_type"));
            } else {
                holder.type.setText(Label.getLabel("demographics_insurance_tertiary_type"));
            }
        } else {
            if (lineItem.getInsurancePhotos().size() == 0) {
                holder.alertLayout.setVisibility(View.VISIBLE);
                holder.edit.setText(Label.getLabel("demographics_insurance_add_photos_button"));
            } else {
                holder.alertLayout.setVisibility(View.INVISIBLE);
                holder.edit.setText(Label.getLabel("practice_checin_edit_clickable_label"));
            }
            holder.type.setText(StringUtil.capitalize(lineItem.getInsuranceType()));
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEditInsuranceClicked(lineItem);
            }
        });

    }

    /**
     * @param insuranceList the insurances list
     */
    public void setInsurancesList(List<DemographicInsurancePayloadDTO> insuranceList) {
        this.insuranceList = insuranceList;
        notifyDataSetChanged();
    }

    public interface OnInsuranceEditClickListener {
        void onEditInsuranceClicked(DemographicInsurancePayloadDTO position);
    }

    class InsuranceDetailsListViewHolder extends RecyclerView.ViewHolder {

        CarePayTextView name;
        CarePayTextView type;
        CarePayTextView edit;
        View alertLayout;
        View separator;

        InsuranceDetailsListViewHolder(View itemView) {
            super(itemView);
            name = (CarePayTextView) itemView.findViewById(R.id.health_insurance_name);
            type = (CarePayTextView) itemView.findViewById(R.id.health_insurance_type);
            edit = (CarePayTextView) itemView.findViewById(R.id.health_insurance_edit);
            alertLayout = itemView.findViewById(R.id.alertLayout);
            separator = itemView.findViewById(R.id.separator);
        }
    }
}
