package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsField;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsPersonalSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.DemographicMetadataDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.transitions.DemographicTransitionsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsFirstNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLastNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsSettingsUpdateNameFragment extends BaseFragment {

    private DemographicDTO demographicsSettingsDTO;
    private String firstNameString = null;
    private String middleNameString = null;
    private String lastNameString = null;
    private EditText firstNameEditText = null;
    private EditText middleNameEditText = null;
    private EditText lastNameEditText = null;
    private Button updateProfileButton = null;
    private TextInputLayout firstNameLabel = null;
    private TextInputLayout middleNameLabel = null;
    private TextInputLayout lastNameLabel = null;
    private LinearLayout rootView;

    private boolean isFirstNameEmpty;
    private boolean isLastNameEmpty;

    private DemographicsSettingsPersonalDetailsDTO demographicsSettingsDetailsDTO = null;
    private DemographicsSettingsFirstNameDTO demographicsSettingsFirstNameDTO = null;
    private DemographicsSettingsLastNameDTO demographicsSettingsLastNameDTO = null;
    private DemographicsSettingsFragmentListener callback;

    public static DemographicsSettingsUpdateNameFragment newInstance() {
        return new DemographicsSettingsUpdateNameFragment();
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
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_name_update, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = (LinearLayout) view.findViewById(R.id.demographicsReviewRootLayout);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("setting_change_name"));
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.demographicReviewProgressBar);
        progressBar.setVisibility(View.GONE);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
        callback.setToolbar(toolbar);
        firstNameEditText = (EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit);
        middleNameEditText = (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        lastNameEditText = (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit);
        updateProfileButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);

        initialiseUIFields(view);
        getProfileProperties();
        setEditTexts();

        getPersonalDetails();
        updateProfileButton.setText(Label.getLabel("demographics_save_changes_label"));
        setClickables();
        formatEditText();
        isFirstNameEmpty = true;
        isLastNameEmpty = true;
    }

    private void initialiseUIFields(View view) {
        firstNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrFirstNameTextInput);
        middleNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrMiddleNameTextInputLayout);
        lastNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrLastNameTextInput);
    }

    private void setEditTexts() {

        firstNameLabel.setTag(firstNameString);
        firstNameEditText.setTag(firstNameLabel);

        middleNameLabel.setTag(middleNameString);
        middleNameEditText.setTag(middleNameLabel);

        lastNameLabel.setTag(lastNameString);
        lastNameEditText.setTag(lastNameLabel);

        setChangeFocusListeners();
    }

    private void setChangeFocusListeners() {
        firstNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
        middleNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        lastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });


    }

    private void getProfileProperties() {
        DemographicMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getMetadata();
        if (demographicsSettingsMetadataDTO != null) {
            DemographicDataModel demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getNewDataModel();
            if (demographicsSettingsDataModelsDTO != null) {
                DemographicDataModel.Demographic demographicsSettingsDetailsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                if (demographicsSettingsDetailsDTO != null) {
                    DemographicsPersonalSection demographicsSettingsPersonalDetailsPreopertiesDTO = demographicsSettingsDetailsDTO.getPersonalDetails();
                    DemographicsPersonalSection.Properties demographicsSettingsPersonalDetailsDTO = demographicsSettingsPersonalDetailsPreopertiesDTO.getProperties();
                    DemographicsField demographicsSettingsFirstNameDTO = demographicsSettingsPersonalDetailsDTO.getFirstName();
                    DemographicsField demographicsSettingsLastNameDTO = demographicsSettingsPersonalDetailsDTO.getLastName();
                    DemographicsField demographicsSettingsMiddleNameDTO = demographicsSettingsPersonalDetailsDTO.getMiddleName();

//                    firstNameString = demographicsSettingsFirstNameDTO.(); TODO make sure this is set properly in xml
//                    lastNameString = demographicsSettingsLastNameDTO.getLabel();
//                    middleNameString = demographicsSettingsMiddleNameDTO.getLabel();
//
//                    firstNameEditText.setHint(firstNameString);
//                    lastNameEditText.setHint(lastNameString);
//                    middleNameEditText.setHint(middleNameString);
                }
            }
        }

    }

    private void getPersonalDetails() {
        DemographicPayloadResponseDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
        if (demographicsSettingsPayloadDTO != null) {
            DemographicPayloadInfoDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
            DemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
            PatientModel demographicsPersonalDetails = demographicPayload.getPersonalDetails();
            String firstNameValString = demographicsPersonalDetails.getFirstName();
            String lastNameValString = demographicsPersonalDetails.getLastName();
            String middleNameValString = demographicsPersonalDetails.getMiddleName();
            if (SystemUtil.isNotEmptyString(firstNameValString)) {
                firstNameEditText.setText(firstNameValString);
                firstNameEditText.requestFocus();
            }

            if (SystemUtil.isNotEmptyString(lastNameValString)) {
                lastNameEditText.setText(lastNameValString);
                lastNameEditText.requestFocus();
            }

            if (SystemUtil.isNotEmptyString(middleNameValString)) {
                middleNameEditText.setText(middleNameValString);
                middleNameEditText.requestFocus();

            }
            rootView.requestFocus();
            hideSoftKeyboard(getActivity());
        }
    }

    private boolean checkFirstName() {
        String firstName = firstNameEditText.getText().toString();
        isFirstNameEmpty = StringUtil.isNullOrEmpty(firstName);
        firstNameLabel.setErrorEnabled(isFirstNameEmpty); // enable for error if either empty or invalid first name
        if (isFirstNameEmpty) {
            firstNameLabel.setError("");
        } else {
            firstNameLabel.setError(null);
        }
        return !isFirstNameEmpty;
    }

    private boolean checkLastName() {
        String lastName = lastNameEditText.getText().toString();
        isLastNameEmpty = StringUtil.isNullOrEmpty(lastName);
        lastNameLabel.setErrorEnabled(isLastNameEmpty); // enable for error if either empty or invalid last name
        if (isLastNameEmpty) {
            lastNameLabel.setError("");
        } else {
            lastNameLabel.setError(null);
        }
        return !isLastNameEmpty;
    }

    private boolean isAllFieldsValid() {
        boolean isFirstNameValid = checkFirstName();
        if (!isFirstNameValid) {
            firstNameEditText.requestFocus();
        }
        boolean isLastNameValid = checkLastName();
        if (!isLastNameValid) {
            lastNameEditText.requestFocus();
        }

        return !isFirstNameEmpty && !isLastNameEmpty;

    }

    private void formatEditText() {
        DemographicMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getMetadata();
        DemographicDataModel demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getNewDataModel();
        DemographicDataModel.Demographic demographicsSettingsDemographicsDTO = demographicsSettingsDataModelsDTO.getDemographic();
        DemographicsPersonalSection demographicsSettingsPersonalDetailsDTO = demographicsSettingsDemographicsDTO.getPersonalDetails();
//        demographicsSettingsDetailsDTO = demographicsSettingsPersonalDetailsDTO.getProperties();

        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isFirstNameEmpty = StringUtil.isNullOrEmpty(firstNameEditText.getText().toString());
                if (!isFirstNameEmpty) {
                    firstNameLabel.setError(null);
                    firstNameLabel.setErrorEnabled(false);
                } else {
//                    demographicsSettingsFirstNameDTO = demographicsSettingsDetailsDTO.getFirstName();
//                    final String firstNameError = demographicsSettingsFirstNameDTO.getValidations().get(0).getErrorMessage();
//                    firstNameLabel.setError(firstNameError);
                    firstNameLabel.setErrorEnabled(true);
                }

            }
        });

        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isLastNameEmpty = StringUtil.isNullOrEmpty(lastNameEditText.getText().toString());
                if (!isLastNameEmpty) {
                    lastNameLabel.setError(null);
                    lastNameLabel.setErrorEnabled(false);
                } else {
                    demographicsSettingsLastNameDTO = demographicsSettingsDetailsDTO.getLastName();
                    final String lastNameError = demographicsSettingsLastNameDTO.getValidations().get(0).getErrorMessage();
                    lastNameLabel.setError(lastNameError);
                    lastNameLabel.setErrorEnabled(true);
                }
            }
        });

    }

    private void setClickables() {
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllFieldsValid()) {
                    updateProfileButton.setEnabled(false);
                    DemographicMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getMetadata();
                    if (demographicsSettingsMetadataDTO != null) {
                        DemographicTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                        TransitionDTO demographicsSettingsUpdateDemographicsDTO = demographicsSettingsTransitionsDTO.getUpdateDemographics();
                        Map<String, String> header = null;
                        DemographicPayloadResponseDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                        if (demographicsSettingsPayloadDTO != null) {
                            DemographicPayloadInfoDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                            DemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                            PatientModel demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                            demographicsPersonalDetails.setFirstName(firstNameEditText.getText().toString());
                            demographicsPersonalDetails.setLastName(lastNameEditText.getText().toString());
                            demographicsPersonalDetails.setMiddleName(middleNameEditText.getText().toString());

                            Gson gson = new Gson();
                            String jsonInString = gson.toJson(demographicPayload);
                            getWorkflowServiceHelper().execute(demographicsSettingsUpdateDemographicsDTO, updateProfileCallback, jsonInString, header);
                        }
                        header = new HashMap<>();
                        header.put("transition", "true");
                    }
                } else {
                    showErrorNotification(Label.getLabel("demographics_missing_information"));
                }
            }
        });
    }

    WorkflowServiceCallback updateProfileCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            updateProfileButton.setEnabled(true);

            PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            updateProfileButton.setEnabled(true);

            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
