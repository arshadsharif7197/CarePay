package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
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
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.DemographicSettingsCurrentPasswordDTO;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsEmailProperties;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsLoginEmailDTO;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsProposedEmailDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.api.client.util.Base64;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateEmailFragment extends BaseFragment {
    private DemographicsSettingsDTO demographicsSettingsDTO;
    private DemographicsSettingsFragmentListener callback;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button updateEmailButton;
    private boolean isPasswordEmpty = true;
    private TextInputLayout emailLabelLayout;
    private TextInputLayout passwordLabelLayout;
    private View rootView;

    public UpdateEmailFragment() {

    }

    /**
     * @return an instance of UpdateEmailFragment
     */
    public static UpdateEmailFragment newInstance() {
        return new UpdateEmailFragment();
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicsSettingsDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_email_update, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view.findViewById(R.id.settings_root);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarLayout);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("setting_change_Email"));

        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setEditTexts(view);

        setPersonalDetails();
        setClickListeners(view);
    }

    private void setEditTexts(View view) {
        emailLabelLayout = (TextInputLayout) view.findViewById(R.id.signInEmailTextInputLayout);
        passwordLabelLayout = (TextInputLayout) view.findViewById(R.id.oldPasswordTextInputLayout);
        emailEditText = (EditText) view.findViewById(R.id.signinEmailEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
//        emailLabelLayout.setTag(Label.getLabel("email_label"));
//        emailEditText.setTag(emailLabelLayout);
        emailLabelLayout.setHint(Label.getLabel("email_label"));
//        passwordLabelLayout.setTag(Label.getLabel("settings_current_password"));
//        passwordEditText.setTag(passwordLabelLayout);
        passwordLabelLayout.setHint(Label.getLabel("settings_current_password"));
        setChangeFocusListeners();
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    updateEmailButton.setEnabled(false);
                } else {
                    updateEmailButton.setEnabled(true);
                }
            }
        });
    }

    private void setChangeFocusListeners() {
        emailEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(emailLabelLayout, null));
        passwordEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(passwordLabelLayout, null));
    }

    private void setPersonalDetails() {
        String email = getCurrentEmail();
        if (SystemUtil.isNotEmptyString(email)) {
            emailEditText.setText(email);
            emailEditText.requestFocus();
        }
        rootView.requestFocus();
        SystemUtil.hideSoftKeyboard(getActivity());

    }

    private boolean isEmailValid() {
        String email = emailEditText.getText().toString();
        boolean isEmailValid = StringUtil.isValidmail(email);
        emailLabelLayout.setErrorEnabled(!isEmailValid); // enable for error if either empty or invalid email
        if (!isEmailValid) {
            emailLabelLayout.setError(Label.getLabel("demographics_invalid_email_error"));
            emailEditText.requestFocus();
        } else {
            emailLabelLayout.setError(null);
        }
        return isEmailValid;
    }

    private boolean isPasswordValid() {
        boolean isPasswordValid = checkPassword();
        if (!isPasswordValid) {
            passwordEditText.requestFocus();
        }
        return !isPasswordEmpty;
    }

    private boolean checkPassword() {
        String password = passwordEditText.getText().toString();
        isPasswordEmpty = StringUtil.isNullOrEmpty(password);
        passwordLabelLayout.setErrorEnabled(isPasswordEmpty); // enable for error if either empty or invalid password
        if (isPasswordEmpty) {
            passwordLabelLayout.setError("");
        } else {
            passwordLabelLayout.setError(null);
        }
        return !isPasswordEmpty;
    }

    private void setClickListeners(View view) {
        updateEmailButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        updateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmailValid() && isPasswordValid()) {
                    DemographicsSettingsEmailProperties demographicsSettingsEmailProperties = demographicsSettingsDTO
                            .getMetadata().getTransitions().getChangeLoginEmail()
                            .getHeader().getMaintenance().getProperties();
                    DemographicsSettingsLoginEmailDTO demographicsSettingsLoginEmailDTO = demographicsSettingsEmailProperties.getLoginEmail();
                    DemographicsSettingsProposedEmailDTO demographicsSettingsProposedEmailDTO = demographicsSettingsEmailProperties.getProposedEmail();
                    DemographicSettingsCurrentPasswordDTO demographicSettingsCurrentPasswordDTO = demographicsSettingsEmailProperties.getCurrentPassword();

                    demographicsSettingsEmailProperties.setLoginEmail(demographicsSettingsLoginEmailDTO);
                    demographicsSettingsEmailProperties.setProposedEmail(demographicsSettingsProposedEmailDTO);
                    demographicsSettingsEmailProperties.setCurrentPassword(demographicSettingsCurrentPasswordDTO);

                    Map<String, String> properties = new HashMap<>();

                    properties.put("login_email", getCurrentEmail());
                    properties.put("proposed_email", emailEditText.getText().toString());
                    properties.put("current_password", passwordEditText.getText().toString());
                    JSONObject attributes = new JSONObject(properties);
                    String encodedAttributes = new String(Base64.encodeBase64(attributes.toString().getBytes()));
                    Map<String, String> header = new HashMap<>();
                    header.put("maintenance", encodedAttributes);

                    DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                    if (demographicsSettingsPayloadDTO != null) {
                        getWorkflowServiceHelper().execute(demographicsSettingsDTO
                                        .getMetadata().getTransitions().getChangeLoginEmail(),
                                updateEmailCallback, null, null, header);
                    }
                }
            }
        });
    }

    WorkflowServiceCallback updateEmailCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            updateEmailButton.setEnabled(true);
            DemographicsSettingsDTO updatedSettings = DtoHelper.getConvertedDTO(DemographicsSettingsDTO.class, workflowDTO);
            String newEmail = updatedSettings.getPayload().getCurrentEmail();
            demographicsSettingsDTO.getPayload().setCurrentEmail(newEmail);
            getApplicationPreferences().setUserId(newEmail);
//            PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
            getActivity().onBackPressed();
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            updateEmailButton.setEnabled(true);
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private String getCurrentEmail() {
        DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
        return demographicsSettingsPayloadDTO.getCurrentEmail();
    }
}

