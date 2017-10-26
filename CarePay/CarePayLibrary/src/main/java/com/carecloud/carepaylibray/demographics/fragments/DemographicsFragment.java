package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.EmployerInterface;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsPersonalSection;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerDto;
import com.carecloud.carepaylibray.demographics.interfaces.EmployerFragmentInterface;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.smartystreets.api.us_zipcode.City;

/**
 * A simple {@link CheckInDemographicsBaseFragment} subclass.
 */
public class DemographicsFragment extends CheckInDemographicsBaseFragment
        implements EmployerFragmentInterface {

    private DemographicDTO demographicDTO;
    private DemographicDataModel dataModel;
    private EmployerInterface callback;

    private PatientModel demographicPersDetailsPayloadDTO;

    private DemographicsOption selectedGender = new DemographicsOption();
    private DemographicsOption selectedRace = new DemographicsOption();
    private DemographicsOption selectedSecondaryRace = new DemographicsOption();
    private DemographicsOption selectedEthnicity = new DemographicsOption();
    private DemographicsOption selectedPreferredLanguage = new DemographicsOption();
    private DemographicsOption selectedDriverLicenseState = new DemographicsOption();
    private DemographicsOption selectedSecondaryPhoneType = new DemographicsOption();
    private DemographicsOption selectedContactMethod = new DemographicsOption();
    private DemographicsOption selectedMaritalStatus = new DemographicsOption();
    private DemographicsOption selectedEmploymentStatus = new DemographicsOption();
    private DemographicsOption selectedEmergencyContactRelationship = new DemographicsOption();
    private DemographicsOption selectedReferralSource = new DemographicsOption();
    private EmployerDto selectedEmployer = new EmployerDto();
    private boolean enableEmployerData;
    private TextInputLayout employerNameTextLayout;
    private TextInputLayout address1TextInputLayout;
    private TextInputLayout address2TextInputLayout;
    private TextInputLayout zipCodeTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private EditText employerNameEditText;
    private EditText addressEditText;
    private EditText addressEditText2;
    private EditText zipCodeEditText;
    private EditText phoneEditText;
    private EditText cityEditText;
    private EditText stateEditText;


    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EmployerInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        dataModel = demographicDTO.getMetadata().getNewDataModel();

        if (demographicDTO.getPayload().getDemographics() != null) {
            demographicPersDetailsPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        initialiseUIFields(view);
        initViews(view);
        checkIfEnableButton(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        stepProgressBar.setCurrentProgressDot(CheckinFlowCallback.DEMOGRAPHICS - 1);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, checkinFlowCallback.getTotalSteps(), CheckinFlowCallback.DEMOGRAPHICS);
        checkinFlowCallback.setCurrentStep(CheckinFlowCallback.DEMOGRAPHICS);
    }

    private void initViews(View view) {
        DemographicPayloadDTO demographicPayload = demographicDTO.getPayload().getDemographics().getPayload();
        DemographicsPersonalSection personalInfoSection = dataModel.getDemographic().getPersonalDetails();

        View genderLayout = view.findViewById(R.id.genderDemographicsLayout);
        TextView chooseGender = (TextView) view.findViewById(R.id.chooseGenderTextView);
        View genderOptional = view.findViewById(R.id.genderOptional);
        setVisibility(genderLayout, personalInfoSection.getProperties().getGender().isDisplayed());
        chooseGender.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getGender().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseGender, selectedGender, genderOptional),
                        Label.getLabel("demographics_review_gender")));
        String gender = demographicPayload.getPersonalDetails().getGender();
        initSelectableInput(chooseGender, selectedGender, gender, personalInfoSection.getProperties().getGender().isRequired() ? null : genderOptional);


        View raceLayout = view.findViewById(R.id.raceDemographicsLayout);
        TextView chooseRace = (TextView) view.findViewById(R.id.chooseRaceTextView);
        View raceOptional = view.findViewById(R.id.raceOptional);
        setVisibility(raceLayout, personalInfoSection.getProperties().getPrimaryRace().isDisplayed());
        chooseRace.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getPrimaryRace().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseRace, selectedRace, raceOptional),
                        Label.getLabel("demographics_review_race")));
        String race = demographicPayload.getPersonalDetails().getPrimaryRace();
        initSelectableInput(chooseRace, selectedRace, race, personalInfoSection.getProperties().getPrimaryRace().isRequired() ? null : raceOptional);


        View secondaryRaceLayout = view.findViewById(R.id.secondaryRaceDemographicsLayout);
        TextView chooseSecondaryRace = (TextView) view.findViewById(R.id.chooseSecondaryRace);
        View secondaryRaceOptional = view.findViewById(R.id.secondaryRaceOptional);
        setVisibility(secondaryRaceLayout, personalInfoSection.getProperties().getSecondaryRace().isDisplayed());
        chooseSecondaryRace.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getSecondaryRace().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseSecondaryRace, selectedSecondaryRace, secondaryRaceOptional),
                        Label.getLabel("demographics_secondary_race")));
        String secondaryRace = demographicPayload.getPersonalDetails().getSecondaryRace();
        initSelectableInput(chooseSecondaryRace, selectedSecondaryRace, secondaryRace, personalInfoSection.getProperties().getSecondaryRace().isRequired() ? null : secondaryRaceOptional);


        View ethnicityLayout = view.findViewById(R.id.ethnicityDemographicsLayout);
        TextView chooseEthnicity = (TextView) view.findViewById(R.id.chooseEthnicityTextView);
        View ethnicityOptional = view.findViewById(R.id.ethnicityOptional);
        setVisibility(ethnicityLayout, personalInfoSection.getProperties().getEthnicity().isDisplayed());
        chooseEthnicity.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getEthnicity().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseEthnicity, selectedEthnicity, ethnicityOptional),
                        Label.getLabel("demographics_review_ethnicity")));
        String ethnicity = demographicPayload.getPersonalDetails().getEthnicity();
        initSelectableInput(chooseEthnicity, selectedEthnicity, ethnicity, personalInfoSection.getProperties().getEthnicity().isRequired() ? null : ethnicityOptional);


        TextInputLayout preferredNameLayout = (TextInputLayout) view.findViewById(R.id.preferredNameInputLayout);
        EditText preferredName = (EditText) view.findViewById(R.id.preferredName);
        preferredName.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(preferredNameLayout, null));
        setVisibility(preferredNameLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredName().isDisplayed());
        preferredName.setText(demographicPayload.getPersonalDetails().getPreferredName());
        preferredName.getOnFocusChangeListener().onFocusChange(preferredName,
                !StringUtil.isNullOrEmpty(preferredName.getText().toString().trim()));
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredName().isRequired()) {
            preferredName.addTextChangedListener(getValidateEmptyTextWatcher(preferredNameLayout));
        } else if (personalInfoSection.getProperties().getPreferredName().isDisplayed() &&
                StringUtil.isNullOrEmpty(demographicPayload.getPersonalDetails().getPreferredName())) {
            View preferredNameOptional = view.findViewById(R.id.preferredNameOptional);
            preferredNameOptional.setVisibility(View.VISIBLE);
        }


        TextInputLayout socialSecurityLayout = (TextInputLayout) view.findViewById(R.id.socialSecurityInputLayout);
        EditText socialSecurity = (EditText) view.findViewById(R.id.socialSecurityNumber);
        socialSecurity.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(socialSecurityLayout, null));
        setVisibility(socialSecurityLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getSocialSecurityNumber().isDisplayed());
        socialSecurity.setText(demographicPayload.getPersonalDetails().getSocialSecurityNumber());
        socialSecurity.getOnFocusChangeListener().onFocusChange(socialSecurity,
                !StringUtil.isNullOrEmpty(socialSecurity.getText().toString().trim()));
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getSocialSecurityNumber().isRequired()) {
            socialSecurity.addTextChangedListener(getValidateEmptyTextWatcher(socialSecurityLayout));
        } else if (personalInfoSection.getProperties().getSocialSecurityNumber().isDisplayed() &&
                StringUtil.isNullOrEmpty(demographicPayload.getPersonalDetails().getSocialSecurityNumber())) {
            View socialSecurityOptional = view.findViewById(R.id.socialSecurityOptional);
            socialSecurityOptional.setVisibility(View.VISIBLE);
        }


        TextInputLayout emailAddressLayout = (TextInputLayout) view.findViewById(R.id.emailInputLayout);
        EditText emailAddress = (EditText) view.findViewById(R.id.email);
        emailAddress.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(emailAddressLayout, null));
        setVisibility(emailAddressLayout, dataModel.getDemographic().getPersonalDetails()
                .getProperties().getEmailAddress().isDisplayed());
        emailAddress.setText(demographicPayload.getPersonalDetails().getEmailAddress());
        emailAddress.getOnFocusChangeListener().onFocusChange(emailAddress,
                !StringUtil.isNullOrEmpty(emailAddress.getText().toString().trim()));
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getEmailAddress().isRequired()) {
            emailAddress.addTextChangedListener(getValidateEmptyTextWatcher(emailAddressLayout));
        } else if (personalInfoSection.getProperties().getEmailAddress().isDisplayed() &&
                StringUtil.isNullOrEmpty(demographicPayload.getPersonalDetails().getEmailAddress())) {
            View emailAddressOptional = view.findViewById(R.id.emailOptional);
            emailAddressOptional.setVisibility(View.VISIBLE);
        }


        View preferredLanguageLayout = view.findViewById(R.id.preferredLanguageDemographicsLayout);
        TextView choosePreferredLanguage = (TextView) view.findViewById(R.id.choosePreferredLanguage);
        View preferredLanguageOptional = view.findViewById(R.id.preferredLanguageOptional);
        setVisibility(preferredLanguageLayout, personalInfoSection.getProperties().getPreferredLanguage().isDisplayed());
        choosePreferredLanguage.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getPreferredLanguage().getOptions(),
                        getDefaultOnOptionsSelectedListener(choosePreferredLanguage, selectedPreferredLanguage, preferredLanguageOptional),
                        Label.getLabel("demographics_preferred_language")));
        String preferredLanguage = demographicPayload.getPersonalDetails().getPreferredLanguage();
        initSelectableInput(choosePreferredLanguage, selectedPreferredLanguage, preferredLanguage,
                personalInfoSection.getProperties().getPreferredLanguage().isRequired() ? null : preferredLanguageOptional);


        TextInputLayout driverLicenseLayout = (TextInputLayout) view.findViewById(R.id.driverLicenseInputLayout);
        EditText driverLicense = (EditText) view.findViewById(R.id.driverLicense);
        driverLicense.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(driverLicenseLayout, null));
        setVisibility(driverLicenseLayout, dataModel.getDemographic().getPersonalDetails()
                .getProperties().getDriversLicenseNumber().isDisplayed());
        driverLicense.setText(demographicPayload.getPersonalDetails().getDriversLicenseNumber());
        driverLicense.getOnFocusChangeListener().onFocusChange(driverLicense,
                !StringUtil.isNullOrEmpty(driverLicense.getText().toString().trim()));
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getDriversLicenseNumber().isRequired()) {
            driverLicense.addTextChangedListener(getValidateEmptyTextWatcher(driverLicenseLayout));
        } else if (personalInfoSection.getProperties().getDriversLicenseNumber().isDisplayed() &&
                StringUtil.isNullOrEmpty(demographicPayload.getPersonalDetails().getDriversLicenseNumber())) {
            View driverLicenseOptional = view.findViewById(R.id.driverLicenseOptional);
            driverLicenseOptional.setVisibility(View.VISIBLE);
        }


        View driverLicenseStateLayout = view.findViewById(R.id.driverLicenseStateDemographicsLayout);
        TextView choosedriverLicenseState = (TextView) view.findViewById(R.id.chooseDriverLicenseState);
        View driverLicenseStateOptional = view.findViewById(R.id.driverLicenseStateOptional);
        setVisibility(driverLicenseStateLayout, personalInfoSection.getProperties().getDriversLicenseState().isDisplayed());
        choosedriverLicenseState.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getDriversLicenseState().getOptions(),
                        getDefaultOnOptionsSelectedListener(choosedriverLicenseState, selectedDriverLicenseState,
                                driverLicenseStateOptional),
                        Label.getLabel("demographics_driver_license_state")));
        String driverLicenseState = demographicPayload.getPersonalDetails().getDriversLicenseState();
        initSelectableInput(choosedriverLicenseState, selectedDriverLicenseState, driverLicenseState,
                personalInfoSection.getProperties().getDriversLicenseState().isRequired() ? null : driverLicenseStateOptional);


        TextInputLayout secondaryPhoneLayout = (TextInputLayout) view.findViewById(R.id.secondaryPhoneInputLayout);
        EditText secondaryPhone = (EditText) view.findViewById(R.id.secondaryPhone);
        secondaryPhone.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(secondaryPhoneLayout, null));
        setVisibility(secondaryPhoneLayout, dataModel.getDemographic().getPersonalDetails()
                .getProperties().getSecondaryPhoneNumber().isDisplayed());
        secondaryPhone.addTextChangedListener(phoneInputFormatter);

        String secondaryPhoneNumberString = demographicPayload.getPersonalDetails().getSecondaryPhoneNumber();
        secondaryPhone.setText(StringUtil.formatPhoneNumber(secondaryPhoneNumberString));
        secondaryPhone.getOnFocusChangeListener().onFocusChange(secondaryPhone,
                !StringUtil.isNullOrEmpty(secondaryPhone.getText().toString().trim()));
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryPhoneNumber().isRequired()) {
            secondaryPhone.addTextChangedListener(getValidateEmptyTextWatcher(secondaryPhoneLayout));
        } else {
            secondaryPhone.addTextChangedListener(clearValidationErrorsOnTextChange(secondaryPhoneLayout));
            if (personalInfoSection.getProperties().getSecondaryPhoneNumber().isDisplayed() &&
                    StringUtil.isNullOrEmpty(demographicPayload.getPersonalDetails().getSecondaryPhoneNumber())) {
                View secondaryPhoneOptional = view.findViewById(R.id.secondaryPhoneOptional);
                secondaryPhoneOptional.setVisibility(View.VISIBLE);
            }
        }


        View secondaryPhoneTypeLayout = view.findViewById(R.id.secondaryPhoneTypeDemographicsLayout);
        TextView chooseSecondaryPhoneType = (TextView) view.findViewById(R.id.chooseSecondaryPhoneType);
        View secondaryPhoneTypeOptional = view.findViewById(R.id.secondaryPhoneTypeOptional);
        setVisibility(secondaryPhoneTypeLayout, personalInfoSection.getProperties().getSecondaryPhoneNumberType().isDisplayed());
        chooseSecondaryPhoneType.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getSecondaryPhoneNumberType().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseSecondaryPhoneType,
                                selectedSecondaryPhoneType, secondaryPhoneTypeOptional),
                        Label.getLabel("demographics_secondary_phone_type")));
        String secondaryPhoneType = demographicPayload.getPersonalDetails().getSecondaryPhoneNumberType();
        initSelectableInput(chooseSecondaryPhoneType, selectedSecondaryPhoneType, secondaryPhoneType,
                personalInfoSection.getProperties().getSecondaryPhoneNumberType().isRequired() ? null : secondaryPhoneTypeOptional);


        View preferredContactMethodLayout = view.findViewById(R.id.preferredContactMethodDemographicsLayout);
        TextView choosePreferredContactMethod = (TextView) view.findViewById(R.id.choosePreferredContactMethod);
        View contactMethodOptional = view.findViewById(R.id.contactMethodOptional);
        setVisibility(preferredContactMethodLayout, personalInfoSection.getProperties().getPreferredContact().isDisplayed());
        choosePreferredContactMethod.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getPreferredContact().getOptions(),
                        getDefaultOnOptionsSelectedListener(choosePreferredContactMethod, selectedContactMethod, contactMethodOptional),
                        Label.getLabel("demographics_preferred_contact_method")));
        String preferredContactMethod = demographicPayload.getPersonalDetails().getPreferredContact();
        initSelectableInput(choosePreferredContactMethod, selectedContactMethod, preferredContactMethod,
                personalInfoSection.getProperties().getPreferredContact().isRequired() ? null : contactMethodOptional);


        View maritalStatusLayout = view.findViewById(R.id.maritalStatusDemographicsLayout);
        TextView chooseMaritalStatus = (TextView) view.findViewById(R.id.chooseMaritalStatus);
        View maritalStatusOptional = view.findViewById(R.id.maritalStatusOptional);
        setVisibility(maritalStatusLayout, personalInfoSection.getProperties().getMaritalStatus().isDisplayed());
        chooseMaritalStatus.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getMaritalStatus().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseMaritalStatus, selectedMaritalStatus, maritalStatusOptional),
                        Label.getLabel("demographics_marital_status")));
        String maritalStatus = demographicPayload.getPersonalDetails().getMaritalStatus();
        initSelectableInput(chooseMaritalStatus, selectedMaritalStatus, maritalStatus, personalInfoSection.getProperties().getMaritalStatus().isRequired() ? null : maritalStatusOptional);


        View employmentStatusLayout = view.findViewById(R.id.employmentStatusDemographicsLayout);
        TextView chooseEmploymentStatus = (TextView) view.findViewById(R.id.chooseEmploymentStatus);
        View employmentStatusOptional = view.findViewById(R.id.employmentStatusOptional);
        setVisibility(employmentStatusLayout, personalInfoSection.getProperties().getEmploymentStatus().isDisplayed());
        chooseEmploymentStatus.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getEmploymentStatus().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseEmploymentStatus, selectedEmploymentStatus, employmentStatusOptional),
                        Label.getLabel("demographics_employment_status")));
        String employmentStatus = demographicPayload.getPersonalDetails().getEmploymentStatus();
        initSelectableInput(chooseEmploymentStatus, selectedEmploymentStatus, employmentStatus,
                personalInfoSection.getProperties().getEmploymentStatus().isRequired() ? null : employmentStatusOptional);

        setUpEmployer(view, demographicPayload, personalInfoSection);

        View emergencyContactRelationshipLayout = view.findViewById(R.id.emergencyContactRelationshipDemographicsLayout);
        TextView chooseEmergencyContactRelationship = (TextView) view.findViewById(R.id.chooseEmergencyContactRelationship);
        View emergencyContactRelationshipOptional = view.findViewById(R.id.emergencyContactRelationshipOptional);
        setVisibility(emergencyContactRelationshipLayout, personalInfoSection.getProperties().getEmergencyContactRelationship().isDisplayed());
        chooseEmergencyContactRelationship.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getEmergencyContactRelationship().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseEmergencyContactRelationship, selectedEmergencyContactRelationship, emergencyContactRelationshipOptional),
                        Label.getLabel("demographics_emergency_contact_relationship")));
        String emergencyContactRelationship = demographicPayload.getPersonalDetails().getEmergencyContactRelationship();
        initSelectableInput(chooseEmergencyContactRelationship, selectedEmergencyContactRelationship,
                emergencyContactRelationship, personalInfoSection.getProperties()
                        .getEmergencyContactRelationship().isRequired() ? null : emergencyContactRelationshipOptional);


        View referralSourceLayout = view.findViewById(R.id.referralSourceDemographicsLayout);
        TextView chooseReferralSource = (TextView) view.findViewById(R.id.chooseReferralSource);
        View referralSourceOptional = view.findViewById(R.id.referralSourceOptional);
        setVisibility(referralSourceLayout, personalInfoSection.getProperties().getReferralSource().isDisplayed());
        chooseReferralSource.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getReferralSource().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseReferralSource, selectedReferralSource, referralSourceOptional),
                        Label.getLabel("demographics_referral_source")));
        String referralSource = demographicPayload.getPersonalDetails().getReferralSource();
        initSelectableInput(chooseReferralSource, selectedReferralSource, referralSource, personalInfoSection.getProperties().getReferralSource().isRequired() ? null : referralSourceOptional);

    }

    private void setUpEmployer(final View view, DemographicPayloadDTO demographicPayload, DemographicsPersonalSection personalInfoSection) {
        boolean isEmploymentStuffVisible = personalInfoSection.getProperties().getEmploymentStatus().isDisplayed();
        if (isEmploymentStuffVisible) {

            final TextView chooseEmploymentStatus = (TextView) view.findViewById(R.id.chooseEmploymentStatus);
            final View employmentStatusOptional = view.findViewById(R.id.employmentStatusOptional);

            chooseEmploymentStatus.setOnClickListener(
                    getSelectOptionsListener(personalInfoSection.getProperties().getEmploymentStatus().getOptions(),
                            new OnOptionSelectedListener() {
                                @Override
                                public void onOptionSelected(DemographicsOption option) {
                                    if (chooseEmploymentStatus != null) {
                                        chooseEmploymentStatus.setText(option.getLabel());
                                    }
                                    if (employmentStatusOptional != null) {
                                        employmentStatusOptional.setVisibility(View.GONE);
                                    }
                                    selectedEmploymentStatus.setLabel(option.getLabel());
                                    selectedEmploymentStatus.setName(option.getName());
                                    enableEmployerData = option.getLabel().toLowerCase().equals("employed")
                                            || option.getLabel().toLowerCase().equals("part time");
                                    enableEmployerData(enableEmployerData);
                                    checkIfEnableButton(view);
                                }
                            }, Label.getLabel("demographics_employment_status")));

            String employmentStatus = demographicPayload.getPersonalDetails().getEmploymentStatus();
            initSelectableInput(chooseEmploymentStatus, selectedEmploymentStatus, employmentStatus,
                    personalInfoSection.getProperties().getEmploymentStatus().isRequired()
                            ? null : employmentStatusOptional);
            enableEmployerData = employmentStatus.toLowerCase().equals("employed")
                    || employmentStatus.toLowerCase().equals("part time");

            selectedEmployer = demographicPayload.getPersonalDetails().getEmployer();

            employerNameTextLayout = (TextInputLayout) view
                    .findViewById(R.id.employerNameTextInputLayout);
            employerNameEditText = (EditText) view.findViewById(R.id.employerNameEditText);
            employerNameEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(employerNameTextLayout, null));
            employerNameEditText.setText(selectedEmployer.getName());
            employerNameEditText.getOnFocusChangeListener().onFocusChange(employerNameEditText,
                    !StringUtil.isNullOrEmpty(employerNameEditText.getText().toString().trim()));

            address1TextInputLayout = (TextInputLayout) view
                    .findViewById(R.id.address1TextInputLayout);
            addressEditText = (EditText) view.findViewById(R.id.addressEditText);
            addressEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(address1TextInputLayout, new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if (!hasFocus) {
                                selectedEmployer.getAddress().setAddress1(addressEditText.getText().toString());
                            }
                        }
                    }));
            if (selectedEmployer.getAddress() != null) {
                addressEditText.setText(selectedEmployer.getAddress().getAddress1());
            }

            address2TextInputLayout = (TextInputLayout) view
                    .findViewById(R.id.address2TextInputLayout);
            addressEditText2 = (EditText) view.findViewById(R.id.addressEditText2);
            addressEditText2.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(address2TextInputLayout, new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if (!hasFocus) {
                                selectedEmployer.getAddress().setAddress2(addressEditText2.getText().toString());
                            }
                        }
                    }));
            if (selectedEmployer.getAddress() != null) {
                addressEditText2.setText(selectedEmployer.getAddress().getAddress2());
            }

            zipCodeTextInputLayout = (TextInputLayout) view
                    .findViewById(R.id.zipCodeTextInputLayout);
            zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeTextView);
            zipCodeEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(zipCodeTextInputLayout,
                    getZipCodeFocusListener(zipCodeEditText)));
            if (selectedEmployer.getAddress() != null) {
                zipCodeEditText.setText(selectedEmployer.getAddress().getZipcode());
            }

            cityTextInputLayout = (TextInputLayout) view
                    .findViewById(R.id.cityTextInputLayout);
            cityEditText = (EditText) view.findViewById(R.id.cityTextView);
            cityEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(cityTextInputLayout, null));
            if (selectedEmployer.getAddress() != null) {
                cityEditText.setText(selectedEmployer.getAddress().getCity());
            }

            stateTextInputLayout = (TextInputLayout) view
                    .findViewById(R.id.stateTextInputLayout);
            stateEditText = (EditText) view.findViewById(R.id.stateTextView);
            stateEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(stateTextInputLayout, null));
            stateEditText.setOnClickListener(
                    getSelectOptionsListener(dataModel.getDemographic().getAddress().getProperties()
                                    .getState().getOptions(),
                            new OnOptionSelectedListener() {
                                @Override
                                public void onOptionSelected(DemographicsOption option) {
                                    stateEditText.setText(option.getLabel());
                                    selectedEmployer.getAddress().setState(option.getLabel());
                                }
                            },
                            Label.getLabel("demographics_documents_title_select_state")));
            stateEditText.setText(selectedEmployer.getAddress().getState());

            phoneTextInputLayout = (TextInputLayout) view
                    .findViewById(R.id.phoneTextInputLayout);
            phoneEditText = (EditText) view.findViewById(R.id.phoneTextView);
            phoneEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(phoneTextInputLayout, new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                selectedEmployer.getAddress().setPhone(phoneEditText.getText().toString());
                            }
                        }
                    }));
            if (selectedEmployer.getAddress() != null) {
                phoneEditText.setText(selectedEmployer.getAddress().getPhone());
            }
            enableEmployerData(enableEmployerData);

        } else {
            manageEmploymentVisibility(view, false);
        }
    }

    private void manageEmploymentVisibility(View view, boolean visible) {
        view.findViewById(R.id.employmentStatusDemographicsLayout)
                .setVisibility(visible ? View.VISIBLE : View.GONE);
        employerNameTextLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        address1TextInputLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        address2TextInputLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        zipCodeTextInputLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        phoneTextInputLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.cityAndStateLayoutContainer)
                .setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void enableEmployerData(boolean enableEmployerData) {
        employerNameTextLayout.setEnabled(enableEmployerData);
        address1TextInputLayout.setEnabled(enableEmployerData);
        address2TextInputLayout.setEnabled(enableEmployerData);
        zipCodeTextInputLayout.setEnabled(enableEmployerData);
        phoneTextInputLayout.setEnabled(enableEmployerData);
        cityTextInputLayout.setEnabled(enableEmployerData);
        stateTextInputLayout.setEnabled(enableEmployerData);
        cleanOrRestoreEmployerData(enableEmployerData);
    }

    private void cleanOrRestoreEmployerData(boolean restore) {
        if (restore) {
            employerNameEditText.setText(selectedEmployer.getName());
            if (selectedEmployer.getAddress() != null) {
                addressEditText.setText(selectedEmployer.getAddress().getAddress1());
                addressEditText2.setText(selectedEmployer.getAddress().getAddress2());
                zipCodeEditText.setText(selectedEmployer.getAddress().getZipcode());
                phoneEditText.setText(selectedEmployer.getAddress().getPhone());
                cityEditText.setText(selectedEmployer.getAddress().getCity());
                stateEditText.setText(selectedEmployer.getAddress().getState());
            }
        } else {
            employerNameEditText.setText(null);
            employerNameTextLayout.setHint(Label.getLabel("demographics_employer_name_hint"));
            addressEditText.setText(null);
            address1TextInputLayout.setHint(Label.getLabel("address1_label"));
            addressEditText2.setText(null);
            address2TextInputLayout.setHint(Label.getLabel("address2_label"));
            zipCodeEditText.setText(null);
            zipCodeTextInputLayout.setHint(Label.getLabel("payment_zipcode"));
            phoneEditText.setText(null);
            phoneTextInputLayout.setHint(Label.getLabel("demographics_phone_number_label"));
            cityEditText.setText(null);
            cityTextInputLayout.setHint(Label.getLabel("payment_city"));
            stateEditText.setText(null);
            stateTextInputLayout.setHint(Label.getLabel("payment_state"));
        }
    }


    private void initialiseUIFields(View view) {
        setHeaderTitle(Label.getLabel("demographics_review_demographics"),
                Label.getLabel("demographics_demographics_heading"),
                Label.getLabel("demographics_demographics_subheading"),
                view);
        initNextButton(view);

    }


    @Override
    protected DemographicDTO updateDemographicDTO(View view) {

        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        updatableDemographicDTO.setPayload(new DemographicPayloadResponseDTO());
        updatableDemographicDTO.getPayload().setDemographics(new DemographicPayloadInfoDTO());
        updatableDemographicDTO.getPayload().getDemographics().setPayload(new DemographicPayloadDTO());

        String gender = selectedGender.getName();
        if (!StringUtil.isNullOrEmpty(gender)) {
            demographicPersDetailsPayloadDTO.setGender(gender);
        }

        String race = selectedRace.getName();
        if (!StringUtil.isNullOrEmpty(race)) {
            demographicPersDetailsPayloadDTO.setPrimaryRace(race);
        }

        String secondaryRace = selectedSecondaryRace.getName();
        if (!StringUtil.isNullOrEmpty(secondaryRace)) {
            demographicPersDetailsPayloadDTO.setSecondaryRace(secondaryRace);
        }

        String ethnicity = selectedEthnicity.getName();
        if (!StringUtil.isNullOrEmpty(ethnicity)) {
            demographicPersDetailsPayloadDTO.setEthnicity(ethnicity);
        }

        String preferredName = ((TextView) findViewById(R.id.preferredName)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(preferredName)) {
            demographicPersDetailsPayloadDTO.setPreferredName(preferredName);
        }

        String socialSecurity = ((TextView) findViewById(R.id.socialSecurityNumber)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(socialSecurity)) {
            demographicPersDetailsPayloadDTO.setSocialSecurityNumber(socialSecurity);
        }

        String emailAddress = ((TextView) findViewById(R.id.email)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(emailAddress)) {
            demographicPersDetailsPayloadDTO.setEmailAddress(emailAddress);
        }

        String preferredLanguage = selectedPreferredLanguage.getName();
        if (!StringUtil.isNullOrEmpty(preferredLanguage)) {
            demographicPersDetailsPayloadDTO.setPreferredLanguage(preferredLanguage);
        }

        String driverLicense = ((TextView) findViewById(R.id.driverLicense)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(driverLicense)) {
            demographicPersDetailsPayloadDTO.setDriversLicenseNumber(driverLicense);
        }

        String driverLicenseState = selectedDriverLicenseState.getName();
        if (!StringUtil.isNullOrEmpty(driverLicenseState)) {
            demographicPersDetailsPayloadDTO.setDriversLicenseState(driverLicenseState);
        }

        String secondaryPhone = ((TextView) findViewById(R.id.secondaryPhone)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(secondaryPhone)) {
            demographicPersDetailsPayloadDTO.setSecondaryPhoneNumber(StringUtil.revertToRawPhoneFormat(secondaryPhone));
        }

        String secondaryPhoneType = selectedSecondaryPhoneType.getName();
        if (!StringUtil.isNullOrEmpty(secondaryPhoneType)) {
            demographicPersDetailsPayloadDTO.setSecondaryPhoneNumberType(secondaryPhoneType);
        }

        String contactMethod = selectedContactMethod.getName();
        if (!StringUtil.isNullOrEmpty(contactMethod)) {
            demographicPersDetailsPayloadDTO.setPreferredContact(contactMethod);
        }

        String maritalStatus = selectedMaritalStatus.getName();
        if (!StringUtil.isNullOrEmpty(maritalStatus)) {
            demographicPersDetailsPayloadDTO.setMaritalStatus(maritalStatus);
        }

        String employmentStatus = selectedEmploymentStatus.getName();
        if (!StringUtil.isNullOrEmpty(employmentStatus)) {
            demographicPersDetailsPayloadDTO.setEmploymentStatus(employmentStatus);
        }

        if (selectedEmployer != null && !StringUtil.isNullOrEmpty(selectedEmployer.getName())) {
            if (enableEmployerData) {
                selectedEmployer.getAddress().setAddress1(addressEditText.getText().toString());
                selectedEmployer.getAddress().setAddress2(addressEditText2.getText().toString());
                selectedEmployer.getAddress().setZipcode(zipCodeEditText.getText().toString());
                selectedEmployer.getAddress().setCity(cityEditText.getText().toString());
                selectedEmployer.getAddress().setState(stateEditText.getText().toString());
                selectedEmployer.getAddress().setPhone(phoneEditText.getText().toString());
                demographicPersDetailsPayloadDTO.setEmployer(selectedEmployer);
            } else {
                demographicPersDetailsPayloadDTO.setEmployer(null);
            }
        }

        String emergencyContactRelationship = selectedEmergencyContactRelationship.getName();
        if (!StringUtil.isNullOrEmpty(emergencyContactRelationship)) {
            demographicPersDetailsPayloadDTO.setEmergencyContactRelationship(emergencyContactRelationship);
        }

        String referralSource = selectedReferralSource.getName();
        if (!StringUtil.isNullOrEmpty(referralSource)) {
            demographicPersDetailsPayloadDTO.setReferralSource(referralSource);
        }

        updatableDemographicDTO.getPayload().getDemographics().getPayload().setPersonalDetails(demographicPersDetailsPayloadDTO);
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        updatableDemographicDTO.getPayload().setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        return updatableDemographicDTO;
    }


    @Override
    protected boolean passConstraints(View view) {
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
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredName().isRequired()
                && checkTextEmptyValue(R.id.preferredName, view)) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getSocialSecurityNumber().isRequired()
                && checkTextEmptyValue(R.id.socialSecurityNumber, view)) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getEmailAddress().isRequired()
                && checkTextEmptyValue(R.id.email, view)) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredLanguage().isRequired()
                && StringUtil.isNullOrEmpty(selectedPreferredLanguage.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getDriversLicenseNumber().isRequired()
                && checkTextEmptyValue(R.id.driverLicense, view)) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getDriversLicenseState().isRequired()
                && StringUtil.isNullOrEmpty(selectedDriverLicenseState.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryPhoneNumber().isRequired()
                && checkTextEmptyValue(R.id.secondaryPhone, view)) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryPhoneNumberType().isRequired()
                && StringUtil.isNullOrEmpty(selectedSecondaryPhoneType.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredContact().isRequired()
                && StringUtil.isNullOrEmpty(selectedContactMethod.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getMaritalStatus().isRequired()
                && StringUtil.isNullOrEmpty(selectedMaritalStatus.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getEmploymentStatus().isRequired()
                && StringUtil.isNullOrEmpty(selectedEmploymentStatus.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getEmployer().isRequired()
                && StringUtil.isNullOrEmpty(selectedEmployer.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties()
                .getEmergencyContactRelationship().isRequired()
                && StringUtil.isNullOrEmpty(selectedEmergencyContactRelationship.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getReferralSource().isRequired()
                && StringUtil.isNullOrEmpty(selectedReferralSource.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getEmployer().isRequired()
                && (selectedEmployer == null || StringUtil.isNullOrEmpty(selectedEmployer.getName()))) {
            return false;
        }


        TextInputLayout phoneLayout = (TextInputLayout) view.findViewById(R.id.secondaryPhoneInputLayout);
        EditText secondaryPhoneNumber = (EditText) view.findViewById(R.id.secondaryPhone);
        if (phoneLayout.getVisibility() == View.VISIBLE &&
                !StringUtil.isNullOrEmpty(secondaryPhoneNumber.getText().toString().trim()) &&
                !ValidationHelper.isValidString(secondaryPhoneNumber.getText().toString().trim(),
                        ValidationHelper.PHONE_NUMBER_PATTERN)) {
            phoneLayout.setErrorEnabled(true);
            phoneLayout.setError(Label.getLabel("demographics_phone_number_validation_msg"));
            return false;
        }

        TextInputLayout emailLayout = (TextInputLayout) view.findViewById(R.id.emailInputLayout);
        EditText emailAddress = (EditText) view.findViewById(R.id.email);
        if (emailLayout.getVisibility() == View.VISIBLE &&
                !StringUtil.isNullOrEmpty(emailAddress.getText().toString().trim()) &&
                !ValidationHelper.isValidString(emailAddress.getText().toString().trim(),
                        ValidationHelper.EMAIL_PATTERN)) {
            emailLayout.setErrorEnabled(true);
            emailLayout.setError(Label.getLabel("demographics_email_validation_msg"));
            return false;
        }

        return true;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_review_demographic_demographics;
    }

    @Override
    public void setEmployer(EmployerDto employer) {
        this.selectedEmployer = employer;
        demographicDTO.getPayload().getDemographics().getPayload()
                .getPersonalDetails().setEmployer(employer);
        setUpEmployer(getView(), demographicDTO.getPayload().getDemographics().getPayload(),
                demographicDTO.getMetadata().getNewDataModel()
                        .getDemographic().getPersonalDetails());
    }

    private View.OnFocusChangeListener getZipCodeFocusListener(final EditText editText) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    getCityAndState(editText.getText().toString());
                    selectedEmployer.getAddress().setZipcode(editText.getText().toString());
                }
            }
        };
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
                    selectedEmployer.getAddress().setCity(smartyStreetsResponse.getCity());
                    cityEditText.setText(smartyStreetsResponse.getCity());
                    String stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    selectedEmployer.getAddress().setState(stateAbbr);
                    stateEditText.setText(stateAbbr);
                }
            }


        }.execute(zipCode);
    }
}
