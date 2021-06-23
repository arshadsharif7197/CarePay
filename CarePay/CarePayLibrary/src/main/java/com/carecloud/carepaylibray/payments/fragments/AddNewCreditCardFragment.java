package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

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
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialogFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentConfirmationInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentCardData;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentMetadata;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 3/1/17
 */
public class AddNewCreditCardFragment extends BaseAddCreditCardFragment
        implements BaseAddCreditCardFragment.IAuthoriseCreditCardResponse {

    private UserPracticeDTO userPracticeDTO;

    public static AddNewCreditCardFragment newInstance(PaymentsModel paymentsDTO, double amount) {
        Bundle args = new Bundle();
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        DtoHelper.bundleDto(args, paymentsDTO);
        AddNewCreditCardFragment addNewCreditCardFragment = new AddNewCreditCardFragment();
        addNewCreditCardFragment.setArguments(args);
        return addNewCreditCardFragment;
    }

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
//            String paymentsDTOString = arguments.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, arguments);
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
//            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
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

    protected WorkflowServiceCallback makePaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
//            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            Log.d("makePaymentCallback", "=========================>\nworkflowDTO=" + workflowDTO.toString());


            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload().getPatientPayments().getPayload();
            if (!payload.getProcessingErrors().isEmpty() && payload.getTotalPaid() == 0D) {
                String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
                Object[] values = {amountToMakePayment, getString(R.string.payment_card_on_file)};
                MixPanelUtil.logEvent(getString(R.string.event_payment_failed), params, values);
                callback.showErrorToast(payload.getProcessingErrors().get(0).getError());
                if (getDialog() != null) {
                    dismiss();
                }
            } else {
                if (getDialog() != null) {
                    dismiss();
                }
                String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
                Object[] values = {amountToMakePayment, getString(R.string.payment_new_card)};
                MixPanelUtil.logEvent(getString(R.string.event_payment_complete), params, values);
                MixPanelUtil.incrementPeopleProperty(getString(R.string.count_payments_completed), 1);
                MixPanelUtil.incrementPeopleProperty(getString(R.string.total_payments_amount), amountToMakePayment);
                showConfirmation(workflowDTO);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            SystemUtil.showErrorToast(getContext(), exceptionMessage);
            Log.e("Server Error", exceptionMessage);

            String[] params = {getActivity().getString(R.string.param_payment_amount), getActivity().getString(R.string.param_payment_type)};
            Object[] values = {amountToMakePayment, getString(R.string.payment_new_card)};
            MixPanelUtil.logEvent(getString(R.string.event_payment_failed), params, values);
        }
    };

    private void addNewCreditCardCall() {
        Gson gson = new Gson();
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getAddCreditCard();
        String body = gson.toJson(creditCardsPayloadDTO);
        Map<String, String> queryMap = new HashMap<>();

        PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();
        queryMap.put("practice_mgmt", metadata.getPracticeMgmt());
        queryMap.put("practice_id", metadata.getPracticeId());
        queryMap.put("patient_id", metadata.getPatientId());

        getWorkflowServiceHelper().execute(transitionDTO, addNewCreditCardCallback, body, queryMap, getWorkflowServiceHelper().getPreferredLanguageHeader());
    }


    protected void makePaymentCall() {
        IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        if (postModel != null && postModel.getAmount() > 0) {
            processPayment(postModel);
        } else {
            processPayment();
        }
    }

    protected void showConfirmation(WorkflowDTO workflowDTO) {
        callback.showPaymentConfirmation(workflowDTO);
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
            postPayment(postModel);
        } else {
            Toast.makeText(getContext(), getString(R.string.payment_failed), Toast.LENGTH_SHORT).show();
            hideProgressDialog();
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
            postPayment(postModel);
        } else {
            Toast.makeText(getContext(), getString(R.string.payment_failed), Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        }

    }

    private void postPayment(IntegratedPaymentPostModel paymentModelJson) {
        Map<String, String> queries = new HashMap<>();

        if (paymentModelJson.getQueryMetadata() != null) {
            queries.put("practice_mgmt", paymentModelJson.getQueryMetadata().getPracticeMgmt());
            queries.put("practice_id", paymentModelJson.getQueryMetadata().getPracticeId());
            queries.put("patient_id", paymentModelJson.getQueryMetadata().getPatientId());
            paymentModelJson.setQueryMetadata(null);
        } else if (userPracticeDTO != null) {
            queries.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
            queries.put("practice_id", userPracticeDTO.getPracticeId());
            queries.put("patient_id", userPracticeDTO.getPatientId());
        } else {
            PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();
            queries.put("practice_mgmt", metadata.getPracticeMgmt());
            queries.put("practice_id", metadata.getPracticeId());
            queries.put("patient_id", metadata.getPatientId());
        }

        if (paymentsModel.getPaymentPayload().getPaymentPostModel() != null &&
                !StringUtil.isNullOrEmpty(paymentsModel.getPaymentPayload().getPaymentPostModel().getOrderId())) {
            IntegratedPaymentPostModel paymentPostModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
            queries.put("store_id", paymentPostModel.getStoreId());
            queries.put("transaction_id", paymentPostModel.getOrderId());
        }

        if (callback.getAppointmentId() != null) {
            queries.put("appointment_id", callback.getAppointmentId());
        }

        if (queries.get("patient_id") == null) {
            queries.remove("patient_id");
            if (callback.getAppointment() != null) {
                queries.put("patient_id", callback.getAppointment().getMetadata().getPatientId());
            } else {
                for (PatientBalanceDTO patientBalanceDTO : paymentsModel.getPaymentPayload().getPatientBalances()) {
                    if (patientBalanceDTO.getBalances().get(0).getMetadata().getPracticeId().equals(queries.get("practice_id"))) {
                        queries.put("patient_id", patientBalanceDTO.getBalances().get(0).getMetadata().getPatientId());
                    }
                }
            }
        }

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment();
        Gson gson = new Gson();
        getWorkflowServiceHelper().execute(transitionDTO, makePaymentCallback, gson.toJson(paymentModelJson), queries, header);

        String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
        Object[] values = {amountToMakePayment, getString(R.string.payment_new_card)};
        MixPanelUtil.logEvent(getString(R.string.event_payment_started), params, values);

    }

    protected IntegratedPaymentCardData getCreditCardModel() {
        IntegratedPaymentCardData creditCardModel = new IntegratedPaymentCardData();
        creditCardModel.setCardType(creditCardsPayloadDTO.getCardType());
        creditCardModel.setCardNumber(creditCardsPayloadDTO.getCardNumber());
        creditCardModel.setExpiryDate(creditCardsPayloadDTO.getExpireDt().replaceAll("/", ""));
        creditCardModel.setNameOnCard(creditCardsPayloadDTO.getNameOnCard());
        creditCardModel.setToken(creditCardsPayloadDTO.getToken());
        creditCardModel.setSaveCard(saveCardOnFileCheckBox.isChecked());
        creditCardModel.setDefault(setAsDefaultCheckBox.isChecked());
        creditCardModel.setCvv(creditCardsPayloadDTO.getCvv());
        creditCardModel.setCard_number(creditCardsPayloadDTO.getCompleteNumber());

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
        hideProgressDialog();
        nextButton.setEnabled(true);
        creditCardNoEditText.requestFocus();
        SystemUtil.showErrorToast(getActivity(), Label.getLabel("payment_invalid_cc_error_text"));
    }

    protected LargeAlertDialogFragment.LargeAlertInterface getLargeAlertInterface() {
        return new LargeAlertDialogFragment.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                callback.onPayButtonClicked(amountToMakePayment, paymentsModel);
                dismiss();
            }
        };
    }

}
