package com.carecloud.carepay.patient.payment.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.payments.models.PaymentsBalancesItem;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PaymentBalancesAdapter extends RecyclerView.Adapter<PaymentBalancesAdapter.ViewHolder> {

    private Context context;
    private List<PaymentsBalancesItem> balances = new ArrayList<>();
    private OnBalanceListItemClickListener listener;
    private PaymentsModel paymentsModel;

    /**
     * @param context         The context
     * @param pendingBalances The list of the pending balances
     * @param listener        the listener
     */
    public PaymentBalancesAdapter(Context context, List<PaymentsBalancesItem> pendingBalances, OnBalanceListItemClickListener listener, PaymentsModel paymentsModel) {
        this.context = context;
        this.balances = pendingBalances;
        this.listener = listener;
        this.paymentsModel = paymentsModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentHistoryListItemView = LayoutInflater.from(context).inflate(
                R.layout.balances_list_item, parent, false);
        return new ViewHolder(paymentHistoryListItemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PaymentsBalancesItem pendingBalance = balances.get(position);

        String locationName = pendingBalance.getMetadata().getPracticeName();
        holder.shortName.setText(StringUtil.getShortName(locationName));
        holder.locationName.setText(locationName);
        holder.amount.setText(StringUtil.getFormattedBalanceAmount(pendingBalance.getBalance().getAmount()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View listItem) {
                listener.onBalanceListItemClickListener(holder.getAdapterPosition());
            }
        });

        boolean canPay = paymentsModel.getPaymentPayload().canMakePayments(pendingBalance.getMetadata().getPracticeId());
        holder.payLabel.setVisibility(canPay ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return balances.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView shortName;
        private TextView locationName;
        private TextView amount;
        private View payLabel;

        ViewHolder(View itemView) {
            super(itemView);
            locationName = (TextView) itemView.findViewById(R.id.balancesLocation);
            amount = (TextView) itemView.findViewById(R.id.balancesTotalAmount);
            shortName = (TextView) itemView.findViewById(R.id.balancesAvatarTextView);
            payLabel = itemView.findViewById(R.id.balancesPayNowTextView);
        }
    }

    public interface OnBalanceListItemClickListener {
        void onBalanceListItemClickListener(int position);
    }
}
