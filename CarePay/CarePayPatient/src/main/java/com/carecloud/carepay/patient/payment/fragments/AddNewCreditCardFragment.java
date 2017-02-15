package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.dialogs.PaymentAmountReceiptDialog;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.payments.fragments.BaseAddCreditCardFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPayloadMetaDataDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsCreditCardBillingInformationDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewCreditCardFragment extends BaseAddCreditCardFragment implements BaseAddCreditCardFragment.IAuthoriseCreditCardResponse {

    private PaymentsModel paymentsModel;
    private PaymentsLabelDTO paymentsLabelDTO;
    private PaymentsModel intakePaymentModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentsLabelDTO = new PaymentsLabelDTO();
        Bundle arguments = getArguments();
        if (arguments != null) {
            Gson gson = new Gson();
            String paymentsDTOString = arguments.getString(CarePayConstants.INTAKE_BUNDLE);
            paymentsModel = gson.fromJson(paymentsDTOString, PaymentsModel.class);
            paymentsDTOString = arguments.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            intakePaymentModel = gson.fromJson(paymentsDTOString, PaymentsModel.class);
            paymentsLabelDTO = intakePaymentModel.getPaymentsMetadata().getPaymentsLabel();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setChildFragment(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        title.setText(paymentsLabelDTO.getPaymentNewCreditCard());
        initilizeViews();
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
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            nextButton.setEnabled(true);
            Log.d("addNewCreditCard", "=========================>\nworkflowDTO=" + workflowDTO.toString());
            makePaymentCall();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            nextButton.setEnabled(true);
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback makePaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            nextButton.setEnabled(true);
            Log.d("makePaymentCallback", "=========================>\nworkflowDTO=" + workflowDTO.toString());
            Gson gson = new Gson();
            PaymentAmountReceiptDialog receiptDialog = new PaymentAmountReceiptDialog(getActivity(),
                    gson.fromJson(workflowDTO.toString(), PaymentsModel.class));
            receiptDialog.show();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            nextButton.setEnabled(true);
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void addNewCreditCardCall() {
        Gson gson = new Gson();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
        queryMap.put("practice_mgmt", intakePaymentModel.getPaymentPayload().getPaymentSettings().getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", intakePaymentModel.getPaymentPayload().getPaymentSettings().getMetadata().getPracticeId());
        queryMap.put("patient_id", intakePaymentModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata().getPatientId());
        TransitionDTO transitionDTO = intakePaymentModel.getPaymentsMetadata().getPaymentsTransitions().getAddCreditCard();
        String body = gson.toJson(creditCardsPayloadDTO);
        WorkflowServiceHelper.getInstance().execute(transitionDTO, addNewCreditCardCallback, body, queryMap, WorkflowServiceHelper.getPreferredLanguageHeader());
    }

    private void makePaymentCall() {

        JSONObject payload = new JSONObject();
        try {
            payload.put("amount", amountToMakePayment);
            JSONObject paymentMethod = new JSONObject();
            paymentMethod.put("amount", amountToMakePayment);
            JSONObject creditCard = new JSONObject();
            creditCard.put("save", saveCardOnFileCheckBox.isChecked());
            creditCard.put("card_type", creditCardsPayloadDTO.getCardType());
            creditCard.put("card_number", creditCardsPayloadDTO.getCardNumber());
            creditCard.put("name_on_card", creditCardsPayloadDTO.getNameOnCard());
            creditCard.put("expire_dt", creditCardsPayloadDTO.getExpireDt());
            creditCard.put("cvv", creditCardsPayloadDTO.getCvv());
            creditCard.put("token", creditCardsPayloadDTO.getToken());
            creditCard.put("is_default", setAsDefaultCheckBox.isChecked());
            Gson gson = new Gson();
            JSONObject billingInformation;
            billingInformation = new JSONObject(gson.toJson(billingInformationDTO, PaymentsCreditCardBillingInformationDTO.class));
            creditCard.put("billing_information", billingInformation);
            paymentMethod.put("credit_card", creditCard);
            paymentMethod.put("execution", "papi");
            paymentMethod.put("type", "credit_card");
            JSONArray paymentMethods = new JSONArray();
            paymentMethods.put(paymentMethod);
            payload.put("payment_methods", paymentMethods);
            PaymentPayloadMetaDataDTO metadata = intakePaymentModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();
            Map<String, String> queries = new HashMap<>();
            queries.put("practice_mgmt", metadata.getPracticeMgmt());
            queries.put("practice_id", metadata.getPracticeId());
            queries.put("patient_id", metadata.getPatientId());
            Map<String, String> header = new HashMap<>();
            header.put("transition", "true");
            TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment();

            WorkflowServiceHelper.getInstance().execute(transitionDTO, makePaymentCallback, payload.toString(), queries, header);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                FragmentManager fm = getActivity().getSupportFragmentManager();
                List<Fragment> backStackFragmentList = fm.getFragments();
                if (backStackFragmentList != null && backStackFragmentList.size() > 0) {
                    int index;
                    for (index = 0; index < backStackFragmentList.size(); index++) {
                        if (backStackFragmentList.get(index) instanceof ChooseCreditCardFragment) {
                            fm.beginTransaction().remove(backStackFragmentList.get(index)).commit();
                            fm.popBackStack();
                            fm.popBackStack();
                            break;
                        }
                    }
                    if (index == backStackFragmentList.size()) {
                        fm.popBackStack();
                    }
                }
            }
        }).show();
    }
}