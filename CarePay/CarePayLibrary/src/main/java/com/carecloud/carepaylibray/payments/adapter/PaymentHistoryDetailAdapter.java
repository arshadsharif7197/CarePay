package com.carecloud.carepaylibray.payments.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by lmenendez on 9/28/17
 */

public class PaymentHistoryDetailAdapter extends RecyclerView.Adapter<PaymentHistoryDetailAdapter.ViewHolder> {
    private Context context;
    private List<PaymentHistoryLineItem> lineItems = new ArrayList<>();
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * Constructor
     * @param context context
     * @param lineItems list of history items
     */
    public PaymentHistoryDetailAdapter(Context context, List<PaymentHistoryLineItem> lineItems){
        this.context = context;
        this.lineItems = lineItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction_history_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PaymentHistoryLineItem lineItem = lineItems.get(position);

        double amount = lineItem.getAmount();
        holder.amount.setText(currencyFormatter.format(amount));
        holder.description.setText(parseDescription(lineItem.getDescription()));

        double refundedAmount = lineItem.getRefundedAmount();
        if( refundedAmount > 0){
            holder.refundAmount.setText(currencyFormatter.format(-refundedAmount));
            holder.refundAmount.setVisibility(View.VISIBLE);
            if(refundedAmount < amount){
                holder.refundAmount.setTextColor(ContextCompat.getColor(context, R.color.lightning_yellow));
            }else {
                holder.refundAmount.setTextColor(ContextCompat.getColor(context, R.color.remove_red));
            }

        }else{
            holder.refundAmount.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return lineItems.size();
    }

    /**
     * Set new list of payment history items
     * @param items items
     */
    public void setLineItems(List<PaymentHistoryLineItem> items){
        this.lineItems = items;
        notifyDataSetChanged();
    }

    private static String parseDescription(String description){
        if(description == null){
            return "";
        }
        switch (description){
            case IntegratedPaymentLineItem.TYPE_COPAY:
                return Label.getLabel("payment_history_item_copay");
            case IntegratedPaymentLineItem.TYPE_COINSURANCE:
                return Label.getLabel("payment_history_item_coinsurance");
            case IntegratedPaymentLineItem.TYPE_DEDUCTABLE:
                return Label.getLabel("payment_history_item_deductible");
            case IntegratedPaymentLineItem.TYPE_PREPAYMENT:
                return Label.getLabel("payment_history_item_prepayment");
            case IntegratedPaymentLineItem.TYPE_CANCELLATION:
                return Label.getLabel("payment_history_item_cancellation");
            default:
                return description;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView description;
        TextView amount;
        TextView refundAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.charge_item_description);
            amount = (TextView) itemView.findViewById(R.id.charge_item_amount);
            refundAmount = (TextView) itemView.findViewById(R.id.refund_item_amount);
        }
    }
}
