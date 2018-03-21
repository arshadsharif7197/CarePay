package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.utils.CreditCardUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author pjohnson on 9/02/18.
 */

public class PaymentPlanEditFragment extends PaymentPlanFragment {


    protected PaymentPlanDTO paymentPlanDTO;

    /**
     * @param paymentsModel  the payment model
     * @param paymentPlanDTO the plan to be edited
     * @return an PaymentPlanFragment instance with the payment plan data filled for editing a payment plan
     */
    public static PaymentPlanEditFragment newInstance(PaymentsModel paymentsModel,
                                                      PaymentPlanDTO paymentPlanDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);

        PaymentPlanEditFragment fragment = new PaymentPlanEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void attachCallback(Context context) {
        super.attachCallback(context);
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
        paymentPlanAmount = paymentPlanDTO.getPayload().getAmount();
        dateOptions = generateDateOptions();
        paymentDateOption = dateOptions.get(0);
        getPaymentPlanSettings(paymentPlanDTO.getMetadata().getPracticeId());
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    }


    @Override
    public void onViewCreated(final View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        setUpUI(view);
    }

    private void setUpUI(final View view) {
        planNameEditText.setText(paymentPlanDTO.getPayload().getDescription());
        planNameEditText.getOnFocusChangeListener().onFocusChange(planNameEditText, true);

        paymentDateEditText.setText(StringUtil.getOrdinal(getApplicationPreferences().getUserLanguage(),
                paymentPlanDTO.getPayload().getPaymentPlanDetails().getDayOfMonth()));

        numberPaymentsEditText.setText(String.valueOf(paymentPlanDTO.getPayload().getPaymentPlanDetails().getInstallments()));
        numberPaymentsEditText.getOnFocusChangeListener().onFocusChange(numberPaymentsEditText, true);

        monthlyPaymentEditText.setText(currencyFormatter.format(paymentPlanDTO.getPayload().getPaymentPlanDetails().getAmount()));
        createPlanButton.setText(Label.getLabel("save_button_label"));
        createPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields(true)) {
                    SystemUtil.hideSoftKeyboard(getContext(), view);
                    updatePaymentPlan();
                }
            }
        });
        View headerMessage = view.findViewById(R.id.headerMessage);
        if (headerMessage != null) {
            headerMessage.setVisibility(View.GONE);
        }
        View addExistingPlan = view.findViewById(R.id.payment_plan_add_existing);
        if (addExistingPlan != null) {
            addExistingPlan.setVisibility(View.GONE);
        }
        setUpPaymentMethodLabel(view);
        isCalclatingTime = false;
        isCalculatingAmount = false;
    }

    private void setUpPaymentMethodLabel(View view) {
        PaymentsPatientsCreditCardsPayloadListDTO creditCard = null;
        for (PaymentsPatientsCreditCardsPayloadListDTO creditCardModel :
                paymentsModel.getPaymentPayload().getPatientCreditCards()) {
            if (paymentPlanDTO.getPayload().getPaymentMethod().getPapiPaymentID()
                    .equals(creditCardModel.getPayload().getCreditCardsId())) {
                creditCard = creditCardModel;
                break;
            }
        }
        if (creditCard != null) {

            view.findViewById(R.id.paymentMethodContainer).setVisibility(View.VISIBLE);
            TextView paymentMethod = (TextView) view.findViewById(R.id.creditCardNumberTextView);
            String paymentMethodMessage = CreditCardUtil.getCreditCardType(creditCard.getPayload().getToken());
            if (paymentMethodMessage == null) {
                paymentMethodMessage = creditCard.getPayload().getCardType().toUpperCase();
            }
            paymentMethod.setText(paymentMethodMessage + " ***" + creditCard.getPayload().getCardNumber());
        }
    }

    private void updatePaymentPlan() {
        PaymentPlanPostModel postModel = new PaymentPlanPostModel();
        postModel.setAmount(paymentPlanAmount);
        postModel.setExecution(paymentPlanDTO.getPayload().getExecution());
        postModel.setLineItems(paymentPlanDTO.getPayload().getLineItems());
        postModel.setPapiPaymentMethod(paymentPlanDTO.getPayload().getPaymentMethod());
        postModel.setDescription(planNameEditText.getText().toString());

        PaymentPlanModel paymentPlanModel = new PaymentPlanModel();
        paymentPlanModel.setAmount(monthlyPaymentAmount);
        paymentPlanModel.setFrequencyCode(PaymentPlanModel.FREQUENCY_MONTHLY);
        paymentPlanModel.setInstallments(monthlyPaymentCount);
        paymentPlanModel.setEnabled(true);

        try {
            paymentPlanModel.setDayOfMonth(Integer.parseInt(paymentDateOption.getName()));
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        postModel.setPaymentPlanModel(paymentPlanModel);
        TransitionDTO updatePaymentTransition = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getUpdatePaymentPlan();


        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", paymentPlanDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", paymentPlanDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", paymentPlanDTO.getMetadata().getPatientId());
        queryMap.put("payment_plan_id", paymentPlanDTO.getMetadata().getPaymentPlanId());


        getWorkflowServiceHelper().execute(updatePaymentTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                dismiss();
                callback.onPaymentPlanEdited(workflowDTO);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();

            }
        }, new Gson().toJson(postModel), queryMap);
    }

}
