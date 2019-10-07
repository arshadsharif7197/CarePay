package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateNameFragment extends DemographicsBaseSettingsFragment {

    private DemographicDTO demographicsSettingsDTO;
    private DemographicDataModel dataModel;

    private Button updateProfileButton;
    private EditText firstName;
    private EditText lastName;
    private EditText middleName;

    private DemographicsSettingsFragmentListener callback;

    public static UpdateNameFragment newInstance() {
        return new UpdateNameFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DemographicsSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DemographicsSettingsFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicDTO) callback.getDto();
        dataModel = demographicsSettingsDTO.getMetadata().getNewDataModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_name_update, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("setting_change_name"));
        toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
        callback.setToolbar(toolbar);

        updateProfileButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateName();
            }
        });
        updateProfileButton.setEnabled(false);
        updateProfileButton.setClickable(false);

        initViews(view);

    }

    private void initViews(View view) {
        DemographicPayloadDTO demographicPayload = demographicsSettingsDTO.getPayload().getDemographics().getPayload();

        TextInputLayout firstNameLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrFirstNameTextInput);
        firstName = (EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit);
        firstName.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(firstNameLayout, null));
        firstName.setText(demographicPayload.getPersonalDetails().getFirstName());
        firstName.getOnFocusChangeListener().onFocusChange(firstName,
                !StringUtil.isNullOrEmpty(firstName.getText().toString().trim()));
        setVisibility(firstNameLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getFirstName().isDisplayed());
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getFirstName().isRequired()) {
            firstName.addTextChangedListener(getValidateEmptyTextWatcher(firstNameLayout));
        }


        TextInputLayout lastNameLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrLastNameTextInput);
        lastName = (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit);
        lastName.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(lastNameLayout, null));
        lastName.setText(demographicPayload.getPersonalDetails().getLastName());
        lastName.getOnFocusChangeListener().onFocusChange(lastName,
                !StringUtil.isNullOrEmpty(lastName.getText().toString().trim()));
        setVisibility(lastNameLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getLastName().isDisplayed());
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getLastName().isRequired()) {
            lastName.addTextChangedListener(getValidateEmptyTextWatcher(lastNameLayout));
        }


        TextInputLayout middleNameLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrMiddleNameTextInputLayout);
        middleName = (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        middleName.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(middleNameLayout, null));
        middleName.setText(demographicPayload.getPersonalDetails().getMiddleName());
        middleName.getOnFocusChangeListener().onFocusChange(middleName,
                !StringUtil.isNullOrEmpty(middleName.getText().toString().trim()));
        setVisibility(middleNameLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getMiddleName().isDisplayed());
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getMiddleName().isRequired()) {
            middleName.addTextChangedListener(getValidateEmptyTextWatcher(middleNameLayout));
            View middleNameOptional = view.findViewById(R.id.middleNameRequired);
            middleNameOptional.setVisibility(View.GONE);
        } else {
            middleName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence sequence, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    checkIfEnableButton(false);
                }
            });
        }

    }


    private DemographicDTO getUpdateModel() {
        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        updatableDemographicDTO.setPayload(new DemographicPayloadResponseDTO());
        updatableDemographicDTO.getPayload().setDemographics(new DemographicPayloadInfoDTO());
        updatableDemographicDTO.getPayload().getDemographics().setPayload(new DemographicPayloadDTO());

        //add all demographic info
        PatientModel patientModel = demographicsSettingsDTO.getPayload().getDemographics().getPayload().getPersonalDetails();

        String firstNameString = firstName.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(firstNameString)) {
            patientModel.setFirstName(firstNameString);
        }

        String middleNameString = middleName.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(middleNameString)) {
            patientModel.setMiddleName(middleNameString);
        }

        String lastNameString = lastName.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(lastNameString)) {
            patientModel.setLastName(lastNameString);
        }

        updatableDemographicDTO.getPayload().getDemographics().getPayload().setPersonalDetails(patientModel);

        return updatableDemographicDTO;
    }


    private void updateName() {
        if (passConstraints(false)) {
            DemographicDTO updateModel = getUpdateModel();
            Gson gson = new Gson();
            String jsonPayload = gson.toJson(updateModel.getPayload().getDemographics().getPayload());

            TransitionDTO updateDemographics = demographicsSettingsDTO.getMetadata().getTransitions().getUpdateDemographics();
            getWorkflowServiceHelper().execute(updateDemographics, updateProfileCallback, jsonPayload);
        }

    }

    private WorkflowServiceCallback updateProfileCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            DemographicDTO updatedModel = DtoHelper.getConvertedDTO(DemographicDTO.class, workflowDTO);
            PatientModel personalDetails = updatedModel.getPayload().getDemographics().getPayload().getPersonalDetails();
            demographicsSettingsDTO.getPayload().getDemographics().getPayload().setPersonalDetails(personalDetails);
            ApplicationPreferences.getInstance().setUserFullName(StringUtil
                    .getCapitalizedUserName(personalDetails.getFirstName(), personalDetails.getLastName()));
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));

            MixPanelUtil.logEvent(getString(R.string.event_change_name));

            getActivity().onBackPressed();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    protected void checkIfEnableButton(boolean userInteraction) {
        boolean isEnabled = passConstraints(false);
        if (updateProfileButton != null) {
            updateProfileButton.setEnabled(isEnabled);
            updateProfileButton.setClickable(isEnabled);
        }
    }

    @Override
    protected boolean passConstraints(boolean isUserInteraction) {
        View view = getView();
        if (view == null) {
            return false;
        }

        if (dataModel.getDemographic().getPersonalDetails().getProperties().getFirstName().isRequired()
                && checkTextEmptyValue(R.id.reviewdemogrFirstNameEdit, view)) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getLastName().isRequired()
                && checkTextEmptyValue(R.id.reviewdemogrLastNameEdit, view)) {
            return false;
        }
        if (dataModel.getDemographic().getPersonalDetails().getProperties().getMiddleName().isRequired()
                && checkTextEmptyValue(R.id.reviewdemogrMiddleNameEdit, view)) {
            return false;
        }

        return true;
    }
}
