package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Intent;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.signinsignup.models.TextWatcherModel;
import com.carecloud.carepaylibray.utils.Utility;
import com.carecloud.carepaylibray.signinsignup.SigninSignupActivity;

/**
 * Created by harish_revuri on 9/7/2016.
 */
public class SignupFragment extends Fragment{


    private TextView errorFirstNameView, textMiddleNameView, errorLastNameView, errorEmailView, errorPasswordView, errorRepeatPasswordView;
    private EditText firstNameText;
    private EditText middleNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText passowrdText;
    private EditText repeatPassowrdText;
    private TextView accountExistTextView;

    private Button submitButton;

    private boolean isValidFirstName, isValidMiddleName, isValidLastName, isValidEmail, isValidPassword, isValidRepeatPassword;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.signup_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.signup_toolbar_title);
        Utility.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SigninSignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });


        submitButton = (Button) view.findViewById(R.id.signupButton);
        submitButton.setBackground(getResources().getDrawable(R.drawable.button_light_gray_background));

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidInfo()) {
                    //Submit Registration

                    Intent intent = new Intent(getContext(), DemographicsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });

        errorFirstNameView = (TextView) view.findViewById(R.id.txt_first_name_error);
        textMiddleNameView = (TextView) view.findViewById(R.id.text_middle_name_optional);
        errorLastNameView = (TextView) view.findViewById(R.id.txt_last_name_error);
        errorEmailView = (TextView) view.findViewById(R.id.txt_email_error);
        errorPasswordView = (TextView) view.findViewById(R.id.txt_create_password);
        errorRepeatPasswordView = (TextView) view.findViewById(R.id.txt_repeat_password);

        firstNameText = (EditText) view.findViewById(R.id.et_first_name);
        middleNameText = (EditText) view.findViewById(R.id.et_middle_name);
        lastNameText = (EditText) view.findViewById(R.id.et_last_name);
        emailText = (EditText) view.findViewById(R.id.emailEditText);
        passowrdText = (EditText) view.findViewById(R.id.et_create_password);
        repeatPassowrdText = (EditText) view.findViewById(R.id.et_repeat_password);

        accountExistTextView = (TextView)view.findViewById(R.id.oldUserTextView);
        Typeface editTextFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_regular.otf");
        firstNameText.setTypeface(editTextFontFamily);
        middleNameText.setTypeface(editTextFontFamily);
        lastNameText.setTypeface(editTextFontFamily);
        emailText.setTypeface(editTextFontFamily);
        passowrdText.setTypeface(editTextFontFamily);
        repeatPassowrdText.setTypeface(editTextFontFamily);
        errorEmailView.setTypeface(editTextFontFamily);
        middleNameText.setTypeface(editTextFontFamily);
        lastNameText.setTypeface(editTextFontFamily);
        emailText.setTypeface(editTextFontFamily);
        passowrdText.setTypeface(editTextFontFamily);
        repeatPassowrdText.setTypeface(editTextFontFamily);


        firstNameText.addTextChangedListener(new TextWatcherModel(TextWatcherModel.InputType.TYPE_TEXT, firstNameText, errorFirstNameView, "Enter First Name", false, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidFirstName = isValid;
                checkForButtonEnable();
            }
        }));
        middleNameText.addTextChangedListener(new TextWatcherModel(TextWatcherModel.InputType.TYPE_TEXT, middleNameText, textMiddleNameView, "Enter Middle Name", true, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidMiddleName = isValid;
                checkForButtonEnable();
            }
        }));
        lastNameText.addTextChangedListener(new TextWatcherModel(TextWatcherModel.InputType.TYPE_TEXT, lastNameText, errorLastNameView, "Enter Last Name", false, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidLastName = isValid;
                checkForButtonEnable();
            }
        }));
        emailText.addTextChangedListener(new TextWatcherModel(TextWatcherModel.InputType.TYPE_EMAIL, emailText, errorEmailView, "Enter Valid Email", false, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidEmail = isValid;
                checkForButtonEnable();
            }
        }));
        passowrdText.addTextChangedListener(new TextWatcherModel(TextWatcherModel.InputType.TYPE_PASSWORD, passowrdText, errorPasswordView, "Enter password", false, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidPassword = isValid;
                checkForButtonEnable();
            }
        }));
        repeatPassowrdText.addTextChangedListener(new TextWatcherModel(TextWatcherModel.InputType.TYPE_PASSWORD, repeatPassowrdText, errorRepeatPasswordView, "Confirm password can't be empty", false, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidRepeatPassword = isValid;
                checkForButtonEnable();
            }
        }));



        accountExistTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = ((SigninSignupActivity) getActivity()).getmFragmentManager();
                SigninFragment fragment = (SigninFragment) fm.findFragmentByTag(SigninFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new SigninFragment();
                }
                fm.beginTransaction()
                        .replace(R.id.signinLayout, fragment, SigninFragment.class.getSimpleName())
                        .commit();
            }
        });

        return view;
    }


    private boolean isValidInfo() {

        if (isValidFirstName && isValidMiddleName && isValidLastName && isValidEmail && isValidPassword && isValidRepeatPassword) {

            if (passowrdText.getText().toString().equals(repeatPassowrdText.getText().toString())) {
                return true;
            } else {
                Toast.makeText(getActivity(), "Passwords not matched", Toast.LENGTH_LONG).show();
                return false;
            }

        } else {
            Toast.makeText(getActivity(), "Fields can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private void checkForButtonEnable(){
        if (isValidFirstName && isValidMiddleName && isValidLastName && isValidEmail && isValidPassword && isValidRepeatPassword) {
            submitButton.setBackgroundResource(R.drawable.button_selector);
            submitButton.setTextColor(Color.WHITE);
        } else {
            submitButton.setBackgroundResource(R.drawable.button_light_gray_background);

        }
    }
}