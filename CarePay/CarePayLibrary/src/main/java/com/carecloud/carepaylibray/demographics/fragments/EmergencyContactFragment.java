package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomOptionsAdapter;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicEmergencyContactSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.interfaces.EmergencyContactInterface;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.gson.Gson;
import com.smartystreets.api.us_zipcode.City;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 30/10/17.
 */

public class EmergencyContactFragment extends BaseDialogFragment {

    private EmergencyContactInterface callback;
    private DemographicDTO dto;
    private EditText stateEditText;
    private EditText cityEditText;
    private EditText genderEditText;
    private EditText dateOfBirthEditText;
    private EditText firstNameEditText;
    private EditText middleNameEditText;
    private EditText lastNameEditText;
    private EditText primaryPhoneEditText;
    private EditText secondaryPhoneEditText;
    private EditText addressEditText;
    private EditText emailEditText;
    private EditText addressEditText2;
    private EditText zipCodeEditText;
    private EditText ecRelationshipEditText;
    private Button saveButton;
    private TextInputLayout emailInputLayout;
    private TextInputLayout zipCodeTextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout lastNameInputLayout;
    private TextInputLayout firstNameInputLayout;
    private TextInputLayout primaryPhoneTextInputLayout;
    private TextInputLayout dateBirthTextInputLayout;
    private TextInputLayout secondaryPhoneTextInputLayout;
    private TextInputLayout ecRelationshipInputLayout;
    private TextInputLayout address1TextInputLayout;
    private View parentScrollView;
    private DemographicsOption selectedGender = new DemographicsOption();
    private DemographicsOption selectedRelationship = new DemographicsOption();

    public EmergencyContactFragment() {

    }

    public static EmergencyContactFragment newInstance() {
        return new EmergencyContactFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (EmergencyContactInterface) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EmergencyContactInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        dto = (DemographicDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emergency_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarLayout);
        if (getDialog() == null) {
            TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
            title.setText(Label.getLabel("demographics_emergency_contact_title"));
            toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
            callback.setToolbar(toolbar);
        } else {
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            title.setText(Label.getLabel("demographics_emergency_contact_title"));
            view.findViewById(R.id.edit_insurance_close_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
        setUpView(view);
    }

    private void setUpView(View view) {
        DemographicPayloadDTO demographicPayload = dto.getPayload().getDemographics().getPayload();
        PatientModel emergencyContact = demographicPayload.getEmergencyContact();
        DemographicEmergencyContactSection emergencyContactSection = dto.getMetadata().getNewDataModel().getDemographic().getEmergencyContact();

        saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmergencyContactInfo();
            }
        });
        saveButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View buttonView, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !buttonView.isSelected()) {
                    checkIfEnableButton(true);
                    return true;
                }
                return false;
            }
        });

        if (!isEmptyEC(emergencyContact)) {
            saveButton.setText(Label.getLabel("demographics.emergencyContact.button.label.saveChanges"));
        } else {
            emergencyContact = new PatientModel();
        }

        parentScrollView = view.findViewById(R.id.scrollView);

        firstNameInputLayout = view.findViewById(R.id.firstNameInputLayout);
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        setupDemographicField(firstNameEditText, firstNameInputLayout,
                emergencyContact.getFirstName(),
                emergencyContactSection.getProperties().getFirstName().isRequired() || true,
                view.findViewById(R.id.firstNameRequired), null);

        TextInputLayout middleNameInputLayout = view.findViewById(R.id.middleNameInputLayout);
        middleNameEditText = view.findViewById(R.id.middleNameEditText);
        setupDemographicField(middleNameEditText, middleNameInputLayout,
                emergencyContact.getMiddleName(),
                emergencyContactSection.getProperties().getMiddleName().isRequired(),
                view.findViewById(R.id.middleNameRequired), null);

        lastNameInputLayout = view.findViewById(R.id.lastNameInputLayout);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        setupDemographicField(lastNameEditText, lastNameInputLayout,
                emergencyContact.getLastName(),
                emergencyContactSection.getProperties().getLastName().isRequired() || true,
                view.findViewById(R.id.lastNameRequired), null);


        primaryPhoneTextInputLayout = view.findViewById(R.id.primaryPhoneTextInputLayout);
        primaryPhoneEditText = view.findViewById(R.id.primaryPhoneEditText);
        setupDemographicField(primaryPhoneEditText, primaryPhoneTextInputLayout,
                StringUtil.formatPhoneNumber(emergencyContact.getPhoneNumber()),
                emergencyContactSection.getProperties().getPrimaryPhoneNumber().isRequired() || true,
                view.findViewById(R.id.phoneNumberRequired), null);
        primaryPhoneEditText.addTextChangedListener(phoneInputFormatter);
        primaryPhoneEditText.setOnClickListener(selectEndOnClick);

        secondaryPhoneTextInputLayout = view.findViewById(R.id.secondaryPhoneTextInputLayout);
        secondaryPhoneEditText = view.findViewById(R.id.secondaryPhoneEditText);
        setupDemographicField(secondaryPhoneEditText, secondaryPhoneTextInputLayout,
                StringUtil.formatPhoneNumber(emergencyContact.getSecondaryPhoneNumber()),
                emergencyContactSection.getProperties().getSecondaryPhoneNumber().isRequired(),
                view.findViewById(R.id.secondaryPhoneRequired), null);
        secondaryPhoneEditText.addTextChangedListener(phoneInputFormatter);
        secondaryPhoneEditText.setOnClickListener(selectEndOnClick);

        TextInputLayout address2TextInputLayout = view.findViewById(R.id.address2TextInputLayout);
        addressEditText2 = view.findViewById(R.id.addressEditText2);
        setupDemographicField(addressEditText2, address2TextInputLayout,
                emergencyContact.getAddress().getAddress2(),
                emergencyContactSection.getProperties().getAddress().getProperties().getAddress2().isRequired(),
                view.findViewById(R.id.addressLine2Required), null);

        zipCodeTextInputLayout = view.findViewById(R.id.zipCodeTextInputLayout);
        zipCodeEditText = view.findViewById(R.id.zipCodeTextView);
        setupDemographicField(zipCodeEditText, zipCodeTextInputLayout,
                emergencyContact.getAddress().getZipcode(),
                emergencyContactSection.getProperties().getAddress().getProperties().getZipcode().isRequired(),
                view.findViewById(R.id.zipCodeRequired), getZipCodeFocusListener(zipCodeEditText));
        zipCodeEditText.addTextChangedListener(zipInputFormatter);

        cityTextInputLayout = view.findViewById(R.id.cityTextInputLayout);
        cityEditText = view.findViewById(R.id.cityTextView);
        setupDemographicField(cityEditText, cityTextInputLayout,
                emergencyContact.getAddress().getCity(),
                emergencyContactSection.getProperties().getAddress().getProperties().getCity().isRequired(),
                view.findViewById(R.id.cityRequired), null);

        stateTextInputLayout = view.findViewById(R.id.stateTextInputLayout);
        stateEditText = view.findViewById(R.id.stateTextView);
        setupDemographicField(stateEditText, stateTextInputLayout,
                emergencyContact.getAddress().getState(),
                emergencyContactSection.getProperties().getAddress().getProperties().getState().isRequired(),
                view.findViewById(R.id.stateRequired), null);
        stateEditText.setOnClickListener(
                getOptionsListener(emergencyContactSection.getProperties().getAddress()
                                .getProperties().getState().getOptions(),
                        new CheckInDemographicsBaseFragment.OnOptionSelectedListener() {
                            @Override
                            public void onOptionSelected(DemographicsOption option) {
                                stateEditText.setText(option.getLabel());
                            }
                        },
                        Label.getLabel("demographics_documents_title_select_state")));

        emailInputLayout = view.findViewById(R.id.emailInputLayout);
        emailEditText = view.findViewById(R.id.emailEditText);
        setupDemographicField(emailEditText, emailInputLayout,
                emergencyContact.getEmail(),
                emergencyContactSection.getProperties().getEmailAddress().isRequired(),
                view.findViewById(R.id.emailRequired), null);

        dateBirthTextInputLayout = view.findViewById(R.id.dateOfBirthInputLayout);
        dateOfBirthEditText = view.findViewById(R.id.dateOfBirthEditText);
        setupDemographicField(dateOfBirthEditText, dateBirthTextInputLayout,
                emergencyContact.getFormattedDateOfBirth(),
                emergencyContactSection.getProperties().getDateOfBirth().isRequired(),
                view.findViewById(R.id.dobRequired), null);
        dateOfBirthEditText.addTextChangedListener(dateInputFormatter);
        dateOfBirthEditText.setOnClickListener(selectEndOnClick);

        TextInputLayout genderInputLayout = view.findViewById(R.id.genderInputLayout);
        genderEditText = view.findViewById(R.id.genderEditText);
        genderEditText.setOnClickListener(
                getOptionsListener(emergencyContactSection.getProperties().getGender().getOptions(),
                        new CheckInDemographicsBaseFragment.OnOptionSelectedListener() {
                            @Override
                            public void onOptionSelected(DemographicsOption option) {
                                selectedGender = option;
                                genderEditText.setText(option.getLabel());
                                genderEditText.getOnFocusChangeListener().onFocusChange(genderEditText,
                                        !StringUtil.isNullOrEmpty(genderEditText.getText().toString()));
                            }
                        },
                        Label.getLabel("demographics_review_gender")));
        initSelectableInput(genderEditText, genderInputLayout,
                selectedGender, emergencyContact.getGender(),
                view.findViewById(R.id.genderRequiredLabel),
                emergencyContactSection.getProperties().getGender().isRequired(),
                emergencyContactSection.getProperties().getGender().getOptions());

        ecRelationshipInputLayout = view.findViewById(R.id.emergencyContactRelationshipInputLayout);
        ecRelationshipEditText = view.findViewById(R.id.emergencyContactRelationshipEditText);
        ecRelationshipEditText.setOnClickListener(
                getOptionsListener(emergencyContactSection.getProperties().getRelationshipType().getOptions(),
                        new CheckInDemographicsBaseFragment.OnOptionSelectedListener() {
                            @Override
                            public void onOptionSelected(DemographicsOption option) {
                                selectedRelationship = option;
                                ecRelationshipEditText.setText(option.getLabel());
                                ecRelationshipEditText.getOnFocusChangeListener()
                                        .onFocusChange(ecRelationshipEditText,
                                                !StringUtil.isNullOrEmpty(ecRelationshipEditText
                                                        .getText().toString()));
                                checkIfEnableButton(false);
                            }
                        },
                        Label.getLabel("demographics_emergency_contact_relationship")));
        initSelectableInput(ecRelationshipEditText, ecRelationshipInputLayout,
                selectedRelationship, emergencyContact.getEmergencyContactRelationship(),
                view.findViewById(R.id.emergencyContactRelationshipRequired),
                emergencyContactSection.getProperties().getRelationshipType().isRequired() || true,
                emergencyContactSection.getProperties().getRelationshipType().getOptions());

        address1TextInputLayout = view.findViewById(R.id.address1TextInputLayout);
        addressEditText = view.findViewById(R.id.addressEditText);
        setupDemographicField(addressEditText, address1TextInputLayout,
                emergencyContact.getAddress().getAddress1(),
                emergencyContactSection.getProperties().getAddress().getProperties().getAddress1().isRequired(),
                view.findViewById(R.id.addressLine1Required), null);
        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    addressEditText2.setEnabled(false);
                    addressEditText2.setText("");
                    addressEditText2.getOnFocusChangeListener().onFocusChange(addressEditText2,
                            !StringUtil.isNullOrEmpty(addressEditText2.getText().toString()));
                    unsetFieldError(zipCodeTextInputLayout);
                    unsetFieldError(cityTextInputLayout);
                    unsetFieldError(stateTextInputLayout);
                } else {
                    addressEditText2.setEnabled(true);

                }
                checkIfEnableButton(false);
            }
        });

        checkIfEnableButton(false);
    }

    private void setupDemographicField(EditText editText, TextInputLayout inputLayout, String value, boolean isRequired, View requiredView, View.OnFocusChangeListener additionalFocusListener) {
        editText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(inputLayout, additionalFocusListener));
        editText.setText(value);
        boolean hasValue = !StringUtil.isNullOrEmpty(editText.getText().toString().trim());
        editText.getOnFocusChangeListener().onFocusChange(editText, hasValue);

        if (isRequired) {
            editText.addTextChangedListener(getValidateRequiredEmptyTextWatcher(inputLayout));
        }
        if (isRequired && requiredView != null) {
            requiredView.setVisibility(hasValue ? View.GONE : View.VISIBLE);
            editText.addTextChangedListener(getValidateOptionalEmptyTextWatcher(requiredView));
        }
    }


    private void initSelectableInput(EditText editText, TextInputLayout inputLayout,
                                     DemographicsOption storeOption, String storedName,
                                     View requiredView, boolean isRequired,
                                     List<DemographicsOption> options) {
        editText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(inputLayout, null));
        String key = storeOption.getName();
        if (StringUtil.isNullOrEmpty(key)) {
            key = storedName;
        }
        storeOption = getOptionByKey(options, key, storeOption);
        boolean hasValue = !StringUtil.isNullOrEmpty(storedName);
        if (hasValue) {
            editText.setText(storeOption.getLabel());
        }
        if (isRequired && requiredView != null && !hasValue) {
            requiredView.setVisibility(View.VISIBLE);
        }
        editText.getOnFocusChangeListener().onFocusChange(editText, hasValue);
    }

    private DemographicsOption getOptionByKey(List<DemographicsOption> options,
                                              String name,
                                              DemographicsOption storeOption) {
        for (DemographicsOption option : options) {
            if (option.getName().equals(name)) {
                storeOption.setName(option.getName());
                storeOption.setLabel(option.getLabel());
                return storeOption;
            }
        }
        storeOption.setName(name);
        storeOption.setLabel(name);
        return storeOption;
    }

    private boolean isEmptyEC(PatientModel emergencyContact) {
        return emergencyContact == null || (emergencyContact.getFirstName() == null
                && emergencyContact.getLastName() == null
                && emergencyContact.getPhoneNumber() == null
                && emergencyContact.getEmergencyContactRelationship() == null);
    }

    private void updateEmergencyContactInfo() {
        if (passConstraints(true)) {
            Map<String, String> queries = new HashMap<>();
            if (!dto.getPayload().getAppointmentpayloaddto().isEmpty()) {
                queries.put("practice_mgmt", dto.getPayload().getAppointmentpayloaddto().get(0)
                        .getMetadata().getPracticeMgmt());
                queries.put("practice_id", dto.getPayload().getAppointmentpayloaddto().get(0)
                        .getMetadata().getPracticeId());
                queries.put("appointment_id", dto.getPayload().getAppointmentpayloaddto().get(0)
                        .getMetadata().getAppointmentId());
            }
            Map<String, String> header = new HashMap<>();
            DemographicDTO updateModel = getUpdateModel();
            Gson gson = new Gson();
            String jsonPayload = gson.toJson(updateModel.getPayload().getDemographics().getPayload());

            TransitionDTO updateDemographics = dto.getMetadata().getTransitions().getUpdateDemographics();
            getWorkflowServiceHelper().execute(updateDemographics, updateDemographicsCallback, jsonPayload, queries, header);
        }
    }

    private boolean passConstraints(boolean userInteraction) {
        DemographicEmergencyContactSection dataModel = dto.getMetadata().getNewDataModel()
                .getDemographic().getEmergencyContact();
        if (StringUtil.isNullOrEmpty(firstNameEditText.getText().toString())) {
            if (userInteraction) {
                setError(firstNameInputLayout);
            }
            return false;
        } else {
            unsetFieldError(firstNameInputLayout);
        }

        if (dataModel.getProperties().getMiddleName().isRequired()
                && StringUtil.isNullOrEmpty(middleNameEditText.getText().toString())) {
            return false;
        }

        if (StringUtil.isNullOrEmpty(lastNameEditText.getText().toString())) {
            if (userInteraction) {
                setError(lastNameInputLayout);
            }
            return false;
        } else {
            unsetFieldError(lastNameInputLayout);
        }

        if (validatePhoneNumber(primaryPhoneTextInputLayout, primaryPhoneEditText, userInteraction, true))
            return false;

        if (validatePhoneNumber(secondaryPhoneTextInputLayout, secondaryPhoneEditText, userInteraction,
                dataModel.getProperties().getSecondaryPhoneNumber().isRequired()))
            return false;

        if (StringUtil.isNullOrEmpty(ecRelationshipEditText.getText().toString())) {
            if (userInteraction) {
                setError(ecRelationshipInputLayout);
            }
            return false;
        } else {
            unsetFieldError(ecRelationshipInputLayout);
        }

        if (!StringUtil.isNullOrEmpty(addressEditText.getText().toString())
                || !StringUtil.isNullOrEmpty(stateEditText.getText().toString())
                || !StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())
                || !StringUtil.isNullOrEmpty(cityEditText.getText().toString())) {


            if (StringUtil.isNullOrEmpty(addressEditText.getText().toString())) {
                if (userInteraction) {
                    setError(address1TextInputLayout);
                }
                return false;
            } else {
                unsetFieldError(address1TextInputLayout);
            }

            if (StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())) {
                if (userInteraction) {
                    setError(zipCodeTextInputLayout);
                }
                return false;
            } else {
                unsetFieldError(zipCodeTextInputLayout);
            }

            if (!StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString().trim()) &&
                    !ValidationHelper.isValidString(zipCodeEditText.getText().toString().trim(),
                            ValidationHelper.ZIP_CODE_PATTERN)) {
                setError(zipCodeTextInputLayout, Label.getLabel("demographics_zip_code_validation_msg"),
                        userInteraction);
                return false;
            } else {
                unsetFieldError(zipCodeTextInputLayout);
            }

            if (StringUtil.isNullOrEmpty(cityEditText.getText().toString())) {
                if (userInteraction) {
                    setError(cityTextInputLayout);
                }
                return false;
            } else {
                unsetFieldError(cityTextInputLayout);
            }

            if (StringUtil.isNullOrEmpty(stateEditText.getText().toString())) {
                if (userInteraction) {
                    setError(stateTextInputLayout);
                }
                return false;
            } else {
                unsetFieldError(stateTextInputLayout);
            }
        }

        if (!StringUtil.isNullOrEmpty(emailEditText.getText().toString())
                && (!ValidationHelper.isValidEmail(emailEditText.getText().toString().trim()))) {
            setError(emailInputLayout, Label.getLabel("demographics_email_validation_msg"), userInteraction);
            return false;
        } else if (dataModel.getProperties().getEmailAddress().isRequired()
                && StringUtil.isNullOrEmpty(emailEditText.getText().toString())) {
            setError(emailInputLayout);
            return false;
        } else {
            unsetFieldError(emailInputLayout);
        }

        if (dataModel.getProperties().getDateOfBirth().isRequired()
                && StringUtil.isNullOrEmpty(dateOfBirthEditText.getText().toString())) {
            return false;
        }

        if (!StringUtil.isNullOrEmpty(dateOfBirthEditText.getText().toString().trim())) {
            String dateValidationResult = DateUtil
                    .getDateOfBirthValidationResultMessage(dateOfBirthEditText.getText().toString().trim());
            if (dateValidationResult != null) {
                setError(dateBirthTextInputLayout, dateValidationResult, userInteraction);
                return false;
            } else {
                unsetFieldError(dateBirthTextInputLayout);
            }
        } else {
            unsetFieldError(dateBirthTextInputLayout);
        }

        if (dataModel.getProperties().getGender().isRequired()
                && StringUtil.isNullOrEmpty(genderEditText.getText().toString())) {
            return false;
        }
        return true;
    }

    private boolean validatePhoneNumber(TextInputLayout textInputLayout,
                                        EditText editText,
                                        boolean userInteraction,
                                        boolean isRequired) {
        if (isRequired && StringUtil.isNullOrEmpty(editText.getText().toString())) {
            if (userInteraction) {
                setError(textInputLayout);
            }
            return true;
        }

        if (!StringUtil.isNullOrEmpty(editText.getText().toString().trim()) &&
                !ValidationHelper.isValidString(editText.getText().toString().trim(),
                        ValidationHelper.PHONE_NUMBER_PATTERN)) {
            setError(textInputLayout,
                    Label.getLabel("demographics_phone_number_validation_msg"), userInteraction);
            return true;
        } else {
            unsetFieldError(textInputLayout);
        }
        return false;
    }

    private DemographicDTO getUpdateModel() {
        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        updatableDemographicDTO.setPayload(new DemographicPayloadResponseDTO());
        updatableDemographicDTO.getPayload().setDemographics(new DemographicPayloadInfoDTO());
        updatableDemographicDTO.getPayload().getDemographics().setPayload(new DemographicPayloadDTO());
        updatableDemographicDTO.getPayload().getDemographics().getPayload()
                .setPersonalDetails(dto.getPayload().getDemographics().getPayload().getPersonalDetails());

        PatientModel emergencyContact = dto.getPayload().getDemographics()
                .getPayload().getEmergencyContact();
        if (emergencyContact == null) {
            emergencyContact = new PatientModel();
        }

        String firstName = firstNameEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(firstName)) {
            emergencyContact.setFirstName(firstName);
        }
        String middleName = middleNameEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(middleName)) {
            emergencyContact.setMiddleName(middleName);
        }
        String lastName = lastNameEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(lastName)) {
            emergencyContact.setLastName(lastName);
        }

        String primaryPhone = StringUtil.revertToRawFormat(primaryPhoneEditText.getText().toString().trim());
        if (!StringUtil.isNullOrEmpty(primaryPhone)) {
            emergencyContact.setPhoneNumber(primaryPhone);
        }
        String secondaryPhone = StringUtil.revertToRawFormat(secondaryPhoneEditText.getText().toString().trim());
        if (!StringUtil.isNullOrEmpty(secondaryPhone)) {
            emergencyContact.setSecondaryPhoneNumber(secondaryPhone);
        }

        String addressLine1 = addressEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(addressLine1)) {
            emergencyContact.getAddress().setAddress1(addressLine1);
        }
        String addressLine2 = addressEditText2.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(addressLine2)) {
            emergencyContact.getAddress().setAddress2(addressLine2);
        }
        String zipCode = zipCodeEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(zipCode)) {
            emergencyContact.getAddress().setZipcode(StringUtil.revertZipToRawFormat(zipCode));
        }
        String city = cityEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(city)) {
            emergencyContact.getAddress().setCity(city);
        }
        String state = stateEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(state)) {
            emergencyContact.getAddress().setState(state);
        }
        String email = emailEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(email)) {
            emergencyContact.setEmail(email);
        }
        String dateOfBirth = dateOfBirthEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(dateOfBirth)) {
            emergencyContact.setDateOfBirth(DateUtil.getInstance().setDateRaw(dateOfBirth, true)
                    .toStringWithFormatYyyyDashMmDashDd());
        }
        String gender = selectedGender.getName();
        if (!StringUtil.isNullOrEmpty(gender)) {
            emergencyContact.setGender(gender);
        }
        String relationShip = selectedRelationship.getName();
        if (!StringUtil.isNullOrEmpty(relationShip)) {
            emergencyContact.setEmergencyContactRelationship(relationShip);
        }
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setEmergencyContact(emergencyContact);

        return updatableDemographicDTO;
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
            dto.getPayload().getDemographics().getPayload()
                    .setEmergencyContact(updatedModel.getPayload().getDemographics()
                            .getPayload().getEmergencyContact());

            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
            if (getDialog() == null) {
                getActivity().onBackPressed();
            } else {
                dismiss();
            }
            callback.updateEmergencyContact(updatedModel.getPayload().getDemographics()
                    .getPayload().getEmergencyContact());
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
                    stateEditText.setText(smartyStreetsResponse.getStateAbbreviation());
                    stateEditText.getOnFocusChangeListener().onFocusChange(stateEditText,
                            !StringUtil.isNullOrEmpty(stateEditText.getText().toString().trim()));
                    cityEditText.getOnFocusChangeListener().onFocusChange(cityEditText,
                            !StringUtil.isNullOrEmpty(cityEditText.getText().toString().trim()));
                    unsetFieldError(zipCodeTextInputLayout);
                    checkIfEnableButton(false);
                }
            }


        }.execute(zipCode);
    }

    protected View.OnClickListener getOptionsListener(final List<DemographicsOption> options,
                                                      final CheckInDemographicsBaseFragment.OnOptionSelectedListener listener,
                                                      final String title) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(getContext(), options, title, listener);
            }
        };
    }

    private void showChooseDialog(Context context,
                                  List<DemographicsOption> options,
                                  String title,
                                  final CheckInDemographicsBaseFragment.OnOptionSelectedListener listener) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        // add cancel button
        dialog.setNegativeButton(Label.getLabel("demographics_cancel_label"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pos) {
                dialogInterface.dismiss();
            }
        });

        // create dialog layout
        View customView = LayoutInflater.from(context).inflate(R.layout.alert_list_layout, null, false);
        dialog.setView(customView);
        TextView titleTextView = (TextView) customView.findViewById(R.id.title_view);
        titleTextView.setText(title);
        titleTextView.setVisibility(View.VISIBLE);


        // create the adapter
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomOptionsAdapter customOptionsAdapter = new CustomOptionsAdapter(context, options);
        listView.setAdapter(customOptionsAdapter);


        final AlertDialog alert = dialog.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();

        // set item click listener
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long row) {
                DemographicsOption selectedOption = (DemographicsOption) adapterView.getAdapter()
                        .getItem(position);
                if (listener != null) {
                    listener.onOptionSelected(selectedOption);
                }
                alert.dismiss();
            }
        };
        listView.setOnItemClickListener(clickListener);
    }

    protected TextWatcher dateInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            lastLength = sequence.length();
        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringUtil.autoFormatDateOfBirth(editable, lastLength);
            checkIfEnableButton(false);
        }
    };

    protected void checkIfEnableButton(boolean userInteraction) {
        boolean isEnabled = passConstraints(userInteraction);
        if (saveButton != null) {
            saveButton.setClickable(isEnabled);
            saveButton.setSelected(isEnabled);
        }
    }

    protected TextWatcher getValidateRequiredEmptyTextWatcher(final TextInputLayout inputLayout) {
        return new TextWatcher() {
            public int count;

            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
                this.count = sequence.length();
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString())) {
                    inputLayout.setErrorEnabled(true);
                    inputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
                } else if (count == 0) {
                    unsetFieldError(inputLayout);
                }
                checkIfEnableButton(false);
            }
        };
    }

    private TextWatcher getEmptyValidatorWatcher(final TextInputLayout inputLayout, final boolean checkEnable) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    unsetFieldError(inputLayout);
                }
                if (checkEnable) {
                    checkIfEnableButton(false);
                }
            }
        };
    }

    protected TextWatcher getValidateOptionalEmptyTextWatcher(final View optionalView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString())) {
                    optionalView.setVisibility(View.VISIBLE);
                } else {
                    optionalView.setVisibility(View.GONE);
                }
                checkIfEnableButton(false);
            }
        };
    }

    protected TextWatcher phoneInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            lastLength = sequence.length();
        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringUtil.autoFormatPhone(editable, lastLength);
        }
    };

    protected TextWatcher zipInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            lastLength = sequence.length();
        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringUtil.autoFormatZipcode(editable, lastLength);
            checkIfEnableButton(false);
        }
    };

    private void setError(TextInputLayout textInputLayout) {
        setError(textInputLayout, Label.getLabel("demographics_required_validation_msg"), true);
    }

    private void setError(TextInputLayout inputLayout, String errorMessage, boolean requestFocus) {
        if (!inputLayout.isErrorEnabled()) {
            inputLayout.setError(errorMessage);
            inputLayout.setErrorEnabled(true);
        }
        if (requestFocus) {
            inputLayout.clearFocus();
            inputLayout.requestFocus();
            if (!inputLayout.hasFocus()) {
                parentScrollView.scrollTo(0, inputLayout.getScrollY());
            }
        }
    }

    private void unsetFieldError(TextInputLayout textInputLayout) {
        textInputLayout.setErrorEnabled(false);
        textInputLayout.setError(null);
    }

    private View.OnClickListener selectEndOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText editText = (EditText) view;
            editText.setSelection(editText.length());
        }
    };
}
