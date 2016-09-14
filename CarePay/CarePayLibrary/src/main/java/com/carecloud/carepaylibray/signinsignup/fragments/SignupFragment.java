package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Intent;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
public class SignupFragment extends Fragment {

    private TextInputLayout errorFirstNameView, textMiddleNameView, errorLastNameView, errorEmailView, errorPasswordView, errorRepeatPasswordView;
    private EditText firstNameText;
    private EditText middleNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText repeatPasswordText;
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
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SigninSignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });


        submitButton = (Button) view.findViewById(R.id.submitSignupButton);
        submitButton.setBackground(getResources().getDrawable(R.drawable.button_light_gray_background));
        Typeface buttonFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/gotham_rounded_medium.otf");
        submitButton.setTypeface(buttonFontFamily);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidInfo()) {
                    //Submit Registration
                    Intent intent = new Intent(getActivity(), DemographicsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });


        errorFirstNameView = (TextInputLayout)view.findViewById(R.id.firstNameTextInputLayout);
        textMiddleNameView = (TextInputLayout)view.findViewById(R.id.middleNameTextInputLayout);
        errorLastNameView = (TextInputLayout) view.findViewById(R.id.lastNameTextInputLayout);
        errorEmailView = (TextInputLayout) view.findViewById(R.id.emailTextInputLayout);
        errorPasswordView = (TextInputLayout) view.findViewById(R.id.createPasswordTextInputLayout);
        errorRepeatPasswordView = (TextInputLayout) view.findViewById(R.id.repeatPasswordTextInputLayout);


        firstNameText = (EditText) view.findViewById(R.id.firstNameEditText);
        middleNameText = (EditText) view.findViewById(R.id.middleNameEditText);
        lastNameText = (EditText) view.findViewById(R.id.lastNameEditText);
        emailText = (EditText) view.findViewById(R.id.emailEditText);
        passwordText = (EditText) view.findViewById(R.id.createPasswordEditText);
        repeatPasswordText = (EditText) view.findViewById(R.id.repeatPasswordEditText);

        accountExistTextView = (TextView)view.findViewById(R.id.oldUserTextView);

        accountExistTextView = (TextView) view.findViewById(R.id.oldUserTextView);
        Typeface editTextFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_regular.otf");
        Typeface textViewFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_semibold.otf");
        Typeface floatingFontfamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_semibold.otf");

        firstNameText.setTypeface(textViewFontFamily);
        middleNameText.setTypeface(textViewFontFamily);
        lastNameText.setTypeface(textViewFontFamily);
        emailText.setTypeface(textViewFontFamily);
        passwordText.setTypeface(textViewFontFamily);
        repeatPasswordText.setTypeface(textViewFontFamily);

        errorFirstNameView.setTypeface(editTextFontFamily);
        textMiddleNameView.setTypeface(editTextFontFamily);
        errorLastNameView.setTypeface(editTextFontFamily);
        errorEmailView.setTypeface(editTextFontFamily);
        errorPasswordView.setTypeface(editTextFontFamily);
        errorRepeatPasswordView.setTypeface(editTextFontFamily);


        accountExistTextView.setTypeface(textViewFontFamily);

        firstNameText.addTextChangedListener(new TextWatcherModel(editTextFontFamily, floatingFontfamily,TextWatcherModel.InputType.TYPE_TEXT, firstNameText, errorFirstNameView, "Enter First Name", false, new TextWatcherModel.OnInputChangedListner() {
           @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidFirstName = isValid;
                checkForButtonEnable();
            }
        }));
        middleNameText.addTextChangedListener(new TextWatcherModel(editTextFontFamily, floatingFontfamily,TextWatcherModel.InputType.TYPE_TEXT, middleNameText, textMiddleNameView, "Enter Middle Name", true, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidMiddleName = isValid;
                checkForButtonEnable();
            }
        }));
        lastNameText.addTextChangedListener(new TextWatcherModel(editTextFontFamily, floatingFontfamily,TextWatcherModel.InputType.TYPE_TEXT, lastNameText, errorLastNameView, "Enter Last Name", false, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidLastName = isValid;
                checkForButtonEnable();
            }
        }));
        emailText.addTextChangedListener(new TextWatcherModel(editTextFontFamily, floatingFontfamily,TextWatcherModel.InputType.TYPE_EMAIL, emailText, errorEmailView, "Enter Valid Email", false, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidEmail = isValid;
                checkForButtonEnable();
            }
        }));
        passwordText.addTextChangedListener(new TextWatcherModel(editTextFontFamily, floatingFontfamily,TextWatcherModel.InputType.TYPE_PASSWORD, passwordText, errorPasswordView, "Enter password", false, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidPassword = isValid;
                checkForButtonEnable();
            }
        }));
        repeatPasswordText.addTextChangedListener(new TextWatcherModel(editTextFontFamily, floatingFontfamily,TextWatcherModel.InputType.TYPE_PASSWORD, repeatPasswordText, errorRepeatPasswordView, "Confirm password can't be empty", false, new TextWatcherModel.OnInputChangedListner() {
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
                        .replace(R.id.signin_layout, fragment, SigninFragment.class.getSimpleName())
                        .commit();
            }
        });

        return view;
    }

    private boolean isValidInfo() {

        if (isValidFirstName && isValidMiddleName && isValidLastName && isValidEmail && isValidPassword && isValidRepeatPassword) {

            if (passwordText.getText().toString().equals(repeatPasswordText.getText().toString())) {
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

    private void checkForButtonEnable() {
        if (isValidFirstName && isValidMiddleName && isValidLastName && isValidEmail && isValidPassword && isValidRepeatPassword) {
            submitButton.setEnabled(true);
            submitButton.setBackgroundResource(R.drawable.button_selector);
            submitButton.setTextColor(Color.WHITE);
        } else {
            submitButton.setEnabled(false);
            submitButton.setBackgroundResource(R.drawable.button_light_gray_background);

        }
    }
}