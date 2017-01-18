package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadAddressDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsCreditCardDetailsFragment extends Fragment {

    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO = null;
    private DemographicsSettingsLabelsDTO settingsLabelsDTO;

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
        title.setText(StringUtil.getEncodedCardNumber(creditCardsPayloadDTO.getPayload()
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
        }
    }

    private View.OnClickListener setAsDefaultButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private WorkflowServiceCallback setAsDefaultCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {

        }

        @Override
        public void onFailure(String exceptionMessage) {
            System.out.print(exceptionMessage);
        }
    };
}
