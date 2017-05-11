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

import java.util.ArrayList;
import java.util.List;

public class InsuranceLineItemsListAdapter extends
        RecyclerView.Adapter<InsuranceLineItemsListAdapter.InsuranceDetailsListViewHolder> {

    private Context context;
    private OnInsuranceEditClickListener listener;
    private List<DemographicInsurancePayloadDTO> insuranceList = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context        context
     * @param demographicDTO Demographic DTO
     */
    public InsuranceLineItemsListAdapter(Context context, DemographicDTO demographicDTO,
                                         OnInsuranceEditClickListener listener) {

        this.context = context;
        this.listener = listener;
        loadFilteredList(demographicDTO);
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
        holder.name.setText(provider+" "+(plan!=null?plan:""));
        holder.type.setText(lineItem.getInsuranceType());
    }

    private void loadFilteredList(DemographicDTO demographicDTO){
        insuranceList.clear();
        for(DemographicInsurancePayloadDTO insurance : demographicDTO.getPayload().getDemographics().getPayload().getInsurances()){
            if(!insurance.isDelete()){
                insuranceList.add(insurance);
            }
        }
    }

    /**
     * @param demographicDTO Demographic DTO
     */
    public void setDemographicDTO(DemographicDTO demographicDTO) {
        loadFilteredList(demographicDTO);
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
