package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.payments.fragments.BaseAddCreditCardFragment;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingAddCreditCardFragment extends BaseAddCreditCardFragment implements BaseAddCreditCardFragment.IAuthoriseCreditCardResponse {

    private DemographicDTO demographicsSettingsDTO = null;
    private DemographicsSettingsFragmentListener callback;

    @Override
    protected void attachCallback(Context context) {
        try {
            callback = (DemographicsSettingsFragmentListener) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicDTO) callback.getDto();
        merchantServicesList = demographicsSettingsDTO.getPayload().getMerchantServices();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setAuthorizeCallback(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        title.setText(Label.getLabel("new_credit_card_heading"));
        nextButton.setText(Label.getLabel("settings.addCreditCard.button.label.addNew"));
        nextButton.setBackgroundResource(R.drawable.bg_green_selector);
        saveCardOnFileCheckBox.setChecked(true);
        saveCardOnFileCheckBox.setEnabled(false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
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
            DemographicDTO removeCreditCardResponseDTO = gson.fromJson(workflowDTO.toString(),
                    DemographicDTO.class);
            demographicsSettingsDTO.getPayload().setPatientCreditCards(removeCreditCardResponseDTO.getPayload().getPatientCreditCards());
            callback.onCreditCardOperation(demographicsSettingsDTO);
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
            getActivity().onBackPressed();

            MixPanelUtil.logEvent(getString(R.string.event_updated_credit_cards), getString(R.string.param_is_payment), false);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
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