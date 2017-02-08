package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.demographics.activities.DemographicsSettingsActivity;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsCreditCardBillingInformationDTO;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.payeezysdk.sdk.payeezydirecttransactions.RequestTask;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingAddCreditCardFragment extends BaseAddCreditCardFragment implements RequestTask.AuthorizeCreditCardCallback {

    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private DemographicsSettingsLabelsDTO settingsLabelsDTO;
    private PaymentCreditCardsPayloadDTO creditCardsPayloadDTO;
    private PaymentsCreditCardBillingInformationDTO billingInformationDTO;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            Gson gson = new Gson();
            String demographicsSettingsDTOString = arguments.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
            settingsLabelsDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO().getLabels();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        title.setText(settingsLabelsDTO.getNewCreditCardHeadingLabel());
        initilizeViews();
    }

    private void initilizeViews() {
        creditCardNoTextInput.setTag(settingsLabelsDTO.getCreditCardNumberLabel());
        creditCardNoEditText.setHint(settingsLabelsDTO.getCreditCardNumberLabel());

        nameOnCardTextInputLayout.setTag(settingsLabelsDTO.getCreditCardNameLabel());
        nameOnCardEditText.setHint(settingsLabelsDTO.getCreditCardNameLabel());

        verificationCodeTextInput.setTag(settingsLabelsDTO.getCreditCardCvcLabel());
        verificationCodeEditText.setHint(settingsLabelsDTO.getCreditCardCvcLabel());

        expirationDateTextView.setText(settingsLabelsDTO.getCreditCardExpirationLabel());
        pickDateTextView.setText(settingsLabelsDTO.getCreditCardPickDateLabel());

        saveCardOnFileCheckBox.setVisibility(View.GONE);
        setAsDefaultCheckBox.setText(settingsLabelsDTO.getEditCreditCardDefaultLabel());

        billingAddressTextView.setText(settingsLabelsDTO.getCreditCardBillingAddressLabel());
        useProfileAddressCheckBox.setText(settingsLabelsDTO.getSettingUseProfileAddressLabel());

        address1TextInput.setTag(settingsLabelsDTO.getAddress1Label());
        address1EditText.setHint(settingsLabelsDTO.getAddress1Label());

        address2TextInput.setTag(settingsLabelsDTO.getAddress2Label());
        address2EditText.setHint(settingsLabelsDTO.getAddress2Label());

        zipCodeTextInput.setTag(settingsLabelsDTO.getZipcodeLabel());
        zipCodeEditText.setHint(settingsLabelsDTO.getZipcodeLabel());

        cityTextInput.setTag(settingsLabelsDTO.getCityLabel());
        cityEditText.setHint(settingsLabelsDTO.getCityLabel());

        stateTextInput.setTag(settingsLabelsDTO.getStateLabel());
        stateAutoCompleteTextView.setHint(settingsLabelsDTO.getStateLabel());

        nextButton.setText(settingsLabelsDTO.getCreditCardAddNew());
        nextButton.setOnClickListener(nextButtonListener);
    }

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setDTOs();
            ProgressDialogUtil.getInstance(getActivity()).show();
            authorizeCreditCard();
        }
    };

    private WorkflowServiceCallback addNewCreditCardCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            DemographicsSettingsDTO removeCreditCardResponseDTO = gson.fromJson(workflowDTO.toString(),
                    DemographicsSettingsDTO.class);
            demographicsSettingsDTO.getPayload().setPatientCreditCards(removeCreditCardResponseDTO.getPayload()
                    .getPatientCreditCards());
            ((DemographicsSettingsActivity) getActivity()).onCreditCardOperation(demographicsSettingsDTO);
            ProgressDialogUtil.getInstance(getActivity()).dismiss();
            getActivity().onBackPressed();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDefaultFailureDialog(getActivity());
            ProgressDialogUtil.getInstance(getActivity()).dismiss();
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void setDTOs() {
        creditCardsPayloadDTO = new PaymentCreditCardsPayloadDTO();
        billingInformationDTO = new PaymentsCreditCardBillingInformationDTO();
        billingInformationDTO.setSameAsPatient(useProfileAddressCheckBox.isChecked());
        creditCardsPayloadDTO.setCardNumber(getLastFour());
        creditCardsPayloadDTO.setNameOnCard(nameOnCardEditText.getText().toString().trim());
        creditCardsPayloadDTO.setCvv(Integer.parseInt(verificationCodeEditText.getText().toString().trim()));
        String expiryDate = pickDateTextView.getText().toString();
        expiryDate = expiryDate.substring(0, 2) + expiryDate.substring(expiryDate.length() - 2);
        creditCardsPayloadDTO.setExpireDt(expiryDate);
        creditCardsPayloadDTO.setCardType(getCreditCardType(getCardNumber()));
        billingInformationDTO.setLine1(address1EditText.getText().toString().trim());
        billingInformationDTO.setLine2(address2EditText.getText().toString().trim());
        billingInformationDTO.setZip(zipCodeEditText.getText().toString().trim());
        billingInformationDTO.setCity(cityEditText.getText().toString().trim());
        billingInformationDTO.setState(stateAutoCompleteTextView.getText().toString().trim());
        creditCardsPayloadDTO.setBillingInformation(billingInformationDTO);
    }

    private void authorizeCreditCard() {
        String amount = "1";
        String currency = "USD";
        String paymentMethod = "credit_card";
        String cvv = creditCardsPayloadDTO.getCvv() + "";
        String expiryDate = creditCardsPayloadDTO.getExpireDt();
        String name = creditCardsPayloadDTO.getNameOnCard();
        String type = creditCardsPayloadDTO.getCardType();
        String number = getCardNumber();
        String state = billingInformationDTO.getState();
        String addressline1 = billingInformationDTO.getLine1();
        String zip = billingInformationDTO.getZip();
        String country = "US";
        String city = billingInformationDTO.getCity();

        try {
            RequestTask requestTask = new RequestTask(getActivity(), SettingAddCreditCardFragment.this);
            requestTask.execute("authorize", amount, currency, paymentMethod, cvv, expiryDate, name, type, number, state, addressline1, zip, country, city);
            System.out.println("first authorize call end");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("authorize call end");
    }

    @Override
    public void onAuthorizeCreditCard(String resString) {

        if (resString != null && resString.length() > 800) {
            int startIndex = resString.indexOf("value");
            startIndex = resString.indexOf("=", startIndex + 1);
            int endIndex = resString.indexOf(",", startIndex);
            String tokenValue = resString.substring(startIndex, endIndex);
            tokenValue = tokenValue.replace(" ", "");
            tokenValue = tokenValue.replace(":", "");
            tokenValue = tokenValue.replace("=", "");
            tokenValue = tokenValue.replace("}", "");
            creditCardsPayloadDTO.setToken(tokenValue);

            addNewCreditCardCall();
        } else {
            SystemUtil.showDefaultFailureDialog(getActivity());
            ProgressDialogUtil.getInstance(getActivity()).dismiss();
        }
    }

    private void addNewCreditCardCall() {
        Gson gson = new Gson();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
        TransitionDTO transitionDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO().getTransitions().getAddCreditCard();
        String body = gson.toJson(creditCardsPayloadDTO);
        WorkflowServiceHelper.getInstance().execute(transitionDTO, addNewCreditCardCallback, body, queryMap, WorkflowServiceHelper.getPreferredLanguageHeader());
    }
}