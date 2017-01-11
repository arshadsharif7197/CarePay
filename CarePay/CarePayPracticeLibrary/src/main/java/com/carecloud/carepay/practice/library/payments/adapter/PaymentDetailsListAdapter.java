package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PaymentDetailsItemDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;

import java.util.List;

public class PaymentDetailsListAdapter extends RecyclerView.Adapter<PaymentDetailsListAdapter.PaymentDetailsListViewHolder> {

    private Context context;
    private List<PaymentDetailsItemDTO> detailsList;
    private PaymentsLabelDTO paymentsLabelDTO;
    private boolean isPaymentReceiptData;

    /**
     * Instantiates a new Payment details list adapter.
     *
     * @param context              the context
     * @param isPaymentReceiptData the is payment receipt data
     * @param detailsList          the details list
     * @param paymentsLabelDTO     the payments label dto
     */
    public PaymentDetailsListAdapter(Context context, boolean isPaymentReceiptData, List<PaymentDetailsItemDTO> detailsList, PaymentsLabelDTO paymentsLabelDTO) {
        this.context = context;
        this.isPaymentReceiptData = isPaymentReceiptData;
        this.detailsList = detailsList;
        this.paymentsLabelDTO = paymentsLabelDTO;
    }

    @Override
    public PaymentDetailsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentDetailsListItemView = LayoutInflater.from(context).inflate(
                R.layout.practice_payment_details_list_item, parent, false);
        return new PaymentDetailsListViewHolder(paymentDetailsListItemView);
    }

    @Override
    public void onBindViewHolder(final PaymentDetailsListViewHolder holder, int position) {
        PaymentDetailsItemDTO paymentDetailsItem = detailsList.get(position);
        holder.paymentDetailLabel.setText(paymentDetailsItem.getLabel());
        holder.paymentDetailAmount.setText(paymentDetailsItem.getValue());
        if(position==0 && isPaymentReceiptData){
            holder.paymentDetail.setText(paymentsLabelDTO.getPaymentResponsibilityDetails());
        } else {
            holder.paymentDetail.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }

    class PaymentDetailsListViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView paymentDetailLabel;
        private CarePayTextView paymentDetailAmount;
        private CarePayTextView paymentDetail;

        PaymentDetailsListViewHolder(View itemView) {
            super(itemView);

            paymentDetailLabel = (CarePayTextView) itemView.findViewById(R.id.payment_details_label);
            paymentDetailAmount = (CarePayTextView) itemView.findViewById(R.id.payment_details_value);
            paymentDetail = (CarePayTextView)itemView.findViewById(R.id.payment_details_view);
        }
    }
}
