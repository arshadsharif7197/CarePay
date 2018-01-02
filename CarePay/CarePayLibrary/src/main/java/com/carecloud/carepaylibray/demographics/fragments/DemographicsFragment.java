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
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicEmergencyContactSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicEmploymentInfoSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicPhysicianSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsField;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsPersonalSection;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerDto;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmploymentInfoModel;
import com.carecloud.carepaylibray.demographics.dtos.payload.PhysicianDto;
import com.carecloud.carepaylibray.demographics.interfaces.DemographicExtendedInterface;
import com.carecloud.carepaylibray.demographics.interfaces.EmergencyContactFragmentInterface;
import com.carecloud.carepaylibray.demographics.interfaces.PhysicianFragmentInterface;
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
        implements EmergencyContactFragmentInterface, PhysicianFragmentInterface {

    private DemographicDataModel dataModel;
    private DemographicDTO demographicDTO;

    private DemographicExtendedInterface callback;
    private PatientModel demographicPersDetailsPayloadDTO;

    private EmploymentInfoModel demographicEmploymentInfoModel;
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
    private PatientModel emergencyContac;
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
        setUpExtendedDemographicFields(view, demographicPayload, dataModel.getDemographic());
        setUpEmergencyContact(view, demographicPayload.getEmergencyContact());
        setUpEmployer(view, demographicPayload, dataModel.getDemographic().getEmploymentInfo());
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

    private void setUpExtendedDemographicFields(View view, DemographicPayloadDTO demographicPayload,
                                                DemographicDataModel.Demographic demographic) {

        DemographicsPersonalSection personalInfoSection = demographic.getPersonalDetails();

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

        setUpDemographicField(view, StringUtil.captialize(demographicPayload.getPersonalDetails().getPreferredName()),
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

        EditText secondaryPhoneEditText = (EditText) view.findViewById(R.id.secondaryPhone);
        setUpDemographicField(view, StringUtil
                        .formatPhoneNumber(demographicPayload.getPersonalDetails().getSecondaryPhoneNumber()),
                personalInfoSection.getProperties().getSecondaryPhoneNumber(),
                R.id.secondaryPhoneDemographicsLayout, R.id.secondaryPhoneInputLayout,
                R.id.secondaryPhone, R.id.secondaryPhoneOptional, null, null);
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

        setUpPrimaryCarePhysician(view, demographicPayload.getPrimaryPhysician(), demographic.getPrimaryPhysician());
        setUpReferringPhysician(view, demographicPayload.getReferringPhysician(), demographic.getReferringPhysician());
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
        } else {
            editText.addTextChangedListener(getOptionalViewTextWatcher(optionalView));
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
        emergencyContac = emergencyContact;
        TextInputLayout emergencyContactInputLayout = (TextInputLayout) view.findViewById(R.id.emergencyContactInputLayout);
        emergencyContactInputLayout.setVisibility(demographicDTO.getMetadata().getNewDataModel()
                .getDemographic().getEmergencyContact().isDisplay() ? View.VISIBLE : View.GONE);
        EditText emergencyContactEditText = (EditText) view.findViewById(R.id.emergencyContactEditText);
        emergencyContactEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(emergencyContactInputLayout, null));
        if (emergencyContact != null) {
            emergencyContactEditText.setText(StringUtil.captialize(emergencyContact.getFullName()));
            unsetFieldError(emergencyContactInputLayout);
        }
        emergencyContactEditText.getOnFocusChangeListener().onFocusChange(emergencyContactEditText,
                !StringUtil.isNullOrEmpty(emergencyContactEditText.getText().toString().trim()));
        DemographicEmergencyContactSection emergencyContactSection = dataModel.getDemographic().getEmergencyContact();
        view.findViewById(R.id.emergencyContactDemographicsLayout)
                .setVisibility(emergencyContactSection.isDisplay() ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.emergencyContactOptionalLabel)
                .setVisibility(!emergencyContactSection.isRequired() && emergencyContact == null ? View.VISIBLE : View.GONE);
        emergencyContactEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showAddEditEmergencyContactDialog();
            }
        });

    }

    private void setUpEmployer(final View view, DemographicPayloadDTO demographicPayload,
                               DemographicEmploymentInfoSection employmentInfoSection) {
        boolean isEmploymentStuffVisible = employmentInfoSection.isDisplay();
        if (isEmploymentStuffVisible) {
            final EditText employmentStatusEditText = (EditText) view.findViewById(R.id.employmentStatusEditText);
            final View employmentStatusOptional = view.findViewById(R.id.employmentStatusOptional);
            final TextInputLayout employmentStatusInputLayout = (TextInputLayout) view
                    .findViewById(R.id.employmentStatusInputLayout);
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
                                    unsetFieldError(employmentStatusInputLayout);
                                    checkIfEnableButton(getView());
                                }
                            }, Label.getLabel("demographics_employment_status")));

            String employmentStatus = demographicPayload.getEmploymentInfoModel().getEmploymentStatus();

            employmentStatusEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(employmentStatusInputLayout, null));
            employmentStatusEditText.setText(employmentStatus);
            employmentStatusEditText.getOnFocusChangeListener()
                    .onFocusChange(employmentStatusEditText,
                            !StringUtil.isNullOrEmpty(employmentStatusEditText.getText().toString()));
            selectedEmploymentStatus.setName(employmentStatus);
            selectedEmploymentStatus.setLabel(employmentStatus);
            if (!StringUtil.isNullOrEmpty(employmentStatus)) {
                showEmployerFields = employmentStatus.toLowerCase().equals("employed")
                        || employmentStatus.toLowerCase().equals("part time");
            } else if (!employmentInfoSection.isRequired()) {
                employmentStatusOptional.setVisibility(View.VISIBLE);
            }
            if (!employmentInfoSection.isRequired()) {
                view.findViewById(R.id.employmentInfoOptionalTextView).setVisibility(View.VISIBLE);
            }

        } else {
            view.findViewById(R.id.employmentStatusDemographicsLayout).setVisibility(View.GONE);
            view.findViewById(R.id.employmentInfoContainer).setVisibility(View.GONE);
        }

        setEmployerInfoFields(view, demographicPayload, employmentInfoSection, isEmploymentStuffVisible);

    }

    private void setEmployerInfoFields(final View view, DemographicPayloadDTO demographicPayload,
                                       DemographicEmploymentInfoSection employmentInfoSection,
                                       boolean isEmploymentStuffVisible) {
        employerDependentFieldsLayout = view.findViewById(R.id.employerDependentLayout);

        selectedEmployer = demographicPayload.getEmploymentInfoModel().getEmployerDto();
        if (selectedEmployer == null) {
            selectedEmployer = new EmployerDto();
        }

        final TextInputLayout employerNameTextInputLayout = (TextInputLayout) view.findViewById(R.id.employerNameTextInputLayout);
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
                if (editable.length() > 0) {
                    employerNameTextInputLayout.setError(null);
                    employerNameTextInputLayout.setErrorEnabled(false);
                    showErrorViews(false, (ViewGroup) view.findViewById(R.id.employerNameDemographicsLayout));
                }
                selectedEmployer.setName(editable.toString());
                checkIfEnableButton(getView());
            }
        });
        if (employmentInfoSection.isRequired()) {
            employerNameEditText.addTextChangedListener(getValidateEmptyTextWatcher(employerNameTextInputLayout));
        }

        TextInputLayout address2TextInputLayout = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        employerAddressEditText2 = (EditText) view.findViewById(R.id.addressEditText2);
        employerAddressEditText2.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(address2TextInputLayout, null));
        employerAddressEditText2.setText(StringUtil.captialize(selectedEmployer.getAddress().getAddress2()));
        employerAddressEditText2.getOnFocusChangeListener()
                .onFocusChange(employerAddressEditText2, !StringUtil.isNullOrEmpty(employerAddressEditText2
                        .getText().toString()));

        zipCodeTextInputLayout = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeTextView);
        zipCodeEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(zipCodeTextInputLayout,
                getZipCodeFocusListener(zipCodeEditText)));
        zipCodeEditText.setText(StringUtil.formatZipCode(selectedEmployer.getAddress().getZipcode()));
        zipCodeEditText.addTextChangedListener(zipInputFormatter);
        zipCodeEditText.setOnClickListener(selectEndOnClick);
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

        address1TextInputLayout = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        employerAddressEditText = (EditText) view.findViewById(R.id.addressEditText);
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
                    employerAddressEditText2.getOnFocusChangeListener().onFocusChange(employerAddressEditText2,
                            !StringUtil.isNullOrEmpty(employerAddressEditText2.getText().toString()));
                    zipCodeTextInputLayout.setErrorEnabled(false);
                    zipCodeTextInputLayout.setError(null);
                    zipCodeEditText.getOnFocusChangeListener().onFocusChange(zipCodeEditText,
                            !StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString()));
                    cityTextInputLayout.setErrorEnabled(false);
                    cityTextInputLayout.setError(null);
                    stateTextInputLayout.setErrorEnabled(false);
                    stateTextInputLayout.setError(null);
                } else {
                    employerAddressEditText2.setEnabled(true);
                    address1TextInputLayout.setErrorEnabled(false);
                    address1TextInputLayout.setError(null);
                    showErrorViews(false, (ViewGroup) view.findViewById(R.id.address1DemographicsLayout));
                }
                checkIfEnableButton(view);
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
        phoneEditText.setText(StringUtil.formatPhoneNumber(selectedEmployer.getAddress().getPhoneNumber()));
        phoneEditText.getOnFocusChangeListener()
                .onFocusChange(phoneEditText, !StringUtil.isNullOrEmpty(phoneEditText.getText().toString()));
        phoneEditText.setOnClickListener(selectEndOnClick);

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
    protected boolean passConstraints(View view) {
        try {
            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getGender().isRequired(), selectedGender.getName(), R.id.genderDemographicsLayout,
                    R.id.genderInputLayout, isUserAction())) return false;

            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getPrimaryRace().isRequired(), selectedRace.getName(), R.id.raceDemographicsLayout,
                    R.id.raceInputLayout, isUserAction())) return false;

            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getSecondaryRace().isRequired(), selectedSecondaryRace.getName(),
                    R.id.secondaryRaceDemographicsLayout, R.id.secondaryRaceInputLayout, isUserAction()))
                return false;

            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getEthnicity().isRequired(), selectedEthnicity.getName(),
                    R.id.ethnicityDemographicsLayout, R.id.ethnicityInputLayout, isUserAction()))
                return false;

            EditText ssnEditText = (EditText) view.findViewById(R.id.socialSecurityNumber);
            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getSocialSecurityNumber().isRequired(), ssnEditText.getText().toString(),
                    R.id.socialSecurityContainer, R.id.socialSecurityInputLayout, isUserAction()))
                return false;

            TextInputLayout socialSecurityInputLayout = (TextInputLayout) view.findViewById(R.id.socialSecurityInputLayout);
            if (socialSecurityInputLayout.getVisibility() == View.VISIBLE &&
                    !StringUtil.isNullOrEmpty(ssnEditText.getText().toString().trim()) &&
                    !ValidationHelper.isValidString(ssnEditText.getText().toString().trim(),
                            ValidationHelper.SOCIAL_SECURITY_NUMBER_PATTERN)) {
                if (isUserAction()) {
                    socialSecurityInputLayout.setErrorEnabled(true);
                    socialSecurityInputLayout.setError(Label.getLabel("demographics_social_security_number_validation_msg"));
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.socialSecurityContainer));
                }
                return false;
            } else {
                unsetFieldError(socialSecurityInputLayout);
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.socialSecurityContainer));
            }

            EditText preferredNameEditText = (EditText) view.findViewById(R.id.preferredName);
            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getPreferredName().isRequired(), preferredNameEditText.getText().toString(),
                    R.id.preferredNameContainer, R.id.preferredNameInputLayout, isUserAction()))
                return false;

            EditText emailEditText = (EditText) view.findViewById(R.id.email);
            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getEmailAddress().isRequired(), emailEditText.getText().toString(),
                    R.id.emailContainer, R.id.emailInputLayout, isUserAction()))
                return false;

            TextInputLayout emailLayout = (TextInputLayout) view.findViewById(R.id.emailInputLayout);
            if (emailLayout.getVisibility() == View.VISIBLE &&
                    !StringUtil.isNullOrEmpty(emailEditText.getText().toString().trim()) &&
                    !ValidationHelper.isValidEmail(emailEditText.getText().toString().trim())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.emailContainer));
                    setFieldError(emailLayout, Label.getLabel("demographics_email_validation_msg"), isUserAction());
                }
                return false;
            } else {
                unsetFieldError(emailLayout);
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.emailContainer));
            }

            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getPreferredLanguage().isRequired(), selectedPreferredLanguage.getName(),
                    R.id.preferredLanguageDemographicsLayout, R.id.preferredLanguageInputLayout, isUserAction()))
                return false;

            EditText driverLicenseEditText = (EditText) view.findViewById(R.id.driverLicense);
            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getDriversLicenseNumber().isRequired(), driverLicenseEditText.getText().toString(),
                    R.id.driverLicenseContainer, R.id.driverLicenseInputLayout, isUserAction()))
                return false;

            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getDriversLicenseState().isRequired(), selectedDriverLicenseState.getName(),
                    R.id.driverLicenseStateDemographicsLayout, R.id.driverLicenseStateInputLayout, isUserAction()))
                return false;

            EditText secondaryPhoneEditText = (EditText) view.findViewById(R.id.secondaryPhone);
            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getSecondaryPhoneNumber().isRequired(), secondaryPhoneEditText.getText().toString(),
                    R.id.secondaryPhoneDemographicsLayout, R.id.secondaryPhoneInputLayout, isUserAction()))
                return false;

            TextInputLayout phoneLayout = (TextInputLayout) view.findViewById(R.id.secondaryPhoneInputLayout);
            if (phoneLayout.getVisibility() == View.VISIBLE &&
                    !StringUtil.isNullOrEmpty(secondaryPhoneEditText.getText().toString().trim()) &&
                    !ValidationHelper.isValidString(secondaryPhoneEditText.getText().toString().trim(),
                            ValidationHelper.PHONE_NUMBER_PATTERN)) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.secondaryPhoneDemographicsLayout));
                    setFieldError(phoneLayout, Label.getLabel("demographics_phone_number_validation_msg"), isUserAction());
                }
                return false;
            } else {
                unsetFieldError(phoneLayout);
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.secondaryPhoneDemographicsLayout));
            }

            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getSecondaryPhoneNumberType().isRequired(), selectedSecondaryPhoneType.getName(),
                    R.id.secondaryPhoneTypeDemographicsLayout, R.id.secondaryPhoneTypeInputLayout, isUserAction()))
                return false;

            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getPreferredContact().isRequired(), selectedContactMethod.getName(),
                    R.id.preferredContactMethodDemographicsLayout, R.id.preferredContactMethodInputLayout, isUserAction()))
                return false;

            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getMaritalStatus().isRequired(), selectedMaritalStatus.getName(),
                    R.id.maritalStatusDemographicsLayout, R.id.maritalStatusInputLayout, isUserAction()))
                return false;

            if (dataModel.getDemographic().getEmergencyContact().isRequired()
                    && (emergencyContac == null
                    || StringUtil.isNullOrEmpty(emergencyContac.getFirstName())
                    || StringUtil.isNullOrEmpty(emergencyContac.getLastName())
                    || StringUtil.isNullOrEmpty(emergencyContac.getPhoneNumber())
                    || StringUtil.isNullOrEmpty(emergencyContac.getEmergencyContactRelationship()))) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.emergencyContactDemographicsLayout));
                    setDefaultError(view, R.id.emergencyContactInputLayout, isUserAction());
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.emergencyContactDemographicsLayout));
            }

            if (dataModel.getDemographic().getPrimaryPhysician().isRequired()
                    && primaryPhysician == null) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.primaryPhysicianDemographicsLayout));
                    setDefaultError(view, R.id.primaryPhysicianInputLayout, isUserAction());
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.primaryPhysicianDemographicsLayout));
            }

            if (dataModel.getDemographic().getReferringPhysician().isRequired()
                    && referringPhysician == null) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.referringPhysicianDemographicsLayout));
                    setDefaultError(view, R.id.referringPhysicianInputLayout, isUserAction());
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.referringPhysicianDemographicsLayout));
            }

            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getReferralSource().isRequired(), selectedReferralSource.getName(),
                    R.id.referralSourceDemographicsLayout, R.id.referralSourceInputLayout, isUserAction()))
                return false;

            if (dataModel.getDemographic().getEmploymentInfo().isRequired()
                    && StringUtil.isNullOrEmpty(selectedEmploymentStatus.getName())) {
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.employmentStatusDemographicsLayout));
                    setDefaultError(view, R.id.employmentStatusInputLayout, isUserAction());
                }
                return false;
            } else {
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.employmentStatusDemographicsLayout));
            }

            if (showEmployerFields && (!StringUtil.isNullOrEmpty(employerAddressEditText.getText().toString())
                    || !StringUtil.isNullOrEmpty(stateEditText.getText().toString())
                    || !StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())
                    || !StringUtil.isNullOrEmpty(cityEditText.getText().toString()))) {

                if (StringUtil.isNullOrEmpty(selectedEmployer.getName())) {
                    if (isUserAction()) {
                        setDefaultError(view, R.id.employerNameTextInputLayout, isUserAction());
                        showErrorViews(true, (ViewGroup) view.findViewById(R.id.employerNameDemographicsLayout));
                    } else {
                        showErrorViews(false, (ViewGroup) view.findViewById(R.id.employerNameDemographicsLayout));
                    }
                    return false;
                }

                if (StringUtil.isNullOrEmpty(employerAddressEditText.getText().toString())) {
                    if (isUserAction()) {
                        showErrorViews(true, (ViewGroup) view.findViewById(R.id.address1DemographicsLayout));
                        setDefaultError(address1TextInputLayout, isUserAction());

                    } else {
                        showErrorViews(false, (ViewGroup) view.findViewById(R.id.address1DemographicsLayout));
                        unsetFieldError(view, R.id.address1TextInputLayout);
                    }
                    return false;
                }

                if (StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())) {
                    if (isUserAction()) {
                        setDefaultError(zipCodeTextInputLayout, isUserAction());
                        showErrorViews(true, (ViewGroup) view.findViewById(R.id.zipCodeContainer));
                    } else {
                        unsetFieldError(zipCodeTextInputLayout);
                        showErrorViews(false, (ViewGroup) view.findViewById(R.id.zipCodeContainer));
                    }
                    return false;
                } else {
                    unsetFieldError(zipCodeTextInputLayout);
                    showErrorViews(false, (ViewGroup) view.findViewById(R.id.zipCodeContainer));
                }

                if (!StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString().trim()) &&
                        !ValidationHelper.isValidString(zipCodeEditText.getText().toString().trim(),
                                ValidationHelper.ZIP_CODE_PATTERN)) {
                    if(isUserAction()){
                        showErrorViews(true, (ViewGroup) view.findViewById(R.id.zipCodeContainer));
                    }
                    setFieldError(zipCodeTextInputLayout, Label.getLabel("demographics_zip_code_validation_msg"), isUserAction());
                    return false;
                } else {
                    unsetFieldError(zipCodeTextInputLayout);
                    showErrorViews(false, (ViewGroup) view.findViewById(R.id.zipCodeContainer));
                }

                if (StringUtil.isNullOrEmpty(cityEditText.getText().toString())) {
                    if (isUserAction()) {
                        setDefaultError(view, R.id.cityTextInputLayout, isUserAction());
                        showErrorViews(true, (ViewGroup) view.findViewById(R.id.cityAndStateLayoutContainer));
                    }
                    return false;
                } else {
                    unsetFieldError(cityTextInputLayout);
                    showErrorViews(false, (ViewGroup) view.findViewById(R.id.cityAndStateLayoutContainer));
                }

                if (StringUtil.isNullOrEmpty(stateEditText.getText().toString())) {
                    if (isUserAction()) {
                        setDefaultError(view, R.id.stateTextInputLayout, isUserAction());
                        showErrorViews(true, (ViewGroup) view.findViewById(R.id.cityAndStateLayoutContainer));
                    }
                    return false;
                } else {
                    unsetFieldError(view, R.id.stateTextInputLayout);
                    showErrorViews(false, (ViewGroup) view.findViewById(R.id.cityAndStateLayoutContainer));
                }


                TextInputLayout employerPhoneLayout = (TextInputLayout) view.findViewById(R.id.phoneTextInputLayout);
                EditText employerPhoneNumber = (EditText) view.findViewById(R.id.phoneTextView);
                if (employerPhoneLayout.getVisibility() == View.VISIBLE &&
                        !StringUtil.isNullOrEmpty(employerPhoneNumber.getText().toString().trim()) &&
                        !ValidationHelper.isValidString(employerPhoneNumber.getText().toString().trim(),
                                ValidationHelper.PHONE_NUMBER_PATTERN)) {
                    if (isUserAction()) {
                        showErrorViews(true, (ViewGroup) view.findViewById(R.id.employerPhoneContainer));
                    }
                    setFieldError(employerPhoneLayout,
                            Label.getLabel("demographics_phone_number_validation_msg"), isUserAction());
                    return false;
                } else {
                    unsetFieldError(employerPhoneLayout);
                    showErrorViews(false, (ViewGroup) view.findViewById(R.id.employerPhoneContainer));
                }


            } else {
                zipCodeTextInputLayout.setError(null);
                zipCodeTextInputLayout.setErrorEnabled(false);
                stateTextInputLayout.setError(null);
                stateTextInputLayout.setErrorEnabled(false);
                cityTextInputLayout.setError(null);
                cityTextInputLayout.setErrorEnabled(false);
            }

            if (dataModel.getDemographic().getEmploymentInfo().isRequired()
                    && StringUtil.isNullOrEmpty(selectedEmployer.getName())
                    && showEmployerFields) {
                if (isUserAction()) {
                    setDefaultError(view, R.id.employerNameTextInputLayout, isUserAction());
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.employerNameDemographicsLayout));
                } else {
                    showErrorViews(false, (ViewGroup) view.findViewById(R.id.employerNameDemographicsLayout));
                }
                return false;
            }


            return true;
        } finally {
            setUserAction(false);
        }
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
            demographicPersDetailsPayloadDTO.setSocialSecurityNumber(StringUtil.revertToRawFormat(socialSecurity));
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
            demographicPersDetailsPayloadDTO.setSecondaryPhoneNumber(StringUtil.revertToRawFormat(secondaryPhone));
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
            selectedEmployer.getAddress().setZipcode(StringUtil.revertZipToRawFormat(zipCode));

            String city = ((TextView) findViewById(R.id.cityTextView)).getText().toString().trim();
            selectedEmployer.getAddress().setCity(city);

            String state = ((TextView) findViewById(R.id.stateTextView)).getText().toString().trim();
            selectedEmployer.getAddress().setState(state);

            String phone = ((TextView) findViewById(R.id.phoneTextView)).getText().toString().trim();
            selectedEmployer.getAddress().setPhoneNumber(phone);

            demographicEmploymentInfoModel.setEmployerDto(selectedEmployer);
        } else {
            demographicEmploymentInfoModel.setEmployerDto(null);
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
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setEmploymentInfoModel(demographicEmploymentInfoModel);
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setPrimaryPhysician(primaryPhysician);
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setReferringPhysician(referringPhysician);
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        updatableDemographicDTO.getPayload().setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        return updatableDemographicDTO;
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
                    zipCodeTextInputLayout.setError(null);
                    zipCodeTextInputLayout.setErrorEnabled(false);
                    checkIfEnableButton(getView());
                }
            }


        }.execute(zipCode);
    }

    @Override
    public void updateEmergencyContact(PatientModel emergencyContact) {
        setUpEmergencyContact(getView(), emergencyContact);
        demographicDTO.getPayload().getDemographics().getPayload().setEmergencyContact(emergencyContact);
        checkIfEnableButton(getView());
    }

    @Override
    public void setPhysician(PhysicianDto physician, int physicianType) {
        TextInputLayout inputLayout;
        if (physicianType == SearchPhysicianFragment.PRIMARY_PHYSICIAN) {
            setUpPrimaryCarePhysician(getView(), physician,
                    demographicDTO.getMetadata().getNewDataModel().getDemographic().getPrimaryPhysician());
            inputLayout = (TextInputLayout) getView().findViewById(R.id.primaryPhysicianInputLayout);
        } else {
            setUpReferringPhysician(getView(), physician,
                    demographicDTO.getMetadata().getNewDataModel().getDemographic().getReferringPhysician());
            inputLayout = (TextInputLayout) getView().findViewById(R.id.referringPhysicianInputLayout);
        }
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
        checkIfEnableButton(getView());
    }
}
