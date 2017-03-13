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
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.CreditCardModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentType;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 3/1/17.
 */

public class AddNewCreditCardFragment extends BaseAddCreditCardFragment implements BaseAddCreditCardFragment.IAuthoriseCreditCardResponse{
    private PaymentsModel paymentsModel;
    private PaymentsLabelDTO paymentsLabelDTO;

    PaymentNavigationCallback callback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (PaymentNavigationCallback) context;
        }catch(ClassCastException cce){
            throw new ClassCastException("Attached context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentsLabelDTO = new PaymentsLabelDTO();
        Bundle arguments = getArguments();
        if (arguments != null) {
            Gson gson = new Gson();
            String paymentsDTOString = arguments.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            paymentsModel = gson.fromJson(paymentsDTOString, PaymentsModel.class);
            paymentsLabelDTO = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setChildFragment(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        title.setText(paymentsLabelDTO.getPaymentNewCreditCard());
        initilizeViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initilizeViews() {
        creditCardNoTextInput.setTag(paymentsLabelDTO.getPaymentCreditCardNumber());
        creditCardNoEditText.setHint(paymentsLabelDTO.getPaymentCreditCardNumber());

        nameOnCardTextInputLayout.setTag(paymentsLabelDTO.getPaymentNameOnCardText());
        nameOnCardEditText.setHint(paymentsLabelDTO.getPaymentNameOnCardText());

        verificationCodeTextInput.setTag(paymentsLabelDTO.getPaymentVerificationNumber());
        verificationCodeEditText.setHint(paymentsLabelDTO.getPaymentVerificationNumber());

        expirationDateTextView.setText(paymentsLabelDTO.getPaymentExpirationDate());
        pickDateTextView.setText(paymentsLabelDTO.getPaymentPickDate());

        saveCardOnFileCheckBox.setText(paymentsLabelDTO.getPaymentSaveCardOnFile());
        setAsDefaultCheckBox.setText(paymentsLabelDTO.getPaymentSetAsDefaultCreditCard());

        billingAddressTextView.setText(paymentsLabelDTO.getPaymentBillingAddressText());
        useProfileAddressCheckBox.setText(paymentsLabelDTO.getPaymentUseProfileAddress());

        address1TextInput.setTag(paymentsLabelDTO.getPaymentAddressLine1Text());
        address1EditText.setHint(paymentsLabelDTO.getPaymentAddressLine1Text());

        address2TextInput.setTag(paymentsLabelDTO.getPaymentAddressLine2Text());
        address2EditText.setHint(paymentsLabelDTO.getPaymentAddressLine2Text());

        zipCodeTextInput.setTag(paymentsLabelDTO.getPaymentZipcode());
        zipCodeEditText.setHint(paymentsLabelDTO.getPaymentZipcode());

        cityTextInput.setTag(paymentsLabelDTO.getPaymentCity());
        cityEditText.setHint(paymentsLabelDTO.getPaymentCity());

        stateTextInput.setTag(paymentsLabelDTO.getPaymentState());
        stateAutoCompleteTextView.setHint(paymentsLabelDTO.getPaymentState());

        nextButton.setText(paymentsLabelDTO.getPaymentPayText());

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
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            SystemUtil.showDefaultFailureDialog(getActivity());
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
            Gson gson = new Gson();
            callback.showReceipt(gson.fromJson(workflowDTO.toString(), PaymentsModel.class));
            if(getDialog()!=null){
                dismiss();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void addNewCreditCardCall() {
        Gson gson = new Gson();
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getAddCreditCard();
        String body = gson.toJson(creditCardsPayloadDTO);
        getWorkflowServiceHelper().execute(transitionDTO, addNewCreditCardCallback, body, getWorkflowServiceHelper().getPreferredLanguageHeader());
    }


    private void makePaymentCall() {
        if(amountToMakePayment == 0){
            return;//TODO something else needs to happen here...
        }

        CreditCardModel creditCardModel = new CreditCardModel();
        creditCardModel.setCardType(creditCardsPayloadDTO.getCardType());
        creditCardModel.setCardNumber(creditCardsPayloadDTO.getCardNumber());
        creditCardModel.setExpiryDate(creditCardsPayloadDTO.getExpireDt().replaceAll("/",""));
        creditCardModel.setNameOnCard(creditCardsPayloadDTO.getNameOnCard());
        creditCardModel.setToken(creditCardsPayloadDTO.getToken());
        creditCardModel.setCvv(creditCardsPayloadDTO.getCvv());
        creditCardModel.setSaveCard(saveCardOnFileCheckBox.isChecked());
        creditCardModel.setDefault(setAsDefaultCheckBox.isChecked());

        creditCardModel.setBillingInformation(billingInformationDTO);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(PaymentType.credit_card);
        paymentMethod.setExecution(PaymentExecution.papi);
        paymentMethod.setAmount(amountToMakePayment);
        paymentMethod.setCreditCard(creditCardModel);

        PaymentPostModel paymentPostModel = new PaymentPostModel();
        paymentPostModel.setAmount(amountToMakePayment);
        paymentPostModel.addPaymentMethod(paymentMethod);

        Gson gson = new Gson();
        if(paymentPostModel.isPaymentModelValid()){
            postPayment(gson.toJson(paymentPostModel));
        }else{
            Toast.makeText(getContext(), getString(R.string.payment_failed), Toast.LENGTH_SHORT).show();
        }



//        JSONObject payload = new JSONObject();
//        try {
//            payload.put("amount", amountToMakePayment);
//            JSONObject paymentMethod = new JSONObject();
//            paymentMethod.put("amount", amountToMakePayment);
//            JSONObject creditCard = new JSONObject();
//            creditCard.put("save", saveCardOnFileCheckBox.isChecked());
//            creditCard.put("card_type", creditCardsPayloadDTO.getCardType());
//            creditCard.put("card_number", creditCardsPayloadDTO.getCardNumber());
//            creditCard.put("name_on_card", creditCardsPayloadDTO.getNameOnCard());
//            creditCard.put("expire_dt", creditCardsPayloadDTO.getExpireDt());
//            creditCard.put("cvv", creditCardsPayloadDTO.getCvv());
//            creditCard.put("token", creditCardsPayloadDTO.getToken());
//            creditCard.put("is_default", setAsDefaultCheckBox.isChecked());
//            Gson gson = new Gson();
//            JSONObject billingInformation;
//            billingInformation = new JSONObject(gson.toJson(billingInformationDTO, PaymentsCreditCardBillingInformationDTO.class));
//            creditCard.put("billing_information", billingInformation);
//            paymentMethod.put("credit_card", creditCard);
//            paymentMethod.put("execution", "papi");
//            paymentMethod.put("type", "credit_card");
//            JSONArray paymentMethods = new JSONArray();
//            paymentMethods.put(paymentMethod);
//            payload.put("payment_methods", paymentMethods);
//            PendingBalanceMetadataDTO metadata = intakePaymentModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();
//            Map<String, String> queries = new HashMap<>();
//            queries.put("practice_mgmt", metadata.getPracticeMgmt());
//            queries.put("practice_id", metadata.getPracticeId());
//            queries.put("patient_id", metadata.getPatientId());
//            Map<String, String> header = new HashMap<>();
//            header.put("transition", "true");
//            TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment();
//
//            getWorkflowServiceHelper().execute(transitionDTO, makePaymentCallback, payload.toString(), queries, header);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void postPayment(String paymentModelJson){
        PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", metadata.getPracticeMgmt());
        queries.put("practice_id", metadata.getPracticeId());
        queries.put("patient_id", metadata.getPatientId());


        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment();
        getWorkflowServiceHelper().execute(transitionDTO, makePaymentCallback, paymentModelJson, queries, header);

    }


    @Override
    public void onAuthorizeCreditCardSuccess() {
        if (saveCardOnFileCheckBox.isChecked()) {
            addNewCreditCardCall();
        } else {
            makePaymentCall();
        }
    }

    @Override
    public void onAuthorizeCreditCardFailed() {
        new LargeAlertDialog(getActivity(), paymentsLabelDTO.getPaymentFailedErrorMessage(), paymentsLabelDTO.getPaymentChangeMethodButton(), R.color.Feldgrau, R.drawable.icn_card_error, new LargeAlertDialog.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                getFragmentManager().popBackStackImmediate();
            }
        }).show();
    }

}
