package com.carecloud.carepaylibray.demographics.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;

import java.util.List;

public class InsuranceLineItemsListAdapter extends
        RecyclerView.Adapter<InsuranceLineItemsListAdapter.InsuranceDetailsListViewHolder> {

    private Context context;
    private DemographicDTO model;
    private List<DemographicInsurancePayloadDTO> detailsList;
    private OnInsuranceEditClickListener listener;

    /**
     * Constructor
     *
     * @param context     context
     * @param model       model
     * @param detailsList details list
     */
    public InsuranceLineItemsListAdapter(Context context, DemographicDTO model,
                                         List<DemographicInsurancePayloadDTO> detailsList,
                                         OnInsuranceEditClickListener listener) {

        this.context = context;
        this.model = model;
        this.detailsList = detailsList;
        this.listener = listener;
    }

    @Override
    public InsuranceDetailsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentDetailsListItemView = LayoutInflater.from(context).inflate(
                R.layout.insurance_line_item, parent, false);
        return new InsuranceDetailsListViewHolder(paymentDetailsListItemView);
    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }

    class InsuranceDetailsListViewHolder extends RecyclerView.ViewHolder {

        CarePayTextView name;
        CarePayTextView type;
        CarePayTextView edit;

        InsuranceDetailsListViewHolder(View itemView) {
            super(itemView);

            name = (CarePayTextView) itemView.findViewById(R.id.health_insurance_name);
            type = (CarePayTextView) itemView.findViewById(R.id.health_insurance_type);
            edit = (CarePayTextView) itemView.findViewById(R.id.health_insurance_edit);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onEditInsuranceClicked(detailsList.get(getAdapterPosition()));
                }
            });

            DemographicLabelsDTO labels = model.getMetadata().getLabels();
            edit.setText(labels.getPracticeCheckinEditClickableLabel());
        }
    }

    @Override
    public void onBindViewHolder(final InsuranceDetailsListViewHolder holder, int position) {
        final DemographicInsurancePayloadDTO lineItem = detailsList.get(position);
        holder.name.setText(lineItem.getInsurancePlan());
        holder.type.setText(lineItem.getInsuranceType());
    }

    public interface OnInsuranceEditClickListener {
        void onEditInsuranceClicked(DemographicInsurancePayloadDTO lineItem);
    }
}
