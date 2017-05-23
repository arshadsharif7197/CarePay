package com.carecloud.carepaylibray.demographics.fragments;

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
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsPersonalSection;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;

/**
 * A simple {@link CheckInDemographicsBaseFragment} subclass.
 */
public class DemographicsFragment extends CheckInDemographicsBaseFragment  {

    private DemographicDTO demographicDTO;
    private DemographicDataModel dataModel;

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
    public void onViewCreated(View view, Bundle icicle){
        initialiseUIFields(view);
        initViews(view);
        checkIfEnableButton(view);

    }

    @Override
    public void onResume(){
        super.onResume();
        stepProgressBar.setCurrentProgressDot(2);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 3);
        checkinFlowCallback.setCurrentStep(3);
    }

    private void initViews(View view){
        DemographicPayloadDTO demographicPayload = demographicDTO.getPayload().getDemographics().getPayload();
        DemographicsPersonalSection personalInfoSection = dataModel.getDemographic().getPersonalDetails();

        View genderLayout = view.findViewById(R.id.genderDemographicsLayout);
        TextView chooseGender = (TextView) view.findViewById(R.id.chooseGenderTextView);
        setVisibility(genderLayout, personalInfoSection.getProperties().getGender().isDisplayed());
        chooseGender.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getGender().getOptions(),
                getDefaultOnOptionsSelectedListener(chooseGender, selectedGender),
                Label.getLabel("demographics_review_gender")));
        String gender = demographicPayload.getPersonalDetails().getGender();
        initSelectableInput(chooseGender, selectedGender, gender);


        View raceLayout = view.findViewById(R.id.raceDemographicsLayout);
        TextView chooseRace = (TextView) view.findViewById(R.id.chooseRaceTextView);
        setVisibility(raceLayout, personalInfoSection.getProperties().getPrimaryRace().isDisplayed());
        chooseRace.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getPrimaryRace().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseRace, selectedRace),
                        Label.getLabel("demographics_review_race")));
        String race = demographicPayload.getPersonalDetails().getPrimaryRace();
        initSelectableInput(chooseRace, selectedRace, race);


        View secondaryRaceLayout = view.findViewById(R.id.secondaryRaceDemographicsLayout);
        TextView chooseSecondaryRace = (TextView) view.findViewById(R.id.chooseSecondaryRace);
        setVisibility(secondaryRaceLayout, personalInfoSection.getProperties().getSecondaryRace().isDisplayed());
        chooseSecondaryRace.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getSecondaryRace().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseSecondaryRace, selectedSecondaryRace),
                        Label.getLabel("demographics_secondary_race")));
        String secondaryRace = demographicPayload.getPersonalDetails().getSecondaryRace();
        initSelectableInput(chooseSecondaryRace, selectedSecondaryRace, secondaryRace);


        View ethnicityLayout = view.findViewById(R.id.ethnicityDemographicsLayout);
        TextView chooseEthnicity = (TextView) view.findViewById(R.id.chooseEthnicityTextView);
        setVisibility(ethnicityLayout, personalInfoSection.getProperties().getEthnicity().isDisplayed());
        chooseEthnicity.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getEthnicity().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseEthnicity, selectedEthnicity),
                        Label.getLabel("demographics_review_ethnicity")));
        String ethnicity = demographicPayload.getPersonalDetails().getEthnicity();
        initSelectableInput(chooseEthnicity, selectedEthnicity, ethnicity);


        TextInputLayout preferredNameLayout = (TextInputLayout) view.findViewById(R.id.preferredNameInputLayout);
        EditText preferredName = (EditText) view.findViewById(R.id.preferredName);
        preferredName.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(preferredNameLayout, null));
        setVisibility(preferredNameLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredName().isDisplayed());
        preferredName.setText(demographicPayload.getPersonalDetails().getPreferredName());
        preferredName.getOnFocusChangeListener().onFocusChange(preferredName, !StringUtil.isNullOrEmpty(preferredName.getText().toString()));
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredName().isRequired()) {
            preferredName.addTextChangedListener(getValidateEmptyTextWatcher(preferredNameLayout));
        }


        TextInputLayout socialSecurityLayout = (TextInputLayout) view.findViewById(R.id.socialSecurityInputLayout);
        EditText socialSecurity = (EditText) view.findViewById(R.id.socialSecurityNumber);
        socialSecurity.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(socialSecurityLayout, null));
        setVisibility(socialSecurityLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getSocialSecurityNumber().isDisplayed());
        socialSecurity.setText(demographicPayload.getPersonalDetails().getSocialSecurityNumber());
        socialSecurity.getOnFocusChangeListener().onFocusChange(socialSecurity, !StringUtil.isNullOrEmpty(socialSecurity.getText().toString()));
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getSocialSecurityNumber().isRequired()) {
            socialSecurity.addTextChangedListener(getValidateEmptyTextWatcher(socialSecurityLayout));
        }


        TextInputLayout emailAddressLayout = (TextInputLayout) view.findViewById(R.id.emailInputLayout);
        EditText emailAddress = (EditText) view.findViewById(R.id.email);
        emailAddress.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(emailAddressLayout, null));
        setVisibility(emailAddressLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getEmailAddress().isDisplayed());
        emailAddress.setText(demographicPayload.getPersonalDetails().getEmailAddress());
        emailAddress.getOnFocusChangeListener().onFocusChange(emailAddress, !StringUtil.isNullOrEmpty(emailAddress.getText().toString()));
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getEmailAddress().isRequired()) {
            emailAddress.addTextChangedListener(getValidateEmptyTextWatcher(emailAddressLayout));
        }


        View preferredLanguageLayout = view.findViewById(R.id.preferredLanguageDemographicsLayout);
        TextView choosePreferredLanguage = (TextView) view.findViewById(R.id.choosePreferredLanguage);
        setVisibility(preferredLanguageLayout, personalInfoSection.getProperties().getPreferredLanguage().isDisplayed());
        choosePreferredLanguage.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getPreferredLanguage().getOptions(),
                        getDefaultOnOptionsSelectedListener(choosePreferredLanguage, selectedPreferredLanguage),
                        Label.getLabel("demographics_preferred_language")));
        String preferredLanguage = demographicPayload.getPersonalDetails().getPreferredLanguage();
        initSelectableInput(choosePreferredLanguage, selectedPreferredLanguage, preferredLanguage);


        TextInputLayout driverLicenseLayout = (TextInputLayout) view.findViewById(R.id.driverLicenseInputLayout);
        EditText driverLicense = (EditText) view.findViewById(R.id.driverLicense);
        driverLicense.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(driverLicenseLayout, null));
        setVisibility(driverLicenseLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getDriversLicenseNumber().isDisplayed());
        driverLicense.setText(demographicPayload.getPersonalDetails().getDriversLicenseNumber());
        driverLicense.getOnFocusChangeListener().onFocusChange(driverLicense, !StringUtil.isNullOrEmpty(driverLicense.getText().toString()));
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getDriversLicenseNumber().isRequired()) {
            driverLicense.addTextChangedListener(getValidateEmptyTextWatcher(driverLicenseLayout));
        }


        View driverLicenseStateLayout = view.findViewById(R.id.driverLicenseStateDemographicsLayout);
        TextView choosedriverLicenseState = (TextView) view.findViewById(R.id.chooseDriverLicenseState);
        setVisibility(driverLicenseStateLayout, personalInfoSection.getProperties().getDriversLicenseState().isDisplayed());
        choosedriverLicenseState.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getDriversLicenseState().getOptions(),
                        getDefaultOnOptionsSelectedListener(choosedriverLicenseState, selectedDriverLicenseState),
                        Label.getLabel("demographics_driver_license_state")));
        String driverLicenseState = demographicPayload.getPersonalDetails().getDriversLicenseState();
        initSelectableInput(choosedriverLicenseState, selectedDriverLicenseState, driverLicenseState);


        TextInputLayout secondaryPhoneLayout = (TextInputLayout) view.findViewById(R.id.secondaryPhoneInputLayout);
        EditText secondaryPhone = (EditText) view.findViewById(R.id.secondaryPhone);
        secondaryPhone.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(secondaryPhoneLayout, null));
        setVisibility(secondaryPhoneLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryPhoneNumber().isDisplayed());
        secondaryPhone.addTextChangedListener(phoneInputFormatter);

        String secondaryPhoneNumberString = demographicPayload.getPersonalDetails().getSecondaryPhoneNumber();
        secondaryPhone.setText(StringUtil.formatPhoneNumber(secondaryPhoneNumberString));
        secondaryPhone.getOnFocusChangeListener().onFocusChange(secondaryPhone, !StringUtil.isNullOrEmpty(secondaryPhone.getText().toString()));
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryPhoneNumber().isRequired()) {
            secondaryPhone.addTextChangedListener(getValidateEmptyTextWatcher(secondaryPhoneLayout));
        }else{
            secondaryPhone.addTextChangedListener(clearValidationErrorsOnTextChange(secondaryPhoneLayout));
        }


        View secondaryPhoneTypeLayout = view.findViewById(R.id.secondaryPhoneTypeDemographicsLayout);
        TextView chooseSecondaryPhoneType = (TextView) view.findViewById(R.id.chooseSecondaryPhoneType);
        setVisibility(secondaryPhoneTypeLayout, personalInfoSection.getProperties().getSecondaryPhoneNumberType().isDisplayed());
        chooseSecondaryPhoneType.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getSecondaryPhoneNumberType().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseSecondaryPhoneType, selectedSecondaryPhoneType),
                        Label.getLabel("demographics_secondary_phone_type")));
        String secondaryPhoneType = demographicPayload.getPersonalDetails().getSecondaryPhoneNumberType();
        initSelectableInput(chooseSecondaryPhoneType, selectedSecondaryPhoneType, secondaryPhoneType);


        View preferredContactMethodLayout = view.findViewById(R.id.preferredContactMethodDemographicsLayout);
        TextView choosePreferredContactMethod = (TextView) view.findViewById(R.id.choosePreferredContactMethod);
        setVisibility(preferredContactMethodLayout, personalInfoSection.getProperties().getPreferredContact().isDisplayed());
        choosePreferredContactMethod.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getPreferredContact().getOptions(),
                        getDefaultOnOptionsSelectedListener(choosePreferredContactMethod, selectedContactMethod),
                        Label.getLabel("demographics_preferred_contact_method")));
        String preferredContactMethod = demographicPayload.getPersonalDetails().getPreferredContact();
        initSelectableInput(choosePreferredContactMethod, selectedContactMethod, preferredContactMethod);


        View maritalStatusLayout = view.findViewById(R.id.maritalStatusDemographicsLayout);
        TextView chooseMaritalStatus = (TextView) view.findViewById(R.id.chooseMaritalStatus);
        setVisibility(maritalStatusLayout, personalInfoSection.getProperties().getMaritalStatus().isDisplayed());
        chooseMaritalStatus.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getMaritalStatus().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseMaritalStatus, selectedMaritalStatus),
                        Label.getLabel("demographics_marital_status")));
        String maritalStatus = demographicPayload.getPersonalDetails().getMaritalStatus();
        initSelectableInput(chooseMaritalStatus, selectedMaritalStatus, maritalStatus);


        View employmentStatusLayout = view.findViewById(R.id.employmentStatusDemographicsLayout);
        TextView chooseEmploymentStatus = (TextView) view.findViewById(R.id.chooseEmploymentStatus);
        setVisibility(employmentStatusLayout, personalInfoSection.getProperties().getEmploymentStatus().isDisplayed());
        chooseEmploymentStatus.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getEmploymentStatus().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseEmploymentStatus, selectedEmploymentStatus),
                        Label.getLabel("demographics_employment_status")));
        String employmentStatus = demographicPayload.getPersonalDetails().getEmploymentStatus();
        initSelectableInput(chooseEmploymentStatus, selectedEmploymentStatus, employmentStatus);


        View emergencyContactRelationshipLayout = view.findViewById(R.id.emergencyContactRelationshipDemographicsLayout);
        TextView chooseEmergencyContactRelationship = (TextView) view.findViewById(R.id.chooseEmergencyContactRelationship);
        setVisibility(emergencyContactRelationshipLayout, personalInfoSection.getProperties().getEmergencyContactRelationship().isDisplayed());
        chooseEmergencyContactRelationship.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getEmergencyContactRelationship().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseEmergencyContactRelationship, selectedEmergencyContactRelationship),
                        Label.getLabel("demographics_emergency_contact_relationship")));
        String emergencyContactRelationship = demographicPayload.getPersonalDetails().getEmergencyContactRelationship();
        initSelectableInput(chooseEmergencyContactRelationship, selectedEmergencyContactRelationship, emergencyContactRelationship);


        View referralSourceLayout = view.findViewById(R.id.referralSourceDemographicsLayout);
        TextView chooseReferralSource = (TextView) view.findViewById(R.id.chooseReferralSource);
        setVisibility(referralSourceLayout, personalInfoSection.getProperties().getReferralSource().isDisplayed());
        chooseReferralSource.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getReferralSource().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseReferralSource, selectedReferralSource),
                        Label.getLabel("demographics_referral_source")));
        String referralSource = demographicPayload.getPersonalDetails().getReferralSource();
        initSelectableInput(chooseReferralSource, selectedReferralSource, referralSource);

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

        String preferredName = ((TextView) findViewById(R.id.preferredName)).getText().toString();
        if (!StringUtil.isNullOrEmpty(preferredName)) {
            demographicPersDetailsPayloadDTO.setPreferredName(preferredName);
        }

        String socialSecurity = ((TextView) findViewById(R.id.socialSecurityNumber)).getText().toString();
        if (!StringUtil.isNullOrEmpty(socialSecurity)) {
            demographicPersDetailsPayloadDTO.setSocialSecurityNumber(socialSecurity);
        }

        String emailAddress = ((TextView) findViewById(R.id.email)).getText().toString();
        if (!StringUtil.isNullOrEmpty(emailAddress)) {
            demographicPersDetailsPayloadDTO.setEmailAddress(emailAddress);
        }

        String preferredLanguage = selectedPreferredLanguage.getName();
        if (!StringUtil.isNullOrEmpty(preferredLanguage)) {
            demographicPersDetailsPayloadDTO.setPreferredLanguage(preferredLanguage);
        }

        String driverLicense = ((TextView) findViewById(R.id.driverLicense)).getText().toString();
        if (!StringUtil.isNullOrEmpty(driverLicense)) {
            demographicPersDetailsPayloadDTO.setDriversLicenseNumber(driverLicense);
        }

        String driverLicenseState = selectedDriverLicenseState.getName();
        if (!StringUtil.isNullOrEmpty(driverLicenseState)) {
            demographicPersDetailsPayloadDTO.setDriversLicenseState(driverLicenseState);
        }

        String secondaryPhone = ((TextView) findViewById(R.id.secondaryPhone)).getText().toString();
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
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getGender().isRequired()
                && StringUtil.isNullOrEmpty(selectedGender.getName())){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getPrimaryRace().isRequired()
                && StringUtil.isNullOrEmpty(selectedRace.getName())){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryRace().isRequired()
                && StringUtil.isNullOrEmpty(selectedSecondaryRace.getName())){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getEthnicity().isRequired()
                && StringUtil.isNullOrEmpty(selectedEthnicity.getName())){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredName().isRequired()
                && checkTextEmptyValue(R.id.preferredName, view)){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getSocialSecurityNumber().isRequired()
                && checkTextEmptyValue(R.id.socialSecurityNumber, view)){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getEmailAddress().isRequired()
                && checkTextEmptyValue(R.id.email, view)){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredLanguage().isRequired()
                && StringUtil.isNullOrEmpty(selectedPreferredLanguage.getName())){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getDriversLicenseNumber().isRequired()
                && checkTextEmptyValue(R.id.driverLicense, view)){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getDriversLicenseState().isRequired()
                && StringUtil.isNullOrEmpty(selectedDriverLicenseState.getName())){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryPhoneNumber().isRequired()
                && checkTextEmptyValue(R.id.secondaryPhone, view)){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryPhoneNumberType().isRequired()
                && StringUtil.isNullOrEmpty(selectedSecondaryPhoneType.getName())){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredContact().isRequired()
                && StringUtil.isNullOrEmpty(selectedContactMethod.getName())){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getMaritalStatus().isRequired()
                && StringUtil.isNullOrEmpty(selectedMaritalStatus.getName())){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getEmploymentStatus().isRequired()
                && StringUtil.isNullOrEmpty(selectedEmploymentStatus.getName())){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getEmergencyContactRelationship().isRequired()
                && StringUtil.isNullOrEmpty(selectedEmergencyContactRelationship.getName())){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getReferralSource().isRequired()
                && StringUtil.isNullOrEmpty(selectedReferralSource.getName())){
            return false;
        }


        EditText secondaryPhoneNumber = (EditText) view.findViewById(R.id.secondaryPhone);
        if(!StringUtil.isNullOrEmpty(secondaryPhoneNumber.getText().toString()) && !ValidationHelper.isValidString(secondaryPhoneNumber.getText().toString().trim(), ValidationHelper.PHONE_NUMBER_PATTERN)){
            TextInputLayout phoneLayout = (TextInputLayout) view.findViewById(R.id.secondaryPhoneInputLayout);
            phoneLayout.setErrorEnabled(true);
            phoneLayout.setError(Label.getLabel("demographics_phone_number_validation_msg"));
            return false;
        }

        EditText emailAddress = (EditText) view.findViewById(R.id.email);
        if(!StringUtil.isNullOrEmpty(emailAddress.getText().toString()) && !ValidationHelper.isValidString(emailAddress.getText().toString().trim(), ValidationHelper.EMAIL_PATTERN)){
            TextInputLayout emailLayout = (TextInputLayout) view.findViewById(R.id.emailInputLayout);
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
}
