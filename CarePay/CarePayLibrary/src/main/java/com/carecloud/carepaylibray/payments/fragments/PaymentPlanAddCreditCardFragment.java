package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 1/23/18
 */

public class PaymentPlanAddCreditCardFragment extends AddNewCreditCardFragment {

    private PaymentPlanCreateInterface callback;
    protected PaymentPlanPostModel paymentPlanPostModel;
    protected PaymentPlanDTO paymentPlanDTO;

    /**
     * @param paymentsModel        payment model
     * @param paymentPlanPostModel payment plan post model
     * @param onlySelectMode
     * @return new instance
     */
    public static PaymentPlanAddCreditCardFragment newInstance(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel, boolean onlySelectMode) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanPostModel);
        args.putBoolean(CarePayConstants.ONLY_SELECT_MODE, onlySelectMode);
        PaymentPlanAddCreditCardFragment fragment = new PaymentPlanAddCreditCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param paymentsModel  payment model
     * @param paymentPlanDTO payment plan details
     * @param onlySelectMode
     * @return new instance
     */
    public static PaymentPlanAddCreditCardFragment newInstance(PaymentsModel paymentsModel,
                                                               PaymentPlanDTO paymentPlanDTO,
                                                               boolean onlySelectMode) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putBoolean(CarePayConstants.ONLY_SELECT_MODE, onlySelectMode);
        PaymentPlanAddCreditCardFragment fragment = new PaymentPlanAddCreditCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PaymentPlanCreateInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            } else if (context instanceof AppointmentViewHandler) {
                callback = (PaymentPlanCreateInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PaymentPlanCreateInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentConfirmationInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentPlanPostModel = DtoHelper.getConvertedDTO(PaymentPlanPostModel.class, args);
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        saveCardOnFileCheckBox.setChecked(true);
        saveCardOnFileCheckBox.setEnabled(false);
        if (paymentPlanPostModel != null || onlySelectMode) {
            nextButton.setText(Label.getLabel("payment_plan_continue"));
        }
    }

    @Override
    protected void makePaymentCall() {
        PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
        papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
        papiPaymentMethod.setCardData(getCreditCardModel());

        if (paymentPlanPostModel != null) {
            paymentPlanPostModel.setPapiPaymentMethod(papiPaymentMethod);
            paymentPlanPostModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);

            callback.onDisplayPaymentPlanTerms(paymentsModel, paymentPlanPostModel);
        }

        if (paymentPlanDTO != null) {
            IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
            postModel.setPapiPaymentMethod(papiPaymentMethod);
            postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);

            makePlanPayment();
        }
    }

    private void makePlanPayment() {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", paymentPlanDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", paymentPlanDTO.getMetadata().getPracticeId());
        queries.put("patient_id", paymentPlanDTO.getMetadata().getPatientId());
        queries.put("payment_plan_id", paymentPlanDTO.getMetadata().getPaymentPlanId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        Gson gson = new Gson();
        String paymentModelJson = gson.toJson(paymentsModel.getPaymentPayload().getPaymentPostModel());
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePlanPayment();
        getWorkflowServiceHelper().execute(transitionDTO, makePaymentCallback, paymentModelJson, queries, header);
    }

    @Override
    protected void authorizeOrSelectCreditCard() {
        if (onlySelectMode) {
            creditCardsPayloadDTO.setCompleteNumber(creditCardNoEditText.getText().toString().replace(" ", "").trim());
            creditCardsPayloadDTO.setDefault(setAsDefaultCheckBox.isChecked());
            creditCardsPayloadDTO.setSaveCardOnFile(saveCardOnFileCheckBox.isChecked());
            callback.onCreditCardSelected(creditCardsPayloadDTO);
        } else {
            authorizeCreditCard();
        }
    }


}
