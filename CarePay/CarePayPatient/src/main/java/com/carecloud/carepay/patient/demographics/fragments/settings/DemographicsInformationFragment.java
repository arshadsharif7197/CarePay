package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsAddressSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsPersonalSection;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.gson.Gson;
import com.smartystreets.api.us_zipcode.City;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsInformationFragment extends DemographicsBaseSettingsFragment {

    private DemographicDTO demographicsSettingsDTO;
    private DemographicDataModel dataModel;

    private EditText dateOfBirth;
    private EditText phoneNumber;
    private EditText address;
    private EditText address2;
    private EditText zipCode;
    private EditText cityEditText;
    private TextView stateEditText;
    private View nextButton;

    private DemographicsSettingsFragmentListener callback;

    private DemographicsOption selectedGender = new DemographicsOption();
    private DemographicsOption selectedRace = new DemographicsOption();
    private DemographicsOption selectedSecondaryRace = new DemographicsOption();
    private DemographicsOption selectedEthnicity = new DemographicsOption();
    private DemographicsOption selectedState = new DemographicsOption();

    /**
     * @return an instance of DemographicsInformationFragment
     */
    public static DemographicsInformationFragment newInstance() {
        return new DemographicsInformationFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DemographicsSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DemographicsSettingsFragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicDTO) callback.getDto();
        dataModel = demographicsSettingsDTO.getMetadata().getNewDataModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demographics_information, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("demographics_label"));
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        callback.setToolbar(toolbar);

        nextButton = view.findViewById(R.id.buttonAddDemographicInfo);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDemographics();
            }
        });

        View additionalDemographics = view.findViewById(R.id.add_additional_info);
        additionalDemographics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.displayExpandedDemographicsFragment();
            }
        });

        initViews(view);
        checkIfEnableButton();

    }

    private void initViews(View view) {
        DemographicPayloadDTO demographicPayload = demographicsSettingsDTO.getPayload().getDemographics().getPayload();

        initPersonalInfo(view, demographicPayload);
        setUpBaseDemographicFields(view, demographicPayload, demographicsSettingsDTO.getMetadata()
                .getNewDataModel().getDemographic().getPersonalDetails());
        initAddressInfo(view, demographicPayload);
    }

    private void initPersonalInfo(View view, DemographicPayloadDTO demographicPayload) {
        TextInputLayout dateBirthLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrDOBTextInput);
        dateOfBirth = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        dateOfBirth.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(dateBirthLayout, null));
        setVisibility(dateBirthLayout, dataModel.getDemographic().getPersonalDetails().getProperties()
                .getDateOfBirth().isDisplayed());
        dateOfBirth.addTextChangedListener(dateInputFormatter);

        String dateString = demographicPayload.getPersonalDetails().getFormattedDateOfBirth();
        dateOfBirth.setText(dateString);
        dateOfBirth.getOnFocusChangeListener().onFocusChange(dateOfBirth, !StringUtil.isNullOrEmpty(dateString));
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getDateOfBirth().isRequired()) {
            dateOfBirth.addTextChangedListener(getValidateEmptyTextWatcher(dateBirthLayout));
        } else {
            dateOfBirth.addTextChangedListener(clearValidationErrorsOnTextChange(dateBirthLayout));
        }


        TextInputLayout phoneNumberLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
        phoneNumber = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        phoneNumber.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(phoneNumberLayout, null));
        setVisibility(phoneNumberLayout, dataModel.getDemographic().getAddress().getProperties().getPhone().isDisplayed());
        phoneNumber.addTextChangedListener(phoneInputFormatter);

        String phoneNumberString = demographicPayload.getAddress().getPhone();
        phoneNumber.setText(StringUtil.formatPhoneNumber(phoneNumberString));
        phoneNumber.getOnFocusChangeListener().onFocusChange(phoneNumber, !StringUtil.isNullOrEmpty(phoneNumberString));
        if (dataModel.getDemographic().getAddress().getProperties().getPhone().isRequired()) {
            phoneNumber.addTextChangedListener(getValidateEmptyTextWatcher(phoneNumberLayout));
        } else {
            phoneNumber.addTextChangedListener(clearValidationErrorsOnTextChange(phoneNumberLayout));
        }

    }

    private void setUpBaseDemographicFields(View view, DemographicPayloadDTO demographicPayload,
                                            DemographicsPersonalSection personalInfoSection) {
        setUpDemographicField(view, demographicPayload.getPersonalDetails().getGender(),
                personalInfoSection.getProperties().getGender(), com.carecloud.carepaylibrary.R.id.genderDemographicsLayout,
                com.carecloud.carepaylibrary.R.id.genderInputLayout, com.carecloud.carepaylibrary.R.id.genderEditText,
                com.carecloud.carepaylibrary.R.id.genderOptionalLabel, selectedGender, Label.getLabel("demographics_review_gender"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getPrimaryRace(),
                personalInfoSection.getProperties().getPrimaryRace(), com.carecloud.carepaylibrary.R.id.raceDemographicsLayout,
                com.carecloud.carepaylibrary.R.id.raceInputLayout, com.carecloud.carepaylibrary.R.id.raceEditText,
                com.carecloud.carepaylibrary.R.id.raceOptionalLabel, selectedRace, Label.getLabel("demographics_review_race"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getSecondaryRace(),
                personalInfoSection.getProperties().getSecondaryRace(), com.carecloud.carepaylibrary.R.id.secondaryRaceDemographicsLayout,
                com.carecloud.carepaylibrary.R.id.secondaryRaceInputLayout, com.carecloud.carepaylibrary.R.id.secondaryRaceEditText, com.carecloud.carepaylibrary.R.id.secondaryRaceOptional,
                selectedSecondaryRace, Label.getLabel("demographics_secondary_race"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getEthnicity(),
                personalInfoSection.getProperties().getEthnicity(), com.carecloud.carepaylibrary.R.id.ethnicityDemographicsLayout,
                com.carecloud.carepaylibrary.R.id.ethnicityInputLayout, com.carecloud.carepaylibrary.R.id.ethnicityEditText,
                com.carecloud.carepaylibrary.R.id.ethnicityOptional, selectedEthnicity, Label.getLabel("demographics_review_ethnicity"));
    }

    private void initAddressInfo(View view, DemographicPayloadDTO demographicPayload) {
        DemographicsAddressSection addressSection = dataModel.getDemographic().getAddress();

        TextInputLayout addressInputLayout = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        address = (EditText) view.findViewById(R.id.addressEditTextId);
        address.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(addressInputLayout, null));
        setVisibility(addressInputLayout, addressSection.getProperties().getAddress1().isDisplayed());
        address.setText(StringUtil.captialize(demographicPayload.getAddress().getAddress1()));
        address.getOnFocusChangeListener().onFocusChange(address,
                !StringUtil.isNullOrEmpty(address.getText().toString().trim()));
        if (addressSection.getProperties().getAddress1().isRequired()) {
            address.addTextChangedListener(getValidateEmptyTextWatcher(addressInputLayout));
        }
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString())) {
                    address2.setEnabled(false);
                    address2.setText("");
                } else {
                    address2.setEnabled(true);
                }
            }
        });


        TextInputLayout address2InputLayout = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        address2 = (EditText) view.findViewById(R.id.addressEditText2Id);
        address2.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(address2InputLayout, null));
        setVisibility(address2InputLayout, addressSection.getProperties().getAddress2().isDisplayed());
        address2.setText(StringUtil.captialize(demographicPayload.getAddress().getAddress2()));
        address2.getOnFocusChangeListener().onFocusChange(address2,
                !StringUtil.isNullOrEmpty(address2.getText().toString().trim()));
        if (addressSection.getProperties().getAddress2().isRequired()) {
            address2.addTextChangedListener(getValidateEmptyTextWatcher(address2InputLayout));
            View address2Optional = view.findViewById(R.id.demogrAddressOptionalLabel);
            address2Optional.setVisibility(View.GONE);
        }


        TextInputLayout zipCodeInputLayout = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        zipCode = (EditText) view.findViewById(R.id.zipCodeId);
        zipCode.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(zipCodeInputLayout,
                getZipCodeFocusListener(zipCode)));
        setVisibility(zipCodeInputLayout, addressSection.getProperties().getZipcode().isDisplayed());
        zipCode.addTextChangedListener(zipInputFormatter);
        zipCode.setText(demographicPayload.getAddress().getZipcode());
        zipCode.getOnFocusChangeListener().onFocusChange(zipCode,
                !StringUtil.isNullOrEmpty(zipCode.getText().toString().trim()));
        if (addressSection.getProperties().getZipcode().isRequired()) {
            zipCode.addTextChangedListener(getValidateEmptyTextWatcher(zipCodeInputLayout));
        } else {
            zipCode.addTextChangedListener(clearValidationErrorsOnTextChange(zipCodeInputLayout));
        }


        TextInputLayout cityInputLayout = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        cityEditText = (EditText) view.findViewById(R.id.cityId);
        cityEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(cityInputLayout, null));
        setVisibility(cityInputLayout, addressSection.getProperties().getCity().isDisplayed());
        cityEditText.setText(StringUtil.captialize(demographicPayload.getAddress().getCity()));
        cityEditText.getOnFocusChangeListener().onFocusChange(cityEditText,
                !StringUtil.isNullOrEmpty(cityEditText.getText().toString().trim()));
        if (addressSection.getProperties().getCity().isRequired()) {
            cityEditText.addTextChangedListener(getValidateEmptyTextWatcher(cityInputLayout));
        }


        TextInputLayout stateInputLayout = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
        stateEditText = (EditText) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView);
        stateEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(stateInputLayout, null));
        setVisibility(stateInputLayout, addressSection.getProperties().getState().isDisplayed());
        stateEditText.setOnClickListener(
                getSelectOptionsListener(addressSection.getProperties().getState().getOptions(),
                        getDefaultOnOptionsSelectedListener(stateEditText, selectedState, null),
                        Label.getLabel("demographics_documents_title_select_state")));

        String state = demographicPayload.getAddress().getState();
        initSelectableInput(stateEditText, selectedState, state, null);
        stateEditText.getOnFocusChangeListener().onFocusChange(stateEditText, true);

    }

    @Override
    protected void checkIfEnableButton() {
        boolean isEnabled = passConstraints();
        if (nextButton != null) {
            nextButton.setEnabled(isEnabled);
            nextButton.setClickable(isEnabled);
        }
    }

    @Override
    protected boolean passConstraints() {
        View view = getView();
        if (view == null) {
            return false;
        }

        if (dataModel.getDemographic().getPersonalDetails().getProperties().getDateOfBirth().isRequired()
                && checkTextEmptyValue(R.id.revewidemogrDOBEdit, view)) {
            return false;
        }
        if (dataModel.getDemographic().getAddress().getProperties().getPhone().isRequired()
                && checkTextEmptyValue(R.id.reviewgrdemoPhoneNumberEdit, view)) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getGender().isRequired()
                && StringUtil.isNullOrEmpty(selectedGender.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getPrimaryRace().isRequired()
                && StringUtil.isNullOrEmpty(selectedRace.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryRace().isRequired()
                && StringUtil.isNullOrEmpty(selectedSecondaryRace.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getEthnicity().isRequired()
                && StringUtil.isNullOrEmpty(selectedEthnicity.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getAddress().getProperties().getAddress1().isRequired()
                && checkTextEmptyValue(R.id.addressEditTextId, view)) {
            return false;
        }
        if (dataModel.getDemographic().getAddress().getProperties().getAddress2().isRequired()
                && checkTextEmptyValue(R.id.addressEditText2Id, view)) {
            return false;
        }
        if (dataModel.getDemographic().getAddress().getProperties().getZipcode().isRequired()
                && checkTextEmptyValue(R.id.zipCodeId, view)) {
            return false;
        }
        if (dataModel.getDemographic().getAddress().getProperties().getCity().isRequired()
                && checkTextEmptyValue(R.id.cityId, view)) {
            return false;
        }
        if (dataModel.getDemographic().getAddress().getProperties().getState().isRequired()
                && StringUtil.isNullOrEmpty(selectedState.getName())) {
            return false;
        }


        //These are for validating correct input regardless of required fields
        TextInputLayout dateBirthLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrDOBTextInput);
        EditText dateOfBirth = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        if (dateBirthLayout.getVisibility() == View.VISIBLE &&
                !StringUtil.isNullOrEmpty(dateOfBirth.getText().toString().trim())) {
            String dateValidationResult = DateUtil
                    .getDateOfBirthValidationResultMessage(dateOfBirth.getText().toString().trim());
            if (dateValidationResult != null) {
                dateBirthLayout.setErrorEnabled(true);
                dateBirthLayout.setError(dateValidationResult);
                return false;
            }
        }

        TextInputLayout phoneLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
        EditText phoneNumber = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        if (phoneLayout.getVisibility() == View.VISIBLE &&
                !StringUtil.isNullOrEmpty(phoneNumber.getText().toString().trim()) &&
                !ValidationHelper.isValidString(phoneNumber.getText().toString().trim(), ValidationHelper.PHONE_NUMBER_PATTERN)) {
            phoneLayout.setErrorEnabled(true);
            phoneLayout.setError(Label.getLabel("demographics_phone_number_validation_msg"));
            return false;
        }

        TextInputLayout zipLayout = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        EditText zipCode = (EditText) view.findViewById(R.id.zipCodeId);
        if (zipLayout.getVisibility() == View.VISIBLE &&
                !StringUtil.isNullOrEmpty(zipCode.getText().toString().trim()) &&
                !ValidationHelper.isValidString(zipCode.getText().toString().trim(),
                        ValidationHelper.ZIP_CODE_PATTERN)) {
            zipLayout.setErrorEnabled(true);
            zipLayout.setError(Label.getLabel("demographics_zip_code_validation_msg"));
            return false;
        }

        return true;
    }


    /**
     * Background task to call smarty streets zip code lookup.
     * The response is a com.smartystreets.api.us_zipcode.City object,
     * that contains city, mailableCity, stateAbbreviation and state.
     */
    private void getCityAndState(String zipCode) {

        new AsyncTask<String, Void, City>() {

            @Override
            protected City doInBackground(String... params) {
                return AddressUtil.getCityAndStateByZipCode(params[0]);
            }

            @Override
            protected void onPostExecute(City smartyStreetsResponse) {
                super.onPostExecute(smartyStreetsResponse);

                if (smartyStreetsResponse != null) {
                    cityEditText.setText(smartyStreetsResponse.getCity());
                    String stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    stateEditText.setText(stateAbbr);
                }
            }

        }.execute(zipCode);
    }

    private View.OnFocusChangeListener getZipCodeFocusListener(final EditText zipCode) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    getCityAndState(zipCode.getText().toString());
                }
            }
        };
    }


    private DemographicDTO getUpdateModel() {
        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        updatableDemographicDTO.setPayload(new DemographicPayloadResponseDTO());
        updatableDemographicDTO.getPayload().setDemographics(new DemographicPayloadInfoDTO());
        updatableDemographicDTO.getPayload().getDemographics().setPayload(new DemographicPayloadDTO());

        //add all demographic info
        PatientModel patientModel = demographicsSettingsDTO.getPayload().getDemographics().getPayload().getPersonalDetails();

        String dobString = dateOfBirth.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(dobString)) {
            // the date is DateUtil as
            patientModel.setDateOfBirth(DateUtil.getInstance().setDateRaw(dobString).toStringWithFormatYyyyDashMmDashDd());
        }

        String gender = selectedGender.getName();
        if (!StringUtil.isNullOrEmpty(gender)) {
            patientModel.setGender(gender);
        }

        String race = selectedRace.getName();
        if (!StringUtil.isNullOrEmpty(race)) {
            patientModel.setPrimaryRace(race);
        }

        String secondaryRace = selectedSecondaryRace.getName();
        if (!StringUtil.isNullOrEmpty(secondaryRace)) {
            patientModel.setSecondaryRace(secondaryRace);
        }

        String ethnicity = selectedEthnicity.getName();
        if (!StringUtil.isNullOrEmpty(ethnicity)) {
            patientModel.setEthnicity(ethnicity);
        }


        DemographicAddressPayloadDTO addressModel = demographicsSettingsDTO.getPayload().getDemographics().getPayload().getAddress();

        String phoneNumberString = phoneNumber.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(phoneNumberString)) {
            // 'de-format' before saving to model
            addressModel.setPhone(StringUtil.revertToRawFormat(phoneNumberString));
        }

        String addressString = address.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(addressString)) {
            addressModel.setAddress1(addressString);
        }

        String address2String = address2.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(address2String)) {
            addressModel.setAddress2(address2String);
        }

        String zipCodeString = zipCode.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(zipCodeString)) {
            // 'de-format' before saving to model
            addressModel.setZipcode(StringUtil.revertZipToRawFormat(zipCodeString));
        }

        String cityString = cityEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(cityString)) {
            addressModel.setCity(cityString);
        }

        String state = selectedState.getName();
        if (!StringUtil.isNullOrEmpty(state)) {
            addressModel.setState(state);
        }

        updatableDemographicDTO.getPayload().getDemographics().getPayload().setPersonalDetails(patientModel);
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setAddress(addressModel);

        return updatableDemographicDTO;
    }


    private void updateDemographics() {
        if (passConstraints()) {
            Map<String, String> header = new HashMap<>();
            DemographicDTO updateModel = getUpdateModel();
            Gson gson = new Gson();
            String jsonPayload = gson.toJson(updateModel.getPayload().getDemographics().getPayload());

            TransitionDTO updateDemographics = demographicsSettingsDTO.getMetadata().getTransitions().getUpdateDemographics();
            getWorkflowServiceHelper().execute(updateDemographics, updateDemographicsCallback, jsonPayload, header);
        }
    }


    private WorkflowServiceCallback updateDemographicsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();

            DemographicDTO updatedModel = DtoHelper.getConvertedDTO(DemographicDTO.class, workflowDTO);
            demographicsSettingsDTO.getPayload().getDemographics().getPayload().setAddress(updatedModel.getPayload().getDemographics().getPayload().getAddress());
            demographicsSettingsDTO.getPayload().getDemographics().getPayload().setPersonalDetails(updatedModel.getPayload().getDemographics().getPayload().getPersonalDetails());

            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
            MixPanelUtil.logEvent(getString(R.string.event_updated_demographics), getString(R.string.param_is_checkin), false);
            getActivity().onBackPressed();

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


}
