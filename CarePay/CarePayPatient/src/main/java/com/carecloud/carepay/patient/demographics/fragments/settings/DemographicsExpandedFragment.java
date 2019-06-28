package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicEmergencyContactSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicEmploymentInfoSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicPhysicianSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsPersonalSection;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerDto;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmploymentInfoModel;
import com.carecloud.carepaylibray.demographics.dtos.payload.PhysicianDto;
import com.carecloud.carepaylibray.demographics.fragments.SearchPhysicianFragment;
import com.carecloud.carepaylibray.demographics.interfaces.DemographicExtendedInterface;
import com.carecloud.carepaylibray.demographics.interfaces.EmergencyContactFragmentInterface;
import com.carecloud.carepaylibray.demographics.interfaces.PhysicianFragmentInterface;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.smartystreets.api.us_zipcode.City;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsExpandedFragment extends DemographicsBaseSettingsFragment
        implements EmergencyContactFragmentInterface, PhysicianFragmentInterface {

    private DemographicDTO demographicsSettingsDTO;
    private DemographicDataModel dataModel;

    private View nextButton;
    private DemographicExtendedInterface callback;

    private DemographicsOption selectedPreferredLanguage = new DemographicsOption();
    private DemographicsOption selectedDriverLicenseState = new DemographicsOption();
    private DemographicsOption selectedSecondaryPhoneType = new DemographicsOption();
    private DemographicsOption selectedContactMethod = new DemographicsOption();
    private DemographicsOption selectedMaritalStatus = new DemographicsOption();
    private DemographicsOption selectedEmploymentStatus = new DemographicsOption();
    private DemographicsOption selectedReferralSource = new DemographicsOption();
    private EmployerDto selectedEmployer = new EmployerDto();
    private PatientModel selectedEmergencyContact = new PatientModel();
    private PhysicianDto primaryPhysician = new PhysicianDto();
    private PhysicianDto referringPhysician = new PhysicianDto();
    private boolean showEmployerFields;
    private View employerDependentFieldsLayout;
    private EditText employerAddressEditText;
    private EditText employerAddressEditText2;
    private EditText zipCodeEditText;
    private EditText cityEditText;
    private EditText stateEditText;
    private TextInputLayout zipCodeTextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout address1TextInputLayout;
    private TextInputLayout employerNameTextInputLayout;


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
            callback = (DemographicExtendedInterface) context;
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
        Toolbar toolbar = view.findViewById(R.id.settings_toolbar);
        TextView title = toolbar.findViewById(R.id.settings_toolbar_title);
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

        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View buttonView, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !buttonView.isSelected()) {
                    checkIfEnableButton(true);
                    return true;
                }
                return false;
            }
        });

        initViews(view);
        checkIfEnableButton(false);
    }


    private void initViews(View view) {
        DemographicPayloadDTO demographicPayload = demographicsSettingsDTO.getPayload().getDemographics().getPayload();

        setUpExtendedDemographicFields(view, demographicPayload, dataModel.getDemographic());

        setUpEmergencyContact(view, demographicPayload.getEmergencyContact());
        setUpEmployer(view, demographicPayload, dataModel.getDemographic().getEmploymentInfo());

    }

    private void setUpExtendedDemographicFields(View view, DemographicPayloadDTO demographicPayload,
                                                DemographicDataModel.Demographic demographic) {
        DemographicsPersonalSection personalInfoSection = demographic.getPersonalDetails();

        setUpDemographicField(view, StringUtil.captialize(demographicPayload.getPersonalDetails().getPreferredName()).trim(),
                personalInfoSection.getProperties().getPreferredName(), R.id.preferredNameContainer,
                R.id.preferredNameInputLayout, R.id.preferredName, R.id.preferredNameRequired, null, null);

        setUpDemographicField(view, StringUtil.formatSocialSecurityNumber(demographicPayload
                        .getPersonalDetails().getSocialSecurityNumber()),
                personalInfoSection.getProperties().getSocialSecurityNumber(),
                R.id.socialSecurityContainer, R.id.socialSecurityInputLayout,
                R.id.socialSecurityNumber, R.id.socialSecurityRequired, null, null);
        EditText socialSecurityNumber = view.findViewById(R.id.socialSecurityNumber);
        socialSecurityNumber.addTextChangedListener(ssnInputFormatter);
        if (!dataModel.getDemographic().getPersonalDetails().getProperties().getSocialSecurityNumber().isRequired()) {
            socialSecurityNumber.addTextChangedListener(
                    clearValidationErrorsOnTextChange(view.findViewById(R.id.socialSecurityInputLayout)));
        }

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getEmailAddress(),
                personalInfoSection.getProperties().getEmailAddress(), R.id.emailContainer, R.id.emailInputLayout,
                R.id.email, R.id.emailRequired, null, null);
        view.findViewById(R.id.email).setVisibility(View.GONE);

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getPreferredLanguage(),
                personalInfoSection.getProperties().getPreferredLanguage(),
                R.id.preferredLanguageDemographicsLayout, R.id.preferredLanguageInputLayout,
                R.id.preferredLanguageEditText, R.id.preferredLanguageRequired, selectedPreferredLanguage,
                Label.getLabel("demographics_preferred_language"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getDriversLicenseNumber(),
                personalInfoSection.getProperties().getDriversLicenseNumber(),
                R.id.driverLicenseContainer, R.id.driverLicenseInputLayout,
                R.id.driverLicense, R.id.driverLicenseRequired, null, null);

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getDriversLicenseState(),
                personalInfoSection.getProperties().getDriversLicenseState(),
                R.id.driverLicenseStateDemographicsLayout, R.id.driverLicenseStateInputLayout,
                R.id.driverLicenseStateEditText, R.id.driverLicenseStateRequired, selectedDriverLicenseState,
                Label.getLabel("demographics_driver_license_state"));

        setUpDemographicField(view, StringUtil
                        .formatPhoneNumber(demographicPayload.getPersonalDetails().getSecondaryPhoneNumber()),
                personalInfoSection.getProperties().getSecondaryPhoneNumber(),
                R.id.secondaryPhoneDemographicsLayout, R.id.secondaryPhoneInputLayout,
                R.id.secondaryPhone, R.id.secondaryPhoneRequired, null, null);
        EditText secondaryPhoneEditText = view.findViewById(R.id.secondaryPhone);
        secondaryPhoneEditText.addTextChangedListener(phoneInputFormatter);
        if (!dataModel.getDemographic().getPersonalDetails().getProperties().getSecondaryPhoneNumber().isRequired()) {
            secondaryPhoneEditText.addTextChangedListener(
                    clearValidationErrorsOnTextChange(view.findViewById(R.id.secondaryPhoneInputLayout)));
        }

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getSecondaryPhoneNumberType(),
                personalInfoSection.getProperties().getSecondaryPhoneNumberType(),
                R.id.secondaryPhoneTypeDemographicsLayout, R.id.secondaryPhoneTypeInputLayout,
                R.id.secondaryPhoneTypeEditText, R.id.secondaryPhoneTypeRequired, selectedSecondaryPhoneType,
                Label.getLabel("demographics_secondary_phone_type"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getPreferredContact(),
                personalInfoSection.getProperties().getPreferredContact(),
                R.id.preferredContactMethodDemographicsLayout, R.id.preferredContactMethodInputLayout,
                R.id.preferredContactMethodEditText, R.id.contactMethodRequired, selectedContactMethod,
                Label.getLabel("demographics_preferred_contact_method"));

        setUpDemographicField(view, demographicPayload.getPersonalDetails().getMaritalStatus(),
                personalInfoSection.getProperties().getMaritalStatus(),
                R.id.maritalStatusDemographicsLayout, R.id.maritalStatusInputLayout,
                R.id.maritalStatusEditText, R.id.maritalStatusRequired, selectedMaritalStatus,
                Label.getLabel("demographics_marital_status"));

        //hide referral source
        view.findViewById(R.id.referralSourceDemographicsLayout).setVisibility(View.GONE);

        setUpPrimaryCarePhysician(view, demographicPayload.getPrimaryPhysician(), demographic.getPrimaryPhysician());
        setUpReferringPhysician(view, demographicPayload.getReferringPhysician(), demographic.getReferringPhysician());
    }

    private void setUpPrimaryCarePhysician(View view, PhysicianDto primaryPhysician,
                                           DemographicPhysicianSection physicianMetadata) {
        this.primaryPhysician = primaryPhysician;
        setUpPhysicianField(view, primaryPhysician, physicianMetadata,
                R.id.primaryPhysicianDemographicsLayout, R.id.primaryPhysicianInputLayout,
                R.id.primaryPhysicianEditText, R.id.primaryPhysicianRequired, SearchPhysicianFragment.PRIMARY_PHYSICIAN);
    }

    private void setUpReferringPhysician(View view, PhysicianDto referringPhysician,
                                         DemographicPhysicianSection physicianMetadata) {
        this.referringPhysician = referringPhysician;
        setUpPhysicianField(view, referringPhysician, physicianMetadata,
                R.id.referringPhysicianDemographicsLayout, R.id.referringPhysicianInputLayout,
                R.id.referringPhysicianEditText, R.id.referringPhysicianRequired, SearchPhysicianFragment.REFERRING_PHYSICIAN);
    }

    protected void setUpPhysicianField(View view, final PhysicianDto physician,
                                       DemographicPhysicianSection demographicsField,
                                       int containerLayout, int inputLayoutId, int editTextId,
                                       int optionalViewId, final int physicianType) {
        view.findViewById(containerLayout).setVisibility(demographicsField.isDisplay() ? View.VISIBLE : View.GONE);
        final TextInputLayout inputLayout = view.findViewById(inputLayoutId);
        final EditText editText = view.findViewById(editTextId);
        editText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(inputLayout, null));
        if (physician != null) {
            editText.setText(physician.getFormattedName());
        }
        editText.getOnFocusChangeListener().onFocusChange(editText,
                !StringUtil.isNullOrEmpty(editText.getText().toString().trim()));
        final View optionalView = view.findViewById(optionalViewId);
        optionalView.setVisibility(!demographicsField.isRequired() && physician == null ? View.VISIBLE : View.GONE);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showSearchPhysicianFragmentDialog(physician, physicianType);
            }
        });
    }

    private void setUpEmergencyContact(View view, PatientModel emergencyContact) {
        selectedEmergencyContact = emergencyContact;

        DemographicEmergencyContactSection emergencyContactSection = dataModel.getDemographic().getEmergencyContact();
        TextInputLayout emergencyContactInputLayout = view.findViewById(R.id.emergencyContactInputLayout);
        emergencyContactInputLayout.setVisibility(emergencyContactSection.isDisplay() ? View.VISIBLE : View.GONE);
        EditText emergencyContactEditText = view.findViewById(R.id.emergencyContactEditText);
        emergencyContactEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(emergencyContactInputLayout, null));
        if (emergencyContact != null) {
            emergencyContactEditText.setText(StringUtil.captialize(emergencyContact.getFullName()).trim());
        }
        emergencyContactEditText.getOnFocusChangeListener().onFocusChange(emergencyContactEditText,
                !StringUtil.isNullOrEmpty(emergencyContactEditText.getText().toString().trim()));
        view.findViewById(R.id.emergencyContactRequiredLabel)
                .setVisibility(emergencyContactSection.isRequired()
                        && StringUtil.isNullOrEmpty(emergencyContactEditText.getText().toString())
                        ? View.VISIBLE : View.GONE);
        emergencyContactEditText.setOnClickListener(view1 -> callback.showAddEditEmergencyContactDialog());
    }

    private void setUpEmployer(final View view, DemographicPayloadDTO demographicPayload,
                               DemographicEmploymentInfoSection employmentInfoSection) {

        final EditText employmentStatusEditText = view.findViewById(R.id.employmentStatusEditText);
        final View employmentStatusRequired = view.findViewById(R.id.employmentStatusRequired);

        employmentStatusEditText.setOnClickListener(
                getSelectOptionsListener(employmentInfoSection.getProperties().getEmploymentStatus().getOptions(),
                        new OnOptionSelectedListener() {
                            @Override
                            public void onOptionSelected(DemographicsOption option) {
                                employmentStatusEditText.setText(option.getLabel());
                                employmentStatusRequired.setVisibility(View.GONE);

                                selectedEmploymentStatus.setLabel(option.getLabel());
                                selectedEmploymentStatus.setName(option.getName());
                                showEmployerFields = option.getLabel().toLowerCase().equals("employed")
                                        || option.getLabel().toLowerCase().equals("part time");
                                manageEmployerFieldsVisibility(showEmployerFields);
                                checkIfEnableButton(false);
                            }
                        }, Label.getLabel("demographics_employment_status")));

        String employmentStatus = demographicPayload.getEmploymentInfoModel().getEmploymentStatus();
        TextInputLayout employmentStatusInputLayout = view.findViewById(R.id.employmentStatusInputLayout);
        employmentStatusEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(employmentStatusInputLayout, null));
        employmentStatusEditText.setText(employmentStatus);
        employmentStatusEditText.getOnFocusChangeListener()
                .onFocusChange(employmentStatusEditText, !StringUtil.isNullOrEmpty(employmentStatusEditText.getText().toString()));
        selectedEmploymentStatus.setName(employmentStatus);
        selectedEmploymentStatus.setLabel(employmentStatus);
        if (employmentStatus != null) {
            showEmployerFields = employmentStatus.toLowerCase().equals("employed")
                    || employmentStatus.toLowerCase().equals("part time");
        }

        if (!employmentInfoSection.getProperties().getEmploymentStatus().isRequired()
                && StringUtil.isNullOrEmpty(employmentStatus)) {
            employmentStatusRequired.setVisibility(View.VISIBLE);
        }
        view.findViewById(R.id.employmentInfoRequiredTextView).setVisibility(
                employmentInfoSection.isRequired() ? View.VISIBLE : View.GONE);

        setEmployerInfoFields(view, demographicPayload, employmentInfoSection);
    }

    private void setEmployerInfoFields(View view, DemographicPayloadDTO demographicPayload,
                                       DemographicEmploymentInfoSection employmentInfoSection) {
        employerDependentFieldsLayout = view.findViewById(R.id.employerDependentLayout);

        selectedEmployer = demographicPayload.getEmploymentInfoModel().getEmployerDto();
        if (selectedEmployer == null) {
            selectedEmployer = new EmployerDto();
        }

        employerNameTextInputLayout = view.findViewById(R.id.employerNameTextInputLayout);
        EditText employerNameEditText = view.findViewById(R.id.employerNameEditText);
        employerNameEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(employerNameTextInputLayout, null));
        employerNameEditText.setText(StringUtil.captialize(selectedEmployer.getName()).trim());
        employerNameEditText.getOnFocusChangeListener().onFocusChange(employerNameEditText,
                !StringUtil.isNullOrEmpty(employerNameEditText.getText().toString().trim()));
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
            }
        });
        if (employmentInfoSection.isRequired()) {
            employerNameEditText.addTextChangedListener(getValidateEmptyTextWatcher(employerNameTextInputLayout));
        } else {
            employerNameEditText.addTextChangedListener(clearValidationErrorsOnTextChange(employerNameTextInputLayout));
        }

        TextInputLayout address2TextInputLayout = view.findViewById(R.id.address2TextInputLayout);
        employerAddressEditText2 = view.findViewById(R.id.addressEditText2);
        employerAddressEditText2.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(address2TextInputLayout, null));
        employerAddressEditText2.setText(StringUtil.captialize(selectedEmployer.getAddress().getAddress2()).trim());
        employerAddressEditText2.getOnFocusChangeListener()
                .onFocusChange(employerAddressEditText2, !StringUtil.isNullOrEmpty(employerAddressEditText2.getText().toString()));

        zipCodeTextInputLayout = view.findViewById(R.id.zipCodeTextInputLayout);
        zipCodeEditText = view.findViewById(R.id.zipCodeTextView);
        zipCodeEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(zipCodeTextInputLayout,
                getZipCodeFocusListener(zipCodeEditText)));
        zipCodeEditText.setText(StringUtil.formatZipCode(selectedEmployer.getAddress().getZipcode()));
        zipCodeEditText.addTextChangedListener(zipInputFormatter);
        zipCodeEditText.addTextChangedListener(clearValidationErrorsOnTextChange(zipCodeTextInputLayout));
        zipCodeEditText.setOnClickListener(selectEndOnClick);
        zipCodeEditText.getOnFocusChangeListener()
                .onFocusChange(zipCodeEditText, !StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString()));

        cityTextInputLayout = view.findViewById(R.id.cityTextInputLayout);
        cityEditText = view.findViewById(R.id.cityTextView);
        cityEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(cityTextInputLayout, null));
        cityEditText.setText(StringUtil.captialize(selectedEmployer.getAddress().getCity()).trim());
        cityEditText.getOnFocusChangeListener()
                .onFocusChange(cityEditText, !StringUtil.isNullOrEmpty(cityEditText.getText().toString()));
        cityEditText.addTextChangedListener(clearValidationErrorsOnTextChange(cityTextInputLayout));

        stateTextInputLayout = view.findViewById(R.id.stateTextInputLayout);
        stateEditText = view.findViewById(R.id.stateTextView);
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
        stateEditText.getOnFocusChangeListener()
                .onFocusChange(stateEditText, !StringUtil.isNullOrEmpty(stateEditText.getText().toString()));
        stateEditText.addTextChangedListener(clearValidationErrorsOnTextChange(stateTextInputLayout));

        address1TextInputLayout = view.findViewById(R.id.address1TextInputLayout);
        employerAddressEditText = view.findViewById(R.id.addressEditText);
        employerAddressEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(address1TextInputLayout, null));
        employerAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    employerAddressEditText2.setEnabled(false);
                    employerAddressEditText2.setText("");
                    employerAddressEditText2.getOnFocusChangeListener()
                            .onFocusChange(employerAddressEditText2,
                                    !StringUtil.isNullOrEmpty(employerAddressEditText2.getText().toString()));
                    unsetFieldError(zipCodeTextInputLayout);
                    unsetFieldError(cityTextInputLayout);
                    unsetFieldError(stateTextInputLayout);
                } else {
                    employerAddressEditText2.setEnabled(true);

                }
                checkIfEnableButton(false);
            }
        });
        employerAddressEditText.setText(StringUtil.captialize(selectedEmployer.getAddress().getAddress1()).trim());
        employerAddressEditText.getOnFocusChangeListener()
                .onFocusChange(employerAddressEditText, !StringUtil.isNullOrEmpty(employerAddressEditText
                        .getText().toString()));

        TextInputLayout phoneTextInputLayout = view.findViewById(R.id.phoneTextInputLayout);
        EditText phoneEditText = view.findViewById(R.id.phoneTextView);
        phoneEditText.addTextChangedListener(phoneInputFormatter);
        phoneEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(phoneTextInputLayout, null));
        if (selectedEmployer.getAddress() != null) {
            phoneEditText.setText(StringUtil.formatPhoneNumber(selectedEmployer.getAddress().getPhoneNumber()));
        }
        phoneEditText.getOnFocusChangeListener()
                .onFocusChange(phoneEditText, !StringUtil.isNullOrEmpty(phoneEditText.getText().toString()));
        phoneEditText.setOnClickListener(selectEndOnClick);

        manageEmployerFieldsVisibility(showEmployerFields);
    }

    private void manageEmployerFieldsVisibility(boolean visible) {
        employerDependentFieldsLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void checkIfEnableButton(boolean userInteraction) {
        boolean isEnabled = passConstraints(userInteraction);
        if (nextButton != null) {
            nextButton.setSelected(isEnabled);
            nextButton.setClickable(isEnabled);
        }
    }

    @Override
    protected boolean passConstraints(boolean isUserInteraction) {
        View view = getView();
        if (view == null) {
            return false;
        }

        if (dataModel.getDemographic().getPersonalDetails().getProperties().getSocialSecurityNumber().isRequired()
                && checkTextEmptyValue(R.id.socialSecurityNumber, view)) {
            return false;
        }
        TextInputLayout socialSecurityInputLayout = view.findViewById(R.id.socialSecurityInputLayout);
        EditText socialSecurityNumber = view.findViewById(R.id.socialSecurityNumber);
        if (socialSecurityInputLayout.getVisibility() == View.VISIBLE &&
                !StringUtil.isNullOrEmpty(socialSecurityNumber.getText().toString().trim()) &&
                !ValidationHelper.isValidString(socialSecurityNumber.getText().toString().trim(),
                        ValidationHelper.SOCIAL_SECURITY_NUMBER_PATTERN)) {
            setFieldError(socialSecurityInputLayout,
                    Label.getLabel("demographics_social_security_number_validation_msg"), isUserInteraction);
            return false;
        } else {
            unsetFieldError(socialSecurityInputLayout);
        }

        if (dataModel.getDemographic().getPersonalDetails().getProperties().getPreferredName().isRequired()
                && checkTextEmptyValue(R.id.preferredName, view)) {
            return false;
        }

        if (dataModel.getDemographic().getPersonalDetails().getProperties().getEmailAddress().isRequired()
                && checkTextEmptyValue(R.id.email, view)) {
            return false;
        }
        TextInputLayout emailLayout = view.findViewById(R.id.emailInputLayout);
        EditText emailAddress = view.findViewById(R.id.email);
        if (emailLayout.getVisibility() == View.VISIBLE &&
                !StringUtil.isNullOrEmpty(emailAddress.getText().toString().trim()) &&
                !ValidationHelper.isValidEmail(emailAddress.getText().toString().trim())) {
            setFieldError(emailLayout,
                    Label.getLabel("demographics_email_validation_msg"), isUserInteraction);
            return false;
        } else {
            unsetFieldError(emailLayout);
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
        TextInputLayout phoneLayout = view.findViewById(R.id.secondaryPhoneInputLayout);
        EditText secondaryPhoneNumber = view.findViewById(R.id.secondaryPhone);
        if (phoneLayout.getVisibility() == View.VISIBLE &&
                !StringUtil.isNullOrEmpty(secondaryPhoneNumber.getText().toString().trim()) &&
                !ValidationHelper.isValidString(secondaryPhoneNumber.getText().toString().trim(),
                        ValidationHelper.PHONE_NUMBER_PATTERN)) {
            setFieldError(phoneLayout,
                    Label.getLabel("demographics_phone_number_validation_msg"), isUserInteraction);
            return false;
        } else {
            unsetFieldError(phoneLayout);
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
        if (dataModel.getDemographic().getEmergencyContact().isRequired()
                && StringUtil.isNullOrEmpty(selectedEmergencyContact.getFirstName())) {
            return false;
        }
        if (dataModel.getDemographic().getPrimaryPhysician().isRequired()
                && primaryPhysician == null) {
            return false;
        }
        if (dataModel.getDemographic().getReferringPhysician().isRequired()
                && referringPhysician == null) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getReferralSource().isRequired()
                && selectedReferralSource == null) {
            return false;
        }

        if (dataModel.getDemographic().getEmploymentInfo().isRequired()
                && StringUtil.isNullOrEmpty(selectedEmployer.getName())
                && showEmployerFields) {
            return false;
        } else {
            unsetFieldError(employerNameTextInputLayout);
        }

        if (showEmployerFields && (!StringUtil.isNullOrEmpty(employerAddressEditText.getText().toString())
                || !StringUtil.isNullOrEmpty(stateEditText.getText().toString())
                || !StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())
                || !StringUtil.isNullOrEmpty(cityEditText.getText().toString()))) {

            if (StringUtil.isNullOrEmpty(selectedEmployer.getName())) {
                if (isUserInteraction) {
                    setDefaultError(view, R.id.employerNameTextInputLayout, isUserInteraction);
                }
                return false;
            }

            if (StringUtil.isNullOrEmpty(employerAddressEditText.getText().toString())) {
                if (isUserInteraction) {
                    setFieldError(address1TextInputLayout, isUserInteraction);

                }
                return false;
            } else {
                unsetFieldError(address1TextInputLayout);
            }

            if (StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())) {
                if (isUserInteraction) {
                    setFieldError(zipCodeTextInputLayout, isUserInteraction);
                } else {
                    unsetFieldError(zipCodeTextInputLayout);
                }
                return false;
            }

            if (!StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString().trim()) &&
                    !ValidationHelper.isValidString(zipCodeEditText.getText().toString().trim(),
                            ValidationHelper.ZIP_CODE_PATTERN)) {
                setFieldError(zipCodeTextInputLayout, Label.getLabel("demographics_zip_code_validation_msg"),
                        isUserInteraction);
                return false;
            } else {
                unsetFieldError(zipCodeTextInputLayout);
            }

            if (StringUtil.isNullOrEmpty(cityEditText.getText().toString())) {
                if (isUserInteraction) {
                    setDefaultError(view, R.id.cityTextInputLayout, isUserInteraction);
                }
                return false;
            } else {
                unsetFieldError(cityTextInputLayout);
            }

            if (StringUtil.isNullOrEmpty(stateEditText.getText().toString())) {
                if (isUserInteraction) {
                    setDefaultError(view, R.id.stateTextInputLayout, isUserInteraction);
                }
                return false;
            } else {
                unsetFieldError(stateTextInputLayout);
            }


            TextInputLayout employerPhoneLayout = view.findViewById(R.id.phoneTextInputLayout);
            EditText employerPhoneNumber = view.findViewById(R.id.phoneTextView);
            if (employerPhoneLayout.getVisibility() == View.VISIBLE &&
                    !StringUtil.isNullOrEmpty(employerPhoneNumber.getText().toString().trim()) &&
                    !ValidationHelper.isValidString(employerPhoneNumber.getText().toString().trim(),
                            ValidationHelper.PHONE_NUMBER_PATTERN)) {
                setFieldError(employerPhoneLayout,
                        Label.getLabel("demographics_phone_number_validation_msg"), isUserInteraction);
                return false;
            } else {
                unsetFieldError(employerPhoneLayout);
            }
        } else {
            unsetFieldError(zipCodeTextInputLayout);
            unsetFieldError(stateTextInputLayout);
            unsetFieldError(cityTextInputLayout);
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
            patientModel.setSocialSecurityNumber(StringUtil.revertToRawFormat(socialSecurity));
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
            patientModel.setSecondaryPhoneNumber(StringUtil.revertToRawFormat(secondaryPhone));
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
            selectedEmployer.getAddress().setZipcode(StringUtil.revertZipToRawFormat(zipCode));

            String city = ((TextView) findViewById(R.id.cityTextView)).getText().toString().trim();
            selectedEmployer.getAddress().setCity(city);

            String state = ((TextView) findViewById(R.id.stateTextView)).getText().toString().trim();
            selectedEmployer.getAddress().setState(state);

            String phone = StringUtil.revertToRawFormat(((TextView) findViewById(R.id.phoneTextView))
                    .getText().toString().trim());
            selectedEmployer.getAddress().setPhoneNumber(phone);

            employmentInfoModel.setEmployerDto(selectedEmployer);
        } else {
            employmentInfoModel.setEmployerDto(null);
        }

        updatableDemographicDTO.getPayload().getDemographics().getPayload().setPrimaryPhysician(primaryPhysician);
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setReferringPhysician(referringPhysician);
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setPersonalDetails(patientModel);
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setEmploymentInfoModel(employmentInfoModel);

        return updatableDemographicDTO;
    }

    private void updateDemographics() {
        if (passConstraints(true)) {
            Map<String, String> header = new HashMap<>();
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

            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
            MixPanelUtil.logEvent(getString(R.string.event_updated_demographics), getString(R.string.param_is_checkin), false);
            getActivity().onBackPressed();
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
                    cityEditText.getOnFocusChangeListener()
                            .onFocusChange(cityEditText, !StringUtil.isNullOrEmpty(cityEditText.getText().toString()));
                    String stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    selectedEmployer.getAddress().setState(stateAbbr);
                    stateEditText.setText(stateAbbr);
                    stateEditText.getOnFocusChangeListener()
                            .onFocusChange(stateEditText, !StringUtil.isNullOrEmpty(stateEditText.getText().toString()));
                    checkIfEnableButton(false);
                }
            }


        }.execute(zipCode);
    }

    @Override
    public void updateEmergencyContact(PatientModel emergencyContact) {
        setUpEmergencyContact(getView(), emergencyContact);
    }

    @Override
    public void setPhysician(PhysicianDto physician, int physicianType) {
        if (physicianType == SearchPhysicianFragment.PRIMARY_PHYSICIAN) {
            setUpPrimaryCarePhysician(getView(), physician,
                    demographicsSettingsDTO.getMetadata().getNewDataModel().getDemographic().getPrimaryPhysician());
        } else {
            setUpReferringPhysician(getView(), physician,
                    demographicsSettingsDTO.getMetadata().getNewDataModel().getDemographic().getReferringPhysician());
        }
        checkIfEnableButton(false);
    }
}
