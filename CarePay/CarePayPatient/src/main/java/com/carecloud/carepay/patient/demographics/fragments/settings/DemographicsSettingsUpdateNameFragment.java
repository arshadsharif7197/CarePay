package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDataModelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsFirstNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLastNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMiddleNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPropertiesDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsTransitionsDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsSettingsUpdateNameFragment extends BaseFragment {
    private static final String LOG_TAG = DemographicsSettingsFragment.class.getSimpleName();
    private AppCompatActivity appCompatActivity;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private String profileString = null;
    private String firstNameString = null;
    private String middleNameString = null;
    private String lastNameString = null;
    private String firstNameValString = null;
    private String lastNameValString = null;
    private String middleNameValString = null;
    private String saveChangesString = null;
    private String changeNameString = null;
    private EditText firstNameEditText = null;
    private EditText middleNameEditText = null;
    private EditText lastNameEditText = null;
    private Button updateProfileButton = null;
    private DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = null;
    private TextInputLayout firstNameLabel = null;
    private TextInputLayout middleNameLabel = null;
    private TextInputLayout lastNameLabel = null;
    private LinearLayout rootview;

    private boolean isFirstNameEmpty;
    private boolean isLastNameEmpty;

    private DemographicsSettingsPersonalDetailsDTO demographicsSettingsDetailsDTO = null;
    private DemographicsSettingsFirstNameDTO demographicsSettingsFirstNameDTO = null;
    private DemographicsSettingsLastNameDTO demographicsSettingsLastNameDTO = null;
    private ProgressBar progressBar = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_name_update, container, false);
        rootview = (LinearLayout) view.findViewById(R.id.demographicsReviewRootLayout);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        progressBar = (ProgressBar) view.findViewById(R.id.demographicReviewProgressBar);
        progressBar.setVisibility(View.GONE);
        SystemUtil.setGothamRoundedMediumTypeface(appCompatActivity, title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            bundle = getArguments();
            String demographicsSettingsDTOString = bundle.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
        }
        firstNameEditText = (EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit);
        middleNameEditText = (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        lastNameEditText = (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit);
        updateProfileButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        getEditProfileLabels();

        initialiseUIFields(view);
        getProfileProperties();
        setEditTexts(view);

        getPersonalDetails();
        title.setText(changeNameString);
        updateProfileButton.setText(saveChangesString);
        setClickables(view);
        formatEditText();
        isFirstNameEmpty = true;
        isLastNameEmpty = true;
        return view;

    }

    private void initialiseUIFields(View view) {

        firstNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrFirstNameTextInput);
        middleNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrMiddleNameTextInputLayout);
        lastNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrLastNameTextInput);

    }

    private void setEditTexts(View view) {

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

    /**
     * demographics Edit Profile labels
     */
    public void getEditProfileLabels() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                if (demographicsSettingsLabelsDTO != null) {
                    profileString = demographicsSettingsLabelsDTO.getProfileHeadingLabel();
                    saveChangesString = demographicsSettingsLabelsDTO.getDemographicsSaveChangesLabel();
                    changeNameString = demographicsSettingsLabelsDTO.getDemographicsChangeNameLabel();
                }
            }
        }
    }

    public void getProfileProperties() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                if (demographicsSettingsDataModelsDTO != null) {
                    DemographicsSettingsDetailsDTO demographicsSettingsDetailsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                    if (demographicsSettingsDetailsDTO != null) {
                        DemographicsSettingsPersonalDetailsPropertiesDTO demographicsSettingsPersonalDetailsPreopertiesDTO = demographicsSettingsDetailsDTO.getPersonalDetails();
                        DemographicsSettingsPersonalDetailsDTO demographicsSettingsPersonalDetailsDTO = demographicsSettingsPersonalDetailsPreopertiesDTO.getProperties();
                        DemographicsSettingsFirstNameDTO demographicsSettingsFirstNameDTO = demographicsSettingsPersonalDetailsDTO.getFirstName();
                        DemographicsSettingsLastNameDTO demographicsSettingsLastNameDTO = demographicsSettingsPersonalDetailsDTO.getLastName();
                        DemographicsSettingsMiddleNameDTO demographicsSettingsMiddleNameDTO = demographicsSettingsPersonalDetailsDTO.getMiddleName();

                        firstNameString = demographicsSettingsFirstNameDTO.getLabel();
                        lastNameString = demographicsSettingsLastNameDTO.getLabel();
                        middleNameString = demographicsSettingsMiddleNameDTO.getLabel();

                        firstNameEditText.setHint(firstNameString);
                        lastNameEditText.setHint(lastNameString);
                        middleNameEditText.setHint(middleNameString);
                    }
                }
            }
        }

    }

    private void getPersonalDetails() {
        String userId ;

        if(!HttpConstants.isUseUnifiedAuth()) {
            userId = getAppAuthoriztionHelper().getCurrUser();
        }else{
            userId = getAppAuthoriztionHelper().getUserAlias();
        }

        if (demographicsSettingsDTO != null) {
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if (demographicsSettingsPayloadDTO != null) {
                DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                PatientModel demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                firstNameValString = demographicsPersonalDetails.getFirstName();
                lastNameValString = demographicsPersonalDetails.getLastName();
                middleNameValString = demographicsPersonalDetails.getMiddleName();
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
                rootview.requestFocus();
                hideSoftKeyboard(getActivity());

            }
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
        return !isLastNameEmpty ;
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
      try{
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                DemographicsSettingsDetailsDTO demographicsSettingsDemographicsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                DemographicsSettingsPersonalDetailsPropertiesDTO demographicsSettingsPersonalDetailsDTO = demographicsSettingsDemographicsDTO.getPersonalDetails();
                demographicsSettingsDetailsDTO = demographicsSettingsPersonalDetailsDTO.getProperties();

            }
        }
      }catch (Exception e){
          e.printStackTrace();
      }

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
                    demographicsSettingsFirstNameDTO = demographicsSettingsDetailsDTO.getFirstName();
                    final String firstNameError = demographicsSettingsFirstNameDTO.getValidations().get(0).getErrorMessage();
                    firstNameLabel.setError(firstNameError);
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

    private void setClickables(View view) {

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (isAllFieldsValid()) {
                        updateProfileButton.setEnabled(false);
                        if (demographicsSettingsDTO != null) {
                            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                            if (demographicsSettingsMetadataDTO != null) {
                                DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                                TransitionDTO demographicsSettingsUpdateDemographicsDTO = demographicsSettingsTransitionsDTO.getUpdateDemographics();
                                JSONObject payload = new JSONObject();
                                Map<String, String> queries = null;
                                Map<String, String> header = null;
                                try {
                                    if (demographicsSettingsDTO != null) {
                                        DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                                        if (demographicsSettingsPayloadDTO != null) {
                                            DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                                            DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                                            PatientModel demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                                            demographicsPersonalDetails.setFirstName(firstNameEditText.getText().toString());
                                            demographicsPersonalDetails.setLastName(lastNameEditText.getText().toString());
                                            demographicsPersonalDetails.setMiddleName(middleNameEditText.getText().toString());

                                            Gson gson = new Gson();
                                            String jsonInString = gson.toJson(demographicPayload);
                                            getWorkflowServiceHelper().execute(demographicsSettingsUpdateDemographicsDTO, updateProfileCallback, jsonInString, header);
                                        }
                                    }
                                    header = new HashMap<>();
                                    header.put("transition", "true");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else
                    {
                        showErrorNotification(demographicsSettingsLabelsDTO.getDemographicsMissingInformation());
                    }
                }catch(Exception e){
                    e.printStackTrace();
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

            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            updateProfileButton.setEnabled(true);

            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
