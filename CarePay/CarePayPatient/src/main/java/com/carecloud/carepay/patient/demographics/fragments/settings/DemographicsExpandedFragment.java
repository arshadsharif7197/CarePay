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
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.EmergencyContactInterface;
import com.carecloud.carepaylibray.demographics.EmergencyContactInterfaceFragment;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicEmergencyContactSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicEmploymentInfoSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsPersonalSection;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerDto;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmploymentInfoModel;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
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
public class DemographicsExpandedFragment extends DemographicsBaseSettingsFragment
        implements EmergencyContactInterfaceFragment {

    private DemographicDTO demographicsSettingsDTO;
    private DemographicDataModel dataModel;

    private View nextButton;
    private EmergencyContactInterface callback;

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


    /**
     * @return an instance of DemographicsInformationFragment
     */
    public static DemographicsExpandedFragment newInstance() {
        return new DemographicsExpandedFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (EmergencyContactInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EmergencyContactInterface");
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicDTO) callback.getDto();
        dataModel = demographicsSettingsDTO.getMetadata().getNewDataModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demographics_expanded, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("demographics_additional"));
        toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
        callback.setToolbar(toolbar);

        nextButton = findViewById(R.id.buttonAddDemographicInfo);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDemographics();
            }
        });

        initViews(view);
        checkIfEnableButton();
    }


    private void initViews(View view) {
        DemographicPayloadDTO demographicPayload = demographicsSettingsDTO.getPayload().getDemographics().getPayload();
        initDemographicInfo(view, demographicPayload);
    }

    private void initDemographicInfo(View view, DemographicPayloadDTO demographicPayload) {
        DemographicsPersonalSection personalInfoSection = dataModel.getDemographic().getPersonalDetails();

        TextInputLayout preferredNameLayout = (TextInputLayout) view.findViewById(R.id.preferredNameInputLayout);
        EditText preferredName = (EditText) view.findViewById(R.id.preferredName);
        preferredName.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(preferredNameLayout, null));
        setVisibility(preferredNameLayout, personalInfoSection.getProperties().getPreferredName().isDisplayed());
        preferredName.setText(demographicPayload.getPersonalDetails().getPreferredName());
        preferredName.getOnFocusChangeListener().onFocusChange(preferredName,
                !StringUtil.isNullOrEmpty(preferredName.getText().toString().trim()));
        if (personalInfoSection.getProperties().getPreferredName().isRequired()) {
            preferredName.addTextChangedListener(getValidateEmptyTextWatcher(preferredNameLayout));
        } else if (personalInfoSection.getProperties().getPreferredName().isDisplayed() &&
                StringUtil.isNullOrEmpty(demographicPayload.getPersonalDetails().getPreferredName())) {
            View preferredNameOptional = view.findViewById(R.id.preferredNameOptional);
            preferredNameOptional.setVisibility(View.VISIBLE);
        }


        TextInputLayout socialSecurityLayout = (TextInputLayout) view.findViewById(R.id.socialSecurityInputLayout);
        EditText socialSecurity = (EditText) view.findViewById(R.id.socialSecurityNumber);
        socialSecurity.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(socialSecurityLayout, null));
        setVisibility(socialSecurityLayout, personalInfoSection.getProperties().getSocialSecurityNumber().isDisplayed());
        socialSecurity.setText(demographicPayload.getPersonalDetails().getSocialSecurityNumber());
        socialSecurity.getOnFocusChangeListener().onFocusChange(socialSecurity,
                !StringUtil.isNullOrEmpty(socialSecurity.getText().toString().trim()));
        if (personalInfoSection.getProperties().getSocialSecurityNumber().isRequired()) {
            socialSecurity.addTextChangedListener(getValidateEmptyTextWatcher(socialSecurityLayout));
        } else if (personalInfoSection.getProperties().getSocialSecurityNumber().isDisplayed() &&
                StringUtil.isNullOrEmpty(demographicPayload.getPersonalDetails().getSocialSecurityNumber())) {
            View socialSecurityOptional = view.findViewById(R.id.socialSecurityOptional);
            socialSecurityOptional.setVisibility(View.VISIBLE);
        }


        TextInputLayout emailAddressLayout = (TextInputLayout) view.findViewById(R.id.emailInputLayout);
        EditText emailAddress = (EditText) view.findViewById(R.id.email);
        emailAddress.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(emailAddressLayout, null));
        setVisibility(emailAddressLayout, personalInfoSection.getProperties().getEmailAddress().isDisplayed());
        emailAddress.setText(demographicPayload.getPersonalDetails().getEmailAddress());
        emailAddress.getOnFocusChangeListener().onFocusChange(emailAddress,
                !StringUtil.isNullOrEmpty(emailAddress.getText().toString().trim()));
        if (personalInfoSection.getProperties().getEmailAddress().isRequired()) {
            emailAddress.addTextChangedListener(getValidateEmptyTextWatcher(emailAddressLayout));
        } else if (personalInfoSection.getProperties().getEmailAddress().isDisplayed() &&
                StringUtil.isNullOrEmpty(demographicPayload.getPersonalDetails().getEmailAddress())) {
            View emailAddressOptional = view.findViewById(R.id.emailOptional);
            emailAddressOptional.setVisibility(View.VISIBLE);
        }


        View preferredLanguageLayout = view.findViewById(R.id.preferredLanguageDemographicsLayout);
        TextView choosePreferredLanguage = (TextView) view.findViewById(R.id.choosePreferredLanguage);
        View preferredLanguageOptional = view.findViewById(R.id.preferredLanguageOptional);
        setVisibility(preferredLanguageLayout, personalInfoSection.getProperties()
                .getPreferredLanguage().isDisplayed());
        choosePreferredLanguage.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getPreferredLanguage().getOptions(),
                        getDefaultOnOptionsSelectedListener(choosePreferredLanguage,
                                selectedPreferredLanguage, preferredLanguageOptional),
                        Label.getLabel("demographics_preferred_language")));
        String preferredLanguage = demographicPayload.getPersonalDetails().getPreferredLanguage();
        initSelectableInput(choosePreferredLanguage, selectedPreferredLanguage, preferredLanguage,
                personalInfoSection.getProperties().getPreferredLanguage().isRequired()
                        ? null : preferredLanguageOptional);


        TextInputLayout driverLicenseLayout = (TextInputLayout) view
                .findViewById(R.id.driverLicenseInputLayout);
        EditText driverLicense = (EditText) view.findViewById(R.id.driverLicense);
        driverLicense.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(driverLicenseLayout, null));
        setVisibility(driverLicenseLayout, personalInfoSection.getProperties()
                .getDriversLicenseNumber().isDisplayed());
        driverLicense.setText(demographicPayload.getPersonalDetails().getDriversLicenseNumber());
        driverLicense.getOnFocusChangeListener().onFocusChange(driverLicense,
                !StringUtil.isNullOrEmpty(driverLicense.getText().toString().trim()));
        if (personalInfoSection.getProperties().getDriversLicenseNumber().isRequired()) {
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
                        getDefaultOnOptionsSelectedListener(choosedriverLicenseState,
                                selectedDriverLicenseState, driverLicenseStateOptional),
                        Label.getLabel("demographics_driver_license_state")));
        String driverLicenseState = demographicPayload.getPersonalDetails().getDriversLicenseState();
        initSelectableInput(choosedriverLicenseState, selectedDriverLicenseState, driverLicenseState,
                personalInfoSection.getProperties().getDriversLicenseState().isRequired()
                        ? null : driverLicenseStateOptional);


        TextInputLayout secondaryPhoneLayout = (TextInputLayout) view.findViewById(R.id.secondaryPhoneInputLayout);
        EditText secondaryPhone = (EditText) view.findViewById(R.id.secondaryPhone);
        secondaryPhone.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(secondaryPhoneLayout, null));
        setVisibility(secondaryPhoneLayout, personalInfoSection.getProperties().getSecondaryPhoneNumber().isDisplayed());
        secondaryPhone.addTextChangedListener(phoneInputFormatter);

        String secondaryPhoneNumberString = demographicPayload.getPersonalDetails().getSecondaryPhoneNumber();
        secondaryPhone.setText(StringUtil.formatPhoneNumber(secondaryPhoneNumberString));
        secondaryPhone.getOnFocusChangeListener().onFocusChange(secondaryPhone,
                !StringUtil.isNullOrEmpty(secondaryPhone.getText().toString().trim()));
        if (personalInfoSection.getProperties().getSecondaryPhoneNumber().isRequired()) {
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
        setVisibility(secondaryPhoneTypeLayout, personalInfoSection.getProperties()
                .getSecondaryPhoneNumberType().isDisplayed());
        chooseSecondaryPhoneType.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getSecondaryPhoneNumberType().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseSecondaryPhoneType,
                                selectedSecondaryPhoneType, secondaryPhoneTypeOptional),
                        Label.getLabel("demographics_secondary_phone_type")));
        String secondaryPhoneType = demographicPayload.getPersonalDetails().getSecondaryPhoneNumberType();
        initSelectableInput(chooseSecondaryPhoneType, selectedSecondaryPhoneType, secondaryPhoneType,
                personalInfoSection.getProperties().getSecondaryPhoneNumberType().isRequired()
                        ? null : secondaryPhoneTypeOptional);


        View preferredContactMethodLayout = view.findViewById(R.id.preferredContactMethodDemographicsLayout);
        TextView choosePreferredContactMethod = (TextView) view.findViewById(R.id.choosePreferredContactMethod);
        View contactMethodOptional = view.findViewById(R.id.contactMethodOptional);
        setVisibility(preferredContactMethodLayout, personalInfoSection.getProperties()
                .getPreferredContact().isDisplayed());
        choosePreferredContactMethod.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getPreferredContact().getOptions(),
                        getDefaultOnOptionsSelectedListener(choosePreferredContactMethod,
                                selectedContactMethod, contactMethodOptional),
                        Label.getLabel("demographics_preferred_contact_method")));
        String preferredContactMethod = demographicPayload.getPersonalDetails().getPreferredContact();
        initSelectableInput(choosePreferredContactMethod, selectedContactMethod, preferredContactMethod,
                personalInfoSection.getProperties().getPreferredContact().isRequired()
                        ? null : contactMethodOptional);


        View maritalStatusLayout = view.findViewById(R.id.maritalStatusDemographicsLayout);
        TextView chooseMaritalStatus = (TextView) view.findViewById(R.id.chooseMaritalStatus);
        View maritalStatusOptional = view.findViewById(R.id.maritalStatusOptional);
        setVisibility(maritalStatusLayout, personalInfoSection.getProperties().getMaritalStatus().isDisplayed());
        chooseMaritalStatus.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getMaritalStatus().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseMaritalStatus,
                                selectedMaritalStatus, maritalStatusOptional),
                        Label.getLabel("demographics_marital_status")));
        String maritalStatus = demographicPayload.getPersonalDetails().getMaritalStatus();
        initSelectableInput(chooseMaritalStatus, selectedMaritalStatus, maritalStatus,
                personalInfoSection.getProperties().getMaritalStatus().isRequired()
                        ? null : maritalStatusOptional);


        View referralSourceLayout = view.findViewById(R.id.referralSourceDemographicsLayout);
        TextView chooseReferralSource = (TextView) view.findViewById(R.id.chooseReferralSource);
        View referralSourceOptional = view.findViewById(R.id.referralSourceOptional);
        setVisibility(referralSourceLayout, personalInfoSection.getProperties().getReferralSource().isDisplayed());
        chooseReferralSource.setOnClickListener(
                getSelectOptionsListener(personalInfoSection.getProperties().getReferralSource().getOptions(),
                        getDefaultOnOptionsSelectedListener(chooseReferralSource,
                                selectedReferralSource, referralSourceOptional),
                        Label.getLabel("demographics_referral_source")));
        String referralSource = demographicPayload.getPersonalDetails().getReferralSource();
        initSelectableInput(chooseReferralSource, selectedReferralSource, referralSource,
                personalInfoSection.getProperties().getReferralSource().isRequired()
                        ? null : referralSourceOptional);

        setUpEmergencyContact(view, demographicPayload.getEmergencyContact());
        setUpEmployer(view, demographicPayload, dataModel.getDemographic().getEmploymentInfo());
    }

    private void setUpEmergencyContact(View view, PatientModel emergencyContact) {
/*
        DemographicEmergencyContactSection emergencyContactSection = dataModel.getDemographic().getEmergencyContact();
        TextInputLayout emergencyContactInputLayout = (TextInputLayout) view.findViewById(R.id.emergencyContactInputLayout);
        emergencyContactInputLayout.setVisibility(emergencyContactSection.isDisplay() ? View.VISIBLE : View.GONE);
        EditText emergencyContactEditText = (EditText) view.findViewById(R.id.emergencyContactEditText);
        emergencyContactEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(emergencyContactInputLayout, null));
        if (emergencyContact != null) {
            emergencyContactEditText.setText(emergencyContact.getFullName());
        }
        emergencyContactEditText.getOnFocusChangeListener().onFocusChange(emergencyContactEditText,
                !StringUtil.isNullOrEmpty(emergencyContactEditText.getText().toString().trim()));
        view.findViewById(R.id.emergencyContactOptionalLabel)
                .setVisibility(emergencyContactSection.isRequired() ? View.GONE : View.VISIBLE);
        emergencyContactEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showAddEditEmergencyContactDialog();
            }
        });
*/

        DemographicEmergencyContactSection emergencyContactSection = dataModel.getDemographic().getEmergencyContact();
        View emergencyContactLayout = view.findViewById(R.id.emergencyContactDemographicsLayout);
        TextView chooseEmergencyContact = (TextView) view.findViewById(R.id.emergencyContactEditText);
        View EmergencyContactOptional = view.findViewById(R.id.emergencyContactOptionalLabel);
        setVisibility(emergencyContactLayout, emergencyContactSection.isDisplay());
        chooseEmergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showAddEditEmergencyContactDialog();
            }
        });
        String emergencyContactName = emergencyContact!=null?emergencyContact.getFullName():null;
        initSelectableInput(chooseEmergencyContact, new DemographicsOption(), emergencyContactName, emergencyContactSection.isRequired() ? null : EmergencyContactOptional);


    }

    private void setUpEmployer(final View view, DemographicPayloadDTO demographicPayload,
                               DemographicEmploymentInfoSection employmentInfoSection) {

        final TextView chooseEmploymentStatus = (TextView) view.findViewById(R.id.chooseEmploymentStatus);
        final View employmentStatusOptional = view.findViewById(R.id.employmentStatusOptional);

        chooseEmploymentStatus.setOnClickListener(
                getSelectOptionsListener(employmentInfoSection.getProperties().getEmploymentStatus().getOptions(),
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
                                showEmployerFields = option.getLabel().toLowerCase().equals("employed")
                                        || option.getLabel().toLowerCase().equals("part time");
                                manageEmployerFieldsVisibility(showEmployerFields);
                                checkIfEnableButton();
                            }
                        }, Label.getLabel("demographics_employment_status")));

        String employmentStatus = demographicPayload.getEmploymentInfoModel().getEmploymentStatus();
        initSelectableInput(chooseEmploymentStatus, selectedEmploymentStatus, employmentStatus,
                employmentInfoSection.getProperties().getEmploymentStatus().isRequired()
                        ? null : employmentStatusOptional);
        if (employmentStatus != null) {
            showEmployerFields = employmentStatus.toLowerCase().equals("employed")
                    || employmentStatus.toLowerCase().equals("part time");
        }
        if (!employmentInfoSection.isRequired()) {
            view.findViewById(R.id.employmentInfoOptionalTextView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.employmentInfoContainer).setVisibility(View.GONE);
        }

        setEmployerInfoFields(view, demographicPayload);

    }

    private void setEmployerInfoFields(View view, DemographicPayloadDTO demographicPayload) {
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
                checkIfEnableButton();
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

        manageEmployerFieldsVisibility(showEmployerFields);
    }

    private void manageEmployerFieldsVisibility(boolean visible) {
        employerDependentFieldsLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
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
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getReferralSource().isRequired()
                && StringUtil.isNullOrEmpty(selectedReferralSource.getName())) {
            return false;
        }

        if (dataModel.getDemographic().getEmploymentInfo().isRequired()
                && StringUtil.isNullOrEmpty(selectedEmploymentStatus.getName())) {
            return false;
        }
        if (dataModel.getDemographic().getEmploymentInfo().isRequired()
                && StringUtil.isNullOrEmpty(selectedEmployer.getName())
                && showEmployerFields) {
            return false;
        }
//        if (dataModel.getDemographic().getPersonalDetails().getProperties()
//                .getEmergencyContactRelationship().isRequired()
//                && StringUtil.isNullOrEmpty(selectedEmergencyContactRelationship.getName())) {
//            return false;
//        }

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


    private DemographicDTO getUpdateModel() {
        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        updatableDemographicDTO.setPayload(new DemographicPayloadResponseDTO());
        updatableDemographicDTO.getPayload().setDemographics(new DemographicPayloadInfoDTO());
        updatableDemographicDTO.getPayload().getDemographics().setPayload(new DemographicPayloadDTO());

        //add all demographic info
        PatientModel patientModel = demographicsSettingsDTO.getPayload().getDemographics()
                .getPayload().getPersonalDetails();

        String preferredName = ((TextView) findViewById(R.id.preferredName)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(preferredName)) {
            patientModel.setPreferredName(preferredName);
        }

        String socialSecurity = ((TextView) findViewById(R.id.socialSecurityNumber)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(socialSecurity)) {
            patientModel.setSocialSecurityNumber(socialSecurity);
        }

        String emailAddress = ((TextView) findViewById(R.id.email)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(emailAddress)) {
            patientModel.setEmailAddress(emailAddress);
        }

        String preferredLanguage = selectedPreferredLanguage.getName();
        if (!StringUtil.isNullOrEmpty(preferredLanguage)) {
            patientModel.setPreferredLanguage(preferredLanguage);
        }

        String driverLicense = ((TextView) findViewById(R.id.driverLicense)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(driverLicense)) {
            patientModel.setDriversLicenseNumber(driverLicense);
        }

        String driverLicenseState = selectedDriverLicenseState.getName();
        if (!StringUtil.isNullOrEmpty(driverLicenseState)) {
            patientModel.setDriversLicenseState(driverLicenseState);
        }

        String secondaryPhone = ((TextView) findViewById(R.id.secondaryPhone)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(secondaryPhone)) {
            patientModel.setSecondaryPhoneNumber(StringUtil.revertToRawPhoneFormat(secondaryPhone));
        }

        String secondaryPhoneType = selectedSecondaryPhoneType.getName();
        if (!StringUtil.isNullOrEmpty(secondaryPhoneType)) {
            patientModel.setSecondaryPhoneNumberType(secondaryPhoneType);
        }

        String contactMethod = selectedContactMethod.getName();
        if (!StringUtil.isNullOrEmpty(contactMethod)) {
            patientModel.setPreferredContact(contactMethod);
        }

        String maritalStatus = selectedMaritalStatus.getName();
        if (!StringUtil.isNullOrEmpty(maritalStatus)) {
            patientModel.setMaritalStatus(maritalStatus);
        }


        EmploymentInfoModel employmentInfoModel = demographicsSettingsDTO.getPayload().getDemographics()
                .getPayload().getEmploymentInfoModel();

        String employmentStatus = selectedEmploymentStatus.getName();
        if (!StringUtil.isNullOrEmpty(employmentStatus)) {
            employmentInfoModel.setEmploymentStatus(employmentStatus);
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

            employmentInfoModel.setEmployerDto(selectedEmployer);
        } else {
            employmentInfoModel.setEmployerDto(null);
        }

        String emergencyContactRelationship = selectedEmergencyContactRelationship.getName();
        if (!StringUtil.isNullOrEmpty(emergencyContactRelationship)) {
            patientModel.setEmergencyContactRelationship(emergencyContactRelationship);
        }

        String referralSource = selectedReferralSource.getName();
        if (!StringUtil.isNullOrEmpty(referralSource)) {
            patientModel.setReferralSource(referralSource);
        }

        updatableDemographicDTO.getPayload().getDemographics().getPayload().setPersonalDetails(patientModel);
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setEmploymentInfoModel(employmentInfoModel);

        return updatableDemographicDTO;
    }


    private void updateDemographics() {
        if (passConstraints()) {
            Map<String, String> header = new HashMap<>();
//            header.put("transition", "true");

            DemographicDTO updateModel = getUpdateModel();
            Gson gson = new Gson();
            String jsonPayload = gson.toJson(updateModel.getPayload().getDemographics().getPayload());

            TransitionDTO updateDemographics = demographicsSettingsDTO.getMetadata()
                    .getTransitions().getUpdateDemographics();
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

            DemographicDTO updatedModel = DtoHelper
                    .getConvertedDTO(DemographicDTO.class, workflowDTO);
            demographicsSettingsDTO.getPayload().getDemographics().getPayload()
                    .setPersonalDetails(updatedModel.getPayload().getDemographics()
                            .getPayload().getPersonalDetails());

            getActivity().onBackPressed();
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

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

    @Override
    public void updateEmergencyContact(PatientModel emergencyContact) {
        setUpEmergencyContact(getView(), emergencyContact);
    }
}
