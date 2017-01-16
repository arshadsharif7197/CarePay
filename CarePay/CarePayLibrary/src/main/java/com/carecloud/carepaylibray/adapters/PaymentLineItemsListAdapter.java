package com.carecloud.carepaylibray.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.PaymentDetailsDialog;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

public class PaymentLineItemsListAdapter extends RecyclerView.Adapter<PaymentLineItemsListAdapter.PaymentDetailsListViewHolder>{

    private Context context;
    private List<PatiencePayloadDTO> detailsList;
    private PaymentsModel paymentReceiptModel;
    private PaymentDetailsDialog.PayNowClickListener payListener;

    public PaymentLineItemsListAdapter(Context context, PaymentsModel paymentReceiptModel, List<PatiencePayloadDTO> detailsList, PaymentDetailsDialog.PayNowClickListener payListener) {
        this.context = context;
        this.detailsList = detailsList;
        this.payListener = payListener;
        this.paymentReceiptModel = paymentReceiptModel;
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
        final PatiencePayloadDTO paymentLineItem = detailsList.get(position);
        holder.paymentDetailLabel.setText(paymentLineItem.getType());
        holder.paymentDetailAmount.setText(StringUtil.getFormattedBalanceAmount(paymentLineItem.getAmount()));

        if (paymentLineItem.getDetails() != null && paymentLineItem.getDetails().size() > 0) {
            holder.lineItemNameLabelDetails.setVisibility(View.VISIBLE);
            holder.lineItemNameLabelDetails.setText("Details"); // Change to constant

            holder.lineItemNameLabelDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Call for payment details dialog
                    PaymentDetailsDialog detailsDialog = new PaymentDetailsDialog(context ,
                            paymentReceiptModel, paymentLineItem, payListener);
                   detailsDialog.show();
                }
            });
        }
        else if(holder.lineItemNameLabelDetails.getVisibility() == View.VISIBLE)
        {
            holder.lineItemNameLabelDetails.setVisibility(View.GONE);
        }


    }

}
