package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.dtos.DemographicSettingsCurrentPasswordDTO;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsEmailProperties;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsHeaderDTO;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsLoginEmailDTO;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsMaintainanceDTO;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsProposedEmailDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;

import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsTransitionsDTO;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.api.client.util.Base64;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsSettingUpdateEmailFragment extends BaseFragment {
    private AppCompatActivity appCompatActivity;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;

    private String changeEmailString = null;
    private String saveChangesString = null;
    private String emailString = null;
    private String passwordString = null;

    private EditText emailEditText = null;
    private EditText passwordEditText = null;
    private Button updateEmailButton = null;
    private boolean isEmailEmpty;
    private boolean isPasswordEmpty;
    private TextInputLayout emailLabel = null;
    private TextInputLayout passwordLabel = null;
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
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);

        updateEmailButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);

        getSettingsLabels();
        title.setText(changeEmailString);
        updateEmailButton.setText(saveChangesString);

        initialiseUIFields(view);
        setEditTexts(view);

        getPersonalDetails();
        setClickables(view);
        isEmailEmpty = true;
        isPasswordEmpty = true;
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
                        passwordString = demographicsSettingsLabelsDTO.getSettingsCurrentPasswordLabel();

                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initialiseUIFields(View view) {

        emailLabel = (TextInputLayout) view.findViewById(R.id.signInEmailTextInputLayout);
        passwordLabel = (TextInputLayout) view.findViewById(R.id.oldPasswordTextInputLayout);

    }

    private void setEditTexts(View view) {
        emailLabel.setTag(emailString);
        emailEditText.setTag(emailLabel);
        emailEditText.setHint(emailString);
        passwordLabel.setTag(passwordString);
        passwordEditText.setTag(passwordLabel);
        passwordEditText.setHint(passwordString);
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
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        emailLabel.setErrorEnabled(isEmailEmpty); // enable for error if either empty or invalid email
        if (isEmailEmpty) {
            emailLabel.setError("");
        } else {
            emailLabel.setError(null);
        }
        return !isEmailEmpty;
    }

    private boolean checkPassword() {
        String password = passwordEditText.getText().toString();
        isPasswordEmpty = StringUtil.isNullOrEmpty(password);
        passwordLabel.setErrorEnabled(isPasswordEmpty); // enable for error if either empty or invalid password
        if (isPasswordEmpty) {
            passwordLabel.setError("");
        } else {
            passwordLabel.setError(null);
        }
        return !isPasswordEmpty;
    }

    private boolean isEmailValid() {
        boolean isEmailValid = checkEmail();
        if (!isEmailValid) {
            emailEditText.requestFocus();
        }
        return !isEmailEmpty ;
    }

    private boolean isPasswordValid() {
        boolean isPasswordValid = checkPassword();
        if (!isPasswordValid) {
            passwordEditText.requestFocus();
        }
        return !isPasswordEmpty ;
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
                    if (isEmailValid() && demographicsSettingsDTO != null ) {
                            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                                DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                                TransitionDTO demographicsSettingsUpdateEmailDTO = demographicsSettingsTransitionsDTO.getChangeLoginEmail();
                                DemographicsSettingsHeaderDTO demographicsSettingsHeaderDTO =  demographicsSettingsUpdateEmailDTO.getHeader();
                                DemographicsSettingsMaintainanceDTO demographicsSettingsMaintainanceDTO = demographicsSettingsHeaderDTO.getMaintenance();
                                DemographicsSettingsEmailProperties demographicsSettingsEmailProperties = demographicsSettingsMaintainanceDTO.getProperties();
                                DemographicsSettingsLoginEmailDTO demographicsSettingsLoginEmailDTO = demographicsSettingsEmailProperties.getLoginEmail();
                                DemographicsSettingsProposedEmailDTO demographicsSettingsProposedEmailDTO = demographicsSettingsEmailProperties.getProposedEmail();
                                DemographicSettingsCurrentPasswordDTO demographicSettingsCurrentPasswordDTO = demographicsSettingsEmailProperties.getCurrentPassword();

                                demographicsSettingsEmailProperties.setLoginEmail(demographicsSettingsLoginEmailDTO);
                                demographicsSettingsEmailProperties.setProposedEmail(demographicsSettingsProposedEmailDTO);
                                demographicsSettingsEmailProperties.setCurrentPassword(demographicSettingsCurrentPasswordDTO);

                                Map<String, String> properties = null;
                                properties = new HashMap<>();
                                properties = new HashMap<>();

                                properties.put("login_email",getCurrentEmail());
                                properties.put("proposed_email", emailEditText.getText().toString());
                                properties.put("current_password",passwordEditText.getText().toString());
                                JSONObject attributes = new JSONObject( properties );
                                String encodedAttributes = new String(Base64.encodeBase64( attributes.toString().getBytes()));
                                Map<String, String> header = null;
                                header = new HashMap<>();
                                header.put("maintenance", encodedAttributes);

                                try {
                                    if (demographicsSettingsDTO != null) {
                                        DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                                        if (demographicsSettingsPayloadDTO != null) {
                                            //DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                                            //DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload().;
                                            //DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();

                                            Gson gson = new Gson();
                                            String jsonInString = gson.toJson(demographicsSettingsPayloadDTO);
                                            getWorkflowServiceHelper().execute(demographicsSettingsUpdateEmailDTO, updateEmailCallback,null, null, header);
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
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

    private String getCurrentEmail(){
        String currentEmail = null;
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if (demographicsSettingsPayloadDTO != null) {
                currentEmail = demographicsSettingsPayloadDTO.getCurrentEmail();
            }
        }
        return currentEmail;
    }

}

