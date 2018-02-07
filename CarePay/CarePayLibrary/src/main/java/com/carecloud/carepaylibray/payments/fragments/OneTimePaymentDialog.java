package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanHistory;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 2/5/18
 */

public class OneTimePaymentDialog extends PartialPaymentDialog {

    private PaymentPlanDTO paymentPlanDTO;
    private PaymentsModel paymentsDTO;
    private Context context;

    private Map<String, PaymentPlanLineItem> currentPlanItems = new HashMap<>();
    private Map<String, BalanceItemDTO> currentBalanceItems = new HashMap<>();

    /**
     * Contructor
     *
     * @param context         context must implement PayNowClickListener
     * @param paymentsDTO     payment model
     * @param paymentPlanDTO  payment plan
     */
    public OneTimePaymentDialog(Context context, PaymentsModel paymentsDTO, PaymentPlanDTO paymentPlanDTO) {
        super(context, paymentsDTO, null);
        this.context = context;
        this.paymentsDTO = paymentsDTO;
        this.paymentPlanDTO = paymentPlanDTO;
        initPlanItems();
        initBalanceItems();
    }

    @Override
    protected double getMinimumPayment(String practiceId){
        double planTotal = paymentPlanDTO.getPayload().getAmount();
        PaymentSettingsBalanceRangeRule selectedRule = new PaymentSettingsBalanceRangeRule();
        if(practiceId != null) {
            for (PaymentsPayloadSettingsDTO payloadSettingsDTO : paymentsDTO.getPaymentPayload().getPaymentSettings()) {
                if (practiceId.equals(payloadSettingsDTO.getMetadata().getPracticeId())) {
                    for(PaymentSettingsBalanceRangeRule rule : payloadSettingsDTO.getPayload().getPaymentPlans().getBalanceRangeRules()){
                        double ruleAmount = rule.getMinBalanceRequired().getValue();
                        if(planTotal > ruleAmount &&
                                ruleAmount > selectedRule.getMinBalanceRequired().getValue()){
                            selectedRule = rule;
                        }
                    }
                }
            }
        }
        return selectedRule.getMinAmount().getValue();
    }

    @Override
    protected double calculateFullAmount() {
        return SystemUtil.safeSubtract(paymentPlanDTO.getPayload().getAmount(), paymentPlanDTO.getPayload().getAmountPaid());
//        return Math.round((paymentPlanDTO.getPayload().getAmount() - paymentPlanDTO.getPayload().getAmountPaid())*100)/100;
    }

    @Override
    protected void onPaymentClick(EditText enterPartialAmountEditText) {
        try {
            double amount = Double.parseDouble(enterPartialAmountEditText.getText().toString());
            createPaymentModel(amount);
            payListener.onPayButtonClicked(amount, paymentsDTO);
            dismiss();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Toast.makeText(context, "Please enter valid amount!", Toast.LENGTH_LONG).show();
        }
    }

    private void initPlanItems(){
        currentPlanItems.clear();
        for(PaymentPlanLineItem lineItem : paymentPlanDTO.getPayload().getLineItems()){
            currentPlanItems.put(lineItem.getTypeId(), lineItem);
        }
        for(PaymentPlanHistory historyItem : paymentPlanDTO.getPayload().getPaymentPlanDetails().getPaymentPlanHistoryList()){
            for(PaymentPlanLineItem lineItem : historyItem.getPaymentPlanLineItems()){
                PaymentPlanLineItem currentItem = currentPlanItems.get(lineItem.getTypeId());
                currentItem.setAmount(SystemUtil.safeSubtract(currentItem.getAmount(), lineItem.getAmount()));
            }
        }
    }

    private void initBalanceItems(){
        currentBalanceItems.clear();
        for(PatientBalanceDTO patientBalanceDTO : paymentsDTO.getPaymentPayload().getPatientBalances()){
            for(PendingBalanceDTO pendingBalanceDTO : patientBalanceDTO.getBalances()){
                if(pendingBalanceDTO.getMetadata().getPracticeId().equals(paymentPlanDTO.getMetadata().getPracticeId())){
                    for(PendingBalancePayloadDTO payloadDTO : pendingBalanceDTO.getPayload()){
                        for(BalanceItemDTO balanceItemDTO : payloadDTO.getDetails()){
                            currentBalanceItems.put(String.valueOf(balanceItemDTO.getId()), balanceItemDTO);
                        }
                    }
                }
            }
        }
    }

    private void createPaymentModel(double amount){
        IntegratedPaymentPostModel postModel = paymentsDTO.getPaymentPayload().getPaymentPostModel();
        if (postModel == null) {
            postModel = new IntegratedPaymentPostModel();
        }
        postModel.setAmount(amount);
        postModel.setLineItems(getLineItems(amount));

        paymentsDTO.getPaymentPayload().setPaymentPostModel(postModel);
    }

    private List<IntegratedPaymentLineItem> getLineItems(double amount){
        List<IntegratedPaymentLineItem> lineItems = new ArrayList<>();
        for(PaymentPlanLineItem paymentPlanLineItem : paymentPlanDTO.getPayload().getLineItems()){
            if(amount <= 0D){//once we have depleted the entire amount we can stop
                break;
            }

            PaymentPlanLineItem currentLineItem = currentPlanItems.get(paymentPlanLineItem.getTypeId());
            BalanceItemDTO currentBalanceItem = currentBalanceItems.get(paymentPlanLineItem.getTypeId());
            IntegratedPaymentLineItem newLineItem = new IntegratedPaymentLineItem();

            if(currentBalanceItem == null){
                //this balance item has probably been fully paid off so go to next item
                continue;
            }

            if(amount > currentLineItem.getAmount()){
                if(currentLineItem.getAmount() >= currentBalanceItem.getBalance()){
                    newLineItem.setAmount(currentBalanceItem.getBalance());//use the balance item amount to ensure we don't go over
                }else{
                    newLineItem.setAmount(currentLineItem.getAmount());//use the current plan item amount to max it out
                }

            }else{
                if(amount > currentBalanceItem.getBalance()){
                    newLineItem.setAmount(currentBalanceItem.getBalance());//use the balance amount because its less than the amount trying to be paid
                }else{
                    newLineItem.setAmount(amount);
                }
            }
            newLineItem.setDescription(currentLineItem.getDescription());
            newLineItem.setId(currentLineItem.getTypeId());
            newLineItem.setItemType(currentLineItem.getType());
            if(currentBalanceItem.getProviderId() != null) {
                newLineItem.setProviderID(currentBalanceItem.getProviderId().toString());
            }
            if(currentBalanceItem.getLocationId() != null) {
                newLineItem.setLocationID(currentBalanceItem.getLocationId().toString());
            }

            lineItems.add(newLineItem);
            amount = SystemUtil.safeSubtract(amount, newLineItem.getAmount());

        }

        return lineItems;
    }



}
