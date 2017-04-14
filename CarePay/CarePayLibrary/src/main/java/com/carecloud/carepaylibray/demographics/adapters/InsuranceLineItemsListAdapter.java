package com.carecloud.carepaylibray.demographics.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;

public class InsuranceLineItemsListAdapter extends
        RecyclerView.Adapter<InsuranceLineItemsListAdapter.InsuranceDetailsListViewHolder> {

    private Context context;
    private DemographicDTO demographicDTO;
    private OnInsuranceEditClickListener listener;

    /**
     * Constructor
     *
     * @param context        context
     * @param demographicDTO Demographic DTO
     */
    public InsuranceLineItemsListAdapter(Context context, DemographicDTO demographicDTO,
                                         OnInsuranceEditClickListener listener) {

        this.context = context;
        this.demographicDTO = demographicDTO;
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
        if (demographicDTO == null) {
            return 0;
        }

        return demographicDTO.getPayload().getDemographics().getPayload().getInsurances().size();
    }

    @Override
    public void onBindViewHolder(final InsuranceDetailsListViewHolder holder, int position) {
        final DemographicInsurancePayloadDTO lineItem = demographicDTO.getPayload().getDemographics().getPayload().getInsurances().get(position);
        String plan = lineItem.getInsurancePlan();
        String provider = lineItem.getInsuranceProvider();
        holder.name.setText(provider+" "+(plan!=null?plan:""));
        holder.type.setText(lineItem.getInsuranceType());
    }

    /**
     * @param demographicDTO Demographic DTO
     */
    public void setDemographicDTO(DemographicDTO demographicDTO) {
        this.demographicDTO = demographicDTO;

        notifyDataSetChanged();
    }

    public interface OnInsuranceEditClickListener {
        void onEditInsuranceClicked(int position);
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
                    listener.onEditInsuranceClicked(getAdapterPosition());
                }
            });
        }
    }
}
