package com.carecloud.carepaylibray.signinsignup.fragments;

import android.graphics.Typeface;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by harish_revuri on 9/7/2016.
 */
public class SignupFragment extends Fragment{

    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRepeatPassword;
    private Button btnSignup;
    private TextView textViewAccountExist;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        editTextName = (EditText) view.findViewById(R.id.fullnameEditText);
        editTextEmail = (EditText) view.findViewById(R.id.emailsignupEditText);
        editTextPassword = (EditText) view.findViewById(R.id.createPasswordEditText);
        editTextRepeatPassword = (EditText) view.findViewById(R.id.repeatPasswordEditText);

        btnSignup = (Button) view.findViewById(R.id.signupButton);

        textViewAccountExist = (TextView) view.findViewById(R.id.oldUserTextView);

        Typeface editTextFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_regular.otf");
        editTextName.setTypeface(editTextFontFamily);
        editTextEmail.setTypeface(editTextFontFamily);
        editTextPassword.setTypeface(editTextFontFamily);
        editTextRepeatPassword.setTypeface(editTextFontFamily);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isvalidData()) {
                    //TODO submit details to Server.
                } else {
                    Toast.makeText(getActivity(), "Please fill required fields.", Toast.LENGTH_LONG).show();
                }
            }
        });


        textViewAccountExist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Navigate screen to Login PAGE.
            }
        });
        return view;
    }

    private boolean isvalidData() {
        boolean isvalid = true;

        if (editTextName.getText().toString().length() <= 0) {
            editTextName.setError("Enter valid name");
            isvalid = false;
        }

        if (!isValidmail()) {
            editTextEmail.setError("Enter valid mail");
            isvalid = false;
        }

        if (!editTextPassword.getText().toString().equalsIgnoreCase(editTextRepeatPassword.getText().toString())) {
            editTextPassword.setText("");
            editTextRepeatPassword.setText("");
            Toast.makeText(getActivity(), "Password are not matched.", Toast.LENGTH_LONG).show();
            isvalid = false;
        }


        return isvalid;
    }

    private boolean isValidmail() {
        String email = editTextEmail.getText().toString();
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}



