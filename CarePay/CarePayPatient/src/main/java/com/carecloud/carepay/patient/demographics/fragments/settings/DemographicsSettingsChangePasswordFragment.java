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
import com.carecloud.carepay.service.library.dtos.DemographicSettingsCurrentPasswordDTO;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsEmailProperties;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsHeaderDTO;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsMaintainanceDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsTransitionsDTO;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.api.client.util.Base64;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsSettingsChangePasswordFragment extends BaseFragment {
    private static final String LOG_TAG = DemographicsSettingsFragment.class.getSimpleName();
    private AppCompatActivity appCompatActivity;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;

    private String currentPasswordString = null;
    private String newPasswordString = null;
    private String repeatPasswordString = null;

    private EditText currentPasswordEditText = null;
    private EditText newPasswordEditText = null;
    private EditText repeatPasswordEditText = null;

    private TextView passwordHelpLabel = null;

    private Button updatePasswordButton = null;
    private boolean isCurrentPasswordEmpty;
    private boolean isNewPasswordEmpty;
    private boolean isRepeatPasswordEmpty;

    private TextInputLayout currentPasswordLabel = null;
    private TextInputLayout newPasswordLabel = null;
    private TextInputLayout repeatPasswordLabel = null;

    private String changePasswordString = null;
    private String passwordHelpString = null;
    private String saveChangesString = null;

    private LinearLayout rootview;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    public DemographicsSettingsChangePasswordFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_settings_change_password, container, false);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        setGothamRoundedMediumTypeface(appCompatActivity, title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            bundle = getArguments();
            String demographicsSettingsDTOString = bundle.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
        }

        currentPasswordEditText = (EditText) view.findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = (EditText) view.findViewById(R.id.newPasswordEditText);
        repeatPasswordEditText = (EditText) view.findViewById(R.id.repeatPasswordEditText);
        passwordHelpLabel = (TextView) view.findViewById(R.id.passwordHelpLabel);

        updatePasswordButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        getSettingsLabels();
        title.setText(changePasswordString);
        updatePasswordButton.setText(saveChangesString);

        initialiseUIFields(view);
        setEditTexts(view);
        passwordHelpLabel.setText(passwordHelpString);
        setClickables(view);
        isCurrentPasswordEmpty = true;
        isNewPasswordEmpty = true;
        isRepeatPasswordEmpty = true;

        return view;

    }

    /**
     * demographics settings labels
     */
    public void getSettingsLabels() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                if (demographicsSettingsLabelsDTO != null) {

                    changePasswordString = demographicsSettingsLabelsDTO.getSettingschangePasswordLabel();
                    passwordHelpString = demographicsSettingsLabelsDTO.getPasswordHelpLabel();
                    saveChangesString = demographicsSettingsLabelsDTO.getDemographicsSaveChangesLabel();
                    currentPasswordString = demographicsSettingsLabelsDTO.getSettingsCurrentPasswordLabel();
                    newPasswordString = demographicsSettingsLabelsDTO.getSettingsNewPasswordLabel();
                    repeatPasswordString = demographicsSettingsLabelsDTO.getSettingRepeatNewPasswordLabel();

                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initialiseUIFields(View view) {

        currentPasswordLabel = (TextInputLayout) view.findViewById(R.id.oldPasswordTextInputLayout);
        newPasswordLabel = (TextInputLayout) view.findViewById(R.id.newPasswordTextInputLayout);
        repeatPasswordLabel = (TextInputLayout) view.findViewById(R.id.repeatPasswordTextInputLayout);
    }

    private void setEditTexts(View view) {
        currentPasswordLabel.setTag(currentPasswordString);
        currentPasswordEditText.setTag(currentPasswordLabel);
        currentPasswordEditText.setHint(currentPasswordString);

        newPasswordLabel.setTag(newPasswordString);
        newPasswordEditText.setTag(newPasswordLabel);
        newPasswordEditText.setHint(newPasswordString);

        repeatPasswordLabel.setTag(repeatPasswordString);
        repeatPasswordEditText.setTag(repeatPasswordLabel);
        repeatPasswordEditText.setHint(repeatPasswordString);

        setChangeFocusListeners();
    }

    private void setChangeFocusListeners() {
        currentPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
        newPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
        repeatPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

    }

    private boolean checkCurrentPassword() {
        String currentPassword = currentPasswordEditText.getText().toString();
        isCurrentPasswordEmpty = StringUtil.isNullOrEmpty(currentPassword);
        currentPasswordLabel.setErrorEnabled(isCurrentPasswordEmpty);
        if (isCurrentPasswordEmpty) {
            currentPasswordLabel.setError("");
        } else {
            currentPasswordLabel.setError(null);
        }
        return !isCurrentPasswordEmpty;
    }

    private boolean checkNewPassword() {
        String newPassword = newPasswordEditText.getText().toString();
        isNewPasswordEmpty = StringUtil.isNullOrEmpty(newPassword);
        newPasswordLabel.setErrorEnabled(isNewPasswordEmpty);
        if (isNewPasswordEmpty) {
            newPasswordLabel.setError("");
        } else {
            newPasswordLabel.setError(null);
        }
        return !isNewPasswordEmpty;
    }

    private boolean checkRepeatPassword() {
        String repeatPassword = repeatPasswordEditText.getText().toString();
        isRepeatPasswordEmpty = StringUtil.isNullOrEmpty(repeatPassword);
        repeatPasswordLabel.setErrorEnabled(isRepeatPasswordEmpty);
        if (isRepeatPasswordEmpty) {
            repeatPasswordLabel.setError("");
        } else {
            repeatPasswordLabel.setError(null);
        }
        return !isRepeatPasswordEmpty;
    }

    private boolean isCurrentPasswordValid() {
        boolean isCurrentPasswordValid = checkCurrentPassword();
        if (!isCurrentPasswordValid) {
            currentPasswordEditText.requestFocus();
        }
        return !isCurrentPasswordEmpty ;
    }

    private boolean isNewPasswordValid() {
        boolean isNewPasswordValid = checkNewPassword();
        if (!isNewPasswordValid) {
            newPasswordEditText.requestFocus();
        }
        return !isNewPasswordEmpty ;
    }

    private boolean isRepeatPasswordValid() {
        boolean isRepeatPasswordValid = checkRepeatPassword();
        if (!isRepeatPasswordValid) {
            repeatPasswordEditText.requestFocus();
        }
        return !isRepeatPasswordEmpty ;
    }

    /**
     * For tests
     *
     * @param activity The activity
     */
    public void setActivity(KeyboardHolderActivity activity) {
        appCompatActivity = activity;
    }

    WorkflowServiceCallback updatePasswordCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private void setClickables(View view) {
        {
            updatePasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (isCurrentPasswordValid() && demographicsSettingsDTO != null ) {
                                DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                                    DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                                    TransitionDTO demographicsSettingsUpdatePasswordDTO = demographicsSettingsTransitionsDTO.getChangePassword();
                                    DemographicsSettingsHeaderDTO demographicsSettingsHeaderDTO =  demographicsSettingsUpdatePasswordDTO.getHeader();
                                    DemographicsSettingsMaintainanceDTO demographicsSettingsMaintainanceDTO = demographicsSettingsHeaderDTO.getMaintenance();
                                    DemographicsSettingsEmailProperties demographicsSettingsEmailProperties = demographicsSettingsMaintainanceDTO.getProperties();
                                    DemographicSettingsCurrentPasswordDTO demographicSettingsCurrentPasswordDTO = demographicsSettingsEmailProperties.getCurrentPassword();

                                    Map<String, String> properties = null;
                                    properties = new HashMap<>();

                                    properties.put("login_email",getCurrentEmail());
                                    properties.put("current_password", currentPasswordEditText.getText().toString());
                                    properties.put("proposed_password",newPasswordEditText.getText().toString());
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
                                                //DemographicPayloadDTO demographicPayload = demographicsDTO.getPayload().;
                                                //PatientModel demographicsPersonalDetails = demographicPayload.getPersonalDetails();

                                                Gson gson = new Gson();
                                                String jsonInString = gson.toJson(demographicsSettingsPayloadDTO);
                                                getWorkflowServiceHelper().execute(demographicsSettingsUpdatePasswordDTO, updatePasswordCallback,null, null, header);
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
    }

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

