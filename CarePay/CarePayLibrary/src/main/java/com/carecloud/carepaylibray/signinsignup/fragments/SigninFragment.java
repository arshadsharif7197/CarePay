package com.carecloud.carepaylibray.signinsignup.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.activities.MainActivityLibrary;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.signinsignup.SigninSignupActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by harish_revuri on 9/7/2016.
 */
public class SigninFragment extends Fragment {


    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signinButton;
    private Button signupButton;
    private TextView changeLanguageTextView;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    private boolean isValidEmail;
    private boolean isValidPassword;

    private OnSigninPageOptionsClickListner signinPageOptionsClickListner;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        emailEditText = (EditText)view.findViewById(R.id.emailEditText);
        passwordEditText = (EditText)view.findViewById(R.id.passwordEditText);

        tilEmail = (TextInputLayout) view.findViewById(R.id.til_email);
        tilPassword =(TextInputLayout) view.findViewById(R.id.til_password);

        signinButton = (Button)view.findViewById(R.id.SigninButton);
        signupButton = (Button)view.findViewById(R.id.SignUpButton);
        changeLanguageTextView = (TextView) view.findViewById(R.id.changeLanguageText);

        changeLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), MainActivityLibrary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();

            }
        });

        Typeface editTextFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_regular.otf");
        emailEditText.setTypeface(editTextFontFamily);
        passwordEditText.setTypeface(editTextFontFamily);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinPageOptionsClickListner.onOptionClick(new SignupFragment(),"signup");
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailEditText.setText("");
        passwordEditText.setText("");
        emailEditText.addTextChangedListener(emailTextWatcher);
        passwordEditText.addTextChangedListener(passwordtextWatcher);
    }

    @Override
    public void onPause() {
        super.onPause();
        emailEditText.removeTextChangedListener(emailTextWatcher);
        passwordEditText.removeTextChangedListener(passwordtextWatcher);
    }

    TextWatcher emailTextWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(!isValidmail()){
                isValidEmail = false;
                tilEmail.setError("Enter valid mail id.");
            }else{
                tilEmail.setError(null);
                isValidEmail = true;
                checkForSigninEnable();
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    TextWatcher passwordtextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(passwordEditText.getText().length()<=0){
                isValidPassword=false;
                tilPassword.setError("Enter password");
            }else{
                isValidPassword=true;
                tilPassword.setError(null);
                checkForSigninEnable();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void checkForSigninEnable() {

        if(isValidEmail && isValidPassword){
            signinButton.setEnabled(true);
            signinButton.setBackground(getResources().getDrawable(R.drawable.agree_button_background));
            signinButton.setTextColor(getResources().getColor(R.color.white));
            signinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isvalidData()){

//                        Intent intent = new Intent(getContext(), DemographicsActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        getActivity().finish();

                    }else{

                        Toast.makeText(getActivity(),"Please fill required fields.",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            signinButton.setEnabled(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        signinPageOptionsClickListner =(OnSigninPageOptionsClickListner) getActivity();



    }

    private boolean isvalidData() {
        boolean isvalid = true;


        if(!isValidmail()){
            emailEditText.setError("Enter valid mail");
            isvalid = false;
        }
        if (passwordEditText.getText().toString().length() <= 0) {
            tilPassword.setError("Enter Password");
            isvalid = false;
        }

        return isvalid;
    }

    private boolean isValidmail() {

        String email = emailEditText.getText().toString();
        Pattern pattern = Pattern.compile(SignupFragment.EMAIL_PATTERN);

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public interface OnSigninPageOptionsClickListner{



        public void onSigninButtonClick();
        public void onOptionClick(SignupFragment fragment, String fragmentTagName);

    }
}

