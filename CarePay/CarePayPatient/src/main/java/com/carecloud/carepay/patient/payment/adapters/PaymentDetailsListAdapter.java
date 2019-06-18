package com.carecloud.carepay.patient.payment.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PaymentDetailsItemDTO;

import java.util.List;

@Deprecated
public class PaymentDetailsListAdapter extends RecyclerView.Adapter<PaymentDetailsListAdapter.PaymentDetailsListViewHolder> {

    private Context context;
    private List<PaymentDetailsItemDTO> detailsList;

    public PaymentDetailsListAdapter(Context context, List<PaymentDetailsItemDTO> detailsList) {
        this.context = context;
        this.detailsList = detailsList;
    }

    @Override
    public PaymentDetailsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentDetailsListItemView = LayoutInflater.from(context).inflate(
                R.layout.payment_details_list_item, parent, false);
        return new PaymentDetailsListViewHolder(paymentDetailsListItemView);
    }

    @Override
    public void onBindViewHolder(final PaymentDetailsListViewHolder holder, int position) {
        PaymentDetailsItemDTO paymentDetailsItem = detailsList.get(position);
        holder.paymentDetailLabel.setText(paymentDetailsItem.getLabel());
        holder.paymentDetailAmount.setText(paymentDetailsItem.getValue());
    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }

    class PaymentDetailsListViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView paymentDetailLabel;
        private CarePayTextView paymentDetailAmount;

        PaymentDetailsListViewHolder(View itemView) {
            super(itemView);

            paymentDetailLabel = (CarePayTextView) itemView.findViewById(R.id.payment_details_label);
            paymentDetailAmount = (CarePayTextView) itemView.findViewById(R.id.payment_details_value);
        }
    }
}
