package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.interfaces.PaymentDetailInterface;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class PracticePaymentPlanAmountFragment extends PracticePartialPaymentDialogFragment {

    private PaymentsModel paymentsModel;
    private PendingBalanceDTO selectedBalance;
    private PaymentDetailInterface callback;

    private String practiceId;
    private boolean canAddToExisting;
    private boolean canCreateMultiple;
    private boolean hasExistingPlans;

    private double minimumPaymentAmount = 0D;
    private double maximumPaymentAmount = 0D;

    /**
     * @param paymentsModel   the payment model
     * @param selectedBalance selected balance
     * @return an instance of PracticePaymentPlanAmountFragment
     */
    public static PracticePaymentPlanAmountFragment newInstance(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);

        PracticePaymentPlanAmountFragment fragment = new PracticePaymentPlanAmountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PaymentDetailInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentDetailInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        selectedBalance = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, args);
        this.practiceId = selectedBalance.getMetadata().getPracticeId();
        fullAmount = calculateFullAmount();
        determineParameters();
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        TextView header = (TextView) findViewById(R.id.partialPaymentHeader);
        header.setText(Label.getLabel("payment_plan_partial_amount_header"));
        applyButton.setEnabled(false);
        applyButton.setText(Label.getLabel("payment_create_payment_plan"));
        TextView footer = (TextView) findViewById(R.id.partialPaymentHeaderBottom);
        if (minimumPaymentAmount > 0D && maximumPaymentAmount < fullAmount) {
            String amountBetween = String.format(Label.getLabel("payment_partial_amount_between"),
                    currencyFormat.format(minimumPaymentAmount),
                    currencyFormat.format(maximumPaymentAmount));
            footer.setText(amountBetween);
            footer.setVisibility(View.VISIBLE);
        } else if (minimumPaymentAmount > 0D) {
            String minimumAmount = Label.getLabel("payment_partial_minimum_amount") +
                    currencyFormat.format(minimumPaymentAmount);
            footer.setText(minimumAmount);
            footer.setVisibility(View.VISIBLE);
        } else if (maximumPaymentAmount < fullAmount) {
            String minimumAmount = Label.getLabel("payment_partial_maximum_amount") +
                    currencyFormat.format(maximumPaymentAmount);
            footer.setText(minimumAmount);
            footer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected double getMinimumPayment() {
        return minimumPaymentAmount;
    }

    @Override
    protected void updateLayout() {
        super.updateLayout();
        double planAmount = StringUtil.isNullOrEmpty(numberStr) ? 0D : Double.parseDouble(numberStr);

        if (planAmount > fullAmount) {
            applyButton.setEnabled(false);
            applyButton.setText(Label.getLabel("payment_create_payment_plan"));
            return;
        }

        boolean canCreatePlan = ((!hasExistingPlans || canCreateMultiple) && hasApplicableRule(practiceId, planAmount));
        if ((canCreatePlan || (hasExistingPlans && canAddToExisting && canAddToExisting(planAmount)))
                && planAmount > 0.0) {
            applyButton.setEnabled(true);
            if (canCreatePlan) {
                applyButton.setText(Label.getLabel("payment_create_payment_plan"));
            } else {//must add to existing
                applyButton.setText(Label.getLabel("payment_plan_add_existing"));
            }
        } else {
            applyButton.setEnabled(false);
            applyButton.setText(Label.getLabel("payment_create_payment_plan"));
        }
    }

    @Override
    protected void onPaymentClick(double amount) {
        callback.onPaymentPlanAmount(paymentsModel, selectedBalance, amount);
        dismiss();
    }

    private double calculateFullAmount() {
        double amount = 0D;
        for (PendingBalancePayloadDTO pendingBalancePayloadDTO : selectedBalance.getPayload()) {
            amount = SystemUtil.safeAdd(amount, pendingBalancePayloadDTO.getAmount());
        }
        return amount;
    }

    private boolean canAddToExisting(double amount) {
        return !paymentsModel.getPaymentPayload().getValidPlans(practiceId, amount).isEmpty();
    }

    private boolean hasApplicableRule(String practiceId, double amount) {
        for (PaymentsPayloadSettingsDTO settingsDTO : paymentsModel.getPaymentPayload().getPaymentSettings()) {
            if (practiceId != null && practiceId.equals(settingsDTO.getMetadata().getPracticeId())) {
                for (PaymentSettingsBalanceRangeRule balanceRangeRule : settingsDTO.getPayload().getPaymentPlans().getBalanceRangeRules()) {
                    double minAmount = balanceRangeRule.getMinBalance().getValue();
                    double maxAmount = balanceRangeRule.getMaxBalance().getValue();
                    if (amount >= minAmount && amount <= maxAmount) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void determineParameters() {
        for (PaymentsPayloadSettingsDTO settingsDTO : paymentsModel.getPaymentPayload().getPaymentSettings()) {
            if (practiceId != null && practiceId.equals(settingsDTO.getMetadata().getPracticeId())) {
                canAddToExisting = settingsDTO.getPayload().getPaymentPlans().isAddBalanceToExisting();
                canCreateMultiple = settingsDTO.getPayload().getPaymentPlans().isCanHaveMultiple();
            }
        }

        minimumPaymentAmount = paymentsModel.getPaymentPayload().getMinimumAllowablePlanAmount(practiceId);
        maximumPaymentAmount = paymentsModel.getPaymentPayload().getMaximumAllowablePlanAmount(practiceId);
        hasExistingPlans = !paymentsModel.getPaymentPayload().getActivePlans(practiceId).isEmpty();
    }

}
