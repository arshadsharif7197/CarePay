package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Intent;

import android.content.Context;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.homescreen.HomeScreenActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by harish_revuri on 9/7/2016.
 */
public class SignupFragment extends Fragment {


    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private EditText firstNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private Button btnSignup;
    private TextView tvAccountExist;
    private OnSignupPageOptionsClickListner onOptionClick;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        firstNameEditText = (EditText) view.findViewById(R.id.firstNameEditText);
        emailEditText = (EditText) view.findViewById(R.id.emailsignupEditText);
        passwordEditText = (EditText) view.findViewById(R.id.createPasswordEditText);
        repeatPasswordEditText = (EditText) view.findViewById(R.id.repeatPasswordEditText);

        btnSignup = (Button) view.findViewById(R.id.signupButton);

        tvAccountExist = (TextView) view.findViewById(R.id.oldUserTextView);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isvalidData()) {
                    //TODO submit details to Server.


                    Intent homescreenintent= new Intent(getActivity(), HomeScreenActivity.class);
                    startActivity(homescreenintent);
                } else {
                    Toast.makeText(getActivity(), "Please fill required fields.", Toast.LENGTH_LONG).show();
                }
            }
        });


        tvAccountExist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Navigate screen to Login PAGE.
                onOptionClick.onSignupOptionClick(SignupFragment.this, "signup");

            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onOptionClick = (OnSignupPageOptionsClickListner) context;
    }

    private boolean isvalidData() {
        boolean isvalid = true;

        if (firstNameEditText.getText().toString().length() <= 0) {
            firstNameEditText.setError("Enter valid name");
            isvalid = false;
        }

        if (!isValidmail()) {
            emailEditText.setError("Enter valid mail");
            isvalid = false;
        }

        if (!passwordEditText.getText().toString().equalsIgnoreCase(repeatPasswordEditText.getText().toString())) {
            passwordEditText.setText("");
            repeatPasswordEditText.setText("");
            Toast.makeText(getActivity(), "Password are not matched.", Toast.LENGTH_LONG).show();
            isvalid = false;
        }

        return isvalid;
    }

    private boolean isValidmail() {
        String email = emailEditText.getText().toString();
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public interface OnSignupPageOptionsClickListner {

        public void onSignupButtonClick();

        public void onSignupOptionClick(Fragment fragment, String fragmentTagName);

    }
}