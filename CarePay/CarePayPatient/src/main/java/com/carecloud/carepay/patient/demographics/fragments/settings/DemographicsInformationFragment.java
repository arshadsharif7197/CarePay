package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsAddressDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCityDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDataModelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDateOfBirthDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDriversLicenseDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsEthnicityDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsFirstNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsGenderDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLastNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMiddleNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadAddressDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPropertiesDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPhoneDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPreferredLanguageDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPrimaryRaceDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsProfilePhotoDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsStateDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsTransitionsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsZipDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsInformationFragment extends Fragment {
    private static final String LOG_TAG = DemographicsSettingsFragment.class.getSimpleName();
    private AppCompatActivity appCompatActivity;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private String demographicsString = null;
    private String personalInfoString = null;
    private String driverLicenseString = null;
    private String addressString = null;
    private String doBString = null;
    private String phoneNumberString = null;
    private String genderString = null;
    private String raceString = null;
    private String ethnityString = null;
    private String languageString = null;
    private String address1String = null;
    private String address2String = null;
    private String zipString = null;
    private String cityString = null;
    private String stateString = null;

    private TextView personalInformationTextView = null;
    private TextView dOBTextView = null;
    private TextView phoneNumberTextView = null;
    private TextView demographicsTextView = null;
    private TextView genderTextView = null;
    private TextView raceTextView = null;
    private TextView ethnityTextView = null;
    private TextView languageTextView = null;
    private TextView driverLicenseTextView = null;
    private TextView addressTextView = null;
    private TextView address1TextView = null;
    private TextView address2TextView = null;
    private TextView zipCodeTextView = null;
    private TextView cityTextView = null;
    private TextView stateTextView = null;

    private EditText dobEditText = null;
    private EditText phoneNumberEditext = null;
    private EditText driverLicenseEditText = null;
    private EditText addressLine1Editext = null;
    private EditText addressLine2Editext = null;
    private EditText zipCodeEditext = null;
    private EditText cityEditext = null;
    private EditText stateEditText = null;

    private String dobValString = null;
    private String phoneValString = null;
    private String genderValString = null;
    private String raceValString = null;
    private String ethnityValString = null;
    private String licenceValString = null;
    private String address1ValString = null;
    private String address2ValString = null;
    private String zipCodeValString = null;
    private String cityValString = null;
    private String stateValString = null;

    private Button updateProfileButton = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_information, container, false);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.review_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.review_toolbar_title);

        setGothamRoundedMediumTypeface(appCompatActivity, title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            bundle = getArguments();
            String demographicsSettingsDTOString = bundle.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
        }
        getDemographicsInformationLabels();
        getProfileProperties();

        title.setText(demographicsString);
        TextView personalInformationTextView = (TextView) view.findViewById(R.id.reviewpersonalInformationLabel);
        TextView dOBTextView = (TextView) view.findViewById(R.id.reviewDOBLabel);
        TextView phoneNumberTextView = (TextView) view.findViewById(R.id.reviewPhoneNumberLabel);
        TextView demographicsTextView = (TextView) view.findViewById(R.id.demographicSectionLabel);
        TextView genderTextView = (TextView) view.findViewById(R.id.reviewGenderLabel);
        TextView raceTextView = (TextView) view.findViewById(R.id.reviewRaceLabel);
        TextView ethnityTextView = (TextView) view.findViewById(R.id.reviewEthnicityLabel);
        TextView languageTextView = (TextView) view.findViewById(R.id.reviewLanguageLabel);
        TextView driverLicenseTextView = (TextView) view.findViewById(R.id.reviewDriverLicenseLabel);
        TextView addressTextView = (TextView) view.findViewById(R.id.reviewAddress);
        TextView address1TextView = (TextView) view.findViewById(R.id.reviewAddress1label);
        TextView address2TextView = (TextView) view.findViewById(R.id.reviewAddress2label);
        TextView zipCodeTextView = (TextView) view.findViewById(R.id.reviewZipcodeLabel);
        TextView cityTextView = (TextView) view.findViewById(R.id.reviewCityLabel);
        TextView stateTextView = (TextView) view.findViewById(R.id.reviewStateLabel);

        TextView genderValueTextView = (TextView) view.findViewById(R.id.reviewGenderTextView);
        TextView raceValueTextView = (TextView) view.findViewById(R.id.reviewRaceTextView);
        TextView ethnityValueTextView = (TextView) view.findViewById(R.id.reviewEthnicityTextView);

        dobEditText = (EditText) view.findViewById(R.id.reviewdemogrDOBEdit);
        phoneNumberEditext = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        driverLicenseEditText = (EditText) view.findViewById(R.id.driverLicenseEditText);
        addressLine1Editext = (EditText) view.findViewById(R.id.addressEditTextId);
        addressLine2Editext = (EditText) view.findViewById(R.id.addressEditText2Id);
        zipCodeEditext = (EditText) view.findViewById(R.id.zipCodeEditText);
        cityEditext = (EditText) view.findViewById(R.id.cityEditText);
        stateEditText = (EditText) view.findViewById(R.id.stateEditText);
        //setTypefaces(view);
        getDemographicDetails();
        personalInformationTextView.setText(personalInfoString);
        dOBTextView.setText(doBString);
        phoneNumberTextView.setText(phoneNumberString);
        demographicsTextView.setText(demographicsString);
        genderTextView.setText(genderString);
        raceTextView.setText(raceString);
        ethnityTextView.setText(ethnityString);
        languageTextView.setText(languageString);
        driverLicenseTextView.setText(driverLicenseString);
        addressTextView.setText(addressString);
        address1TextView.setText(address1String);
        address2TextView.setText(address2String);
        zipCodeTextView.setText(zipString);
        cityTextView.setText(cityString);
        stateTextView.setText(stateString);

        dobEditText.setText(dobValString);
        phoneNumberEditext.setText(phoneValString);
        driverLicenseEditText.setText(licenceValString);
        addressLine1Editext.setText(address1ValString);
        addressLine2Editext.setText(address2ValString);
        zipCodeEditext.setText(zipCodeValString);
        cityEditext.setText(cityValString);
        stateEditText.setText(stateValString);

        genderValueTextView.setText(genderValString);
        raceValueTextView.setText(raceValString);
        ethnityValueTextView.setText(ethnityValString);

        updateProfileButton =  (Button) view.findViewById(R.id.YesCorrectButton);
        setClickables(view);
        return view;

    }

    /**
     * demographics Information labels
     */
    public void getDemographicsInformationLabels() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                if (demographicsSettingsLabelsDTO != null) {
                    demographicsString = demographicsSettingsLabelsDTO.getDemographicsLabel();
                    personalInfoString = demographicsSettingsLabelsDTO.getDemographics_personal_info_Label();
                    driverLicenseString = demographicsSettingsLabelsDTO.getDemographics_driver_license_Label();

                }
            }
        }
    }

    public void getProfileProperties(){
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                if (demographicsSettingsDataModelsDTO != null) {
                    DemographicsSettingsDetailsDTO demographicsSettingsDetailsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                    if(demographicsSettingsDetailsDTO !=null) {
                        DemographicsSettingsPersonalDetailsPropertiesDTO demographicsSettingsPersonalDetailsPreopertiesDTO = demographicsSettingsDetailsDTO.getPersonalDetails();
                        DemographicsSettingsAddressDTO demographicsSettingsAddressDTO = demographicsSettingsDetailsDTO.getAddress();
                        DemographicsSettingsPersonalDetailsDTO demographicsSettingsPersonalDetailsDTO = demographicsSettingsPersonalDetailsPreopertiesDTO.getProperties();
                        DemographicsSettingsDateOfBirthDTO demographicsSettingsDOBDTO = demographicsSettingsPersonalDetailsDTO.getDateOfBirth();
                        DemographicsSettingsPhoneDTO demographicsPhoneNumberDTO = demographicsSettingsAddressDTO.getProperties().getPhone();
                        DemographicsSettingsGenderDTO demographicsSettingsGenderDTO = demographicsSettingsPersonalDetailsDTO.getGender();
                        DemographicsSettingsPrimaryRaceDTO demographicsSettingsRaceDTO = demographicsSettingsPersonalDetailsDTO.getPrimaryRace();
                        DemographicsSettingsEthnicityDTO demographicsSettingsEthnityDTO = demographicsSettingsPersonalDetailsDTO.getEthnicity();
                        DemographicsSettingsPreferredLanguageDTO demographicsSettingsPreferredLanguageDTO = demographicsSettingsPersonalDetailsDTO.getPreferredLanguage();
                        DemographicsSettingsAddressDTO demographicsSettingsAddressDTO1 = demographicsSettingsAddressDTO.getProperties().getAddress1();
                        DemographicsSettingsAddressDTO demographicsSettingsAddressDTO2 = demographicsSettingsAddressDTO.getProperties().getAddress2();
                        DemographicsSettingsZipDTO  demographicsSettingsZipDTO= demographicsSettingsAddressDTO.getProperties().getZipcode();
                        DemographicsSettingsCityDTO demographicsSettingsCityDTO = demographicsSettingsAddressDTO.getProperties().getCity();
                        DemographicsSettingsStateDTO demographicsSettingsStateDTO = demographicsSettingsAddressDTO.getProperties().getState();

                        addressString = demographicsSettingsAddressDTO.getLabel();
                        doBString = demographicsSettingsDOBDTO.getLabel();
                        phoneNumberString = demographicsPhoneNumberDTO.getLabel();
                        genderString = demographicsSettingsGenderDTO.getLabel();
                        raceString = demographicsSettingsRaceDTO.getLabel();
                        ethnityString = demographicsSettingsEthnityDTO.getLabel();
                        languageString = demographicsSettingsPreferredLanguageDTO.getLabel();
                        address1String = demographicsSettingsAddressDTO1.getLabel();
                        address2String = demographicsSettingsAddressDTO2.getLabel();
                        zipString = demographicsSettingsZipDTO.getLabel();
                        cityString = demographicsSettingsCityDTO.getLabel();
                        stateString = demographicsSettingsStateDTO.getLabel();
                        }
                    }
                }
            }
        }

    private void getDemographicDetails(){
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if(demographicsSettingsPayloadDTO!=null){
                DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                DemographicsSettingsPayloadAddressDTO demographicsAddressDetails = demographicPayload.getAddress();
                DemographicsSettingsDriversLicenseDTO demographicsLicenseDetails = demographicPayload.getDriversLicense();

                dobValString = demographicsPersonalDetails.getDateOfBirth();
                phoneValString = demographicsAddressDetails.getPhone();
                genderValString = demographicsPersonalDetails.getGender();
                raceValString = demographicsPersonalDetails.getPrimaryRace();
                ethnityValString = demographicsPersonalDetails.getEthnicity();
                //licenceValString = demographicsLicenseDetails.getLicenseNumber();
                address1ValString = demographicsAddressDetails.getAddress1();
                address2ValString = demographicsAddressDetails.getAddress2();
                zipCodeValString = demographicsAddressDetails.getZipcode();
                cityValString = demographicsAddressDetails.getCity();
                stateValString = demographicsAddressDetails.getState();

            }
        }
    }

    private void setClickables(View view) {

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (demographicsSettingsDTO != null) {
                    DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                    if (demographicsSettingsMetadataDTO != null) {
                        DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                        TransitionDTO demographicsSettingsUpdateDemographicsDTO =demographicsSettingsTransitionsDTO.getUpdateDemographics();
                        JSONObject payload = new JSONObject();
                        Map<String, String> queries = null;
                        Map<String, String> header = null;
                        try {
                            if (demographicsSettingsDTO != null) {
                                DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                                if(demographicsSettingsPayloadDTO!=null){
                                    DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                                    DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                                    DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                                    DemographicsSettingsPayloadAddressDTO demographicsAddressDetails = demographicPayload.getAddress();
                                    demographicsPersonalDetails.setDateOfBirth(dobEditText.getText().toString());
                                    demographicsAddressDetails.setPhone(phoneNumberEditext.getText().toString());
                                    demographicsAddressDetails.setAddress1(addressLine1Editext.getText().toString());
                                    demographicsAddressDetails.setAddress2(addressLine2Editext.getText().toString());
                                    demographicsAddressDetails.setZipcode(zipCodeEditext.getText().toString());
                                    demographicsAddressDetails.setCity(cityEditext.getText().toString());
                                    demographicsAddressDetails.setState(stateEditText.getText().toString());

                                    Gson gson = new Gson();
                                    String jsonInString = gson.toJson(demographicPayload);
                                    WorkflowServiceHelper.getInstance().execute(demographicsSettingsUpdateDemographicsDTO, updateDemographicsCallback,  jsonInString,header);
                                }}
                            header = new HashMap<>();
                            header.put("transition", "true");
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }

        });

    }

    WorkflowServiceCallback updateDemographicsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private void setTypefaces(View view) {
        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.reviewpersonalInformationLabel));

        if (!StringUtil.isNullOrEmpty(dobEditText.getText().toString())) {
            setProximaNovaSemiboldTypeface(getActivity(), dOBTextView);
        } else {
            setProximaNovaRegularTypeface(getActivity(), dOBTextView);
        }

        if (!StringUtil.isNullOrEmpty(phoneNumberEditext.getText().toString())) {
            setProximaNovaExtraboldTypeface(getActivity(), phoneNumberTextView);
        } else {
            setProximaNovaRegularTypeface(getActivity(), phoneNumberTextView);
        }

        if (!StringUtil.isNullOrEmpty(addressLine1Editext.getText().toString())) {
            setProximaNovaExtraboldTypeface(getActivity(), address1TextView);
        } else {
            setProximaNovaRegularTypeface(getActivity(), address1TextView);
        }


        if (!StringUtil.isNullOrEmpty(addressLine2Editext.getText().toString())) {
            setProximaNovaExtraboldTypeface(getActivity(), address2TextView);
        } else {
            setProximaNovaRegularTypeface(getActivity(), address2TextView);
        }

        if (!StringUtil.isNullOrEmpty(zipCodeEditext.getText().toString())) {
            setProximaNovaExtraboldTypeface(getActivity(), zipCodeTextView);
        } else {
            setProximaNovaRegularTypeface(getActivity(), zipCodeTextView);
        }

        if (!StringUtil.isNullOrEmpty(cityEditext.getText().toString())) {
            setProximaNovaExtraboldTypeface(getActivity(), cityTextView);
        } else {
            setProximaNovaRegularTypeface(getActivity(), cityTextView);
        }

        if (!StringUtil.isNullOrEmpty(stateEditText.getText().toString())) {
            setProximaNovaExtraboldTypeface(getActivity(), stateTextView);
        } else {
            setProximaNovaRegularTypeface(getActivity(), stateTextView);
        }
        if (!StringUtil.isNullOrEmpty(driverLicenseEditText.getText().toString())) {
            setProximaNovaExtraboldTypeface(getActivity(), driverLicenseTextView);
        } else {
            setProximaNovaRegularTypeface(getActivity(), driverLicenseTextView);
        }

        setProximaNovaSemiboldTypeface(getActivity(), demographicsTextView);

        setProximaNovaRegularTypeface(getActivity(), raceTextView);

        setProximaNovaRegularTypeface(getActivity(), genderTextView);

        setProximaNovaSemiboldTypeface(getActivity(), addressTextView);


   }

}
