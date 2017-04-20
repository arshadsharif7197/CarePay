package com.carecloud.carepay.practice.library.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeSwitchPinDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeSwitchPinResponseDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by prem_mourya on 10/20/2016.
 */

public class ConfirmationPinDialog extends Dialog implements View.OnClickListener {

    private static final int FULLSCREEN_VALUE = 0x10000000;

    private Context context;
    private EditText pinEditText;
    private CarePayTextView headerLabel;
    private CarePayTextView subHeaderLabel;
    private CustomGothamRoundedMediumButton dialogCancelTextView;

    private PatientModeSwitchPinDTO patientModeSwitchPinDTO;
    private boolean isDynamicLabels ;
    private TransitionDTO transitionDTOPinLink;
    private ConfirmationPinDialogListener callBack;

    public interface ConfirmationPinDialogListener {
        void onError(String errorMessage);
    }

    /**
     * Constructor calling from  Patient screen for Switching to Practice Mode.
     *
     * @param context context
     */
    public ConfirmationPinDialog(Context context, TransitionDTO transitionDTOPinLink, PatientModeSwitchPinDTO patientModeSwitchPinDTO, boolean isDynamicLabels, ConfirmationPinDialogListener callBack) {
        super(context);
        this.context = context;
        this.transitionDTOPinLink = transitionDTOPinLink;
        this.patientModeSwitchPinDTO = patientModeSwitchPinDTO;
        this.isDynamicLabels = isDynamicLabels;
        this.callBack = callBack;
    }

    /**
     * for initialization UI .
     *
     * @param savedInstanceState for saving state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setNavigationBarVisibility();
        setContentView(R.layout.dialog_confirmation_pin);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
        onInitialization();
        onSettingStyle();
        onSetListener();
        onSetPinLabels();

    }

    private void setNavigationBarVisibility() {

        View decorView = getWindow().getDecorView();
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
        dialogCancelTextView = (CustomGothamRoundedMediumButton) findViewById(R.id.dialogCancelTextView);
    }

    /**
     * for setting  UI Component Style .
     */
    private void onSettingStyle() {
        headerLabel.setTextColor(ContextCompat.getColor(context, R.color.white));
        subHeaderLabel.setTextColor(ContextCompat.getColor(context, R.color.confirm_pin_sub_title));
        dialogCancelTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
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
        if (pin.length() == 4 ) {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("pin", pin);
            queryMap.put("practice_mgmt", ((ISession) context).getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
            queryMap.put("practice_id", ((ISession) context).getApplicationMode().getUserPracticeDTO().getPracticeId());
            ((ISession) context).getWorkflowServiceHelper().execute(this.transitionDTOPinLink, commonTransitionCallback, queryMap);
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

    private void onSetPinLabels(){
        headerLabel.setText(patientModeSwitchPinDTO.getPracticeModeSwitchPinHeader());
        subHeaderLabel.setText(patientModeSwitchPinDTO.getPracticeModeSwitchPinEnterUnlock());
        dialogCancelTextView.setText(patientModeSwitchPinDTO.getPracticeModeSwitchPinCancel());
        if(this.isDynamicLabels) {
            onSetpinNumberLabel(R.id.pin_key_one, patientModeSwitchPinDTO.getPracticeModeSwitchPinOne());
            onSetpinNumberLabel(R.id.pin_key_two, patientModeSwitchPinDTO.getPracticeModeSwitchPinTwo());
            onSetpinNumberLabel(R.id.pin_key_three, patientModeSwitchPinDTO.getPracticeModeSwitchPinThree());
            onSetpinNumberLabel(R.id.pin_key_four, patientModeSwitchPinDTO.getPracticeModeSwitchPinFour());
            onSetpinNumberLabel(R.id.pin_key_five, patientModeSwitchPinDTO.getPracticeModeSwitchPinFive());
            onSetpinNumberLabel(R.id.pin_key_six, patientModeSwitchPinDTO.getPracticeModeSwitchPinSix());
            onSetpinNumberLabel(R.id.pin_key_seven, patientModeSwitchPinDTO.getPracticeModeSwitchPinSeven());
            onSetpinNumberLabel(R.id.pin_key_eighth, patientModeSwitchPinDTO.getPracticeModeSwitchPinEight());
            onSetpinNumberLabel(R.id.pin_key_nine, patientModeSwitchPinDTO.getPracticeModeSwitchPinNine());
            onSetpinNumberLabel(R.id.pin_key_zero, patientModeSwitchPinDTO.getPracticeModeSwitchPinZero());
        }
        findViewById(R.id.mainViewLayout).setVisibility(View.VISIBLE);
    }

    private void onSetpinNumberLabel(int pinViewId,String pinNumber){
        ((CustomGothamRoundedMediumButton)findViewById(pinViewId)).setText(pinNumber);
    }

    private WorkflowServiceCallback commonTransitionCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ((ISession) context).showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ((ISession) context).hideProgressDialog();
            Gson gson = new Gson();
            PatientModeSwitchPinResponseDTO patientModeSwitchPinResponseDTO =   gson.fromJson(workflowDTO.toString(),PatientModeSwitchPinResponseDTO.class);
            if(patientModeSwitchPinResponseDTO.getPayload().getPinpad().getPayload()) {
                ((ISession) context).getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE);
                ((BasePracticeActivity) context).onPinConfirmationCheck(true, pinEditText.getText().toString());
                dismiss();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            Gson gson = new Gson();
            String displayMessage;
            try{
                PinError pinError = gson.fromJson(exceptionMessage, PinError.class);
                displayMessage = pinError.getException();
            }catch (JsonSyntaxException jsx){
                jsx.printStackTrace();
                displayMessage = "Pin Error, Please try again";
            }

            ((ISession) context).hideProgressDialog();
            pinEditText.setText("");
            SystemUtil.showErrorToast(getContext(), displayMessage);
            Log.e(context.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private class PinError{
        @SerializedName("exception")
        private String exception;

        public String getException() {
            return exception;
        }

        public void setException(String exception) {
            this.exception = exception;
        }
    }

}
