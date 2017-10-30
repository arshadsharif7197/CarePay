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
import com.carecloud.carepaylibray.demographics.EmergencyContactInterface;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.smartystreets.api.us_zipcode.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 30/10/17.
 */

public class EmergencyContactFragment extends BaseDialogFragment {

    private EmergencyContactInterface callback;
    private DemographicsSettingsDTO dto;
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
    private EditText socialSecurityEditText;

    public EmergencyContactFragment() {

    }

    public static EmergencyContactFragment newInstance() {
        return new EmergencyContactFragment();
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
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        dto = (DemographicsSettingsDTO) callback.getDto();
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
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("demographics_additional"));
        toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
        callback.setToolbar(toolbar);
        setUpView(view);
    }

    private void setUpView(View view) {

        Button saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmergencyContactInfo();
            }
        });

        DemographicPayloadDTO demographicPayload = dto.getPayload().getDemographics().getPayload();

        PatientModel emergencyContact = demographicPayload.getEmergencyContact();
        DemographicDataModel dataModel = dto.getMetadata().getNewDataModel();

        if (emergencyContact != null) {
            TextInputLayout firstNameInputLayout = (TextInputLayout) view
                    .findViewById(R.id.firstNameInputLayout);
            firstNameEditText = (EditText) view.findViewById(R.id.firstNameEditText);
            firstNameEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(firstNameInputLayout, null));
            firstNameEditText.setText(emergencyContact.getFirstName());
            firstNameEditText.getOnFocusChangeListener().onFocusChange(firstNameEditText,
                    !StringUtil.isNullOrEmpty(firstNameEditText.getText().toString().trim()));

            TextInputLayout middleNameInputLayout = (TextInputLayout) view
                    .findViewById(R.id.middleNameInputLayout);
            middleNameEditText = (EditText) view.findViewById(R.id.middleNameEditText);
            middleNameEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(middleNameInputLayout, null));
            middleNameEditText.setText(emergencyContact.getMiddleName());
            middleNameEditText.getOnFocusChangeListener().onFocusChange(middleNameEditText,
                    !StringUtil.isNullOrEmpty(middleNameEditText.getText().toString().trim()));

            TextInputLayout lastNameInputLayout = (TextInputLayout) view
                    .findViewById(R.id.lastNameInputLayout);
            lastNameEditText = (EditText) view.findViewById(R.id.lastNameEditText);
            lastNameEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(lastNameInputLayout, null));
            lastNameEditText.setText(emergencyContact.getLastName());
            lastNameEditText.getOnFocusChangeListener().onFocusChange(lastNameEditText,
                    !StringUtil.isNullOrEmpty(lastNameEditText.getText().toString().trim()));

            TextInputLayout primaryPhoneTextInputLayout = (TextInputLayout) view
                    .findViewById(R.id.primaryPhoneTextInputLayout);
            primaryPhoneEditText = (EditText) view.findViewById(R.id.primaryPhoneEditText);
            primaryPhoneEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(primaryPhoneTextInputLayout, null));
            if (emergencyContact.getPhones().size() > 0) {
                primaryPhoneEditText.setText(emergencyContact.getPhones().get(0));
            }
            primaryPhoneEditText.getOnFocusChangeListener().onFocusChange(primaryPhoneEditText,
                    !StringUtil.isNullOrEmpty(primaryPhoneEditText.getText().toString().trim()));

            TextInputLayout secondaryPhoneTextInputLayout = (TextInputLayout) view
                    .findViewById(R.id.secondaryPhoneTextInputLayout);
            secondaryPhoneEditText = (EditText) view.findViewById(R.id.secondaryPhoneEditText);
            secondaryPhoneEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(secondaryPhoneTextInputLayout, null));
            if (emergencyContact.getPhones().size() > 1) {
                secondaryPhoneEditText.setText(emergencyContact.getPhones().get(1));
            }
            secondaryPhoneEditText.getOnFocusChangeListener().onFocusChange(secondaryPhoneEditText,
                    !StringUtil.isNullOrEmpty(secondaryPhoneEditText.getText().toString().trim()));

            TextInputLayout address1TextInputLayout = (TextInputLayout) view
                    .findViewById(R.id.address1TextInputLayout);
            addressEditText = (EditText) view.findViewById(R.id.addressEditText);
            addressEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(address1TextInputLayout, null));
            addressEditText.setText(emergencyContact.getAddress().getAddress1());


            TextInputLayout address2TextInputLayout = (TextInputLayout) view
                    .findViewById(R.id.address2TextInputLayout);
            addressEditText2 = (EditText) view.findViewById(R.id.addressEditText2);
            addressEditText2.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(address2TextInputLayout, null));
            addressEditText2.setText(emergencyContact.getAddress().getAddress2());


            TextInputLayout zipCodeTextInputLayout = (TextInputLayout) view
                    .findViewById(R.id.zipCodeTextInputLayout);
            zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeTextView);
            zipCodeEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(zipCodeTextInputLayout,
                    getZipCodeFocusListener(zipCodeEditText)));
            zipCodeEditText.setText(emergencyContact.getAddress().getZipcode());


            TextInputLayout cityTextInputLayout = (TextInputLayout) view
                    .findViewById(R.id.cityTextInputLayout);
            cityEditText = (EditText) view.findViewById(R.id.cityTextView);
            cityEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(cityTextInputLayout, null));
            cityEditText.setText(emergencyContact.getAddress().getCity());


            TextInputLayout stateTextInputLayout = (TextInputLayout) view
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

            TextInputLayout emailInputLayout = (TextInputLayout) view.findViewById(R.id.emailInputLayout);
            emailEditText = (EditText) view.findViewById(R.id.emailEditText);
            emailEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(emailInputLayout, null));
            emailEditText.setText(emergencyContact.getEmail());
            emailEditText.getOnFocusChangeListener().onFocusChange(emailEditText,
                    !StringUtil.isNullOrEmpty(emailEditText.getText().toString().trim()));

            TextInputLayout socialSecurityInputLayout = (TextInputLayout) view.findViewById(R.id.socialSecurityInputLayout);
            socialSecurityEditText = (EditText) view.findViewById(R.id.socialSecurityEditText);
            socialSecurityEditText.setOnFocusChangeListener(SystemUtil
                    .getHintFocusChangeListener(socialSecurityInputLayout, null));
            socialSecurityEditText.setText(emergencyContact.getSocialSecurityNumber());
            socialSecurityEditText.getOnFocusChangeListener().onFocusChange(socialSecurityEditText,
                    !StringUtil.isNullOrEmpty(socialSecurityEditText.getText().toString().trim()));

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
                                }
                            },
                            Label.getLabel("demographics_review_gender")));
            genderEditText.setText(emergencyContact.getGender());

        }
    }

    private void updateEmergencyContactInfo() {
        if (passConstraints()) {
            Map<String, String> header = new HashMap<>();
            DemographicDTO updateModel = getUpdateModel();
            Gson gson = new Gson();
            String jsonPayload = gson.toJson(updateModel.getPayload().getDemographics().getPayload());

            TransitionDTO updateDemographics = dto.getMetadata().getTransitions().getUpdateDemographics();
            getWorkflowServiceHelper().execute(updateDemographics, updateDemographicsCallback, jsonPayload, header);
        }
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
        emergencyContact.setEmployer(null);

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
        List<String> phones = new ArrayList<>();
        String primaryPhone = primaryPhoneEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(primaryPhone)) {
            phones.add(primaryPhone);
        }
        String secondaryPhone = secondaryPhoneEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(secondaryPhone)) {
            phones.add(secondaryPhone);
        }
        emergencyContact.setPhones(phones);
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
            emergencyContact.getAddress().setZipcode(zipCode);
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
        String ssn = socialSecurityEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(ssn)) {
            emergencyContact.setSocialSecurityNumber(ssn);
        }
        String dateOfBirth = dateOfBirthEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(dateOfBirth)) {
            emergencyContact.setDateOfBirth(dateOfBirth);
        }
        String gender = genderEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(gender)) {
            emergencyContact.setGender(gender);
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
            DemographicsSettingsDTO updatedModel = DtoHelper
                    .getConvertedDTO(DemographicsSettingsDTO.class, workflowDTO);
            dto.getPayload().getDemographics().getPayload()
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

    private boolean passConstraints() {
        return true;
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
                    stateEditText.setText(smartyStreetsResponse.getStateAbbreviation());
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
}
