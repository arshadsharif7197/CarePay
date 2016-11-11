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

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.practicesetting.models.PracticeSettingDTO;
import com.carecloud.carepay.practice.library.practicesetting.models.PracticeSettingLabelDTO;
import com.carecloud.carepay.practice.library.practicesetting.services.PracticeSettingService;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by prem_mourya on 10/20/2016.
 */

public class ConfirmationPinDialog extends Dialog  implements View.OnClickListener{

    private Context context;
    private EditText pinEditText;
    private CustomGothamRoundedMediumButton headerLabel;
    private CarePayTextView subHeaderLabel;
    private CustomGothamRoundedMediumButton dialogCancelTextView;
    private PracticeSettingDTO practiceSettingResponse;

    /**
     * Constructor.
     * @param context context
     */
    public ConfirmationPinDialog(Context context) {
        super(context);
        this.context=context;
    }

    /**
     * for initialization UI .
     * @param savedInstanceState for saving state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirmation_pin);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getPracticeSetting();
        setCancelable(false);
        onInitialization();
        onSettingStyle();
        onSetListener();

    }

    /**
     * for initialization UI components  .
     */
    private void onInitialization(){
        pinEditText = (EditText)findViewById(R.id.pinEditText);
        headerLabel = (CustomGothamRoundedMediumButton)findViewById(R.id.headerLabel);
        subHeaderLabel = (CarePayTextView)findViewById(R.id.subHeaderLabel);
        dialogCancelTextView = (CustomGothamRoundedMediumButton)findViewById(R.id.dialogCancelTextView);
    }

    /**
     * for setting  UI Component Style .
     */
    private void onSettingStyle(){
        headerLabel.setTextColor(ContextCompat.getColor(context, com.carecloud.carepaylibrary.R.color.white));
        subHeaderLabel.setTextColor(ContextCompat.getColor(context, com.carecloud.carepaylibrary.R.color.white));
        dialogCancelTextView.setTextColor(ContextCompat.getColor(context, com.carecloud.carepaylibrary.R.color.bright_cerulean));
    }

    /**
     * set listner for components .
     */
    private void onSetListener(){
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
     * @param view for clicked view
     */
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if(viewId == R.id.pin_key_clear){
            onEnterPinNumberClear();
        } else  if(viewId == R.id.dialogCancelTextView){
            cancel();
        }else {
            String buttonValue = ((Button) view).getText().toString();
            onEnterPinNumber(buttonValue);
        }
        validatePin();
    }

    private void validatePin(){
        String actualValue = pinEditText.getText().toString();
        if(actualValue !=null && actualValue.length()==4 &&
                actualValue.equalsIgnoreCase(CarePayConstants.PRACTICE_APP_MODE_DEFAULT_PIN)) {
            ((BasePracticeActivity)context).onPinConfirmationCheck(true,practiceSettingResponse);
            dismiss();
        }
    }

    private void onEnterPinNumber(String pinNumberStr){
        String actualValue = pinEditText.getText().toString();
        if(actualValue !=null && actualValue.length() <4) {
            pinNumberStr = actualValue + pinNumberStr;
            pinEditText.setText(pinNumberStr);
        }
    }

    private void onEnterPinNumberClear(){
        String actualValue = pinEditText.getText().toString();
        if(actualValue !=null && actualValue.length() > 0) {
            actualValue = actualValue.substring(0,actualValue.length()-1);
            pinEditText.setText(actualValue);
        }
    }

    private void getPracticeSetting(){
        PracticeSettingService aptService = (new BaseServiceGenerator(context)).createService(PracticeSettingService.class);
        Call<PracticeSettingDTO> call = aptService.getPracticeSettingInformation();
        call.enqueue(new Callback<PracticeSettingDTO>() {

            @Override
            public void onResponse(Call<PracticeSettingDTO> call, Response<PracticeSettingDTO> response) {
                practiceSettingResponse = response.body();
                PracticeSettingLabelDTO practiceSettingLabels = response.body().getMetadata().getLabel();
                onSetViewLabels(practiceSettingLabels);
            }

            @Override
            public void onFailure(Call<PracticeSettingDTO> call, Throwable throwable) {
               cancel();
            }
        });
    }

    private void onSetViewLabels(PracticeSettingLabelDTO practiceSettingLabels){
        headerLabel.setText(practiceSettingLabels.getPracticeSettingPinPracticeMode());
        subHeaderLabel.setText(practiceSettingLabels.getPracticeSettingPinEnterUnlockPracticeMode());
        dialogCancelTextView.setText(practiceSettingLabels.getPracticeSettingPinCancel());
        findViewById(R.id.mainViewLayout).setVisibility(View.VISIBLE);
    }
}
