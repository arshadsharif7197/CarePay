package com.carecloud.carepay.practice.library.payments.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanModel;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author pjohnson on 16/05/18.
 */
public class PaymentPlanDashboardAdapter extends RecyclerView.Adapter<PaymentPlanDashboardAdapter.ViewHolder> {

    private final List<PaymentPlanDTO> paymentPlans;
    private final boolean completed;
    private final NumberFormat currencyFormat;
    private final boolean hasBalanceForPaymentPlan;
    private PaymentPlanDashboardItemInterface callback;
    private PaymentsModel paymentsModel;

    public PaymentPlanDashboardAdapter(List<PaymentPlanDTO> paymentPlans, PaymentsModel paymentsModel, boolean completed, boolean hasBalanceForPaymentPlan) {
        this.paymentPlans = paymentPlans;
        this.completed = completed;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        this.hasBalanceForPaymentPlan = hasBalanceForPaymentPlan;
        this.paymentsModel = paymentsModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard_payment_plan, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PaymentPlanDTO paymentPlan = paymentPlans.get(position);
        holder.paymentPlanAmountTextView.setText(currencyFormat.format(paymentPlan.getPayload().getAmount()));
        holder.paymentPlanNameTextView.setText(paymentPlan.getPayload().getDescription());
        String frequencyPlaceHolder;
        if (paymentPlan.getPayload().getPaymentPlanDetails().getFrequencyCode()
                .equals(PaymentPlanModel.FREQUENCY_MONTHLY)) {
            frequencyPlaceHolder = Label.getLabel("payment.paymentPlanDashboard.item.label.monthlyPayment");
        } else {
            frequencyPlaceHolder = Label.getLabel("payment.paymentPlanDashboard.item.label.weeklyPayment");
        }
        holder.paymentPlanPeriodicPaymentTextView.setText(String.format(frequencyPlaceHolder,
                currencyFormat.format(paymentPlan.getPayload().getPaymentPlanDetails().getAmount())));
        holder.paymentPlanProgressBar.setProgress(paymentPlan.getPayload().getPaymentPlanProgress());
        if (completed) {
            holder.paymentPlanProgressBar.setVisibility(View.GONE);
            holder.addBalanceButton.setVisibility(View.GONE);
            holder.paymentPlanNameTextView.setText(String
                    .format(Label.getLabel("payment.paymentPlanDashboard.item.label.completedOn"),
                            getLastPaymentDate(paymentPlan)));
        } else {
            ScheduledPaymentModel scheduledPayment = paymentsModel.getPaymentPayload().findScheduledPayment(paymentPlan);
            holder.scheduledPaymentIndicator.setVisibility(scheduledPayment != null ? View.VISIBLE : View.GONE);
        }
        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onDetailClicked(paymentPlan, completed);
            }
        });
        holder.addBalanceButton.setEnabled(hasBalanceForPaymentPlan);
        holder.addBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAddBalanceClicked(paymentPlan);
            }
        });
    }

    private String getLastPaymentDate(PaymentPlanDTO paymentPlan) {
        return DateUtil.getInstance().setDateRaw(paymentPlan.getMetadata().getUpdatedDate())
                .getDateAsMonthLiteralDayOrdinalYear();
    }

    @Override
    public int getItemCount() {
        return paymentPlans.size();
    }

    public interface PaymentPlanDashboardItemInterface {
        void onAddBalanceClicked(PaymentPlanDTO paymentPlan);

        void onDetailClicked(PaymentPlanDTO paymentPlan, boolean completed);
    }

    public void setCallback(PaymentPlanDashboardItemInterface callback) {
        this.callback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ProgressBar paymentPlanProgressBar;
        TextView paymentPlanAmountTextView;
        TextView paymentPlanNameTextView;
        TextView paymentPlanPeriodicPaymentTextView;
        View detailsButton;
        View addBalanceButton;
        View scheduledPaymentIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            paymentPlanProgressBar = (ProgressBar) itemView.findViewById(R.id.paymentPlanProgressBar);
            paymentPlanAmountTextView = (TextView) itemView.findViewById(R.id.totalAmountTextView);
            paymentPlanNameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            paymentPlanPeriodicPaymentTextView = (TextView) itemView.findViewById(R.id.periodicAmountTextView);
            detailsButton = itemView.findViewById(R.id.detailsButton);
            addBalanceButton = itemView.findViewById(R.id.addBalanceButton);
            scheduledPaymentIndicator = itemView.findViewById(R.id.scheduledPaymentIndicator);
        }
    }
}
