package com.carecloud.carepay.practice.library.signin;

import android.content.pm.ActivityInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CustomEditTextBase;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by Jahirul Bhuiyan on 10/13/2016.
 * This is the Launcher activity for the practice app
 * Cogtino SDK use for user authentication
 * On success authentication screen will navigate next screen from the transition Json
 * On failed showing the authentication failure dialog with no navigation
 * */
public class SigninActivity extends AppCompatActivity {

    TextView signin_button;
    TextView forgotPasswordButton;
    TextView goBackTextview;

    TextInputLayout signInEmailTextInputLayout;
    TextInputLayout passwordTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setSystemUiVisibility();
        /*signInEmailTextInputLayout=(TextInputLayout)findViewById(R.id.signInEmailTextInputLayout);
        passwordTextInputLayout=(TextInputLayout)findViewById(R.id.passwordTextInputLayout);

        signInEmailTextInputLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                TextInputLayout inputLayout=(TextInputLayout)view;
                if(b) {
                    inputLayout.setHint( inputLayout.getHint().toString().toUpperCase());// WordUtils.capitalize(CustomInputEditText.this.getHint().toString()));
                }else if(!b && StringUtil.isNullOrEmpty(inputLayout.getEditText().getText().toString())){
                    inputLayout.setHint( WordUtils.capitalizeFully(inputLayout.getHint().toString()));
                }
            }

        });

        passwordTextInputLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                TextInputLayout inputLayout=(TextInputLayout)view;
                if(b) {
                    inputLayout.setHint( inputLayout.getHint().toString().toUpperCase());// WordUtils.capitalize(CustomInputEditText.this.getHint().toString()));
                }else if(!b && StringUtil.isNullOrEmpty(inputLayout.getEditText().getText().toString())){
                    inputLayout.setHint( WordUtils.capitalizeFully(inputLayout.getHint().toString()));
                }
            }

        });*/
    }

    public void setSystemUiVisibility() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
