package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Intent;

import android.content.Context;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.homescreen.HomeScreenActivity;
import com.carecloud.carepaylibray.utils.Utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by harish_revuri on 9/7/2016.
 */
public class SignupFragment extends Fragment implements TextWatcher{


    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private Button signUpButton;
    private TextView accountExistTextView;
    private OnSignupPageOptionsClickListner onOptionClick;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.signup_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.signup_toolbar_title);
        Utility.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        firstNameEditText = (EditText) view.findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText)view.findViewById(R.id.lastNameEditText);
        emailEditText = (EditText) view.findViewById(R.id.emailsignupEditText);
        passwordEditText = (EditText) view.findViewById(R.id.createPasswordEditText);
        repeatPasswordEditText = (EditText) view.findViewById(R.id.repeatPasswordEditText);

        signUpButton = (Button) view.findViewById(R.id.signupButton);

        firstNameEditText.addTextChangedListener(this);
        lastNameEditText.addTextChangedListener(this);
        emailEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);
        repeatPasswordEditText.addTextChangedListener(this);

        accountExistTextView = (TextView) view.findViewById(R.id.oldUserTextView);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(firstNameEditText.getText().toString().isEmpty()){
                    firstNameEditText.setError("Please enter your first Name");
                    firstNameEditText.requestFocus();
                    return;
                }
                if(lastNameEditText.getText().toString().isEmpty()){
                    lastNameEditText.setError("Please enter your last Name");
                    lastNameEditText.requestFocus();
                    return;
                }
                if (!isValidEmail()) {
                    emailEditText.setError("Please enter valid Email ID");
                    emailEditText.requestFocus();
                    return;
                }
                if(passwordEditText.getText().toString().isEmpty()){
                    passwordEditText.setError("Please enter password");
                    passwordEditText.requestFocus();
                    return;
                }
                if(repeatPasswordEditText.getText().toString().isEmpty()){
                    repeatPasswordEditText.setError("Please re-enter your password");
                    return;
                }
                if (!passwordEditText.getText().toString().equalsIgnoreCase(repeatPasswordEditText.getText().toString())) {
                    passwordEditText.setText("");
                    repeatPasswordEditText.setText("");
                    Toast.makeText(getActivity(), "Password are not matched.", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent homescreenintent= new Intent(getActivity(), HomeScreenActivity.class);
                startActivity(homescreenintent);
            }
        });


        accountExistTextView.setOnClickListener(new View.OnClickListener() {
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

//    private boolean isvalidData() {
//        boolean isvalid = true;
//
//        if (firstNameEditText.getText().toString().isEmpty()) {
//            firstNameEditText.setError("Enter valid name");
//            return true;
//        }
//
//        if (!isValidEmail()) {
//            emailEditText.setError("Enter valid mail");
//            isvalid = false;
//        }
//
//        if (!passwordEditText.getText().toString().equalsIgnoreCase(repeatPasswordEditText.getText().toString())) {
//            passwordEditText.setText("");
//            repeatPasswordEditText.setText("");
//            Toast.makeText(getActivity(), "Password are not matched.", Toast.LENGTH_LONG).show();
//            isvalid = false;
//        }
//
//        return isvalid;
//    }

    private boolean isValidEmail() {
        String email = emailEditText.getText().toString();
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().isEmpty()) {
            signUpButton.setEnabled(false);
        } else {
            signUpButton.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

        if (editable.toString().isEmpty()){
            signUpButton.setEnabled(false);
        }
        if(editable.toString().isEmpty()){
            signUpButton.setEnabled(true);
        }
    }


    public interface OnSignupPageOptionsClickListner {

        public void onSignupButtonClick();

        public void onSignupOptionClick(Fragment fragment, String fragmentTagName);

    }
}