package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.payments.interfaces.PaymentConfirmationInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentCardData;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentMetadata;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 3/1/17
 */
public class AddNewCreditCardFragment extends BaseAddCreditCardFragment implements BaseAddCreditCardFragment.IAuthoriseCreditCardResponse {

    private UserPracticeDTO userPracticeDTO;

    @Override
    protected void attachCallback(Context context) {
        try {
            if (context instanceof PaymentViewHandler) {
                callback = ((PaymentViewHandler) context).getPaymentPresenter();
            } else if (context instanceof AppointmentViewHandler) {
                callback = (PaymentConfirmationInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PaymentConfirmationInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentConfirmationInterface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            Gson gson = new Gson();
            String paymentsDTOString = arguments.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            paymentsModel = gson.fromJson(paymentsDTOString, PaymentsModel.class);
            if (paymentsModel != null) {
                if (!paymentsModel.getPaymentPayload().getPatientBalances().isEmpty()) {
                    addressPayloadDTO = paymentsModel.getPaymentPayload().getPatientBalances().get(0)
                            .getDemographics().getPayload().getAddress();
                }
                userPracticeDTO = callback.getPracticeInfo(paymentsModel);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setAuthorizeCallback(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        title.setText(Label.getLabel("payment_new_credit_card"));
        nextButton.setText(Label.getLabel("add_credit_card_save_button_label"));
    }


    private WorkflowServiceCallback addNewCreditCardCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            Log.d("addNewCreditCard", "=========================>\nworkflowDTO=" + workflowDTO.toString());
            makePaymentCall();

            MixPanelUtil.logEvent(getString(R.string.event_updated_credit_cards));
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            SystemUtil.showErrorToast(getContext(), exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback makePaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            Log.d("makePaymentCallback", "=========================>\nworkflowDTO=" + workflowDTO.toString());
            callback.showPaymentConfirmation(workflowDTO);
            if (getDialog() != null) {
                dismiss();
            }

            String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
            Object[] values = {amountToMakePayment, getString(R.string.payment_new_card)};
            MixPanelUtil.logEvent(getString(R.string.event_payment_complete), params, values);
            MixPanelUtil.incrementPeopleProperty(getString(R.string.count_payments_completed), 1);
            MixPanelUtil.incrementPeopleProperty(getString(R.string.total_payments_amount), amountToMakePayment);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            SystemUtil.showErrorToast(getContext(), exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);

            String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
            Object[] values = {amountToMakePayment, getString(R.string.payment_new_card)};
            MixPanelUtil.logEvent(getString(R.string.event_payment_failed), params, values);
        }
    };

    private void addNewCreditCardCall() {
        Gson gson = new Gson();
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getAddCreditCard();
        String body = gson.toJson(creditCardsPayloadDTO);
        getWorkflowServiceHelper().execute(transitionDTO, addNewCreditCardCallback, body, getWorkflowServiceHelper().getPreferredLanguageHeader());
    }


    private void makePaymentCall() {
        IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        if (postModel != null && postModel.getAmount() > 0) {
            processPayment(postModel);
        } else {
            processPayment();
        }
    }

    private void processPayment(IntegratedPaymentPostModel postModel) {
        PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
        papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
        papiPaymentMethod.setCardData(getCreditCardModel());

        postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);
        postModel.setPapiPaymentMethod(papiPaymentMethod);

        IntegratedPaymentMetadata integratedPaymentMetadata = postModel.getMetadata();
        if (callback.getAppointmentId() != null && integratedPaymentMetadata.getAppointmentRequestDTO() == null) {
            integratedPaymentMetadata.setAppointmentId(callback.getAppointmentId());
        }

        Gson gson = new Gson();
        if (postModel.isPaymentModelValid()) {
            postPayment(gson.toJson(postModel));
        } else {
            Toast.makeText(getContext(), getString(R.string.payment_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void processPayment() {
        IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
        paymentLineItem.setAmount(amountToMakePayment);
        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_UNAPPLIED);
        paymentLineItem.setDescription("Unapplied Amount");

        PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
        papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
        papiPaymentMethod.setCardData(getCreditCardModel());

        IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
        postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);
        postModel.setPapiPaymentMethod(papiPaymentMethod);
        postModel.setAmount(amountToMakePayment);
        postModel.addLineItem(paymentLineItem);

        IntegratedPaymentMetadata postModelMetadata = postModel.getMetadata();
        postModelMetadata.setAppointmentId(callback.getAppointmentId());

        Gson gson = new Gson();
        if (postModel.isPaymentModelValid()) {
            postPayment(gson.toJson(postModel));
        } else {
            Toast.makeText(getContext(), getString(R.string.payment_failed), Toast.LENGTH_SHORT).show();
        }

    }

    private void postPayment(String paymentModelJson) {
        Map<String, String> queries = new HashMap<>();
        if (userPracticeDTO != null) {
            queries.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
            queries.put("practice_id", userPracticeDTO.getPracticeId());
            queries.put("patient_id", userPracticeDTO.getPatientId());
        } else {
            PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();
            queries.put("practice_mgmt", metadata.getPracticeMgmt());
            queries.put("practice_id", metadata.getPracticeId());
            queries.put("patient_id", metadata.getPatientId());
        }
        if (callback.getAppointmentId() != null) {
            queries.put("appointment_id", callback.getAppointmentId());
        }
        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment();
        getWorkflowServiceHelper().execute(transitionDTO, makePaymentCallback, paymentModelJson, queries, header);

        String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
        Object[] values = {amountToMakePayment, getString(R.string.payment_new_card)};
        MixPanelUtil.logEvent(getString(R.string.event_payment_started), params, values);

    }

    private IntegratedPaymentCardData getCreditCardModel() {
        IntegratedPaymentCardData creditCardModel = new IntegratedPaymentCardData();
        creditCardModel.setCardType(creditCardsPayloadDTO.getCardType());
        creditCardModel.setCardNumber(creditCardsPayloadDTO.getCardNumber());
        creditCardModel.setExpiryDate(creditCardsPayloadDTO.getExpireDt().replaceAll("/", ""));
        creditCardModel.setNameOnCard(creditCardsPayloadDTO.getNameOnCard());
        creditCardModel.setToken(creditCardsPayloadDTO.getToken());
        creditCardModel.setSaveCard(saveCardOnFileCheckBox.isChecked());
        creditCardModel.setDefault(setAsDefaultCheckBox.isChecked());

        @IntegratedPaymentCardData.TokenizationService String tokenizationService = creditCardsPayloadDTO.getTokenizationService().toString();
        creditCardModel.setTokenizationService(tokenizationService);

        return creditCardModel;
    }


    @Override
    public void onAuthorizeCreditCardSuccess() {
        nextButton.setEnabled(true);
        if (saveCardOnFileCheckBox.isChecked()) {
            addNewCreditCardCall();
        } else {
            makePaymentCall();
        }
    }

    @Override
    public void onAuthorizeCreditCardFailed() {
        nextButton.setEnabled(true);
        new LargeAlertDialog(getActivity(), Label.getLabel("payment_failed_error"), Label.getLabel("payment_change_payment_label"), R.color.Feldgrau, R.drawable.icn_card_error, getLargeAlertInterface()).show();
    }

    protected LargeAlertDialog.LargeAlertInterface getLargeAlertInterface() {
        return new LargeAlertDialog.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                callback.onPayButtonClicked(amountToMakePayment, paymentsModel);
                dismiss();
            }
        };
    }

}
