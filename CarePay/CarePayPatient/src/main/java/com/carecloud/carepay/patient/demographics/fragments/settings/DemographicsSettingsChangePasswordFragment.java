package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.DemographicMetadataDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.transitions.DemographicTransitionsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsTransitionsDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.api.client.util.Base64;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsSettingsChangePasswordFragment extends BaseFragment {

    private DemographicsSettingsDTO demographicsSettingsDTO;
    private DemographicsSettingsFragmentListener callback;

    private String currentPasswordString = null;
    private String newPasswordString = null;
    private String repeatPasswordString = null;

    private EditText currentPasswordEditText = null;
    private EditText newPasswordEditText = null;
    private EditText repeatPasswordEditText = null;

    private Button updatePasswordButton = null;
    private boolean isCurrentPasswordEmpty;
    private boolean isNewPasswordEmpty;
    private boolean isRepeatPasswordEmpty;

    private TextInputLayout currentPasswordLabel = null;
    private TextInputLayout newPasswordLabel = null;
    private TextInputLayout repeatPasswordLabel = null;

    private String saveChangesString = null;

    /**
     * @return an instance of DemographicsSettingsChangePasswordFragment
     */
    public static DemographicsSettingsChangePasswordFragment newInstance() {
        return new DemographicsSettingsChangePasswordFragment();
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
        demographicsSettingsDTO = (DemographicsSettingsDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_settings_change_password, container, false);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("settings_change_password"));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
        callback.setToolbar(toolbar);

        currentPasswordEditText = (EditText) view.findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = (EditText) view.findViewById(R.id.newPasswordEditText);
        repeatPasswordEditText = (EditText) view.findViewById(R.id.repeatPasswordEditText);

        updatePasswordButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        getSettingsLabels();
        updatePasswordButton.setText(saveChangesString);

        initialiseUIFields(view);
        setEditTexts();
        TextView passwordHelpLabel = (TextView) view.findViewById(R.id.passwordHelpLabel);
        passwordHelpLabel.setText(Label.getLabel("settings_password_help_text"));
        setClickables();
        isCurrentPasswordEmpty = true;
        isNewPasswordEmpty = true;
        isRepeatPasswordEmpty = true;

        return view;

    }

    /**
     * demographics settings labels
     */
    public void getSettingsLabels() {
        saveChangesString = Label.getLabel("demographics_save_changes_label");
        currentPasswordString = Label.getLabel("settings_current_password");
        newPasswordString = Label.getLabel("settings_new_password");
        repeatPasswordString = Label.getLabel("settings_repeat_new_password");
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

    private void setEditTexts() {
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
        return !isCurrentPasswordEmpty;
    }

    private boolean isNewPasswordValid() {
        boolean isNewPasswordValid = checkNewPassword();
        if (!isNewPasswordValid) {
            newPasswordEditText.requestFocus();
        }
        return !isNewPasswordEmpty;
    }

    private boolean isRepeatPasswordValid() {
        boolean isRepeatPasswordValid = checkRepeatPassword();
        if (!isRepeatPasswordValid) {
            repeatPasswordEditText.requestFocus();
        }
        return !isRepeatPasswordEmpty;
    }

    WorkflowServiceCallback updatePasswordCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private void setClickables() {
        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCurrentPasswordValid()) {
                    DemographicMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getMetadata();
                    DemographicTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                    TransitionDTO demographicsSettingsUpdatePasswordDTO = demographicsSettingsTransitionsDTO.getChangePassword();

                    Map<String, String> properties = new HashMap<>();
                    properties.put("login_email", getCurrentEmail());
                    properties.put("current_password", currentPasswordEditText.getText().toString());
                    properties.put("proposed_password", newPasswordEditText.getText().toString());
                    JSONObject attributes = new JSONObject(properties);
                    String encodedAttributes = new String(Base64.encodeBase64(attributes.toString().getBytes()));
                    Map<String, String> header = new HashMap<>();
                    header.put("maintenance", encodedAttributes);

                    DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                    if (demographicsSettingsPayloadDTO != null) {
                        getWorkflowServiceHelper().execute(demographicsSettingsUpdatePasswordDTO, updatePasswordCallback, null, null, header);
                    }
                }
            }
        });
    }

    private String getCurrentEmail() {
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

