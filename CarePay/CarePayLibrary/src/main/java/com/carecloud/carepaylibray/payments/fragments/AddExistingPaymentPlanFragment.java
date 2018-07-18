package com.carecloud.carepaylibray.payments.fragments;

import android.os.Bundle;
import android.view.View;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO.PATIENT_BALANCE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 2/12/18
 */

public class AddExistingPaymentPlanFragment extends PaymentPlanFragment {

    private PaymentPlanDTO existingPlan;

    public static AddExistingPaymentPlanFragment newInstance(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, PaymentPlanDTO existingPlan, double amount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);
        DtoHelper.bundleDto(args, existingPlan);
        args.putDouble(KEY_PLAN_AMOUNT, amount);

        AddExistingPaymentPlanFragment fragment = new AddExistingPaymentPlanFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle icicle) {
        Bundle args = getArguments();
        existingPlan = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
        super.onCreate(icicle);
    }


    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        View addToExisting = view.findViewById(R.id.payment_plan_add_existing);
        addToExisting.setVisibility(View.GONE);
        if (numberPaymentsEditText.getOnFocusChangeListener() != null) {
            numberPaymentsEditText.getOnFocusChangeListener().onFocusChange(numberPaymentsEditText, true);
        }
        numberPaymentsEditText.setText(String.valueOf(getRemainingPayments()));
        planNameEditText.setText(existingPlan.getPayload().getDescription());

        createPlanButton.setText(Label.getLabel("demographics_save_changes_button"));
    }


    @Override
    protected double calculateTotalAmount(double amount) {
        double totalAmount = SystemUtil.safeSubtract(existingPlan.getPayload().getAmount(),
                existingPlan.getPayload().getAmountPaid());
        return SystemUtil.safeAdd(totalAmount, amount);
    }

    private int getRemainingPayments() {
        return existingPlan.getPayload().getPaymentPlanDetails().getInstallments() -
                existingPlan.getPayload().getPaymentPlanDetails().getFilteredHistory().size();
    }


    @Override
    protected void createPaymentPlanPostModel(boolean userInteraction) {
        if (validateFields(false)) {
            PaymentPlanPostModel postModel = new PaymentPlanPostModel();
            postModel.setMetadata(selectedBalance.getMetadata());
            postModel.setAmount(SystemUtil.safeAdd(existingPlan.getPayload().getAmount(),
                    getAdditionalAmount()));
            postModel.setDescription(planNameEditText.getText().toString());
            postModel.setLineItems(getPaymentPlanLineItems());

            PaymentPlanModel paymentPlanModel = new PaymentPlanModel();
            paymentPlanModel.setAmount(monthlyPaymentAmount);
            paymentPlanModel.setFrequencyCode(PaymentPlanModel.FREQUENCY_MONTHLY);
            paymentPlanModel.setInstallments(monthlyPaymentCount +
                    existingPlan.getPayload().getPaymentPlanDetails().getFilteredHistory().size());
            paymentPlanModel.setEnabled(true);

            postModel.setExecution(existingPlan.getPayload().getExecution());
            postModel.setPapiPaymentMethod(existingPlan.getPayload().getPaymentMethod());

            try {
                paymentPlanModel.setDayOfMonth(Integer.parseInt(paymentDateOption.getName()));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            postModel.setPaymentPlanModel(paymentPlanModel);

            submitPaymentPlanUpdate(postModel);
        }
    }

    private double getAdditionalAmount() {
        double remainingBalance = SystemUtil.safeSubtract(existingPlan.getPayload().getAmount(),
                existingPlan.getPayload().getAmountPaid());
        return SystemUtil.safeSubtract(paymentPlanAmount, remainingBalance);
    }

    protected List<PaymentPlanLineItem> getPaymentPlanLineItems() {
        double amountHolder = getAdditionalAmount();
        List<PaymentPlanLineItem> lineItems = new ArrayList<>();
        lineItems.addAll(existingPlan.getPayload().getLineItems());
        for (PendingBalancePayloadDTO pendingBalance : selectedBalance.getPayload()) {
            if (StringUtil.isNullOrEmpty(pendingBalance.getType()) || pendingBalance.getType().equals(PATIENT_BALANCE)) {//ignore responsibility types
                for (BalanceItemDTO balanceItem : pendingBalance.getDetails()) {
                    if (amountHolder <= 0) {
                        break;
                    }

                    PaymentPlanLineItem lineItem = new PaymentPlanLineItem();
                    lineItem.setDescription(balanceItem.getDescription());
                    lineItem.setType(IntegratedPaymentLineItem.TYPE_APPLICATION);
                    lineItem.setTypeId(balanceItem.getId().toString());

                    if(amountHolder >= balanceItem.getBalance()){
                        lineItem.setAmount(balanceItem.getBalance());
                        amountHolder -= balanceItem.getBalance();
                    }else{
                        lineItem.setAmount(amountHolder);
                        amountHolder = 0;
                    }

                    lineItems.add(lineItem);
                }
            }
        }
        return lineItems;
    }

    private void submitPaymentPlanUpdate(PaymentPlanPostModel paymentPlanPostModel) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", paymentPlanPostModel.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", paymentPlanPostModel.getMetadata().getPracticeId());
        queryMap.put("patient_id", paymentPlanPostModel.getMetadata().getPatientId());
        queryMap.put("payment_plan_id", existingPlan.getMetadata().getPaymentPlanId());

        Gson gson = new Gson();
        String payload = gson.toJson(paymentPlanPostModel);

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getUpdatePaymentPlan();
        getWorkflowServiceHelper().execute(transitionDTO, updatePlanCallback, payload, queryMap);
    }

    private WorkflowServiceCallback updatePlanCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            onPlanEdited(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
        }
    };

    protected void onPlanEdited(WorkflowDTO workflowDTO) {
        callback.onPaymentPlanAddedExisting(workflowDTO);
    }

}
