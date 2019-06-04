package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.interfaces.PaymentDetailInterface;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

public abstract class PaymentPlanAmountDialog extends PartialPaymentDialog {

    protected PaymentsModel paymentsModel;
    protected PendingBalanceDTO selectedBalance;
    protected PaymentDetailInterface callback;

    private String practiceId;
    private boolean canAddToExisting;
    private boolean canCreateMultiple;
    private boolean hasExistingPlans;

    private double minimumPaymentAmount = 0D;
    private double maximumPaymentAmount = 0D;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
            selectedBalance = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, args);
        }
        practiceId = selectedBalance.getMetadata().getPracticeId();
        determineParameters();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button payButton = (Button) findViewById(R.id.payPartialButton);
        payButton.setText(Label.getLabel("payment_create_payment_plan"));
        TextView header = (TextView) findViewById(R.id.partialPaymentHeader);
        header.setText(Label.getLabel("payment_plan_partial_amount_header"));

        TextView footer = (TextView) findViewById(R.id.partialPaymentHeaderBottom);
        double fullAmount = calculateFullAmount();
        if (minimumPaymentAmount > 0D && maximumPaymentAmount < fullAmount) {
            String amountBetween = String.format(Label.getLabel("payment_partial_amount_between"),
                    currencyFormat.format(minimumPaymentAmount),
                    currencyFormat.format(maximumPaymentAmount));
            footer.setText(amountBetween);
            footer.setVisibility(View.VISIBLE);
        } else if (minimumPaymentAmount > 0D) {
            String minimumAmount = String.format(Label.getLabel("payment.partial.amountSelector.minimum.amount"),
                    currencyFormat.format(minimumPaymentAmount));
            footer.setText(minimumAmount);
            footer.setVisibility(View.VISIBLE);
        } else if (maximumPaymentAmount < fullAmount) {
            String maximum = String.format(Label.getLabel("payment.partial.amountSelector.maximum.amount"),
                    currencyFormat.format(maximumPaymentAmount));
            footer.setText(maximum);
            footer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected double getMinimumPayment(String practiceId) {
        return minimumPaymentAmount;
    }

    @Override
    protected double calculateFullAmount() {
        double amount = 0D;
        for (PendingBalancePayloadDTO pendingBalancePayloadDTO : selectedBalance.getPayload()) {
            if (pendingBalancePayloadDTO.getType().equals(PendingBalancePayloadDTO.PATIENT_BALANCE)) {
                amount = SystemUtil.safeAdd(amount, pendingBalancePayloadDTO.getAmount());
            }
        }
        return amount;
    }

    @Override
    protected void onPendingAmountValidation(String amountEditText, Button payPartialButton, TextView partialPaymentTotalAmountTitle) {
        super.onPendingAmountValidation(amountEditText, payPartialButton, partialPaymentTotalAmountTitle);
        if (amountEditText != null && amountEditText.length() > 0) {
            if (amountEditText.length() == 1 && amountEditText.equalsIgnoreCase(".")) {
                amountEditText = "0.";
            }
            double planAmount = Double.parseDouble(amountEditText);

            boolean canCreatePlan = ((!hasExistingPlans || canCreateMultiple)
                    && hasApplicableRule(practiceId, planAmount));
            if (canCreatePlan || (hasExistingPlans && canAddToExisting && canAddToExisting(planAmount))) {
                payPartialButton.setEnabled(true);
                if (canCreatePlan) {
                    payPartialButton.setText(Label.getLabel("payment_create_payment_plan"));
                } else {//must add to existing
                    payPartialButton.setText(Label.getLabel("payment_plan_add_existing"));
                }
            } else {
                payPartialButton.setEnabled(false);
                payPartialButton.setText(Label.getLabel("payment_create_payment_plan"));
            }
        } else {
            payPartialButton.setText(Label.getLabel("payment_create_payment_plan"));
            payPartialButton.setEnabled(false);
        }
    }

    protected void logPaymentPlanStartedMixPanelEvent(boolean addExisting) {
        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_balance_amount),
                getString(R.string.param_is_add_existing)};
        Object[] values = {selectedBalance.getMetadata().getPracticeId(),
                selectedBalance.getPayload().get(0).getAmount(),
                addExisting};

        MixPanelUtil.logEvent(getString(R.string.event_paymentplan_started), params, values);
    }

    private boolean canAddToExisting(double amount) {
        return !paymentsModel.getPaymentPayload().getValidPlans(practiceId, amount).isEmpty();
    }

    private boolean hasApplicableRule(String practiceId, double amount) {
        if (amount > maximumPaymentAmount) {
            return false;
        }
        PaymentsPayloadSettingsDTO settingsDTO = paymentsModel.getPaymentPayload().getPaymentSetting(practiceId);
        for (PaymentSettingsBalanceRangeRule balanceRangeRule : settingsDTO.getPayload()
                .getPaymentPlans().getBalanceRangeRules()) {
            if (isFrequencyEnabled(settingsDTO, balanceRangeRule)) {
                double minAmount = balanceRangeRule.getMinBalance().getValue();
                double maxAmount = balanceRangeRule.getMaxBalance().getValue();
                if (amount >= minAmount && amount <= maxAmount) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isFrequencyEnabled(PaymentsPayloadSettingsDTO settingsDTO,
                                       PaymentSettingsBalanceRangeRule balanceRangeRule) {
        if (balanceRangeRule.getMaxDuration().getInterval().equals(PaymentSettingsBalanceRangeRule.INTERVAL_MONTHS)) {
            return settingsDTO.getPayload().getPaymentPlans().getFrequencyCode().getMonthly().isAllowed();
        } else {
            return settingsDTO.getPayload().getPaymentPlans().getFrequencyCode().getWeekly().isAllowed();
        }
    }

    private void determineParameters() {
        PaymentsPayloadSettingsDTO settingsDTO = paymentsModel.getPaymentPayload().getPaymentSetting(practiceId);
        canAddToExisting = settingsDTO.getPayload().getPaymentPlans().isAddBalanceToExisting();
        canCreateMultiple = settingsDTO.getPayload().getPaymentPlans().isCanHaveMultiple();

        minimumPaymentAmount = paymentsModel.getPaymentPayload().getMinimumAllowablePlanAmount(practiceId);
        maximumPaymentAmount = paymentsModel.getPaymentPayload().getMaximumAllowablePlanAmount(practiceId);
        hasExistingPlans = !paymentsModel.getPaymentPayload().getActivePlans(practiceId).isEmpty();
    }
}
