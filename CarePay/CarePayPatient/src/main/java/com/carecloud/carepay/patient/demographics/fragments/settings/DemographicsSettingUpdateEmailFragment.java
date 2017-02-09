package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;

import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsTransitionsDTO;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsSettingUpdateEmailFragment extends Fragment {
    private AppCompatActivity appCompatActivity;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;

    private String changeEmailString = null;
    private String saveChangesString = null;
    private String emailString = null;

    private EditText emailEditText = null;
    private Button updateEmailButton = null;
    private boolean isEmailEmpty;
    private TextInputLayout emailLabel = null;
    private LinearLayout rootview;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    public DemographicsSettingUpdateEmailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_email_update, container, false);
        rootview = (LinearLayout) view.findViewById(R.id.settings_root);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
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

        emailEditText = (EditText) view.findViewById(R.id.signinEmailEditText);
        updateEmailButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);

        getSettingsLabels();
        title.setText(changeEmailString);
        updateEmailButton.setText(saveChangesString);

        initialiseUIFields(view);
        setEditTexts(view);

        getPersonalDetails();
        setClickables(view);
        isEmailEmpty = true;
        return view;

    }

    /**
     * demographics settings labels
     */
    public void getSettingsLabels() {
        try{
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                if (demographicsSettingsLabelsDTO != null) {

                    changeEmailString = demographicsSettingsLabelsDTO.getDemographicsChangeEmailLabel();
                    saveChangesString = demographicsSettingsLabelsDTO.getDemographicsSaveChangesLabel();
                    emailString = demographicsSettingsLabelsDTO.getEmailLabel();

                }
            }
         }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initialiseUIFields(View view) {

        emailLabel = (TextInputLayout) view.findViewById(R.id.signInEmailTextInputLayout);
    }

    private void setEditTexts(View view) {
        emailLabel.setTag(emailString);
        emailEditText.setTag(emailLabel);
        emailEditText.setHint(emailString);
        setChangeFocusListeners();
    }

    private void setChangeFocusListeners() {
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

    }

    private void getPersonalDetails() {
        String userId = CognitoAppHelper.getCurrUser();

                if (SystemUtil.isNotEmptyString(userId)) {
                    emailEditText.setText(userId);
                    emailEditText.requestFocus();
                }

                rootview.requestFocus();
                SystemUtil.hideSoftKeyboard(getActivity());

    }

    private boolean checkEmail() {
       String email = emailEditText.getText().toString();
        isEmailEmpty = StringUtil.isNullOrEmpty(email);
        emailLabel.setErrorEnabled(isEmailEmpty); // enable for error if either empty or invalid first name
        if (isEmailEmpty) {
            emailLabel.setError("");
        } else {
            emailLabel.setError(null);
        }
        return !isEmailEmpty;
    }

    private boolean isEmailValid() {
        boolean isEmailValid = checkEmail();
        if (!isEmailValid) {
            emailEditText.requestFocus();
        }
        return !isEmailEmpty ;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * For tests
     *
     * @param activity The activity
     */
    public void setActivity(KeyboardHolderActivity activity) {
        appCompatActivity = activity;
    }

    private void setClickables(View view) {
        updateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isEmailValid() ) {
                        updateEmailButton.setEnabled(false);
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
                                            DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();

                                            Gson gson = new Gson();
                                            String jsonInString = gson.toJson(demographicPayload);
                                            WorkflowServiceHelper.getInstance().execute(demographicsSettingsUpdateDemographicsDTO, updateEmailCallback, jsonInString, header);
                                        }
                                    }
                                    header = new HashMap<>();
                                    header.put("transition", "true");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    WorkflowServiceCallback updateEmailCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            updateEmailButton.setEnabled(true);
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            updateEmailButton.setEnabled(true);
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

}

