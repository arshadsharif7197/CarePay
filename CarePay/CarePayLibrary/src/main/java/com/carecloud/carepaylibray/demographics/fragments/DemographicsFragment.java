package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.EmergencyContactInterface;
import com.carecloud.carepaylibray.demographics.EmergencyContactInterfaceFragment;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicEmergencyContactSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicEmploymentInfoSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsField;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsPersonalSection;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerDto;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmploymentInfoModel;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.smartystreets.api.us_zipcode.City;

import java.util.List;

/**
 * A simple {@link CheckInDemographicsBaseFragment} subclass.
 */
public class DemographicsFragment extends CheckInDemographicsBaseFragment
        implements EmergencyContactInterfaceFragment {

    private DemographicDTO demographicDTO;
    private DemographicDataModel dataModel;

    private PatientModel demographicPersDetailsPayloadDTO;
    private EmploymentInfoModel demographicEmploymentInfoModel;
    private PatientModel demographicsEmergencyContactModel;

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
    private boolean showEmployerFields;
    private View employerDependentFieldsLayout;
    private EditText cityEditText;
    private EditText stateEditText;

    private EmergencyContactInterface callback;

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EmergencyContactInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        dataModel = demographicDTO.getMetadata().getNewDataModel();

        if (demographicDTO.getPayload().getDemographics() != null) {
            demographicPersDetailsPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
            demographicEmploymentInfoModel = demographicDTO.getPayload().getDemographics().getPayload().getEmploymentInfoModel();
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
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, checkinFlowCallback.getTotalSteps(),
                CheckinFlowCallback.DEMOGRAPHICS);
        checkinFlowCallback.setCurrentStep(CheckinFlowCallback.DEMOGRAPHICS);
    }

    private void initViews(View view) {
        DemographicPayloadDTO demographicPayload = demographicDTO.getPayload().getDemographics().getPayload();
        DemographicsPersonalSection personalInfoSection = dataModel.getDemographic().getPersonalDetails();
        setUpBaseDemographicFields(view, demographicPayload, personalInfoSection);
        setUpExtendedDemographicFields(view, demographicPayload, personalInfoSection);
        setUpEmergencyContact(view, demographicPayload.getEmergencyContact());
        setUpEmployer(view, demographicPayload, dataModel.getDemographic().getEmploymentInfo());
    }

    private void setUpExtendedDemographicFields(View view, DemographicPayloadDTO demographicPayload,
                                                DemographicsPersonalSection personalInfoSection) {

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getSocialSecurityNumber(),
                personalInfoSection.getProperties().getSocialSecurityNumber(),
                R.id.socialSecurityContainer, R.id.socialSecurityInputLayout,
                R.id.socialSecurityNumber, R.id.socialSecurityOptional, null, null);

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getPreferredName(),
                personalInfoSection.getProperties().getPreferredName(), R.id.preferredNameContainer,
                R.id.preferredNameInputLayout, R.id.preferredName, R.id.preferredNameOptional, null, null);


        setUpDemographicField(view, demographicPayload.getPersonalDetails().getEmailAddress(),
                personalInfoSection.getProperties().getEmailAddress(), R.id.emailContainer, R.id.emailInputLayout,
                R.id.email, R.id.emailOptional, null, null);

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getPreferredLanguage(),
                personalInfoSection.getProperties().getPreferredLanguage(),
                R.id.preferredLanguageDemographicsLayout, R.id.preferredLanguageInputLayout,
                R.id.preferredLanguageEditText, R.id.preferredLanguageOptional, selectedPreferredLanguage,
                Label.getLabel("demographics_preferred_language"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getDriversLicenseNumber(),
                personalInfoSection.getProperties().getDriversLicenseNumber(),
                R.id.driverLicenseContainer, R.id.driverLicenseInputLayout,
                R.id.driverLicense, R.id.driverLicenseOptional, null, null);

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getDriversLicenseState(),
                personalInfoSection.getProperties().getDriversLicenseState(),
                R.id.driverLicenseStateDemographicsLayout, R.id.driverLicenseStateInputLayout,
                R.id.driverLicenseStateEditText, R.id.driverLicenseStateOptional, selectedDriverLicenseState,
                Label.getLabel("demographics_driver_license_state"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getSecondaryPhoneNumber(),
                personalInfoSection.getProperties().getSecondaryPhoneNumber(),
                R.id.secondaryPhoneContainer, R.id.secondaryPhoneInputLayout,
                R.id.secondaryPhone, R.id.secondaryPhoneOptional, null, null);
        EditText secondaryPhoneEditText = (EditText) view.findViewById(R.id.secondaryPhone);
        secondaryPhoneEditText.addTextChangedListener(phoneInputFormatter);
        if (!dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryPhoneNumber().isRequired()) {
            secondaryPhoneEditText.addTextChangedListener(
                    clearValidationErrorsOnTextChange((TextInputLayout) view.findViewById(R.id.secondaryPhoneInputLayout)));
        }

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getSecondaryPhoneNumberType(),
                personalInfoSection.getProperties().getSecondaryPhoneNumberType(),
                R.id.secondaryPhoneTypeDemographicsLayout, R.id.secondaryPhoneTypeInputLayout,
                R.id.secondaryPhoneTypeEditText, R.id.secondaryPhoneTypeOptional, selectedSecondaryPhoneType,
                Label.getLabel("demographics_secondary_phone_type"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getPreferredContact(),
                personalInfoSection.getProperties().getPreferredContact(),
                R.id.preferredContactMethodDemographicsLayout, R.id.preferredContactMethodInputLayout,
                R.id.preferredContactMethodEditText, R.id.contactMethodOptional, selectedContactMethod,
                Label.getLabel("demographics_preferred_contact_method"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getMaritalStatus(),
                personalInfoSection.getProperties().getMaritalStatus(),
                R.id.maritalStatusDemographicsLayout, R.id.maritalStatusInputLayout,
                R.id.maritalStatusEditText, R.id.maritalStatusOptional, selectedMaritalStatus,
                Label.getLabel("demographics_marital_status"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getReferralSource(),
                personalInfoSection.getProperties().getReferralSource(), R.id.referralSourceDemographicsLayout,
                R.id.referralSourceInputLayout, R.id.referralSourceEditText,
                R.id.referralSourceOptional, selectedReferralSource, Label.getLabel("demographics_referral_source"));
    }

    private void setUpBaseDemographicFields(View view, DemographicPayloadDTO demographicPayload,
                                            DemographicsPersonalSection personalInfoSection) {
        setUpDemographicField(view, demographicPayload.getPersonalDetails().getGender(),
                personalInfoSection.getProperties().getGender(), R.id.genderDemographicsLayout,
                R.id.genderInputLayout, R.id.genderEditText,
                R.id.genderOptionalLabel, selectedGender, Label.getLabel("demographics_review_gender"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getPrimaryRace(),
                personalInfoSection.getProperties().getPrimaryRace(), R.id.raceDemographicsLayout,
                R.id.raceInputLayout, R.id.raceEditText,
                R.id.raceOptionalLabel, selectedRace, Label.getLabel("demographics_review_race"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getSecondaryRace(),
                personalInfoSection.getProperties().getSecondaryRace(), R.id.secondaryRaceDemographicsLayout,
                R.id.secondaryRaceInputLayout, R.id.secondaryRaceEditText, R.id.secondaryRaceOptional,
                selectedSecondaryRace, Label.getLabel("demographics_secondary_race"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getEthnicity(),
                personalInfoSection.getProperties().getEthnicity(), R.id.ethnicityDemographicsLayout,
                R.id.ethnicityInputLayout, R.id.ethnicityEditText,
                R.id.ethnicityOptional, selectedEthnicity, Label.getLabel("demographics_review_ethnicity"));
    }

    private void setUpDemographicField(View view, String value, DemographicsField demographicsField,
                                       int containerLayout, int inputLayoutId, int editTextId, int optionalViewId,
                                       DemographicsOption demographicsOption, String optionDialogTitle) {
        view.findViewById(containerLayout).setVisibility(demographicsField.isDisplayed() ? View.VISIBLE : View.GONE);
        final TextInputLayout inputLayout = (TextInputLayout) view.findViewById(inputLayoutId);
        final EditText editText = (EditText) view.findViewById(editTextId);
        editText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(inputLayout, null));
        editText.setText(value);
        editText.getOnFocusChangeListener().onFocusChange(editText,
                !StringUtil.isNullOrEmpty(editText.getText().toString().trim()));
        final View optionalView = view.findViewById(optionalViewId);
        optionalView.setVisibility(!demographicsField.isRequired()
                && StringUtil.isNullOrEmpty(value) ? View.VISIBLE : View.GONE);
        if (demographicsOption != null) {
            editText.setOnClickListener(getEditTextClickListener(demographicsField.getOptions(),
                    inputLayout, editText, optionalView,
                    demographicsOption, optionDialogTitle));
            demographicsOption.setName(editText.getText().toString());
            demographicsOption.setLabel(editText.getText().toString());
        } else if (demographicsField.isRequired()) {
            editText.addTextChangedListener(getValidateEmptyTextWatcher(inputLayout));
        }
    }

    private View.OnClickListener getEditTextClickListener(List<DemographicsOption> options,
                                                          final TextInputLayout inputLayout,
                                                          final EditText editText,
                                                          final View optionalLabel,
                                                          final DemographicsOption demographicsOption,
                                                          final String dialogTitle) {
        return getSelectOptionsListener(options,
                new OnOptionSelectedListener() {
                    @Override
                    public void onOptionSelected(DemographicsOption option) {
                        if (demographicsOption != null) {
                            demographicsOption.setLabel(option.getLabel());
                            demographicsOption.setName(option.getName());
                            demographicsOption.setId(option.getId());
                        }
                        editText.setText(option.getLabel());
                        editText.getOnFocusChangeListener()
                                .onFocusChange(editText, !StringUtil.isNullOrEmpty(editText.getText().toString()));
                        inputLayout.setError(null);
                        inputLayout.setErrorEnabled(false);
                        optionalLabel.setVisibility(View.GONE);
                        checkIfEnableButton(getView());
                    }
                },
                dialogTitle);
    }

    private void setUpEmergencyContact(View view, PatientModel emergencyContact) {

        TextInputLayout emergencyContactInputLayout = (TextInputLayout) view.findViewById(R.id.emergencyContactInputLayout);
//        emergencyContactInputLayout.setVisibility(emergencyContactSection.isDisplay() ? View.VISIBLE : View.GONE);
        EditText emergencyContactEditText = (EditText) view.findViewById(R.id.emergencyContactEditText);
        emergencyContactEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(emergencyContactInputLayout, null));
        if (emergencyContact != null) {
            emergencyContactEditText.setText(emergencyContact.getFullName());
        }
        emergencyContactEditText.getOnFocusChangeListener().onFocusChange(emergencyContactEditText,
                !StringUtil.isNullOrEmpty(emergencyContactEditText.getText().toString().trim()));
        DemographicEmergencyContactSection emergencyContactSection = dataModel.getDemographic().getEmergencyContact();
        view.findViewById(R.id.emergencyContactOptionalLabel)
                .setVisibility(emergencyContactSection.isRequired() ? View.GONE : View.VISIBLE);
        emergencyContactEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showAddEditEmergencyContactDialog();
            }
        });

    }

    private void setUpEmployer(final View view, DemographicPayloadDTO demographicPayload, DemographicEmploymentInfoSection employmentInfoSection) {
        boolean isEmploymentStuffVisible = employmentInfoSection.isDisplay();
        if (isEmploymentStuffVisible) {
            final TextView employmentStatusEditText = (TextView) view.findViewById(R.id.employmentStatusEditText);
            final View employmentStatusOptional = view.findViewById(R.id.employmentStatusOptional);

            employmentStatusEditText.setOnClickListener(
                    getSelectOptionsListener(employmentInfoSection.getProperties().getEmploymentStatus().getOptions(),
                            new OnOptionSelectedListener() {
                                @Override
                                public void onOptionSelected(DemographicsOption option) {
                                    employmentStatusEditText.setText(option.getLabel());

                                    if (employmentStatusOptional != null) {
                                        employmentStatusOptional.setVisibility(View.GONE);
                                    }
                                    selectedEmploymentStatus.setLabel(option.getLabel());
                                    selectedEmploymentStatus.setName(option.getName());
                                    showEmployerFields = option.getLabel().toLowerCase().equals("employed")
                                            || option.getLabel().toLowerCase().equals("part time");
                                    manageEmployerFieldsVisibility(showEmployerFields);
                                    checkIfEnableButton(getView());
                                }
                            }, Label.getLabel("demographics_employment_status")));

            String employmentStatus = demographicPayload.getEmploymentInfoModel().getEmploymentStatus();
            selectedEmploymentStatus.setName(employmentStatus);
            selectedEmploymentStatus.setLabel(employmentStatus);
            if (!StringUtil.isNullOrEmpty(employmentStatus)) {
                employmentStatusOptional.setVisibility(View.VISIBLE);
                showEmployerFields = employmentStatus.toLowerCase().equals("employed")
                        || employmentStatus.toLowerCase().equals("part time");
            }
            if (!employmentInfoSection.isRequired()) {
                view.findViewById(R.id.employmentInfoOptionalTextView).setVisibility(View.VISIBLE);
                view.findViewById(R.id.employmentInfoContainer).setVisibility(View.GONE);
            }

        } else {
            view.findViewById(R.id.employmentStatusDemographicsLayout).setVisibility(View.GONE);
            view.findViewById(R.id.employmentInfoContainer).setVisibility(View.GONE);
        }

        setEmployerInfoFields(view, demographicPayload, isEmploymentStuffVisible);

    }

    private void setEmployerInfoFields(View view, DemographicPayloadDTO demographicPayload, boolean isEmploymentStuffVisible) {
        employerDependentFieldsLayout = view.findViewById(R.id.employerDependentLayout);

        selectedEmployer = demographicPayload.getEmploymentInfoModel().getEmployerDto();
        if (selectedEmployer == null) {
            selectedEmployer = new EmployerDto();
        }

        TextInputLayout employerNameTextInputLayout = (TextInputLayout) view.findViewById(R.id.employerNameTextInputLayout);
        EditText employerNameEditText = (EditText) view.findViewById(R.id.employerNameEditText);
        employerNameEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(employerNameTextInputLayout, null));
        employerNameEditText.setText(selectedEmployer.getName());
        employerNameEditText.getOnFocusChangeListener().onFocusChange(employerNameEditText,
                !StringUtil.isNullOrEmpty(employerNameEditText.getText().toString().trim()));
        employerNameEditText.addTextChangedListener(getValidateEmptyTextWatcher(employerNameTextInputLayout));
        employerNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                selectedEmployer.setName(editable.toString());
                checkIfEnableButton(getView());
            }
        });

        TextInputLayout address1TextInputLayout = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        EditText addressEditText = (EditText) view.findViewById(R.id.addressEditText);
        addressEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(address1TextInputLayout, null));
        if (selectedEmployer.getAddress() != null) {
            addressEditText.setText(selectedEmployer.getAddress().getAddress1());
        }

        TextInputLayout address2TextInputLayout = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        EditText addressEditText2 = (EditText) view.findViewById(R.id.addressEditText2);
        addressEditText2.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(address2TextInputLayout, null));
        if (selectedEmployer.getAddress() != null) {
            addressEditText2.setText(selectedEmployer.getAddress().getAddress2());
        }

        TextInputLayout zipCodeTextInputLayout = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        EditText zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeTextView);
        zipCodeEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(zipCodeTextInputLayout,
                getZipCodeFocusListener(zipCodeEditText)));
        if (selectedEmployer.getAddress() != null) {
            zipCodeEditText.setText(selectedEmployer.getAddress().getZipcode());
        }

        TextInputLayout cityTextInputLayout = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        cityEditText = (EditText) view.findViewById(R.id.cityTextView);
        cityEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(cityTextInputLayout, null));
        if (selectedEmployer.getAddress() != null) {
            cityEditText.setText(selectedEmployer.getAddress().getCity());
        }

        TextInputLayout stateTextInputLayout = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
        stateEditText = (EditText) view.findViewById(R.id.stateTextView);
        stateEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(stateTextInputLayout, null));
        stateEditText.setOnClickListener(
                getSelectOptionsListener(dataModel.getDemographic().getEmploymentInfo()
                                .getProperties().getDemographicEmployerModel()
                                .getProperties().getAddress().getProperties()
                                .getState().getOptions(),
                        new OnOptionSelectedListener() {
                            @Override
                            public void onOptionSelected(DemographicsOption option) {
                                stateEditText.setText(option.getLabel());
                            }
                        },
                        Label.getLabel("demographics_documents_title_select_state")));
        stateEditText.setText(selectedEmployer.getAddress().getState());

        TextInputLayout phoneTextInputLayout = (TextInputLayout) view.findViewById(R.id.phoneTextInputLayout);
        EditText phoneEditText = (EditText) view.findViewById(R.id.phoneTextView);
        phoneEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(phoneTextInputLayout, null));
        if (selectedEmployer.getAddress() != null) {
            phoneEditText.setText(selectedEmployer.getAddress().getPhone());
        }

        manageEmployerFieldsVisibility(showEmployerFields && isEmploymentStuffVisible);

    }

    private void manageEmployerFieldsVisibility(boolean visible) {
        employerDependentFieldsLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
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
            demographicEmploymentInfoModel.setEmploymentStatus(employmentStatus);
        }

        String employerName = ((TextView) findViewById(R.id.employerNameEditText)).getText().toString().trim();
        if (showEmployerFields && !StringUtil.isNullOrEmpty(employerName)) {
            selectedEmployer.setName(employerName);

            String address = ((TextView) findViewById(R.id.addressEditText)).getText().toString().trim();
            selectedEmployer.getAddress().setAddress1(address);

            String address2 = ((TextView) findViewById(R.id.addressEditText2)).getText().toString().trim();
            selectedEmployer.getAddress().setAddress2(address2);

            String zipCode = ((TextView) findViewById(R.id.zipCodeTextView)).getText().toString().trim();
            selectedEmployer.getAddress().setZipcode(zipCode);

            String city = ((TextView) findViewById(R.id.cityTextView)).getText().toString().trim();
            selectedEmployer.getAddress().setCity(city);

            String state = ((TextView) findViewById(R.id.stateTextView)).getText().toString().trim();
            selectedEmployer.getAddress().setState(state);

            String phone = ((TextView) findViewById(R.id.phoneTextView)).getText().toString().trim();
            selectedEmployer.getAddress().setPhone(phone);

            demographicEmploymentInfoModel.setEmployerDto(selectedEmployer);
        } else {
            demographicEmploymentInfoModel.setEmployerDto(null);
        }


        String emergencyContactRelationship = selectedEmergencyContactRelationship.getName();//todo need to move this from personal info
        if (!StringUtil.isNullOrEmpty(emergencyContactRelationship)) {
            demographicPersDetailsPayloadDTO.setEmergencyContactRelationship(emergencyContactRelationship);
        }

        String referralSource = selectedReferralSource.getName();
        if (!StringUtil.isNullOrEmpty(referralSource)) {
            demographicPersDetailsPayloadDTO.setReferralSource(referralSource);
        }

        updatableDemographicDTO.getPayload().getDemographics().getPayload().setPersonalDetails(demographicPersDetailsPayloadDTO);
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setEmploymentInfoModel(demographicEmploymentInfoModel);
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        updatableDemographicDTO.getPayload().setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        return updatableDemographicDTO;
    }


    @Override
    protected boolean passConstraints(View view) {
        try {
            if (dataModel.getDemographic().getPersonalDetails().getProperties().getGender().isRequired()
                    && StringUtil.isNullOrEmpty(selectedGender.getName())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.genderDemographicsLayout));
                    setDefaultError(view, R.id.genderInputLayout);
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.genderDemographicsLayout));
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getPrimaryRace().isRequired()
                    && StringUtil.isNullOrEmpty(selectedRace.getName())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.raceDemographicsLayout));
                    setDefaultError(view, R.id.raceInputLayout);
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.raceDemographicsLayout));
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryRace().isRequired()
                    && StringUtil.isNullOrEmpty(selectedSecondaryRace.getName())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.secondaryRaceDemographicsLayout));
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.secondaryRaceDemographicsLayout));
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getEthnicity().isRequired()
                    && StringUtil.isNullOrEmpty(selectedEthnicity.getName())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.ethnicityDemographicsLayout));
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.ethnicityDemographicsLayout));
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredName().isRequired()
                    && checkTextEmptyValue(R.id.preferredName, view)) {
                if (isUserAction()) {
                    setDefaultError(view, R.id.preferredNameInputLayout);
                }
                return false;
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getSocialSecurityNumber().isRequired()
                    && checkTextEmptyValue(R.id.socialSecurityNumber, view)) {
                if (isUserAction()) {
                    setDefaultError(view, R.id.socialSecurityInputLayout);
                }
                return false;
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getEmailAddress().isRequired()
                    && checkTextEmptyValue(R.id.email, view)) {
                if (isUserAction()) {
                    setDefaultError(view, R.id.emailInputLayout);
                }
                return false;
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredLanguage().isRequired()
                    && StringUtil.isNullOrEmpty(selectedPreferredLanguage.getName())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.preferredLanguageDemographicsLayout));
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.preferredLanguageDemographicsLayout));
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getDriversLicenseNumber().isRequired()
                    && checkTextEmptyValue(R.id.driverLicense, view)) {
                if (isUserAction()) {
                    setDefaultError(view, R.id.driverLicenseInputLayout);
                }
                return false;
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getDriversLicenseState().isRequired()
                    && StringUtil.isNullOrEmpty(selectedDriverLicenseState.getName())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.driverLicenseStateDemographicsLayout));
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.driverLicenseStateDemographicsLayout));
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryPhoneNumber().isRequired()
                    && checkTextEmptyValue(R.id.secondaryPhone, view)) {
                if (isUserAction()) {
                    setDefaultError(view, R.id.secondaryPhoneInputLayout);
                }
                return false;
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryPhoneNumberType().isRequired()
                    && StringUtil.isNullOrEmpty(selectedSecondaryPhoneType.getName())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.secondaryPhoneTypeDemographicsLayout));
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.secondaryPhoneTypeDemographicsLayout));
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredContact().isRequired()
                    && StringUtil.isNullOrEmpty(selectedContactMethod.getName())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.preferredContactMethodDemographicsLayout));
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.preferredContactMethodDemographicsLayout));
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getMaritalStatus().isRequired()
                    && StringUtil.isNullOrEmpty(selectedMaritalStatus.getName())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.maritalStatusDemographicsLayout));
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.maritalStatusDemographicsLayout));
            }

            if (dataModel.getDemographic().getEmergencyContact().isRequired()
                    && (demographicsEmergencyContactModel == null
                    || StringUtil.isNullOrEmpty(demographicsEmergencyContactModel.getFirstName())
                    || StringUtil.isNullOrEmpty(demographicsEmergencyContactModel.getLastName())
                    || demographicsEmergencyContactModel.getPhones() == null
                    || demographicsEmergencyContactModel.getPhones().isEmpty()
                    || StringUtil.isNullOrEmpty(demographicsEmergencyContactModel.getEmergencyContactRelationship()))) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.emergencyContactDemographicsLayout));
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.emergencyContactDemographicsLayout));
            }

            if (dataModel.getDemographic().getPersonalDetails().getProperties().getReferralSource().isRequired()
                    && StringUtil.isNullOrEmpty(selectedReferralSource.getName())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.referralSourceDemographicsLayout));
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.referralSourceDemographicsLayout));
            }

            if (dataModel.getDemographic().getEmploymentInfo().isRequired()
                    && StringUtil.isNullOrEmpty(selectedEmploymentStatus.getName())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.employmentStatusDemographicsLayout));
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.employmentStatusDemographicsLayout));
            }

            if (dataModel.getDemographic().getEmploymentInfo().isRequired()
                    && StringUtil.isNullOrEmpty(selectedEmployer.getName())
                    && showEmployerFields) {
                if (isUserAction()) {
                    setDefaultError(view, R.id.employerNameTextInputLayout);
                }
                return false;
            }

            TextInputLayout phoneLayout = (TextInputLayout) view.findViewById(R.id.secondaryPhoneInputLayout);
            EditText secondaryPhoneNumber = (EditText) view.findViewById(R.id.secondaryPhone);
            if (phoneLayout.getVisibility() == View.VISIBLE &&
                    !StringUtil.isNullOrEmpty(secondaryPhoneNumber.getText().toString().trim()) &&
                    !ValidationHelper.isValidString(secondaryPhoneNumber.getText().toString().trim(),
                            ValidationHelper.PHONE_NUMBER_PATTERN)) {
                setFieldError(phoneLayout, Label.getLabel("demographics_phone_number_validation_msg"));
                return false;
            }

            TextInputLayout emailLayout = (TextInputLayout) view.findViewById(R.id.emailInputLayout);
            EditText emailAddress = (EditText) view.findViewById(R.id.email);
            if (emailLayout.getVisibility() == View.VISIBLE &&
                    !StringUtil.isNullOrEmpty(emailAddress.getText().toString().trim()) &&
                    !ValidationHelper.isValidEmail(emailAddress.getText().toString().trim())) {
                setFieldError(emailLayout, Label.getLabel("demographics_email_validation_msg"));
                return false;
            }

            return true;
        } finally {
            setUserAction(false);
        }
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_review_demographic_demographics;
    }

    private View.OnFocusChangeListener getZipCodeFocusListener(final EditText editText) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    getCityAndState(editText.getText().toString());
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
                    cityEditText.setText(smartyStreetsResponse.getCity());
                    String stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    stateEditText.setText(stateAbbr);
                }
            }


        }.execute(zipCode);
    }

    @Override
    public void updateEmergencyContact(PatientModel emergencyContact) {
        setUpEmergencyContact(getView(), emergencyContact);
        checkIfEnableButton(getView());
    }
}
