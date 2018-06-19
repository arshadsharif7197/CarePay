package com.carecloud.carepay.practice.library.payments.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author pjohnson on 8/05/18.
 */
public class ExistingChargesItemAdapter extends RecyclerView.Adapter<ExistingChargesItemAdapter.ViewHolder> {

    private final List<PaymentPlanLineItem> items;
    private NumberFormat currencyFormat;

    public ExistingChargesItemAdapter(List<PaymentPlanLineItem> items) {
        this.items = items;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_existing_charge, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PaymentPlanLineItem item = items.get(position);
        holder.itemNameTextView.setText(item.getDescription());
        holder.onPlanTextView.setText(currencyFormat
                .format(item.getAmount()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemNameTextView;
        TextView onPlanTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = (TextView) itemView.findViewById(R.id.itemNameTextView);
            onPlanTextView = (TextView) itemView.findViewById(R.id.itemAddToPlanTextView);
        }
    }
}
