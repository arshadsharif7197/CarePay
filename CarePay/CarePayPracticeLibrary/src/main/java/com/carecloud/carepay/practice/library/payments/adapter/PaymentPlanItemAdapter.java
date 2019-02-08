package com.carecloud.carepay.practice.library.payments.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author pjohnson on 8/05/18.
 */
public class PaymentPlanItemAdapter extends RecyclerView.Adapter<PaymentPlanItemAdapter.ViewHolder> {

    private final List<BalanceItemDTO> items;
    private NumberFormat currencyFormat;
    private PaymentPlanItemInterface callback;

    public PaymentPlanItemAdapter(List<BalanceItemDTO> items) {
        this.items = items;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_plan_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BalanceItemDTO item = items.get(position);
        holder.itemNameTextView.setText(item.getDescription());
        holder.itemRemainingBalanceTextView.setText(currencyFormat
                .format(SystemUtil.safeSubtract(item.getMaxAmount(), item.getAmountInPaymentPlan())));
        holder.itemAddToPlanTextView.setText(currencyFormat
                .format(item.getAmountSelected()));
        holder.itemCheckBox.setOnCheckedChangeListener(null);
        holder.itemCheckBox.setChecked(item.isSelected());
        holder.itemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (callback.onItemChecked(item, isChecked)){
                    item.setSelected(isChecked);
                }
                notifyItemChanged(position);
            }
        });
        holder.addToPlanContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAddToPlanClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface PaymentPlanItemInterface {
        boolean onItemChecked(BalanceItemDTO itemDTO, boolean checked);

        void onAddToPlanClicked(BalanceItemDTO item);
    }

    public void setCallback(PaymentPlanItemInterface callback) {
        this.callback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemNameTextView;
        TextView itemRemainingBalanceTextView;
        TextView itemAddToPlanTextView;
        CheckBox itemCheckBox;
        View addToPlanContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = (TextView) itemView.findViewById(R.id.itemNameTextView);
            itemRemainingBalanceTextView = (TextView) itemView.findViewById(R.id.itemRemainingBalanceTextView);
            itemAddToPlanTextView = (TextView) itemView.findViewById(R.id.itemAddToPlanTextView);
            itemCheckBox = (CheckBox) itemView.findViewById(R.id.itemCheckBox);
            addToPlanContainer = itemView.findViewById(R.id.addToPlanContainer);
        }
    }
}
