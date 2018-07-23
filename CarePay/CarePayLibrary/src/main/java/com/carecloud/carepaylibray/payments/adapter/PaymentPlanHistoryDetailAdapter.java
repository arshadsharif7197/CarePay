package com.carecloud.carepaylibray.payments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.models.PaymentPlanHistory;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by lmenendez on 9/28/17
 */

public class PaymentPlanHistoryDetailAdapter extends RecyclerView.Adapter<PaymentPlanHistoryDetailAdapter.ViewHolder> {
    private Context context;
    private List<PaymentPlanHistory> lineItems;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    private int countOnetimePayments;
    /**
     * Constructor
     * @param context context
     * @param lineItems list of history items
     */
    public PaymentPlanHistoryDetailAdapter(Context context, List<PaymentPlanHistory> lineItems){
        this.context = context;
        this.lineItems = lineItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plan_history_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PaymentPlanHistory lineItem = lineItems.get(position);

        double amount = lineItem.getAmount();
        holder.amount.setText(currencyFormatter.format(amount));

        holder.date.setText(DateUtil.getInstance()
                .setDateRaw(lineItem.getDate())
                .getDateAsMonthAbbrDayOrdinalYear());

        if(lineItem.isOneTimePayment()){
            holder.number.setText(Label.getLabel("payment_history_detail_extra_payment"));
            countOnetimePayments++;
        }else{
            String number = position + context.getString(R.string.dash);
            holder.number.setText(number);
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
    public void setLineItems(List<PaymentPlanHistory> items){
        this.lineItems = items;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView number;
        TextView amount;
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.payment_number);
            amount = (TextView) itemView.findViewById(R.id.payment_amount);
            date = (TextView) itemView.findViewById(R.id.payment_date);
        }
    }
}
