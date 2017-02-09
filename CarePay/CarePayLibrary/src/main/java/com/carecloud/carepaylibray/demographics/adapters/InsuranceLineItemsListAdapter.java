package com.carecloud.carepaylibray.demographics.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.PaymentDetailsDialog;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

public class InsuranceLineItemsListAdapter extends RecyclerView.Adapter<InsuranceLineItemsListAdapter.InsuranceDetailsListViewHolder> {

    private Context context;
    private List<DemographicInsurancePayloadDTO> detailsList;
    private DemographicDTO model;
    private HealthInsuranceFragment.InsuranceDocumentScannerFragment listener;

    /**
     * Constructor
     * @param context context
     * @param model model
     * @param detailsList details list
     * @param listener listener
     */
    public InsuranceLineItemsListAdapter(Context context, DemographicDTO model,
                                         List<DemographicInsurancePayloadDTO> detailsList,
                                         HealthInsuranceFragment.InsuranceDocumentScannerFragment listener) {

        this.context = context;
        this.detailsList = detailsList;
        this.listener = listener;
        this.model = model;
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

        InsuranceDetailsListViewHolder(View itemView) {
            super(itemView);
        }

        public CarePayTextView getLineItemNameLabel(){return (CarePayTextView) itemView.findViewById(R.id.lineItemNameLabel);}
        public CarePayTextView getLineItemQueueLabel() {return (CarePayTextView) itemView.findViewById(R.id.lineItemQueueLabel);}
        public CarePayTextView getLineItemNameLabelEdit() {return (CarePayTextView) itemView.findViewById(R.id.lineItemNameLabelEdit);}

        }

    @Override
    public void onBindViewHolder(final InsuranceDetailsListViewHolder holder, final int position) {
        final DemographicInsurancePayloadDTO lineItem = detailsList.get(position);
        holder.getLineItemNameLabel().setText(lineItem.getInsurancePlan());
        holder.getLineItemNameLabelEdit().setText(model.getMetadata().getLabels().getPracticeCheckinEditClickableLabel());
        int index = position+1;
        holder.getLineItemQueueLabel().setText(index + DateUtil.getInstance().getOrdinalSuffix(index));
        holder.getLineItemNameLabelEdit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.navigateToInsuranceDocumentFragment(position, lineItem);
            }
        });
    }

}
