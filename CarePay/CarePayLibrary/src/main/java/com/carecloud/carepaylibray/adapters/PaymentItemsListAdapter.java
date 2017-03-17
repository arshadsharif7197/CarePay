package com.carecloud.carepaylibray.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

public class PaymentItemsListAdapter extends RecyclerView.Adapter<PaymentItemsListAdapter.PaymentDetailsListViewHolder> {

    private Context context;
    private List<BalanceItemDTO> detailsList;

    public PaymentItemsListAdapter(Context context, List<BalanceItemDTO> detailsList) {
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
        BalanceItemDTO paymentDetailsItem = detailsList.get(position);
        holder.paymentDetailLabel.setText(paymentDetailsItem.getDescription());
        holder.paymentDetailAmount.setText(StringUtil.getFormattedBalanceAmount(paymentDetailsItem.getAmount()));
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
