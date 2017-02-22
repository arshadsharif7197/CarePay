package com.carecloud.carepay.patient.payment.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.utils.PatientAppUtil;
import com.carecloud.carepay.patient.demographics.activities.DemographicsSettingsActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadAddressDTO;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsCreditCardDetailsFragment extends BaseFragment {

    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO = null;
    private DemographicsSettingsLabelsDTO settingsLabelsDTO;
    private static final String TAG = SettingsCreditCardDetailsFragment.class.getName();

    public interface IOnCreditCardOperationSuccess {
        void onCreditCardOperation(DemographicsSettingsDTO demographicsSettingsDTO);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            Gson gson = new Gson();
            String demographicsSettingsDTOString = arguments.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);

            demographicsSettingsDTOString = arguments.getString(CarePayConstants.CREDIT_CARD_BUNDLE);
            creditCardsPayloadDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsCreditCardsPayloadDTO.class);

            settingsLabelsDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO().getLabels();
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_credit_card_details, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(StringUtil.getFormattedCardNumber(creditCardsPayloadDTO.getPayload()
                .getCardType(), creditCardsPayloadDTO.getPayload().getCardNumber()));
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");

        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {

        Button setAsDefaultButton = (Button) view.findViewById(R.id.setAsDefaultButton);
        setAsDefaultButton.setOnClickListener(setAsDefaultButtonListener);
        setAsDefaultButton.setText(settingsLabelsDTO.getSettingSetAsDefaultLabel());

        if (demographicsSettingsDTO != null) {
            try {
                CarePayTextView nameOnCardLabel = (CarePayTextView) view.findViewById(R.id.nameOnCardLabel);
                CarePayTextView nameOnCardValue = (CarePayTextView) view.findViewById(R.id.nameOnCardValue);
                nameOnCardLabel.setText(settingsLabelsDTO.getCreditCardNameLabel());
                nameOnCardValue.setText(creditCardsPayloadDTO.getPayload().getNameOnCard());

                CarePayTextView cardNumberLabel = (CarePayTextView) view.findViewById(R.id.cardNumberLabel);
                CarePayTextView cardNumberValue = (CarePayTextView) view.findViewById(R.id.cardNumberValue);
                cardNumberLabel.setText(settingsLabelsDTO.getCreditCardNumberLabel());
                cardNumberValue.setText(creditCardsPayloadDTO.getPayload().getCardNumber());

                CarePayTextView expirationDateLabel = (CarePayTextView) view.findViewById(R.id.expirationDateLabel);
                CarePayTextView expirationDateValue = (CarePayTextView) view.findViewById(R.id.expirationDateValue);
                expirationDateLabel.setText(settingsLabelsDTO.getCreditCardExpirationLabel());
                expirationDateValue.setText(creditCardsPayloadDTO.getPayload().getExpireDt());

                CarePayTextView billingAddressLabel = (CarePayTextView) view.findViewById(R.id.billingAddressLabel);
                billingAddressLabel.setText(settingsLabelsDTO.getCreditCardBillingAddressLabel());

                CarePayTextView addressLabel = (CarePayTextView) view.findViewById(R.id.addressLabel);
                CarePayTextView addressValue = (CarePayTextView) view.findViewById(R.id.addressValue);
                addressLabel.setText(settingsLabelsDTO.getSettingAddressLabel());

                DemographicsSettingsPayloadAddressDTO addressDTO = demographicsSettingsDTO.getPayload().getDemographics()
                        .getPayload().getAddress();
                addressValue.setText(addressDTO.getAddress1());

                CarePayTextView zipcodeLabel = (CarePayTextView) view.findViewById(R.id.zipcodeLabel);
                CarePayTextView zipcodeValue = (CarePayTextView) view.findViewById(R.id.zipcodeValue);
                zipcodeLabel.setText(settingsLabelsDTO.getZipcodeLabel());
                zipcodeValue.setText(addressDTO.getZipcode());

                CarePayTextView cityLabel = (CarePayTextView) view.findViewById(R.id.cityLabel);
                CarePayTextView cityValue = (CarePayTextView) view.findViewById(R.id.cityValue);
                cityLabel.setText(settingsLabelsDTO.getCityLabel());
                cityValue.setText(addressDTO.getCity());

                CarePayTextView stateLabel = (CarePayTextView) view.findViewById(R.id.stateLabel);
                CarePayTextView stateValue = (CarePayTextView) view.findViewById(R.id.stateValue);
                stateLabel.setText(settingsLabelsDTO.getStateLabel());
                stateValue.setText(addressDTO.getState());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.setting_credit_card, menu);
        menu.getItem(0).setTitle(settingsLabelsDTO.getEditCreditCardRemoveLabel());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_remove_credit_card) {
            showConfirmRemoveDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener setAsDefaultButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setAsDefaultRequest();
        }
    };

    private void setAsDefaultRequest() {
        try {
            JSONObject setAsDefaultPayload = new JSONObject();
            setAsDefaultPayload.put("card_id", creditCardsPayloadDTO.getPayload().getHashCreditCardsId());
            setAsDefaultPayload.put("is_default", true);
            String body = setAsDefaultPayload.toString();
            TransitionDTO transitionDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO().getTransitions().getUpdateCreditCard();
            getWorkflowServiceHelper().execute(transitionDTO, creditCardOperationCallback, body, null, getWorkflowServiceHelper().getPreferredLanguageHeader());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeCreditCardRequest() {
        try {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("card_id", creditCardsPayloadDTO.getPayload().getHashCreditCardsId());

            TransitionDTO transitionDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO().getTransitions().getDeleteCreditCard();
            getWorkflowServiceHelper().execute(transitionDTO, creditCardOperationCallback, queryMap, getWorkflowServiceHelper().getPreferredLanguageHeader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WorkflowServiceCallback creditCardOperationCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            try {
                hideProgressDialog();
                Gson gson = new Gson();
                DemographicsSettingsDTO removeCreditCardResponseDTO = gson.fromJson(workflowDTO.toString(),
                        DemographicsSettingsDTO.class);
                demographicsSettingsDTO.getPayload().setPatientCreditCards(removeCreditCardResponseDTO.getPayload()
                        .getPatientCreditCards());
                ((DemographicsSettingsActivity) getActivity()).onCreditCardOperation(demographicsSettingsDTO);
                getActivity().onBackPressed();

                PatientAppUtil.showSuccessNotification(getActivity(), getView(), demographicsSettingsDTO.getDemographicsSettingsMetadataDTO().getLabels().getSettingsSavedSuccessMessage());
            } catch(Exception e) {
                Log.e(TAG, "Credit Card onPostExecute" + e.getMessage());
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            System.out.print(exceptionMessage);
            SystemUtil.showDefaultFailureDialog(getActivity());
        }
    };

    private void showConfirmRemoveDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage("This credit card will be removed?")
                .setPositiveButton(settingsLabelsDTO.getEditCreditCardRemoveLabel(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        removeCreditCardRequest();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
