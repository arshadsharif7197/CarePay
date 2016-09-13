package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.activities.LibraryMainActivity;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.signinsignup.SigninSignupActivity;
import com.carecloud.carepaylibray.signinsignup.models.TextWatcherModel;

/**
 * Created by harish_revuri on 9/7/2016.
 */
public class SigninFragment extends Fragment {


    private TextView errorEmailTextView, errorPasswordTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView changeLanguageTextView;
    private Button signinButton;
    private Button signupButton;

    private boolean isValidEmail, isValidPassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        errorEmailTextView = (TextView) view.findViewById(R.id.txt_email_error);
        errorPasswordTextView = (TextView) view.findViewById(R.id.txt_create_password);

        emailEditText = (EditText) view.findViewById(R.id.emailEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);


        Typeface editTextFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_regular.otf");
        emailEditText.setTypeface(editTextFontFamily);
        passwordEditText.setTypeface(editTextFontFamily);

        emailEditText.addTextChangedListener(new TextWatcherModel(TextWatcherModel.InputType.TYPE_EMAIL, emailEditText, errorEmailTextView, "Enter Valid Email", false, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidEmail = isValid;
                checkForButtonEnable();
            }
        }));
        passwordEditText.addTextChangedListener(new TextWatcherModel(TextWatcherModel.InputType.TYPE_PASSWORD, passwordEditText, errorPasswordTextView, "Enter password", false, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidPassword = isValid;
                checkForButtonEnable();
            }
        }));

        signinButton = (Button) view.findViewById(R.id.SigninButton);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidInfo()) {

                    Intent intent = new Intent(getContext(), AppointmentsActivity.class);
                    startActivity(intent);

                }

            }
        });

        signupButton = (Button) view.findViewById(R.id.SignUpButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = ((SigninSignupActivity) getActivity()).getmFragmentManager();
                SignupFragment fragment = (SignupFragment) fm.findFragmentByTag(SignupFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new SignupFragment();
                }
                fm.beginTransaction()
                        .replace(R.id.signinLayout, fragment, SignupFragment.class.getSimpleName())
                        .commit();
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

        return view;
    }


    private boolean isValidInfo() {

        if (isValidEmail && isValidPassword) {
            return true;
        } else if (!isValidEmail && !isValidPassword) {
            Toast.makeText(getActivity(), "Fields can't be empty", Toast.LENGTH_LONG).show();
            return false;
        } else if (!isValidEmail) {
            Toast.makeText(getActivity(), "Enter valid Email-ID", Toast.LENGTH_LONG).show();
            return false;
        } else if (!isValidPassword) {
            Toast.makeText(getActivity(), "Enter valid password", Toast.LENGTH_LONG).show();
            return false;
        } else {
            Toast.makeText(getActivity(), "Fields can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private void checkForButtonEnable() {
        if (isValidEmail && isValidPassword) {
            signinButton.setBackgroundResource(R.drawable.button_selector);
            signinButton.setTextColor(Color.WHITE);
        } else {
            signinButton.setBackgroundResource(R.drawable.button_light_gray_background);

        }
    }
}