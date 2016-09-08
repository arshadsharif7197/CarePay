package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by harish_revuri on 9/7/2016.
 */
public class SigninFragment extends android.support.v4.app.Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signinButton;
    private Button signupButton;
    private OnSigninPageOptionsClickListner signinPageOptionsClickListner;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        emailEditText = (EditText)view.findViewById(R.id.emailEditText);
        passwordEditText = (EditText)view.findViewById(R.id.passwordEditText);

        signinButton = (Button)view.findViewById(R.id.SigninButton);
        signupButton = (Button)view.findViewById(R.id.SignUpButton);

        Typeface editTextFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_regular.otf");
        emailEditText.setTypeface(editTextFontFamily);
        passwordEditText.setTypeface(editTextFontFamily);


        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isvalidData()){
                    //Navigate To next Page
                }else{
                    Toast.makeText(getActivity(),"Please fill required fields.",Toast.LENGTH_LONG).show();
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinPageOptionsClickListner.onOptionClick(new SignupFragment(),"signup");
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        signinPageOptionsClickListner =(OnSigninPageOptionsClickListner) context;
    }

    private boolean isvalidData() {
        boolean isvalid = true;

        if(!isValidmail()){
            emailEditText.setError("Enter valid mail");
            isvalid = false;
        }
        if (passwordEditText.getText().toString().length() <= 0) {
            passwordEditText.setError("Enter Password");
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

    public  interface OnSigninPageOptionsClickListner{
        public void onSigninButtonClick();
        public void onOptionClick(SignupFragment fragment, String fragmentTagName);
    }
}