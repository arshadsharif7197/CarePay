package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Intent;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.signinsignup.SigninSignupActivity;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;


/**
 * Created by harish_revuri on 9/7/2016.
 * Signup screen
 */
public class SignupFragment extends Fragment {

    private TextInputLayout firstNameInputLayout, middleNameInputLayout, lastNameInputLayout, emailInputLayout, passwordInputLayout, passwordRepeatInputLayout;
    private EditText firstNameText;
    private EditText middleNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText repeatPasswordText;
    private TextView accountExistTextView;
    private Button   submitButton;

    private boolean  isValidFirstName;
    private boolean  isValidLastName;
    private boolean  isValidEmail;
    private boolean  isValidPassword;
    private boolean  isPasswordMatch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // set the toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.signup_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.signup_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
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


        // set the buttons
        submitButton = (Button) view.findViewById(R.id.submitSignupButton);
        Typeface buttonFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/gotham_rounded_medium.otf");
        submitButton.setTypeface(buttonFontFamily);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areAllValid()) {
                    //Submit Registration
                    Intent intent = new Intent(getActivity(), DemographicsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        submitButton.setEnabled(false);

        accountExistTextView = (TextView) view.findViewById(R.id.oldUserTextView);
        accountExistTextView = (TextView) view.findViewById(R.id.oldUserTextView);
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

        isValidFirstName = true;
        isValidLastName = true;
        isValidPassword = true;
        isValidEmail = true;
        isPasswordMatch = true;

        setEditTexts(view);

        return view;
    }

    private void setEditTexts(View view) {

        String hint = getString(R.string.firstname_text);
        firstNameInputLayout = (TextInputLayout) view.findViewById(R.id.firstNameTextInputLayout);
        firstNameInputLayout.setTag(hint);
        firstNameText = (EditText) view.findViewById(R.id.firstNameEditText);
        firstNameText.setTag(firstNameInputLayout);

        hint = getString(R.string.middlename_text);
        middleNameInputLayout = (TextInputLayout) view.findViewById(R.id.middleNameTextInputLayout);
        middleNameInputLayout.setTag(hint);
        middleNameText = (EditText) view.findViewById(R.id.middleNameEditText);
        middleNameText.setTag(middleNameInputLayout);

        hint = getString(R.string.lastname_text);
        lastNameInputLayout = (TextInputLayout) view.findViewById(R.id.lastNameTextInputLayout);
        lastNameInputLayout.setTag(hint);
        lastNameText = (EditText) view.findViewById(R.id.lastNameEditText);
        lastNameText.setTag(lastNameInputLayout);

        hint = getString(R.string.email_text);
        emailInputLayout = (TextInputLayout) view.findViewById(R.id.emailTextInputLayout);
        emailInputLayout.setTag(hint);
        emailText = (EditText) view.findViewById(R.id.emailEditText);
        emailText.setTag(emailInputLayout);

        hint = getString(R.string.password_text);
        passwordInputLayout = (TextInputLayout) view.findViewById(R.id.createPasswordTextInputLayout);
        passwordInputLayout.setTag(hint);
        passwordText = (EditText) view.findViewById(R.id.createPasswordEditText);
        passwordText.setTag(passwordInputLayout);

        hint = getString(R.string.repeat_password_text);
        passwordRepeatInputLayout = (TextInputLayout) view.findViewById(R.id.repeatPasswordTextInputLayout);
        passwordRepeatInputLayout.setTag(hint);
        repeatPasswordText = (EditText) view.findViewById(R.id.repeatPasswordEditText);
        repeatPasswordText.setTag(passwordRepeatInputLayout);

        setEditsTypefaces();

        setChangeFocusListeners();

        setActionListeners();
    }

    private void setActionListeners() {
        firstNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    isValidFirstName = checkFirstName();
                    submitButton.setEnabled(areAllValid());
                    middleNameText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        middleNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) { // no validations for middle name
                    lastNameText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        lastNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    isValidLastName = checkLastName();
                    submitButton.setEnabled(areAllValid());
                    emailText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        emailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    isValidEmail = checkEmail();
                    submitButton.setEnabled(areAllValid());
                    passwordText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    isValidPassword = checkPassword();
                    submitButton.setEnabled(areAllValid());
                    repeatPasswordText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        repeatPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    isPasswordMatch = checkPasswordsMatch();
                    boolean isReadyForNext = areAllValid();
                    Log.v(LOG_TAG, " isREadyForNext = " + isReadyForNext
                            + " isValidFirstName=" + isValidFirstName
                            + " isValidLastNAme=" + isValidLastName
                            + " isEmailValid=" + isValidEmail
                            + " isPasswordValid=" + isValidPassword
                            + " isMAtchedPAssw=" + isPasswordMatch);

                    if (isReadyForNext) {
                        SystemUtil.hideSoftKeyboard(getActivity());
                        submitButton.setEnabled(true);
                        repeatPasswordText.clearFocus();
                        submitButton.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void setEditsTypefaces() {
        // TODO: 9/14/2016 change to use the Utilities
        Typeface editTextFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_regular.otf");
        Typeface textViewFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_semibold.otf");
        Typeface floatingFontfamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_semibold.otf");

        firstNameInputLayout.setTypeface(editTextFontFamily);
        firstNameText.setTypeface(textViewFontFamily);

        middleNameInputLayout.setTypeface(editTextFontFamily);
        middleNameText.setTypeface(textViewFontFamily);

        lastNameInputLayout.setTypeface(editTextFontFamily);
        lastNameText.setTypeface(textViewFontFamily);

        emailInputLayout.setTypeface(editTextFontFamily);
        emailText.setTypeface(textViewFontFamily);

        passwordInputLayout.setTypeface(editTextFontFamily);
        passwordText.setTypeface(textViewFontFamily);

        passwordRepeatInputLayout.setTypeface(editTextFontFamily);
        repeatPasswordText.setTypeface(textViewFontFamily);

        accountExistTextView.setTypeface(textViewFontFamily);
    }

    /**
     * Set listener to capture focus change and toggle the hint text to caps on/off
     */
    public void setChangeFocusListeners() {
        firstNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) { // when loosing focus, validate as well
                    isValidFirstName = checkFirstName();
                    submitButton.setEnabled(areAllValid());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        middleNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                SystemUtil.handleHintChange(view, b);
            }
        });
        lastNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    isValidLastName = checkLastName();
                    submitButton.setEnabled(areAllValid());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    isValidEmail = checkEmail();
                    submitButton.setEnabled(areAllValid());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    isValidPassword = checkPassword();
                    submitButton.setEnabled(areAllValid());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        repeatPasswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    isPasswordMatch = checkPasswordsMatch();
                    submitButton.setEnabled(areAllValid());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
    }

    private boolean checkFirstName() {
        boolean isFirstNameInvalid = StringUtil.isNullOrEmpty(this.firstNameText.getText().toString());
        String error = (isFirstNameInvalid ? "error" : null);
        firstNameInputLayout.setErrorEnabled(isFirstNameInvalid);
        firstNameInputLayout.setError(error);
        return !isFirstNameInvalid;
    }

    private boolean checkLastName() {
        boolean isLastNameInvalid =  StringUtil.isNullOrEmpty(this.lastNameText.getText().toString());
        String error = (isLastNameInvalid ? "error" : null);
        lastNameInputLayout.setErrorEnabled(isLastNameInvalid);
        lastNameInputLayout.setError(error);
        return !isLastNameInvalid;
    }

    private boolean checkEmail() {
        String email = emailText.getText().toString();
        boolean isEmailInvalid = StringUtil.isNullOrEmpty(email) || !StringUtil.isValidmail(email);
        String error = (isEmailInvalid ? "error" : null);
        emailInputLayout.setErrorEnabled(isEmailInvalid);
        emailInputLayout.setError(error);
        return !isEmailInvalid;
    }

    private boolean checkPassword() {
        boolean isPassInvalid =  StringUtil.isNullOrEmpty(passwordText.getText().toString());
        String error = (isPassInvalid ? "error" : null);
        passwordInputLayout.setErrorEnabled(isPassInvalid);
        passwordInputLayout.setError(error);
        return !isPassInvalid;
    }

    private boolean checkPasswordsMatch() {
        String password = passwordText.getText().toString();
        String repeatedPassword = repeatPasswordText.getText().toString();
        boolean isNotMachedPassw =  StringUtil.isNullOrEmpty(password)
                || StringUtil.isNullOrEmpty(repeatedPassword)
                || !(password.equals(repeatedPassword));
        String error = (isNotMachedPassw ? "error" : null);
        passwordRepeatInputLayout.setErrorEnabled(isNotMachedPassw);
        passwordRepeatInputLayout.setError(error);
        return !isNotMachedPassw;
    }

    private boolean areAllValid() {
        return isValidFirstName
                && isValidLastName
                && isValidEmail
                && isValidPassword
                && isPasswordMatch;
    }
}