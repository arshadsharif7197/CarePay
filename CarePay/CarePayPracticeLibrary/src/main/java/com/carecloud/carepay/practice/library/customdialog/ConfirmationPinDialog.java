package com.carecloud.carepay.practice.library.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by prem_mourya on 10/20/2016.
 */

public class ConfirmationPinDialog extends Dialog  implements View.OnClickListener{

    private Context context;
    private EditText pinEditText;
    private CustomGothamRoundedMediumButton headerLabel;
    private CustomProxyNovaRegularLabel subHeaderLabel;
    private CustomGothamRoundedMediumButton dialogCancelTextView;


    public ConfirmationPinDialog(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirmation_pin);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 1.0);
        getWindow().setAttributes(params);
        onInitialization();
        onSettingStyle();
        onSetListener();

    }

    private void onInitialization(){
        pinEditText = (EditText)findViewById(R.id.pinEditText);
        headerLabel = (CustomGothamRoundedMediumButton)findViewById(R.id.headerLabel);
        subHeaderLabel = (CustomProxyNovaRegularLabel)findViewById(R.id.subHeaderLabel);
        dialogCancelTextView = (CustomGothamRoundedMediumButton)findViewById(R.id.dialogCancelTextView);
    }

    private void onSettingStyle(){
        headerLabel.setTextColor(ContextCompat.getColor(context, com.carecloud.carepaylibrary.R.color.white));
        subHeaderLabel.setTextColor(ContextCompat.getColor(context, com.carecloud.carepaylibrary.R.color.white));
        dialogCancelTextView.setTextColor(ContextCompat.getColor(context, com.carecloud.carepaylibrary.R.color.bright_cerulean));

        SystemUtil.setGothamRoundedMediumTypeface(context,(Button)findViewById(R.id.pin_key_one));
        SystemUtil.setGothamRoundedMediumTypeface(context,(Button)findViewById(R.id.pin_key_two));
        SystemUtil.setGothamRoundedMediumTypeface(context,(Button)findViewById(R.id.pin_key_three));
        SystemUtil.setGothamRoundedMediumTypeface(context,(Button)findViewById(R.id.pin_key_four));
        SystemUtil.setGothamRoundedMediumTypeface(context,(Button)findViewById(R.id.pin_key_five));
        SystemUtil.setGothamRoundedMediumTypeface(context,(Button)findViewById(R.id.pin_key_six));
        SystemUtil.setGothamRoundedMediumTypeface(context,(Button)findViewById(R.id.pin_key_seven));
        SystemUtil.setGothamRoundedMediumTypeface(context,(Button)findViewById(R.id.pin_key_eighth));
        SystemUtil.setGothamRoundedMediumTypeface(context,(Button)findViewById(R.id.pin_key_nine));
        SystemUtil.setGothamRoundedMediumTypeface(context,(Button)findViewById(R.id.pin_key_zero));
        SystemUtil.setGothamRoundedMediumTypeface(context,(Button)findViewById(R.id.pin_key_blank));
    }

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
}
