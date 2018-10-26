package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.ExistingChargesItemAdapter;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsToggleOption;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 18/05/18.
 */
public class PracticeModeAddToExistingPaymentPlanFragment extends PracticeModePaymentPlanFragment {

    private PaymentPlanDTO paymentPlan;

    public static PracticeModeAddToExistingPaymentPlanFragment newInstance(PaymentsModel paymentsModel,
                                                                           PaymentPlanDTO paymentPlanDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        PracticeModeAddToExistingPaymentPlanFragment fragment = new PracticeModeAddToExistingPaymentPlanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        paymentPlan = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, getArguments());
        paymentPlanAmount = paymentPlan.getPayload().getAmount();
        super.onCreate(icicle);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        paymentPlanAmount = SystemUtil.safeSubtract(paymentPlan.getPayload().getAmount(),
                paymentPlan.getPayload().getAmountPaid());
        minAmount = paymentPlanAmount;
        setupToolbar(view, Label.getLabel("payment.addBalanceToPaymentPlan.title.label.screenTitle"));
        setUpAmounts(view);
        balanceItems = filterItems();
        setUpItems(view, balanceItems);
        setUpExistingCharges(view, paymentPlan.getPayload().getLineItems());
        setupFields(view);
        setupButtons(view);
        populateFields(paymentPlan);
        setUpCreditCards(view);
    }

    @Override
    protected void setUpAmounts(View view) {
        super.setUpAmounts(view);
        ((TextView) view.findViewById(R.id.paymentLabel))
                .setText(Label.getLabel("payment.addBalanceToPaymentPlan.amounts.label.addToPlan"));
    }

    private void setUpExistingCharges(View view, List<PaymentPlanLineItem> paymentPlanDetails) {
        view.findViewById(R.id.existingChargesLayout).setVisibility(View.VISIBLE);
        RecyclerView existingChargesRecycler = (RecyclerView) view.findViewById(R.id.existingChargesRecycler);
        existingChargesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ExistingChargesItemAdapter adapter = new ExistingChargesItemAdapter(paymentPlan.getPayload()
                .getLineItems());
        existingChargesRecycler.setAdapter(adapter);
    }

    private void populateFields(PaymentPlanDTO paymentPlan) {
        planNameEditText.setText(paymentPlan.getPayload().getDescription());
        frequencyOption = new DemographicsToggleOption();
        frequencyOption.setLabel(StringUtil.capitalize(paymentPlan.getPayload()
                .getPaymentPlanDetails().getFrequencyCode()));
        frequencyOption.setName(paymentPlan.getPayload().getPaymentPlanDetails().getFrequencyCode());
        manageFrequencyChange((DemographicsToggleOption) frequencyOption, true);

        DemographicsOption dateOption;
        if (paymentPlan.getPayload().getPaymentPlanDetails().getFrequencyCode()
                .equals(PaymentPlanDetailsDTO.FREQUENCY_MONTHLY)) {
            dateOption = dateOptions.get(paymentPlan.getPayload()
                    .getPaymentPlanDetails().getDayOfMonth() - 1);
            selectedDateOptions = dateOptions;
        } else {
            if (paymentPlan.getPayload().getPaymentPlanDetails().getDayOfWeek() > dayOfWeekOptions.size()) {
                dateOption = dayOfWeekOptions.get(0);
            } else {
                dateOption = dayOfWeekOptions.get(paymentPlan.getPayload()
                        .getPaymentPlanDetails().getDayOfWeek());
            }
            selectedDateOptions = dayOfWeekOptions;
        }
        paymentDateEditText.setText(dateOption.getLabel());
        installmentsEditText.setText(String.valueOf(paymentPlan.getPayload()
                .getPaymentPlanDetails().getInstallments()));
    }

    @Override
    protected void setupButtons(View view) {
        super.setupButtons(view);
        createPlanButton.setText(Label.getLabel("payment.paymentPlanDashboard.item.button.addBalance"));
    }

    @Override
    public boolean onItemChecked(BalanceItemDTO item, boolean checked) {
        if (checked) {
            double amountSelected = SystemUtil.safeSubtract(item.getBalance(),
                    item.getAmountInPaymentPlan());
            item.setAmountSelected(amountSelected);
            paymentPlanAmount = SystemUtil.safeAdd(paymentPlanAmount, item.getAmountSelected());
            paymentValueTextView.setText(currencyFormatter.format(paymentPlanAmount));
        } else {
            paymentPlanAmount = SystemUtil.safeSubtract(paymentPlanAmount, item.getAmountSelected());
            paymentValueTextView.setText(currencyFormatter.format(paymentPlanAmount));
            item.setAmountSelected(0.00);
        }
        refreshNumberOfPayments(installmentsEditText.getText().toString());
        return true;
    }

    @Override
    public void onAddToPlanClicked(BalanceItemDTO item) {
        showSelectAmountFragment(item, SystemUtil.safeAdd(totalBalance, paymentPlan.getPayload().getAmount()));
    }

    @Override
    protected void mainButtonAction() {
        updatePaymentPlan();
    }

    private void updatePaymentPlan() {
        PaymentPlanPostModel postModel = new PaymentPlanPostModel();
        postModel.setAmount(paymentPlanAmount);
        postModel.setExecution(paymentPlan.getPayload().getExecution());
        List<PaymentPlanLineItem> lineItems = getPaymentPlanLineItems();
        lineItems.addAll(paymentPlan.getPayload().getLineItems());
        postModel.setLineItems(lineItems);
        postModel.setDescription(planNameEditText.getText().toString());

        if (selectedCreditCard != null) {
            PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
            if (selectedCreditCard.getCreditCardsId() == null) {
                papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
                papiPaymentMethod.setCardData(getCreditCardModel(selectedCreditCard));
            } else {
                papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_CARD);
                papiPaymentMethod.setPapiPaymentID(selectedCreditCard.getCreditCardsId());
            }
            postModel.setPapiPaymentMethod(papiPaymentMethod);
        } else {
            postModel.setPapiPaymentMethod(paymentPlan.getPayload().getPaymentMethod());
        }

        PaymentPlanModel paymentPlanModel = new PaymentPlanModel();
        paymentPlanModel.setAmount(amounthPayment);
        paymentPlanModel.setFrequencyCode(PaymentPlanModel.FREQUENCY_MONTHLY);
        paymentPlanModel.setInstallments(installments);
        paymentPlanModel.setEnabled(true);

        if (frequencyOption.getName().equals(PaymentPlanModel.FREQUENCY_MONTHLY)) {
            try {
                paymentPlanModel.setDayOfMonth(Integer.parseInt(paymentDateOption.getName()));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        } else {
            try {
                paymentPlanModel.setDayOfWeek(Integer.parseInt(paymentDateOption.getName()));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }

        postModel.setPaymentPlanModel(paymentPlanModel);

        callEditPaymentPlanService(postModel);
    }

    private void callEditPaymentPlanService(PaymentPlanPostModel postModel) {
        TransitionDTO updatePaymentTransition = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getUpdatePaymentPlan();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", paymentPlan.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", paymentPlan.getMetadata().getPracticeId());
        queryMap.put("patient_id", paymentPlan.getMetadata().getPatientId());
        queryMap.put("payment_plan_id", paymentPlan.getMetadata().getPaymentPlanId());


        getWorkflowServiceHelper().execute(updatePaymentTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                dismiss();
                callback.onPaymentPlanAddedExisting(workflowDTO);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                SystemUtil.showErrorToast(getContext(), exceptionMessage);

            }
        }, new Gson().toJson(postModel), queryMap);
    }
}
