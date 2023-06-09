package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.DemographicMetadataDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.transitions.DemographicTransitionsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.google.api.client.util.Base64;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends DemographicsBaseSettingsFragment {

    private DemographicDTO demographicsSettingsDTO;
    private DemographicsSettingsFragmentListener callback;

    private EditText currentPasswordEditText = null;
    private EditText newPasswordEditText = null;
    private EditText repeatPasswordEditText = null;

    private Button updatePasswordButton = null;


    /**
     * @return an instance of ChangePasswordFragment
     */
    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (DemographicsSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DemographicsSettingsFragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicDTO) callback.getDto();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demographics_settings_change_password, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final Toolbar toolbar = view.findViewById(R.id.settings_toolbar);
        TextView title = toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("settings_change_password"));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
        callback.setToolbar(toolbar);

        updatePasswordButton = view.findViewById(R.id.buttonAddDemographicInfo);
        setClickables();

        initViews(view);
    }

    private void initViews(View view) {
        TextInputLayout currentPasswordLabel = view.findViewById(R.id.oldPasswordTextInputLayout);
        currentPasswordEditText = view.findViewById(R.id.currentPasswordEditText);
        currentPasswordEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(currentPasswordLabel, null));
        currentPasswordEditText.addTextChangedListener(getValidateEmptyTextWatcher(currentPasswordLabel));

        TextInputLayout newPasswordLabel = view.findViewById(R.id.newPasswordTextInputLayout);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        newPasswordEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(newPasswordLabel, null));
        newPasswordEditText.addTextChangedListener(getValidateEmptyTextWatcher(newPasswordLabel));

        TextInputLayout repeatPasswordLabel = view.findViewById(R.id.repeatPasswordTextInputLayout);
        repeatPasswordEditText = view.findViewById(R.id.repeatPasswordEditText);
        repeatPasswordEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(repeatPasswordLabel, null));
        repeatPasswordEditText.addTextChangedListener(getValidateEmptyTextWatcher(repeatPasswordLabel));

    }


    private WorkflowServiceCallback updatePasswordCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));

            MixPanelUtil.logEvent(getString(R.string.event_change_password));

            getActivity().onBackPressed();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private void setClickables() {
        updatePasswordButton.setOnClickListener(view -> {
            if (passConstraints(false)) {
                DemographicMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getMetadata();
                DemographicTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                TransitionDTO changePassword = demographicsSettingsTransitionsDTO.getChangePassword();

                Map<String, String> properties = new HashMap<>();
                properties.put("login_email", getCurrentEmail());
                properties.put("current_password", currentPasswordEditText.getText().toString().trim());
                properties.put("proposed_password", newPasswordEditText.getText().toString().trim());
                properties.put("id_token", getAppAuthorizationHelper().getIdToken());
                JSONObject attributes = new JSONObject(properties);
                String encodedAttributes = new String(Base64.encodeBase64(attributes.toString().getBytes()));

                Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
                header.put("maintenance", encodedAttributes);
                header.put("AccessToken", getAppAuthorizationHelper().getAccessToken());

                DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                if (demographicsSettingsPayloadDTO != null) {
                    getWorkflowServiceHelper().execute(changePassword, updatePasswordCallback,
                            null, null, header);
                }
            }
        });
        updatePasswordButton.setEnabled(false);
        updatePasswordButton.setClickable(false);
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

    @Override
    protected void checkIfEnableButton(boolean userInteraction) {
        boolean isEnabled = passConstraints(false);
        if (updatePasswordButton != null) {
            updatePasswordButton.setEnabled(isEnabled);
            updatePasswordButton.setClickable(isEnabled);
        }
    }

    @Override
    protected boolean passConstraints(boolean isUserInteraction) {
        View view = getView();
        if (view == null) {
            return false;
        }

        if (StringUtil.isNullOrEmpty(currentPasswordEditText.getText().toString().trim())) {
            return false;
        }

        if (StringUtil.isNullOrEmpty(newPasswordEditText.getText().toString().trim())) {
            return false;
        }

        TextInputLayout passwordLayout = view.findViewById(R.id.newPasswordTextInputLayout);
        if (!ValidationHelper.isValidPassword(newPasswordEditText.getText().toString().trim())) {
            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError(Label.getLabel("password_hint_text"));
            return false;
        } else {
            passwordLayout.setError(null);
            passwordLayout.setErrorEnabled(false);
        }

        if (StringUtil.isNullOrEmpty(repeatPasswordEditText.getText().toString().trim())) {
            return false;
        }

        TextInputLayout repeatPasswordLabel = view
                .findViewById(R.id.repeatPasswordTextInputLayout);
        if (!repeatPasswordEditText.getText().toString().trim()
                .equals(newPasswordEditText.getText().toString().trim())) {
            repeatPasswordLabel.setErrorEnabled(true);
            repeatPasswordLabel.setError(Label.getLabel("settings_repeat_password_error"));
            return false;
        } else {
            repeatPasswordLabel.setError(null);
            repeatPasswordLabel.setErrorEnabled(false);
        }

        return true;
    }
}

