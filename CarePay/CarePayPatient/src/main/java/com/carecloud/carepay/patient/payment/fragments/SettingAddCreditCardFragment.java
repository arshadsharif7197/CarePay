package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.demographics.activities.DemographicsSettingsActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.payments.fragments.BaseAddCreditCardFragment;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingAddCreditCardFragment extends BaseAddCreditCardFragment implements BaseAddCreditCardFragment.IAuthoriseCreditCardResponse {

    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private DemographicsSettingsLabelsDTO settingsLabelsDTO;

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
        setChildFragment(this);
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
        saveCardOnFileCheckBox.setEnabled(true);
        setAsDefaultCheckBox.setText(settingsLabelsDTO.getEditCreditCardDefaultLabel());
        setAsDefaultCheckBox.setEnabled(true);

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
    }

    private WorkflowServiceCallback addNewCreditCardCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Gson gson = new Gson();
            DemographicsSettingsDTO removeCreditCardResponseDTO = gson.fromJson(workflowDTO.toString(),
                    DemographicsSettingsDTO.class);
            demographicsSettingsDTO.getPayload().setPatientCreditCards(removeCreditCardResponseDTO.getPayload()
                    .getPatientCreditCards());
            ((DemographicsSettingsActivity) getActivity()).onCreditCardOperation(demographicsSettingsDTO);
            getActivity().onBackPressed();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void addNewCreditCardCall() {
        Gson gson = new Gson();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", getApplicationPreferences().getUserLanguage());
        TransitionDTO transitionDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO().getTransitions().getAddCreditCard();
        String body = gson.toJson(creditCardsPayloadDTO);
        getWorkflowServiceHelper().execute(transitionDTO, addNewCreditCardCallback, body, queryMap, getWorkflowServiceHelper().getPreferredLanguageHeader());
    }

    @Override
    public void onAuthorizeCreditCardSuccess() {
        addNewCreditCardCall();
    }

    @Override
    public void onAuthorizeCreditCardFailed() {
        showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
        hideProgressDialog();
    }
}