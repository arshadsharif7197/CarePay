package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
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
import com.carecloud.carepaylibray.activities.LibraryMainActivity;
import com.carecloud.carepaylibray.homescreen.HomeScreenActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by harish_revuri on 9/7/2016.
 */
public class SigninFragment extends Fragment implements TextWatcher {


    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signinButton;
    private Button signupButton;
    private TextView changeLanguageTextView;


    private OnSigninPageOptionsClickListner signinPageOptionsClickListner;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        emailEditText = (EditText) view.findViewById(R.id.emailEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);

        emailEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);

        signinButton = (Button) view.findViewById(R.id.SigninButton);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isValidEmail()) {
                    emailEditText.setError("Enter valid mail");
                    emailEditText.requestFocus();
                    return;
                }
                if (passwordEditText.getText().toString().isEmpty()) {
                    passwordEditText.setError("Enter Password");
                    passwordEditText.requestFocus();
                    return;
                }

                Intent intent = new Intent(getContext(), HomeScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });

        signupButton = (Button) view.findViewById(R.id.SignUpButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinPageOptionsClickListner.onOptionClick(new SignupFragment(), "signup");
            }
        });

        changeLanguageTextView = (TextView) view.findViewById(R.id.changeLanguageText);

        changeLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), LibraryMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //TODO change to common use
        Typeface editTextFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_regular.otf");
        emailEditText.setTypeface(editTextFontFamily);
        passwordEditText.setTypeface(editTextFontFamily);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        signinPageOptionsClickListner = (OnSigninPageOptionsClickListner) getActivity();
    }

    private boolean isValidEmail() {

        String email = emailEditText.getText().toString();
        Pattern pattern = Pattern.compile(SignupFragment.EMAIL_PATTERN);

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public interface OnSigninPageOptionsClickListner{
        public void onSigninButtonClick();

        public void onOptionClick(SignupFragment fragment, String fragmentTagName);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().isEmpty()) {
            signinButton.setEnabled(false);
        } else {
            signinButton.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}