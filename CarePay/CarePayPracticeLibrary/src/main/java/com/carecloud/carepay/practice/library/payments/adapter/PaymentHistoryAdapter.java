package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.utils.DateUtil;

import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_ACCOUNT;
import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_CARD;
import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 9/28/17
 */

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder> {
    public interface HistoryItemClickListener{
        void onHistoryItemClicked(PaymentHistoryItem item);
    }

    private static final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private List<PaymentHistoryItem> paymentHistoryItems = new ArrayList<>();
    private HistoryItemClickListener callback;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    private boolean isLoading = false;

    /**
     * Constructor
     * @param context context
     * @param paymentHistoryItems list of history items
     * @param callback callback
     */
    public PaymentHistoryAdapter(Context context, List<PaymentHistoryItem> paymentHistoryItems, HistoryItemClickListener callback){
        this.context = context;
        this.paymentHistoryItems = paymentHistoryItems;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_TYPE_LOADING){
            view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.item_transaction_history, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position >= paymentHistoryItems.size()){
            return;
        }

        final PaymentHistoryItem item = paymentHistoryItems.get(position);

        DateUtil dateInstance = DateUtil.getInstance().setDateRaw(item.getPayload().getDate());
        holder.transactionDate.setText(dateInstance.getDateAsMonthLiteralDayOrdinalYear());
        holder.transactionTime.setText(dateInstance.getTime12Hour().concat(","));

        holder.transactionPaymentType.setText(getPaymentMethod(item.getPayload().getPapiPaymentMethod()));

        double totalPaid = item.getPayload().getTotalPaid();
        double amount = item.getPayload().getAmount();
        if(totalPaid == 0 && !item.getPayload().getProcessingErrors().isEmpty()){
            holder.transactionAmount.setText(currencyFormatter.format(amount));
            holder.transactionFlag.setText(Label.getLabel("payment_history_failed_flag"));
            holder.transactionFlag.setVisibility(View.VISIBLE);
        }else {
            holder.transactionAmount.setText(currencyFormatter.format(totalPaid));
            holder.transactionFlag.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onHistoryItemClicked(item);
            }
        });
    }

    @Override
    public int getItemViewType(int position){
        if(position >= paymentHistoryItems.size()){
            return VIEW_TYPE_LOADING;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        if(isLoading && !paymentHistoryItems.isEmpty()){
            return paymentHistoryItems.size()+1;
        }
        return paymentHistoryItems.size();
    }

    /**
     * Set new list of payment history items
     * @param items items
     */
    public void setPaymentHistoryItems(List<PaymentHistoryItem> items){
        this.paymentHistoryItems = items;
        notifyDataSetChanged();
    }

    /**
     * Add a set of payment history items to existing list
     * @param newItems new items
     */
    public void addPaymentHistoryItems(List<PaymentHistoryItem> newItems){
        this.paymentHistoryItems.addAll(newItems);
        notifyDataSetChanged();
    }

    /**
     * sets loading progress bar
     * @param isLoading true to show progress bar
     */
    public void setLoading(boolean isLoading){
        this.isLoading = isLoading;
        notifyDataSetChanged();
    }

    private static String getPaymentMethod(PapiPaymentMethod paymentMethod){
        switch (paymentMethod.getPaymentMethodType()){
            case PAYMENT_METHOD_ACCOUNT:
                return Label.getLabel("payment_method_account");
            case PAYMENT_METHOD_CARD:
            case PAYMENT_METHOD_NEW_CARD:
            default:
                return Label.getLabel("payment_method_creditcard");
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView transactionDate;
        TextView transactionTime;
        TextView transactionPaymentType;
        TextView transactionAmount;
        TextView transactionFlag;

        public ViewHolder(View itemView) {
            super(itemView);
            transactionDate = (TextView) itemView.findViewById(R.id.transaction_date);
            transactionTime = (TextView) itemView.findViewById(R.id.transaction_time);
            transactionPaymentType = (TextView) itemView.findViewById(R.id.transaction_payment_type);
            transactionAmount = (TextView) itemView.findViewById(R.id.transaction_total);
            transactionFlag = (TextView) itemView.findViewById(R.id.transaction_flag);
        }
    }
}
