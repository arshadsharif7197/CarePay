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
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.payments.fragments.BaseAddCreditCardFragment;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingAddCreditCardFragment extends BaseAddCreditCardFragment implements BaseAddCreditCardFragment.IAuthoriseCreditCardResponse {

    private DemographicsSettingsDTO demographicsSettingsDTO = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            Gson gson = new Gson();
            String demographicsSettingsDTOString = arguments.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setAuthorizeCallback(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        title.setText(Label.getLabel("new_credit_card_heading"));
        initilizeViews();
    }

    private void initilizeViews() {
        creditCardNoTextInput.setTag(Label.getLabel("credit_card_number_label"));
        creditCardNoEditText.setHint(Label.getLabel("credit_card_number_label"));

        nameOnCardTextInputLayout.setTag(Label.getLabel("credit_card_name_label"));
        nameOnCardEditText.setHint(Label.getLabel("credit_card_name_label"));

        verificationCodeTextInput.setTag(Label.getLabel("credit_card_cvc_label"));
        verificationCodeEditText.setHint(Label.getLabel("credit_card_cvc_label"));

        expirationDateTextView.setText(Label.getLabel("credit_card_expiration_label"));
        pickDateTextView.setText(Label.getLabel("credit_card_pick_date_label"));

        saveCardOnFileCheckBox.setVisibility(View.GONE);
        saveCardOnFileCheckBox.setEnabled(true);
        setAsDefaultCheckBox.setText(Label.getLabel("edit_credit_card_default_label"));
        setAsDefaultCheckBox.setEnabled(true);

        billingAddressTextView.setText(Label.getLabel("credit_card_billing_address_label"));
        useProfileAddressCheckBox.setText(Label.getLabel("setting_use_profile_address"));

        address1TextInput.setTag(Label.getLabel("address1_label"));
        address1EditText.setHint(Label.getLabel("address1_label"));

        address2TextInput.setTag(Label.getLabel("address2_label"));
        address2EditText.setHint(Label.getLabel("address2_label"));

        zipCodeTextInput.setTag(Label.getLabel("zipcode_label"));
        zipCodeEditText.setHint(Label.getLabel("zipcode_label"));

        cityTextInput.setTag(Label.getLabel("city_label"));
        cityEditText.setHint(Label.getLabel("city_label"));

        stateTextInput.setTag(Label.getLabel("state_label"));
        stateAutoCompleteTextView.setHint(Label.getLabel("state_label"));

        nextButton.setText(Label.getLabel("credit_card_add_new"));
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
        TransitionDTO transitionDTO = demographicsSettingsDTO.getMetadata().getTransitions().getAddCreditCard();
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