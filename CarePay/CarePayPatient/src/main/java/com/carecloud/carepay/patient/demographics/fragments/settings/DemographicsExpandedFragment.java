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

        setUpExtendedDemographicFields(view, demographicPayload, dataModel.getDemographic());

        setUpEmergencyContact(view, demographicPayload.getEmergencyContact());
        setUpEmployer(view, demographicPayload, dataModel.getDemographic().getEmploymentInfo());

    }

    private void setUpExtendedDemographicFields(View view, DemographicPayloadDTO demographicPayload,
                                                DemographicDataModel.Demographic demogarphic) {
        DemographicsPersonalSection personalInfoSection = demogarphic.getPersonalDetails();

        setUpDemographicField(view, StringUtil.captialize(demographicPayload.getPersonalDetails().getPreferredName()),
                personalInfoSection.getProperties().getPreferredName(), R.id.preferredNameContainer,
                R.id.preferredNameInputLayout, R.id.preferredName, R.id.preferredNameOptional, null, null);

        setUpDemographicField(view, StringUtil.formatSocialSecurityNumber(demographicPayload
                        .getPersonalDetails().getSocialSecurityNumber()),
                personalInfoSection.getProperties().getSocialSecurityNumber(),
                R.id.socialSecurityContainer, R.id.socialSecurityInputLayout,
                R.id.socialSecurityNumber, R.id.socialSecurityOptional, null, null);
        EditText socialSecurityNumber = (EditText) view.findViewById(R.id.socialSecurityNumber);
        socialSecurityNumber.addTextChangedListener(ssnInputFormatter);
        if (!dataModel.getDemographic().getPersonalDetails().getProperties().getSocialSecurityNumber().isRequired()) {
            socialSecurityNumber.addTextChangedListener(
                    clearValidationErrorsOnTextChange((TextInputLayout) view.findViewById(R.id.socialSecurityInputLayout)));
        }

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

        setUpDemographicField(view, StringUtil
                        .formatPhoneNumber(demographicPayload.getPersonalDetails().getSecondaryPhoneNumber()),
                personalInfoSection.getProperties().getSecondaryPhoneNumber(),
                R.id.secondaryPhoneDemographicsLayout, R.id.secondaryPhoneInputLayout,
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

        setUpPrimaryCarePhysician(view, demographicPayload.getPrimaryPhysician(), demogarphic.getPrimaryPhysician());
        setUpReferringPhysician(view, demographicPayload.getReferringPhysician(), demogarphic.getReferringPhysician());
    }

    private void setUpPrimaryCarePhysician(View view, PhysicianDto primaryPhysician,
                                           DemographicPhysicianSection physicianMetadata) {
        this.primaryPhysician = primaryPhysician;
        setUpPhysicianField(view, primaryPhysician, physicianMetadata,
                R.id.primaryPhysicianDemographicsLayout, R.id.primaryPhysicianInputLayout,
                R.id.primaryPhysicianEditText, R.id.primaryPhysicianOptional, SearchPhysicianFragment.PRIMARY_PHYSICIAN);
    }

    private void setUpReferringPhysician(View view, PhysicianDto referringPhysician,
                                         DemographicPhysicianSection physicianMetadata) {
        this.referringPhysician = referringPhysician;
        setUpPhysicianField(view, referringPhysician, physicianMetadata,
                R.id.referringPhysicianDemographicsLayout, R.id.referringPhysicianInputLayout,
                R.id.referringPhysicianEditText, R.id.referringPhysicianOptional, SearchPhysicianFragment.REFERRING_PHYSICIAN);
    }

    protected void setUpPhysicianField(View view, final PhysicianDto physician,
                                       DemographicPhysicianSection demographicsField,
                                       int containerLayout, int inputLayoutId, int editTextId,
                                       int optionalViewId, final int physicianType) {
        view.findViewById(containerLayout).setVisibility(demographicsField.isDisplay() ? View.VISIBLE : View.GONE);
        final TextInputLayout inputLayout = (TextInputLayout) view.findViewById(inputLayoutId);
        final EditText editText = (EditText) view.findViewById(editTextId);
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
        TextInputLayout emergencyContactInputLayout = (TextInputLayout) view.findViewById(R.id.emergencyContactInputLayout);
        emergencyContactInputLayout.setVisibility(emergencyContactSection.isDisplay() ? View.VISIBLE : View.GONE);
        EditText emergencyContactEditText = (EditText) view.findViewById(R.id.emergencyContactEditText);
        emergencyContactEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(emergencyContactInputLayout, null));
        if (emergencyContact != null) {
            emergencyContactEditText.setText(StringUtil.captialize(emergencyContact.getFullName()));
        }
        emergencyContactEditText.getOnFocusChangeListener().onFocusChange(emergencyContactEditText,
                !StringUtil.isNullOrEmpty(emergencyContactEditText.getText().toString().trim()));
        view.findViewById(R.id.emergencyContactOptionalLabel)
                .setVisibility(!emergencyContactSection.isRequired()
                        && StringUtil.isNullOrEmpty(emergencyContactEditText.getText().toString())
                        ? View.VISIBLE : View.GONE);
        emergencyContactEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showAddEditEmergencyContactDialog();
            }
        });


    }

    private void setUpEmployer(final View view, DemographicPayloadDTO demographicPayload,
                               DemographicEmploymentInfoSection employmentInfoSection) {

        final EditText employmentStatusEditText = (EditText) view.findViewById(R.id.employmentStatusEditText);
        final View employmentStatusOptional = view.findViewById(R.id.employmentStatusOptional);

        employmentStatusEditText.setOnClickListener(
                getSelectOptionsListener(employmentInfoSection.getProperties().getEmploymentStatus().getOptions(),
                        new OnOptionSelectedListener() {
                            @Override
                            public void onOptionSelected(DemographicsOption option) {
                                employmentStatusEditText.setText(option.getLabel());
                                employmentStatusOptional.setVisibility(View.GONE);

                                selectedEmploymentStatus.setLabel(option.getLabel());
                                selectedEmploymentStatus.setName(option.getName());
                                showEmployerFields = option.getLabel().toLowerCase().equals("employed")
                                        || option.getLabel().toLowerCase().equals("part time");
                                manageEmployerFieldsVisibility(showEmployerFields);
                                checkIfEnableButton();
                            }
                        }, Label.getLabel("demographics_employment_status")));

        String employmentStatus = demographicPayload.getEmploymentInfoModel().getEmploymentStatus();
        TextInputLayout employmentStatusInputLayout = (TextInputLayout) view.findViewById(R.id.employmentStatusInputLayout);
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
            employmentStatusOptional.setVisibility(View.VISIBLE);
        }
        if (!employmentInfoSection.isRequired()) {
            view.findViewById(R.id.employmentInfoOptionalTextView).setVisibility(View.VISIBLE);
//            view.findViewById(R.id.employmentInfoContainer).setVisibility(View.GONE);
        }

        setEmployerInfoFields(view, demographicPayload, employmentInfoSection);

    }

    private void setEmployerInfoFields(View view, DemographicPayloadDTO demographicPayload,
                                       DemographicEmploymentInfoSection employmentInfoSection) {
        employerDependentFieldsLayout = view.findViewById(R.id.employerDependentLayout);

        selectedEmployer = demographicPayload.getEmploymentInfoModel().getEmployerDto();
        if (selectedEmployer == null) {
            selectedEmployer = new EmployerDto();
        }

        TextInputLayout employerNameTextInputLayout = (TextInputLayout) view.findViewById(R.id.employerNameTextInputLayout);
        EditText employerNameEditText = (EditText) view.findViewById(R.id.employerNameEditText);
        employerNameEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(employerNameTextInputLayout, null));
        employerNameEditText.setText(StringUtil.captialize(selectedEmployer.getName()));
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
        }

        TextInputLayout address2TextInputLayout = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        employerAddressEditText2 = (EditText) view.findViewById(R.id.addressEditText2);
        employerAddressEditText2.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(address2TextInputLayout, null));
        employerAddressEditText2.setText(StringUtil.captialize(selectedEmployer.getAddress().getAddress2()));
        employerAddressEditText2.getOnFocusChangeListener()
                .onFocusChange(employerAddressEditText2, !StringUtil.isNullOrEmpty(employerAddressEditText2.getText().toString()));

        zipCodeTextInputLayout = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeTextView);
        zipCodeEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(zipCodeTextInputLayout,
                getZipCodeFocusListener(zipCodeEditText)));
        zipCodeEditText.setText(StringUtil.formatZipCode(selectedEmployer.getAddress().getZipcode()));
        zipCodeEditText.addTextChangedListener(zipInputFormatter);
        zipCodeEditText.getOnFocusChangeListener()
                .onFocusChange(zipCodeEditText, !StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString()));

        cityTextInputLayout = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        cityEditText = (EditText) view.findViewById(R.id.cityTextView);
        cityEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(cityTextInputLayout, null));
        cityEditText.setText(StringUtil.captialize(selectedEmployer.getAddress().getCity()));
        cityEditText.getOnFocusChangeListener()
                .onFocusChange(cityEditText, !StringUtil.isNullOrEmpty(cityEditText.getText().toString()));
        cityEditText.addTextChangedListener(clearValidationErrorsOnTextChange(cityTextInputLayout));

        stateTextInputLayout = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
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
        stateEditText.getOnFocusChangeListener()
                .onFocusChange(stateEditText, !StringUtil.isNullOrEmpty(stateEditText.getText().toString()));
        stateEditText.addTextChangedListener(clearValidationErrorsOnTextChange(stateTextInputLayout));

        address1TextInputLayout = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.address1TextInputLayout);
        employerAddressEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.addressEditText);
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
                    zipCodeTextInputLayout.setErrorEnabled(false);
                    zipCodeTextInputLayout.setError(null);
                    cityTextInputLayout.setErrorEnabled(false);
                    cityTextInputLayout.setError(null);
                    stateTextInputLayout.setErrorEnabled(false);
                    stateTextInputLayout.setError(null);
                } else {
                    employerAddressEditText2.setEnabled(true);

                }
                checkIfEnableButton();
            }
        });
        employerAddressEditText.setText(StringUtil.captialize(selectedEmployer.getAddress().getAddress1()));
        employerAddressEditText.getOnFocusChangeListener()
                .onFocusChange(employerAddressEditText, !StringUtil.isNullOrEmpty(employerAddressEditText
                        .getText().toString()));

        TextInputLayout phoneTextInputLayout = (TextInputLayout) view.findViewById(R.id.phoneTextInputLayout);
        EditText phoneEditText = (EditText) view.findViewById(R.id.phoneTextView);
        phoneEditText.addTextChangedListener(phoneInputFormatter);
        phoneEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(phoneTextInputLayout, null));
        if (selectedEmployer.getAddress() != null) {
            phoneEditText.setText(StringUtil.formatPhoneNumber(selectedEmployer.getAddress().getPhoneNumber()));
        }
        phoneEditText.getOnFocusChangeListener()
                .onFocusChange(phoneEditText, !StringUtil.isNullOrEmpty(phoneEditText.getText().toString()));

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
        return passConstraints(false);
    }

    protected boolean passConstraints(boolean userInteraction) {
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
        if (dataModel.getDemographic().getPrimaryPhysician().isRequired()
                && primaryPhysician == null) {
            return false;
        }
        if (dataModel.getDemographic().getReferringPhysician().isRequired()
                && referringPhysician == null) {
            return false;
        }
        if (dataModel.getDemographic().getEmploymentInfo().isRequired()
                && StringUtil.isNullOrEmpty(selectedEmployer.getName())
                && showEmployerFields) {
            return false;
        }

        if (showEmployerFields && (!StringUtil.isNullOrEmpty(employerAddressEditText.getText().toString())
                || !StringUtil.isNullOrEmpty(stateEditText.getText().toString())
                || !StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())
                || !StringUtil.isNullOrEmpty(cityEditText.getText().toString()))) {

            if (StringUtil.isNullOrEmpty(employerAddressEditText.getText().toString())) {
                if (userInteraction) {
                    address1TextInputLayout.setErrorEnabled(true);
                    address1TextInputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
                    employerAddressEditText.requestFocus();
                }
                return false;
            }

            if (!StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString().trim()) &&
                    !ValidationHelper.isValidString(zipCodeEditText.getText().toString().trim(),
                            ValidationHelper.ZIP_CODE_PATTERN)) {
                zipCodeTextInputLayout.setErrorEnabled(true);
                zipCodeTextInputLayout.setError(Label.getLabel("demographics_zip_code_validation_msg"));
                return false;
            } else {
                zipCodeTextInputLayout.setErrorEnabled(false);
                zipCodeTextInputLayout.setError(null);
            }

            if (StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())) {
                if (userInteraction) {
                    zipCodeTextInputLayout.setErrorEnabled(true);
                    zipCodeTextInputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
                } else {
                    zipCodeTextInputLayout.setErrorEnabled(false);
                    zipCodeTextInputLayout.setError(null);
                }
                return false;
            } else {
                zipCodeTextInputLayout.setErrorEnabled(false);
                zipCodeTextInputLayout.setError(null);
            }

            if (StringUtil.isNullOrEmpty(cityEditText.getText().toString())) {
                if (userInteraction) {
                    cityTextInputLayout.setErrorEnabled(true);
                    cityTextInputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
                }
                return false;
            }
            if (StringUtil.isNullOrEmpty(stateEditText.getText().toString())) {
                if (userInteraction) {
                    stateTextInputLayout.setErrorEnabled(true);
                    stateTextInputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
                }
                return false;
            }
        }
        if (dataModel.getDemographic().getEmergencyContact().isRequired()
                && StringUtil.isNullOrEmpty(selectedEmergencyContact.getFirstName())) {
            return false;
        }

        TextInputLayout socialSecurityInputLayout = (TextInputLayout) view.findViewById(R.id.socialSecurityInputLayout);
        EditText socialSecurityNumber = (EditText) view.findViewById(R.id.socialSecurityNumber);
        if (socialSecurityInputLayout.getVisibility() == View.VISIBLE &&
                !StringUtil.isNullOrEmpty(socialSecurityNumber.getText().toString().trim()) &&
                !ValidationHelper.isValidString(socialSecurityNumber.getText().toString().trim(),
                        ValidationHelper.SOCIAL_SECURITY_NUMBER_PATTERN)) {
            socialSecurityInputLayout.setErrorEnabled(true);
            socialSecurityInputLayout.setError(Label.getLabel("demographics_social_security_number_validation_msg"));
            return false;
        } else {
            socialSecurityInputLayout.setError(null);
            socialSecurityInputLayout.setErrorEnabled(false);
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
                !ValidationHelper.isValidEmail(emailAddress.getText().toString().trim())) {
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
                    zipCodeTextInputLayout.setError(null);
                    zipCodeTextInputLayout.setErrorEnabled(false);
                    checkIfEnableButton();
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
        checkIfEnableButton();
    }
}
