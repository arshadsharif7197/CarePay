package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayEditText;
import com.carecloud.carepaylibray.customcomponents.CarePayRadioButton;
import com.carecloud.carepaylibray.customcomponents.CarePayTextInputLayout;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.unifiedauth.TwoFAuth.Payload;
import com.carecloud.carepaylibray.unifiedauth.TwoFAuth.SendOtp;
import com.carecloud.carepaylibray.unifiedauth.TwoFAuth.SettingsList;
import com.carecloud.carepaylibray.unifiedauth.TwoFAuth.TwoFactorAuth;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFactorAuthFragment extends BaseFragment implements View.OnClickListener {
    private DemographicDTO demographicsSettingsDTO;
    private ChangeEmailDialogFragment changeEmailDialogFragment;
    private DemographicsSettingsFragmentListener callback;
    private CarePayRadioButton emailSetRadioButton;
    private CarePayRadioButton smsSetRadioButton;
    private CarePayEditText editTextEmail;
    private CarePayEditText editTextSms;
    private CarePayTextView emailResendTextView;
    private CarePayTextView smsResendTextView;
    private CarePayEditText editTextVerificationCodeSms;
    private CarePayEditText editTextVerificationCodeEmail;
    private CarePayTextInputLayout smsTextInputLayout;
    private CarePayTextInputLayout emailTextInputLayout;
    private CarePayButton enableDisableButton;
    private boolean isResend=false;

    /**
     * @return an instance of DemographicsSettingsFragment
     */
    public static TwoFactorAuthFragment newInstance() {
        return new TwoFactorAuthFragment();
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
        demographicsSettingsDTO = (DemographicDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_two_factor_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        setUpViews(view);
        setCallBacks();

    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.settings_toolbar);
        TextView title = toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText("Two-Factor authentication");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
    }

    private void setCallBacks() {
        emailSetRadioButton.setOnClickListener(this);
        smsSetRadioButton.setOnClickListener(this);
        enableDisableButton.setOnClickListener(this);
        setTextListeners();
    }

    private void setUpViews(View view) {
        emailSetRadioButton = view.findViewById(R.id.emailSetRadioButton);
        smsSetRadioButton = view.findViewById(R.id.smsSetRadioButton);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextSms = view.findViewById(R.id.editTextSms);
        emailResendTextView = view.findViewById(R.id.emailResendTextView);
        smsResendTextView = view.findViewById(R.id.smsResendTextView);
        editTextVerificationCodeSms = view.findViewById(R.id.editTextVerificationCodeSms);
        editTextVerificationCodeEmail = view.findViewById(R.id.editTextVerificationCodeEmail);
        smsTextInputLayout = view.findViewById(R.id.smsTextInputLayout);
        emailTextInputLayout = view.findViewById(R.id.emailTextInputLayout);
        enableDisableButton = view.findViewById(R.id.enableDisableButton);
        smsTextInputLayout.setVisibility(View.GONE);
        emailTextInputLayout.setVisibility(View.GONE);

       String email= getCurrentEmail();
       editTextEmail.setText(email);
       editTextEmail.setEnabled(false);

//set under line text
        setTextUnderlined(emailResendTextView);
    }


    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emailSetRadioButton:
                smsSetRadioButton.setChecked(false);
                emailSetRadioButton.setChecked(true);
                whenClicked();
                break;
            case R.id.smsSetRadioButton:
                emailSetRadioButton.setChecked(false);
                smsSetRadioButton.setChecked(true);
                whenClicked();
                break;
            case R.id.enableDisableButton:
                enableTwoFactorAuth();
                isResend=false;
                break;

        }


    }

    private void enableTwoFactorAuth() {
        if (smsSetRadioButton.isChecked() && !StringUtil.isNullOrEmpty(editTextSms.getText().toString())) {
            goForEnableSettings("sms");
        } else if (emailSetRadioButton.isChecked() && !StringUtil.isNullOrEmpty(editTextEmail.getText().toString())) {
            goForEnableSettings("email");
        }

    }

    private void goForEnableSettings(String type) {
        makeEnableTwoFactorAuth(type);
        if (type.equals("sms")){
            changeEmailDialogFragment = ChangeEmailDialogFragment.newInstance(type, editTextEmail.getText().toString());
            changeEmailDialogFragment.setLargeAlertInterface(new UpdateEmailFragment.LargeAlertInterface() {
                @Override
                public void onActionButton(String code) {
                    if (code.equals("resend")) {
                        getOtpForEmailUpdate(type);
                        //makeEnableTwoFactorAuth(type);
                    } else {
                        //verifyOtp(code);
                        makeEnableTwoFactorAuth(type);
                    }
                }
            });
           // changeEmailDialogFragment.show(getActivity().getSupportFragmentManager(), changeEmailDialogFragment.getClass().getName());
        }
    }

    private void getOtpForEmailUpdate(String type) {
        SendOtp sendOtp=new SendOtp();
        if (type.equals("email")) {
            sendOtp.setEmail(editTextEmail.getText().toString());
            sendOtp.setOtpType("change_email");
        } else if (type.equals("sms")){
            sendOtp.setPhone_number(editTextSms.getText().toString());
            sendOtp.setOtpType("change_verification_phone");
        }else {
            changeEmailDialogFragment.timer.cancel();
            changeEmailDialogFragment.cancel();
            showErrorNotification("Missing type");
            return;
        }
        Map<String, String> queryParams = new HashMap<>();
        Gson gson = new Gson();
        Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        isResend=true;
        getWorkflowServiceHelper().execute(demographicsSettingsDTO.getMetadata().getTransitions().getSend_otp(),
                updateEmailCallback,
                gson.toJson(sendOtp), queryParams,headers);


    }

    private void makeEnableTwoFactorAuth(String type) {

        SettingsList settingsList = new SettingsList();
        settingsList.setEnabled(true);
        settingsList.setVerificationType(type);
        if (type.equals("sms")&&!StringUtil.isNullOrEmpty(editTextSms.getText().toString())){
            settingsList.setPhone_number(editTextSms.getText().toString());
        }
        List<SettingsList> list = new ArrayList<>();
        list.add(settingsList);

        Payload bodyDto = new Payload();
        bodyDto.setEnabled(true);
        bodyDto.setSettingsList(list);
        Map<String, String> queryParams = new HashMap<>();
        Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        Gson gson = new Gson();
        getWorkflowServiceHelper().execute(demographicsSettingsDTO.getMetadata().getTransitions().getUpdate_two_factor_authentication_settings(),
                updateEmailCallback,
                gson.toJson(bodyDto), queryParams, headers);

    }

    WorkflowServiceCallback updateEmailCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {

            hideProgressDialog();
            if (isResend){
                SystemUtil.showSuccessToast(getContext(), "Send Successfully");
                changeEmailDialogFragment.timer.cancel();
                changeEmailDialogFragment.timer.start();
                isResend=false;
                return;
            }
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
            if (changeEmailDialogFragment!=null&&changeEmailDialogFragment.timer!=null){
                changeEmailDialogFragment.timer.cancel();
                changeEmailDialogFragment.cancel();
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            isResend=false;
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void whenClicked() {
        if (emailSetRadioButton.isChecked()) {
            emailSetRadioButton.setTextColor(ContextCompat
                    .getColor(getActivity(), com.carecloud.carepaylibrary.R.color.black));
            smsSetRadioButton.setTextColor(ContextCompat
                    .getColor(getActivity(), com.carecloud.carepaylibrary.R.color.slateGray));
            smsTextInputLayout.setVisibility(View.GONE);
            emailTextInputLayout.setVisibility(View.VISIBLE);


        }
        if (smsSetRadioButton.isChecked()) {
            smsSetRadioButton.setTextColor(ContextCompat
                    .getColor(getActivity(), com.carecloud.carepaylibrary.R.color.black));
            emailSetRadioButton.setTextColor(ContextCompat
                    .getColor(getActivity(), com.carecloud.carepaylibrary.R.color.slateGray));
            emailTextInputLayout.setVisibility(View.GONE);
            smsTextInputLayout.setVisibility(View.VISIBLE);
        }

    }

    private void setTextUnderlined(CarePayTextView textView) {
        SpannableString content = new SpannableString("Resend code");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

    }

    private void enableVerifyButton(String source) {
        if (StringUtil.isNullOrEmpty(source)) {
            setVerifyButtonEnabled(false);
        } else {
            setVerifyButtonEnabled(true);
        }
    }

    private void setVerifyButtonEnabled(boolean b) {
        enableDisableButton.setEnabled(b);
    }


    private void setTextListeners() {
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean isEmptyEmail = StringUtil.isNullOrEmpty(editTextEmail.getText().toString());
                if (!isEmptyEmail) { // clear the error
                    emailTextInputLayout.setError(null);
                    emailTextInputLayout.setErrorEnabled(false);
                    enableDisableButton.setBackgroundColor(ContextCompat
                            .getColor(getActivity(), com.carecloud.carepaylibrary.R.color.slateGray));
                }
                enableVerifyButton(editable.toString());
            }
        });
        editTextSms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean isEmptyPassword = StringUtil.isNullOrEmpty(editTextSms.getText().toString());
                if (isEmptyPassword) {
                    smsTextInputLayout.setError(null);
                    smsTextInputLayout.setErrorEnabled(false);
                }
                enableVerifyButton(editTextSms.getText().toString());
                enableDisableButton.setBackgroundColor(ContextCompat
                        .getColor(getActivity(), com.carecloud.carepaylibrary.R.color.slateGray));
            }
        });
    }


    //For callback
    public interface LargeAlertInterface {
        void onActionButton(String code);
    }

    private String getCurrentEmail() {
        DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
        return demographicsSettingsPayloadDTO.getCurrentEmail();
    }
}


