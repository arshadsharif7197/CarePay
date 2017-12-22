package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicEmergencyContactSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.interfaces.EmergencyContactInterface;
import com.carecloud.carepaylibray.utils.AddressUtil;
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
    private EditText emergencyContactRelationshipEditText;
    private Button saveButton;
    private TextInputLayout emailInputLayout;
    private TextInputLayout zipCodeTextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.emergency_contact_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deleteEmergencyContact) {
            dto.getPayload().getDemographics().getPayload().setEmergencyContact(null);
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpView(View view) {

        DemographicPayloadDTO demographicPayload = dto.getPayload().getDemographics().getPayload();
        PatientModel emergencyContact = demographicPayload.getEmergencyContact();

        saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmergencyContactInfo();
            }
        });
        if (!isEmptyEC(emergencyContact)) {
            saveButton.setText(Label.getLabel("demographics_save_changes_button"));
        } else {
            emergencyContact = new PatientModel();
        }

        TextInputLayout firstNameInputLayout = (TextInputLayout) view
                .findViewById(R.id.firstNameInputLayout);
        firstNameEditText = (EditText) view.findViewById(R.id.firstNameEditText);
        firstNameEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(firstNameInputLayout, null));
        firstNameEditText.setText(emergencyContact.getFirstName());
        firstNameEditText.getOnFocusChangeListener().onFocusChange(firstNameEditText,
                !StringUtil.isNullOrEmpty(firstNameEditText.getText().toString().trim()));
        firstNameEditText.addTextChangedListener(getValidateRequiredEmptyTextWatcher(firstNameInputLayout));

        TextInputLayout middleNameInputLayout = (TextInputLayout) view
                .findViewById(R.id.middleNameInputLayout);
        middleNameEditText = (EditText) view.findViewById(R.id.middleNameEditText);
        middleNameEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(middleNameInputLayout, null));
        middleNameEditText.setText(emergencyContact.getMiddleName());
        middleNameEditText.getOnFocusChangeListener().onFocusChange(middleNameEditText,
                !StringUtil.isNullOrEmpty(middleNameEditText.getText().toString().trim()));

        DemographicDataModel dataModel = dto.getMetadata().getNewDataModel();
        boolean middleNameRequired = dataModel.getDemographic().getEmergencyContact().getProperties()
                .getMiddleName().isRequired();
        if (middleNameRequired) {
            middleNameEditText.addTextChangedListener(getValidateRequiredEmptyTextWatcher(middleNameInputLayout));
        } else {
            View optionalView = view.findViewById(R.id.middleNameOptionalTextView);
            middleNameEditText.addTextChangedListener(getValidateOptionalEmptyTextWatcher(optionalView));
            optionalView.setVisibility(StringUtil
                    .isNullOrEmpty(emergencyContact.getMiddleName()) ? View.VISIBLE : View.GONE);
        }

        TextInputLayout lastNameInputLayout = (TextInputLayout) view
                .findViewById(R.id.lastNameInputLayout);
        lastNameEditText = (EditText) view.findViewById(R.id.lastNameEditText);
        lastNameEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(lastNameInputLayout, null));
        lastNameEditText.setText(emergencyContact.getLastName());
        lastNameEditText.getOnFocusChangeListener().onFocusChange(lastNameEditText,
                !StringUtil.isNullOrEmpty(lastNameEditText.getText().toString().trim()));
        lastNameEditText.addTextChangedListener(getValidateRequiredEmptyTextWatcher(lastNameInputLayout));

        TextInputLayout primaryPhoneTextInputLayout = (TextInputLayout) view
                .findViewById(R.id.primaryPhoneTextInputLayout);
        primaryPhoneEditText = (EditText) view.findViewById(R.id.primaryPhoneEditText);
        primaryPhoneEditText.addTextChangedListener(phoneInputFormatter);
        primaryPhoneEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(primaryPhoneTextInputLayout, null));
        primaryPhoneEditText.setText(StringUtil.formatPhoneNumber(emergencyContact.getPhoneNumber()));

        primaryPhoneEditText.getOnFocusChangeListener().onFocusChange(primaryPhoneEditText,
                !StringUtil.isNullOrEmpty(primaryPhoneEditText.getText().toString().trim()));
        primaryPhoneEditText.addTextChangedListener(getValidateRequiredEmptyTextWatcher(primaryPhoneTextInputLayout));

        TextInputLayout secondaryPhoneTextInputLayout = (TextInputLayout) view
                .findViewById(R.id.secondaryPhoneTextInputLayout);
        secondaryPhoneEditText = (EditText) view.findViewById(R.id.secondaryPhoneEditText);
        secondaryPhoneEditText.addTextChangedListener(phoneInputFormatter);
        secondaryPhoneEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(secondaryPhoneTextInputLayout, null));
        secondaryPhoneEditText.setText(StringUtil.formatPhoneNumber(emergencyContact.getSecondaryPhoneNumber()));

        secondaryPhoneEditText.getOnFocusChangeListener().onFocusChange(secondaryPhoneEditText,
                !StringUtil.isNullOrEmpty(secondaryPhoneEditText.getText().toString().trim()));
        boolean secondaryPhoneRequired = dataModel.getDemographic().getEmergencyContact().getProperties()
                .getSecondaryPhoneNumber().isRequired();
        if (secondaryPhoneRequired) {
            secondaryPhoneEditText.addTextChangedListener(getValidateRequiredEmptyTextWatcher(secondaryPhoneTextInputLayout));
        } else {
            View optionalView = view.findViewById(R.id.secondaryPhoneOptionalTextView);
            secondaryPhoneEditText.addTextChangedListener(getValidateOptionalEmptyTextWatcher(optionalView));
            optionalView.setVisibility(StringUtil
                    .isNullOrEmpty(secondaryPhoneEditText.getText().toString()) ? View.VISIBLE : View.GONE);
        }

        TextInputLayout address2TextInputLayout = (TextInputLayout) view
                .findViewById(R.id.address2TextInputLayout);
        addressEditText2 = (EditText) view.findViewById(R.id.addressEditText2);
        addressEditText2.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(address2TextInputLayout, null));
        addressEditText2.setText(emergencyContact.getAddress().getAddress2());
        addressEditText2.getOnFocusChangeListener().onFocusChange(addressEditText2,
                !StringUtil.isNullOrEmpty(addressEditText2.getText().toString().trim()));

        zipCodeTextInputLayout = (TextInputLayout) view
                .findViewById(R.id.zipCodeTextInputLayout);
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeTextView);
        zipCodeEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(zipCodeTextInputLayout,
                getZipCodeFocusListener(zipCodeEditText)));
        zipCodeEditText.setText(StringUtil.formatZipCode(emergencyContact.getAddress().getZipcode()));
        zipCodeEditText.addTextChangedListener(zipInputFormatter);
        zipCodeEditText.getOnFocusChangeListener().onFocusChange(zipCodeEditText,
                !StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString().trim()));

        cityTextInputLayout = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        cityEditText = (EditText) view.findViewById(R.id.cityTextView);
        cityEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(cityTextInputLayout, null));
        cityEditText.setText(emergencyContact.getAddress().getCity());
        cityEditText.getOnFocusChangeListener().onFocusChange(cityEditText,
                !StringUtil.isNullOrEmpty(cityEditText.getText().toString().trim()));
        cityEditText.addTextChangedListener(getEmptyValidatorWatcher(cityTextInputLayout, true));

        stateTextInputLayout = (TextInputLayout) view
                .findViewById(R.id.stateTextInputLayout);
        stateEditText = (EditText) view.findViewById(R.id.stateTextView);
        stateEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(stateTextInputLayout, null));
        stateEditText.setText(emergencyContact.getAddress().getState());
        stateEditText.setOnClickListener(
                getOptionsListener(dataModel.getDemographic().getAddress().getProperties()
                                .getState().getOptions(),
                        new CheckInDemographicsBaseFragment.OnOptionSelectedListener() {
                            @Override
                            public void onOptionSelected(DemographicsOption option) {
                                stateEditText.setText(option.getLabel());
                            }
                        },
                        Label.getLabel("demographics_documents_title_select_state")));
        stateEditText.getOnFocusChangeListener().onFocusChange(stateEditText,
                !StringUtil.isNullOrEmpty(stateEditText.getText().toString().trim()));
        stateEditText.addTextChangedListener(getEmptyValidatorWatcher(stateTextInputLayout, true));

        emailInputLayout = (TextInputLayout) view.findViewById(R.id.emailInputLayout);
        emailEditText = (EditText) view.findViewById(R.id.emailEditText);
        emailEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(emailInputLayout, null));
        emailEditText.setText(emergencyContact.getEmail());
        emailEditText.getOnFocusChangeListener().onFocusChange(emailEditText,
                !StringUtil.isNullOrEmpty(emailEditText.getText().toString().trim()));
        emailEditText.addTextChangedListener(getEmptyValidatorWatcher(emailInputLayout, true));

        TextInputLayout dateBirthTextInput = (TextInputLayout) view.findViewById(R.id.dateOfBirthInputLayout);
        dateOfBirthEditText = (EditText) view.findViewById(R.id.dateOfBirthEditText);
        dateOfBirthEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(dateBirthTextInput, null));
        dateOfBirthEditText.addTextChangedListener(dateInputFormatter);
        String dateString = emergencyContact.getFormattedDateOfBirth();
        dateOfBirthEditText.setText(dateString);
        dateOfBirthEditText.getOnFocusChangeListener().onFocusChange(dateOfBirthEditText,
                !StringUtil.isNullOrEmpty(dateString));

        TextInputLayout genderInputLayout = (TextInputLayout) view.findViewById(R.id.genderInputLayout);
        genderEditText = (EditText) view.findViewById(R.id.genderEditText);
        genderEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(genderInputLayout, null));
        genderEditText.setOnClickListener(
                getOptionsListener(dataModel.getDemographic()
                                .getPersonalDetails().getProperties().getGender().getOptions(),
                        new CheckInDemographicsBaseFragment.OnOptionSelectedListener() {
                            @Override
                            public void onOptionSelected(DemographicsOption option) {
                                genderEditText.setText(option.getLabel());
                                genderEditText.getOnFocusChangeListener().onFocusChange(genderEditText,
                                        !StringUtil.isNullOrEmpty(genderEditText.getText().toString()));
                            }
                        },
                        Label.getLabel("demographics_review_gender")));
        genderEditText.setText(emergencyContact.getGender());
        genderEditText.getOnFocusChangeListener().onFocusChange(genderEditText,
                !StringUtil.isNullOrEmpty(emergencyContact.getGender()));

        TextInputLayout emergencyContactRelationshipInputLayout = (TextInputLayout) view
                .findViewById(R.id.emergencyContactRelationshipInputLayout);
        emergencyContactRelationshipEditText = (EditText) view.findViewById(R.id.emergencyContactRelationshipEditText);
        emergencyContactRelationshipEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(emergencyContactRelationshipInputLayout, null));
        emergencyContactRelationshipEditText.setOnClickListener(
                getOptionsListener(dataModel.getDemographic()
                                .getEmergencyContact().getProperties().getRelationshipType().getOptions(),
                        new CheckInDemographicsBaseFragment.OnOptionSelectedListener() {
                            @Override
                            public void onOptionSelected(DemographicsOption option) {
                                emergencyContactRelationshipEditText.setText(option.getLabel());
                                emergencyContactRelationshipEditText.getOnFocusChangeListener()
                                        .onFocusChange(emergencyContactRelationshipEditText,
                                                !StringUtil.isNullOrEmpty(emergencyContactRelationshipEditText.getText().toString()));
                                checkIfEnableButton();
                            }
                        },
                        Label.getLabel("demographics_emergency_contact_relationship")));
        emergencyContactRelationshipEditText.setText(emergencyContact.getEmergencyContactRelationship());
        emergencyContactRelationshipEditText.getOnFocusChangeListener().onFocusChange(emergencyContactRelationshipEditText,
                !StringUtil.isNullOrEmpty(emergencyContact.getEmergencyContactRelationship()));

        TextInputLayout address1TextInputLayout = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        addressEditText = (EditText) view.findViewById(R.id.addressEditText);
        addressEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(address1TextInputLayout, null));
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
                    zipCodeTextInputLayout.setErrorEnabled(false);
                    zipCodeTextInputLayout.setError(null);
                    cityTextInputLayout.setErrorEnabled(false);
                    cityTextInputLayout.setError(null);
                    stateTextInputLayout.setErrorEnabled(false);
                    stateTextInputLayout.setError(null);
                } else {
                    addressEditText2.setEnabled(true);

                }
                checkIfEnableButton();
            }
        });
        addressEditText.setText(emergencyContact.getAddress().getAddress1());
        addressEditText.getOnFocusChangeListener()
                .onFocusChange(addressEditText, !StringUtil.isNullOrEmpty(addressEditText
                        .getText().toString()));

        view.findViewById(R.id.additionalInfoOptionalTextView).setVisibility(View.VISIBLE);
        view.findViewById(R.id.addressInfoOptionalTextView).setVisibility(View.VISIBLE);

        checkIfEnableButton();
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
                queries.put("practice_mgmt", dto.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeMgmt());
                queries.put("practice_id", dto.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeId());
                queries.put("appointment_id", dto.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getAppointmentId());
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
            return false;
        }
        if (dataModel.getProperties().getMiddleName().isRequired()
                && StringUtil.isNullOrEmpty(middleNameEditText.getText().toString())) {
            return false;
        }
        if (StringUtil.isNullOrEmpty(lastNameEditText.getText().toString())) {
            return false;
        }
        if (!StringUtil.isNullOrEmpty(emailEditText.getText().toString())
                && (!ValidationHelper.isValidEmail(emailEditText.getText().toString().trim()))) {
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError(Label.getLabel("demographics_email_validation_msg"));
            return false;
        } else if (dataModel.getProperties().getEmailAddress().isRequired()
                && StringUtil.isNullOrEmpty(emailEditText.getText().toString())) {
            emailInputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
            return false;
        }
        if (dataModel.getProperties().getDateOfBirth().isRequired()
                && StringUtil.isNullOrEmpty(dateOfBirthEditText.getText().toString())) {
            return false;
        }
        if (dataModel.getProperties().getGender().isRequired()
                && StringUtil.isNullOrEmpty(genderEditText.getText().toString())) {
            return false;
        }
        if (StringUtil.isNullOrEmpty(primaryPhoneEditText.getText().toString())) {
            return false;
        }
        if (dataModel.getProperties().getSecondaryPhoneNumber().isRequired()
                && StringUtil.isNullOrEmpty(secondaryPhoneEditText.getText().toString())) {
            return false;
        }
        if (StringUtil.isNullOrEmpty(emergencyContactRelationshipEditText.getText().toString())) {
            return false;
        }
        if (!StringUtil.isNullOrEmpty(addressEditText.getText().toString())
                || !StringUtil.isNullOrEmpty(stateEditText.getText().toString())
                || !StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())
                || !StringUtil.isNullOrEmpty(cityEditText.getText().toString())) {


            if (StringUtil.isNullOrEmpty(addressEditText.getText().toString())) {
//                if (userInteraction) {
//                    add.setErrorEnabled(true);
//                    zipCodeTextInputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
//                    employerAddressEditText.requestFocus();
//                } else {
//                    unsetFieldError(view, R.id.address1TextInputLayout);
//                    showErrorViews(false, (ViewGroup) view.findViewById(R.id.address1DemographicsLayout));
//                }
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
        return true;
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
            emergencyContact.setDateOfBirth(dateOfBirth);
        }
        String gender = genderEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(gender)) {
            emergencyContact.setGender(gender);
        }
        String relationShip = emergencyContactRelationshipEditText.getText().toString().trim();
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
                    zipCodeTextInputLayout.setError(null);
                    zipCodeTextInputLayout.setErrorEnabled(false);
                    checkIfEnableButton();
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
        }
    };

    protected void checkIfEnableButton() {
        boolean isEnabled = passConstraints(false);
        if (saveButton != null) {
            saveButton.setEnabled(isEnabled);
            saveButton.setClickable(isEnabled);
            saveButton.setSelected(isEnabled);
        }
    }

    protected TextWatcher getValidateRequiredEmptyTextWatcher(final TextInputLayout inputLayout) {
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
                    inputLayout.setErrorEnabled(true);
                    inputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
                } else {
                    inputLayout.setError(null);
                    inputLayout.setErrorEnabled(false);
                }
                checkIfEnableButton();
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
                    inputLayout.setError(null);
                    inputLayout.setErrorEnabled(false);
                }
                if (checkEnable) {
                    checkIfEnableButton();
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
                checkIfEnableButton();
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
            checkIfEnableButton();
        }
    };
}