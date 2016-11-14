package com.carecloud.carepay.practice.library.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenLabelDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeLabelsDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeSplashDTO;
import com.carecloud.carepay.practice.library.practicesetting.models.PracticeSettingDTO;
import com.carecloud.carepay.practice.library.practicesetting.models.PracticeSettingLabelDTO;
import com.carecloud.carepay.practice.library.practicesetting.services.PracticeSettingService;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by prem_mourya on 10/20/2016.
 */

public class ConfirmationPinDialog extends Dialog implements View.OnClickListener {

    private Context                         context;
    private EditText                        pinEditText;
    private CustomGothamRoundedMediumButton headerLabel;
    private CarePayTextView                 subHeaderLabel;
    private CustomGothamRoundedMediumButton dialogCancelTextView;
    private PatientModeSplashDTO patientModeSplashDTO;
    private HomeScreenDTO homeScreenDTO;

    /**
     * Constructor calling from Started Patient screen.
     *
     * @param context context
     */
    public ConfirmationPinDialog(Context context, PatientModeSplashDTO patientModeSplashDTO) {
        super(context);
        this.context = context;
        this.patientModeSplashDTO = patientModeSplashDTO;
    }

    /**
     * Constructor calling from Patient Home.
     *
     * @param context context
     */
    public ConfirmationPinDialog(Context context, HomeScreenDTO homeScreenDTO) {
        super(context);
        this.context = context;
        this.homeScreenDTO = homeScreenDTO;
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
        setContentView(R.layout.dialog_confirmation_pin);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
        onInitialization();
        onSettingStyle();
        onSetListener();
        onCheckForConstuctorObject();

    }

    /**
     * for initialization UI components  .
     */
    private void onInitialization() {
        pinEditText = (EditText) findViewById(R.id.pinEditText);
        headerLabel = (CustomGothamRoundedMediumButton) findViewById(R.id.headerLabel);
        subHeaderLabel = (CarePayTextView) findViewById(R.id.subHeaderLabel);
        dialogCancelTextView = (CustomGothamRoundedMediumButton) findViewById(R.id.dialogCancelTextView);
    }

    /**
     * for setting  UI Component Style .
     */
    private void onSettingStyle() {
        headerLabel.setTextColor(ContextCompat.getColor(context, com.carecloud.carepaylibrary.R.color.white));
        subHeaderLabel.setTextColor(ContextCompat.getColor(context, com.carecloud.carepaylibrary.R.color.white));
        dialogCancelTextView.setTextColor(ContextCompat.getColor(context, com.carecloud.carepaylibrary.R.color.bright_cerulean));
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
        if (pin.length() == 4 && pin.equalsIgnoreCase(CarePayConstants.PRACTICE_APP_MODE_DEFAULT_PIN)) {
            ((BasePracticeActivity) context).onPinConfirmationCheck(true, pin);
            dismiss();
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

    private void OnSetPinLabelsForStarted(){
        PatientModeLabelsDTO patientpinLabels = this.patientModeSplashDTO.getMetadata().getLabels();
        headerLabel.setText(patientpinLabels.getPracticeModeSwitchPinHeader());
        subHeaderLabel.setText(patientpinLabels.getPracticeModeSwitchPinEnterUnlock());
        dialogCancelTextView.setText(patientpinLabels.getPracticeModeSwitchPinCancel());
        onSetpinNumberLabel(R.id.pin_key_one,patientpinLabels.getPracticeModeSwitchPinOne());
        onSetpinNumberLabel(R.id.pin_key_two,patientpinLabels.getPracticeModeSwitchPinTwo());
        onSetpinNumberLabel(R.id.pin_key_three,patientpinLabels.getPracticeModeSwitchPinThree());
        onSetpinNumberLabel(R.id.pin_key_four,patientpinLabels.getPracticeModeSwitchPinFour());
        onSetpinNumberLabel(R.id.pin_key_five,patientpinLabels.getPracticeModeSwitchPinFive());
        onSetpinNumberLabel(R.id.pin_key_six,patientpinLabels.getPracticeModeSwitchPinSix());
        onSetpinNumberLabel(R.id.pin_key_seven,patientpinLabels.getPracticeModeSwitchPinSeven());
        onSetpinNumberLabel(R.id.pin_key_eighth,patientpinLabels.getPracticeModeSwitchPinEight());
        onSetpinNumberLabel(R.id.pin_key_nine,patientpinLabels.getPracticeModeSwitchPinNine());
        onSetpinNumberLabel(R.id.pin_key_zero,patientpinLabels.getPracticeModeSwitchPinZero());
        findViewById(R.id.mainViewLayout).setVisibility(View.VISIBLE);
    }

    private void OnSetPinLabelsForPatientHome(){
        HomeScreenLabelDTO homeScreenLabelDTO = this.homeScreenDTO.getMetadata().getLabels();
        headerLabel.setText(homeScreenLabelDTO.getPracticeModeSwitchPinHeader());
        subHeaderLabel.setText(homeScreenLabelDTO.getPracticeModeSwitchPinEnterUnlock());
        dialogCancelTextView.setText(homeScreenLabelDTO.getPracticeModeSwitchPinCancel());
        onSetpinNumberLabel(R.id.pin_key_one,homeScreenLabelDTO.getPracticeModeSwitchPinOne());
        onSetpinNumberLabel(R.id.pin_key_two,homeScreenLabelDTO.getPracticeModeSwitchPinTwo());
        onSetpinNumberLabel(R.id.pin_key_three,homeScreenLabelDTO.getPracticeModeSwitchPinThree());
        onSetpinNumberLabel(R.id.pin_key_four,homeScreenLabelDTO.getPracticeModeSwitchPinFour());
        onSetpinNumberLabel(R.id.pin_key_five,homeScreenLabelDTO.getPracticeModeSwitchPinFive());
        onSetpinNumberLabel(R.id.pin_key_six,homeScreenLabelDTO.getPracticeModeSwitchPinSix());
        onSetpinNumberLabel(R.id.pin_key_seven,homeScreenLabelDTO.getPracticeModeSwitchPinSeven());
        onSetpinNumberLabel(R.id.pin_key_eighth,homeScreenLabelDTO.getPracticeModeSwitchPinEight());
        onSetpinNumberLabel(R.id.pin_key_nine,homeScreenLabelDTO.getPracticeModeSwitchPinNine());
        onSetpinNumberLabel(R.id.pin_key_zero,homeScreenLabelDTO.getPracticeModeSwitchPinZero());
        findViewById(R.id.mainViewLayout).setVisibility(View.VISIBLE);
    }

    private void onSetpinNumberLabel(int pinViewId,String pinNumber){
        ((CustomGothamRoundedMediumButton)findViewById(pinViewId)).setText(pinNumber);
    }

    private void onCheckForConstuctorObject(){
        if(homeScreenDTO != null ){
            OnSetPinLabelsForPatientHome();
        }else if(patientModeSplashDTO != null ){
            OnSetPinLabelsForStarted();
        }
    }
}
