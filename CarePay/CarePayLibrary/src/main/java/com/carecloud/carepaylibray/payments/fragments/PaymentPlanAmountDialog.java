package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.interfaces.PaymentDetailInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

public class PaymentPlanAmountDialog extends PartialPaymentDialog {

    private PaymentsModel paymentsModel;
    private Context context;
    private PendingBalanceDTO selectedBalance;
    private PaymentDetailInterface callback;

    private String practiceId;
    private boolean canAddToExisting;
    private boolean canCreateMultiple;
    private boolean hasExistingPlans;

    private double minimumPaymentAmount = 0D;

    /**
     * Contructor
     *
     * @param context           context must implement PayNowClickListener
     * @param paymentsModel     payment model
     * @param selectedBalance   selected balance
     * @param callback          callback
     */
    public PaymentPlanAmountDialog(Context context, PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, PaymentDetailInterface callback) {
        super(context, paymentsModel, null);
        this.context = context;
        this.paymentsModel = paymentsModel;
        this.selectedBalance = selectedBalance;
        this.callback = callback;
        this.practiceId = selectedBalance.getMetadata().getPracticeId();
        determineParameters();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button payButton = (Button) findViewById(R.id.payPartialButton);
        payButton.setText(Label.getLabel("payment_create_payment_plan"));
        TextView header = (TextView) findViewById(R.id.partialPaymentHeader);
        header.setText(Label.getLabel("payment_plan_partial_amount_header"));
    }

    @Override
    protected double getMinimumPayment(String practiceId){
        return minimumPaymentAmount;
    }

    @Override
    protected double calculateFullAmount() {
        double amount = 0D;
        for(PendingBalancePayloadDTO pendingBalancePayloadDTO : selectedBalance.getPayload()){
            amount = SystemUtil.safeAdd(amount, pendingBalancePayloadDTO.getAmount());
        }
        return amount;
    }

    @Override
    protected void onPendingAmountValidation(String amountEditText, Button payPartialButton, TextView partialPaymentTotalAmountTitle) {
        if (amountEditText != null && amountEditText.length() > 0) {
            if (amountEditText.length() == 1 && amountEditText.equalsIgnoreCase(".")) {
                amountEditText = "0.";
            }
            double planAmount = Double.parseDouble(amountEditText);

            boolean canCreatePlan = ((!hasExistingPlans || canCreateMultiple) && hasApplicableRule(practiceId, planAmount));
            if(canCreatePlan || (hasExistingPlans && canAddToExisting && canAddToExisting(planAmount))){
                super.onPendingAmountValidation(amountEditText, payPartialButton, partialPaymentTotalAmountTitle);
                if(canCreatePlan){
                    payPartialButton.setText(Label.getLabel("payment_create_payment_plan"));
                }else{//must add to existing
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

    @Override
    protected void onPaymentClick(EditText enterPartialAmountEditText) {
        try {
            double amount = Double.parseDouble(enterPartialAmountEditText.getText().toString());
            callback.onPaymentPlanAmount(paymentsModel, selectedBalance, amount);
            dismiss();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Toast.makeText(context, "Please enter valid amount!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean canAddToExisting(double amount){
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

    private void determineParameters(){
        for(PaymentsPayloadSettingsDTO settingsDTO : paymentsModel.getPaymentPayload().getPaymentSettings()) {
            if (practiceId != null && practiceId.equals(settingsDTO.getMetadata().getPracticeId())) {
                canAddToExisting = settingsDTO.getPayload().getPaymentPlans().isAddBalanceToExisting();
                canCreateMultiple = settingsDTO.getPayload().getPaymentPlans().isCanHaveMultiple();

                double minPayment = Double.MAX_VALUE;
                for(PaymentSettingsBalanceRangeRule rangeRule : settingsDTO.getPayload().getPaymentPlans().getBalanceRangeRules()){
                    double minBalance = rangeRule.getMinBalance().getValue();
                    if(minBalance < minPayment){
                        minPayment = minBalance;
                    }
                }

                //checkif minimum amount can be added to an existing
                List<PaymentPlanDTO> activePlans = paymentsModel.getPaymentPayload().getActivePlans(practiceId);
                if(!activePlans.isEmpty()) {
                    if (paymentsModel.getPaymentPayload().getValidPlans(practiceId, 0.01).isEmpty()) {
                        double maxPlanAmount = 0D;
                        for(PaymentPlanDTO paymentPlan : activePlans){
                            double pendingAmount = paymentPlan.getPayload().getAmount();
                            if(pendingAmount > maxPlanAmount){
                                maxPlanAmount = pendingAmount;
                            }
                        }
                        minimumPaymentAmount = SystemUtil.safeSubtract(minPayment, maxPlanAmount);
                    }
                }else{
                    minimumPaymentAmount = minPayment;
                }

            }
        }

        hasExistingPlans = !paymentsModel.getPaymentPayload().getActivePlans(practiceId).isEmpty();
    }
}
