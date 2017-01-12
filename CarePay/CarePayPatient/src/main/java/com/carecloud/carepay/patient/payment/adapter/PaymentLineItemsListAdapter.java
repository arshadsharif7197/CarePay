package com.carecloud.carepay.patient.payment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.dialogs.PaymentDetailsDialog;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;

import java.util.List;

public class PaymentLineItemsListAdapter extends RecyclerView.Adapter<PaymentLineItemsListAdapter.PaymentDetailsListViewHolder>{

    private Context context;
    private List<PatiencePayloadDTO> detailsList;

    public PaymentLineItemsListAdapter(Context context, List<PatiencePayloadDTO> detailsList) {
        this.context = context;
        this.detailsList = detailsList;
    }

    @Override
    public PaymentDetailsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentDetailsListItemView = LayoutInflater.from(context).inflate(
                R.layout.payment_line_item, parent, false);
        return new PaymentDetailsListViewHolder(paymentDetailsListItemView);
    }



    @Override
    public int getItemCount() {
        return detailsList.size();
    }

    class PaymentDetailsListViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView paymentDetailLabel;
        private CarePayTextView paymentDetailAmount;
        private CarePayTextView lineItemNameLabelDetails;

        PaymentDetailsListViewHolder(View itemView) {
            super(itemView);

            paymentDetailLabel = (CarePayTextView) itemView.findViewById(R.id.lineItemNameLabel);
            paymentDetailAmount = (CarePayTextView) itemView.findViewById(R.id.lineItemAmountLabel);
            lineItemNameLabelDetails = (CarePayTextView) itemView.findViewById(R.id.lineItemNameLabelDetails);

        }
    }

    @Override
    public void onBindViewHolder(final PaymentDetailsListViewHolder holder, int position) {
        PatiencePayloadDTO paymentLineItem = detailsList.get(position);
        holder.paymentDetailLabel.setText(paymentLineItem.getType());
        holder.paymentDetailAmount.setText(paymentLineItem.getAmount().toString());

        if (paymentLineItem.getDetails() != null && paymentLineItem.getDetails().size() > 0) {
            holder.lineItemNameLabelDetails.setVisibility(View.VISIBLE);
            holder.lineItemNameLabelDetails.setText("Details"); // Change to constant

            holder.lineItemNameLabelDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Call for payment details dialog
//                    PaymentDetailsDialog detailsDialog = new PaymentDetailsDialog(context ,
//                            PatiencePayloadDTO.getDetails(), ResponsibilityFragment.this);
//                    detailsDialog.show();
                }
            });
        }
        else if(holder.lineItemNameLabelDetails.getVisibility() == View.VISIBLE)
        {
            holder.lineItemNameLabelDetails.setVisibility(View.GONE);
        }


    }

}
