package com.carecloud.carepay.patient.payment.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.payments.models.PaymentListItem;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsBalancesItem;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentBalancesAdapter extends RecyclerView.Adapter<PaymentBalancesAdapter.ViewHolder> {
    private static final int VIEW_TYPE_BALANCE = 0;
    private static final int VIEW_TYPE_PLAN = 1;

    private Context context;
    private List<? extends PaymentListItem> listItems = new ArrayList<>();
    private OnBalanceListItemClickListener listener;
    private PaymentsModel paymentsModel;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * @param context         The context
     * @param pendingBalances The list of the pending listItems
     * @param listener        the listener
     */
    public PaymentBalancesAdapter(Context context, List<? extends PaymentListItem> pendingBalances, OnBalanceListItemClickListener listener, PaymentsModel paymentsModel) {
        this.context = context;
        this.listItems = pendingBalances;
        this.listener = listener;
        this.paymentsModel = paymentsModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentHistoryListItemView;

        if(viewType == VIEW_TYPE_BALANCE) {
            paymentHistoryListItemView = LayoutInflater.from(context).inflate(
                    R.layout.balances_list_item, parent, false);
        }else{
            paymentHistoryListItemView = LayoutInflater.from(context).inflate(
                    R.layout.item_payment_plan, parent, false);
        }
        return new ViewHolder(paymentHistoryListItemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_TYPE_BALANCE) {
            final PaymentsBalancesItem pendingBalance = (PaymentsBalancesItem) listItems.get(position);

            String locationName = pendingBalance.getMetadata().getPracticeName();
            holder.shortName.setText(StringUtil.getShortName(locationName));
            holder.locationName.setText(locationName);
            holder.amount.setText(currencyFormatter.format(pendingBalance.getBalance().getAmount()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View listItem) {
                    listener.onBalanceListItemClickListener(pendingBalance);
                }
            });

            boolean canPay = paymentsModel.getPaymentPayload().canMakePayments(pendingBalance.getMetadata().getPracticeId());
            holder.payLabel.setVisibility(canPay ? View.VISIBLE : View.GONE);
        }else{
            final PaymentPlanDTO paymentPlanItem = (PaymentPlanDTO) listItems.get(position);

            String locationName = paymentPlanItem.getMetadata().getPracticeName();
            holder.shortName.setText(StringUtil.getShortName(locationName));
            holder.locationName.setText(locationName);
            holder.amount.setText(currencyFormatter.format(paymentPlanItem.getPayload().getAmount()));

            String planDetails = currencyFormatter.format(
                    paymentPlanItem.getPayload().getPaymentPlanDetails().getAmount()) + "/" +
                    paymentPlanItem.getPayload().getPaymentPlanDetails().getFrequencyCode();
            holder.planDetail.setText(planDetails);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPaymentPlanItemClickListener(paymentPlanItem);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position){
        if(listItems.get(position) instanceof PaymentsBalancesItem){
            return VIEW_TYPE_BALANCE;
        }
        return VIEW_TYPE_PLAN;
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView shortName;
        private TextView locationName;
        private TextView amount;
        private View payLabel;
        private TextView planDetail;
        private ProgressBar planProgress;

        ViewHolder(View itemView) {
            super(itemView);
            locationName = (TextView) itemView.findViewById(R.id.balancesLocation);
            amount = (TextView) itemView.findViewById(R.id.balancesTotalAmount);
            shortName = (TextView) itemView.findViewById(R.id.balancesAvatarTextView);
            payLabel = itemView.findViewById(R.id.balancesPayNowTextView);
            planDetail = (TextView) itemView.findViewById(R.id.planInstallmentDetail);
            planProgress = (ProgressBar) itemView.findViewById(R.id.paymentPlanProgress);
        }
    }

    public interface OnBalanceListItemClickListener {
        void onBalanceListItemClickListener(PaymentsBalancesItem pendingBalance);

        void onPaymentPlanItemClickListener(PaymentPlanDTO paymentPlan);
    }
}
