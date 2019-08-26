package com.carecloud.carepay.practice.library.customdialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeSwitchPinResponseDTO;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by prem_mourya on 10/20/2016.
 */

public class ConfirmationPinDialog extends BaseDialogFragment implements View.OnClickListener {

    private static final int FULLSCREEN_VALUE = 0x10000000;

    private EditText pinEditText;
    private CarePayTextView headerLabel;
    private CarePayTextView subHeaderLabel;
    private Button dialogCancelTextView;

    private boolean isDynamicLabels;
    private TransitionDTO transitionDTOPinLink;
    private TransitionDTO languageTransition;

    /**
     * Constructor calling from  Patient screen for Switching to Practice Mode.
     *
     */
    public static ConfirmationPinDialog newInstance(TransitionDTO transitionDTOPinLink,
                                 boolean isDynamicLabels, TransitionDTO languageTransition) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, transitionDTOPinLink);
        args.putBoolean("isDymanicLabels", isDynamicLabels);
        args.putString("transitionDTOPinLink", DtoHelper.getStringDTO(languageTransition));
        ConfirmationPinDialog fragment = new ConfirmationPinDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirmation_pin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitialization();
        onSettingStyle();
        onSetListener();
        onSetPinLabels();
    }

    /**
     * for initialization UI .
     *
     * @param savedInstanceState for saving state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            transitionDTOPinLink = DtoHelper.getConvertedDTO(TransitionDTO.class, args);
            isDynamicLabels = args.getBoolean("isDynamicsLabels");
            languageTransition = DtoHelper.getConvertedDTO(TransitionDTO.class, args.getString("transitionDTOPinLink"));
        }
    }

    public void setNavigationBarVisibility() {

        View decorView = getDialog().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | FULLSCREEN_VALUE;
        decorView.setSystemUiVisibility(uiOptions);
    }


    /**
     * for initialization UI components  .
     */
    private void onInitialization() {
        pinEditText = (EditText) findViewById(R.id.pinEditText);
        headerLabel = (CarePayTextView) findViewById(R.id.headerLabel);
        subHeaderLabel = (CarePayTextView) findViewById(R.id.subHeaderLabel);
        dialogCancelTextView = (Button) findViewById(R.id.dialogCancelTextView);
    }

    /**
     * for setting  UI Component Style .
     */
    private void onSettingStyle() {
        headerLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        subHeaderLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.confirm_pin_sub_title));
        dialogCancelTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    /**
     * set listner for components .
     */
    private void onSetListener() {
        (findViewById(R.id.pin_key_one)).setOnClickListener(this);
        (findViewById(R.id.pin_key_two)).setOnClickListener(this);
        (findViewById(R.id.pin_key_three)).setOnClickListener(this);
        (findViewById(R.id.pin_key_four)).setOnClickListener(this);
        (findViewById(R.id.pin_key_five)).setOnClickListener(this);
        (findViewById(R.id.pin_key_six)).setOnClickListener(this);
        (findViewById(R.id.pin_key_seven)).setOnClickListener(this);
        (findViewById(R.id.pin_key_eighth)).setOnClickListener(this);
        (findViewById(R.id.pin_key_nine)).setOnClickListener(this);
        (findViewById(R.id.pin_key_zero)).setOnClickListener(this);
        (findViewById(R.id.pin_key_blank)).setOnClickListener(this);
        (findViewById(R.id.pin_key_clear)).setOnClickListener(this);
        (findViewById(R.id.dialogCancelTextView)).setOnClickListener(this);


    }

    /**
     * for components listener .
     *
     * @param view for clicked view
     */
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.pin_key_clear) {
            onEnterPinNumberClear();
        } else if (viewId == R.id.dialogCancelTextView) {
            cancel();
        } else {
            String buttonValue = ((Button) view).getText().toString();
            onEnterPinNumber(buttonValue);
        }
        validatePin();
    }

    private void validatePin() {
        String pin = pinEditText.getText().toString();
        if (pin.length() == 4) {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("pin", pin);
            queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
            queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
            getWorkflowServiceHelper().execute(this.transitionDTOPinLink, commonTransitionCallback, queryMap);
        }
    }

    private void onEnterPinNumber(String pinNumberStr) {
        String actualValue = pinEditText.getText().toString();
        if (actualValue.length() < 4) {
            pinNumberStr = actualValue + pinNumberStr;
            pinEditText.setText(pinNumberStr);
        }
    }

    private void onEnterPinNumberClear() {
        String actualValue = pinEditText.getText().toString();
        if (actualValue.length() > 0) {
            actualValue = actualValue.substring(0, actualValue.length() - 1);
            pinEditText.setText(actualValue);
        }
    }

    private void onSetPinLabels() {
        headerLabel.setText(Label.getLabel("practice_mode_switch_pin_header"));
        subHeaderLabel.setText(Label.getLabel("practice_mode_switch_pin_enter_unlock"));
        dialogCancelTextView.setText(Label.getLabel("practice_mode_switch_pin_cancel"));
        if (this.isDynamicLabels) {
            onSetpinNumberLabel(R.id.pin_key_one, Label.getLabel("practice_mode_switch_pin_one"));
            onSetpinNumberLabel(R.id.pin_key_two, Label.getLabel("practice_mode_switch_pin_two"));
            onSetpinNumberLabel(R.id.pin_key_three, Label.getLabel("practice_mode_switch_pin_three"));
            onSetpinNumberLabel(R.id.pin_key_four, Label.getLabel("practice_mode_switch_pin_four"));
            onSetpinNumberLabel(R.id.pin_key_five, Label.getLabel("practice_mode_switch_pin_five"));
            onSetpinNumberLabel(R.id.pin_key_six, Label.getLabel("practice_mode_switch_pin_six"));
            onSetpinNumberLabel(R.id.pin_key_seven, Label.getLabel("practice_mode_switch_pin_seven"));
            onSetpinNumberLabel(R.id.pin_key_eighth, Label.getLabel("practice_mode_switch_pin_eight"));
            onSetpinNumberLabel(R.id.pin_key_nine, Label.getLabel("practice_mode_switch_pin_nine"));
            onSetpinNumberLabel(R.id.pin_key_zero, Label.getLabel("practice_mode_switch_pin_zero"));
        }
        findViewById(R.id.mainViewLayout).setVisibility(View.VISIBLE);
    }

    private void onSetpinNumberLabel(int pinViewId, String pinNumber) {
        ((Button) findViewById(pinViewId)).setText(pinNumber);
    }

    private WorkflowServiceCallback commonTransitionCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Gson gson = new Gson();
            PatientModeSwitchPinResponseDTO patientModeSwitchPinResponseDTO = gson.fromJson(workflowDTO.toString(), PatientModeSwitchPinResponseDTO.class);
            if (patientModeSwitchPinResponseDTO.getPayload().getPinpad().getPayload()) {
                BasePracticeActivity practiceActivity = (BasePracticeActivity) getContext();
                if(practiceActivity.getApplicationMode().getUserPracticeDTO() != null) {
                    String patientLanguage = practiceActivity.getApplicationPreferences().getUserLanguage();
                    if(!patientLanguage.equals(CarePayConstants.DEFAULT_LANGUAGE)) {
                        final Map<String, String> headers = practiceActivity.getWorkflowServiceHelper().getApplicationStartHeaders();
                        headers.put("username", practiceActivity.getApplicationPreferences().getUserName());
                        practiceActivity.getApplicationPreferences().setUserLanguage(CarePayConstants.DEFAULT_LANGUAGE);
                        practiceActivity.changeLanguage(languageTransition, CarePayConstants.DEFAULT_LANGUAGE, headers, new BasePracticeActivity.SimpleCallback() {
                            @Override
                            public void callback() {
                                //do nothing more
                            }
                        });
                    }
                    practiceActivity.getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE);
                    practiceActivity.getAppAuthorizationHelper().setUser(practiceActivity.getApplicationMode().getUserPracticeDTO().getUserName());
                    practiceActivity.onPinConfirmationCheck(true, pinEditText.getText().toString());
                    dismiss();
                    identifyPracticeUser(patientModeSwitchPinResponseDTO.getPayload().getUserPracticesList().get(0).getUserId());

                }
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            Gson gson = new Gson();
            String displayMessage;
            try {
                PinError pinError = gson.fromJson(exceptionMessage, PinError.class);
                displayMessage = pinError.getException();
            } catch (JsonSyntaxException jsx) {
                jsx.printStackTrace();
                displayMessage = "Pin Error, Please try again";
            }

            hideProgressDialog();
            pinEditText.setText("");
            SystemUtil.showErrorToast(getContext(), displayMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private class PinError {
        @SerializedName("exception")
        private String exception;

        public String getException() {
            return exception;
        }

        public void setException(String exception) {
            this.exception = exception;
        }
    }

    private void identifyPracticeUser(String userId){
        MixPanelUtil.setUser(getContext(), userId, null);
        MixPanelUtil.addCustomPeopleProperty(getString(R.string.people_is_practice_user), true);
    }

}
