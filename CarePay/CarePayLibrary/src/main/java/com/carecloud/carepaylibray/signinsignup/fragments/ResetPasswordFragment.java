package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.signinsignup.ResetPasswordViewModel;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.android.material.textfield.TextInputLayout;

/**
 * @author pjohnson on 03/12/2017
 */
public class ResetPasswordFragment extends BaseFragment {

    private EditText emailEditText;
    private FragmentActivityInterface listener;
    private Button resetPasswordButton;
    private TextInputLayout signInEmailTextInputLayout;
    public static final int GO_TO_HOME = 200;
    private ResetPasswordViewModel viewModel;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * @return an instance of ResetPasswordFragment
     */
    public static ResetPasswordFragment newInstance() {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FragmentActivityInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Attached Context must implement FragmentActivityInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViewModel();
    }

    private void setUpViewModel() {
        viewModel = new ViewModelProvider(getActivity()).get(ResetPasswordViewModel.class);
        viewModel.getResetPasswordDtoObservable().observe(this, aVoid -> goToNextScreen());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpUi(view);
    }

    private void setUpUi(View view) {
        emailEditText = view.findViewById(R.id.emailEditText);
        resetPasswordButton = view.findViewById(R.id.resetPasswordButton);
        resetPasswordButton.setOnClickListener(view1 -> resetPassword());
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean isEmptyPassword = StringUtil.isNullOrEmpty(emailEditText.getText().toString());
                if (!isEmptyPassword) {
                    signInEmailTextInputLayout.setError(null);
                    signInEmailTextInputLayout.setErrorEnabled(false);
                }
                setResetPasswordButtonEnabled(editable.toString());
            }
        });
        emailEditText.setOnEditorActionListener((textView, actionId, event) -> {
            resetPassword();
            return false;
        });

        Button goBackButton = view.findViewById(R.id.goBackButton);
        if (goBackButton != null) {
            goBackButton.setVisibility(View.VISIBLE);
            goBackButton.setOnClickListener(view12 -> getActivity().onBackPressed());
        }

        ImageView signInHome = view.findViewById(R.id.signInHome);
        if (signInHome != null) {
            signInHome.setVisibility(View.VISIBLE);
            signInHome.setOnClickListener(view13 -> {
                getActivity().setResult(GO_TO_HOME);
                getActivity().finish();
            });
        }

        signInEmailTextInputLayout = view.findViewById(R.id.signInEmailTextInputLayout);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        CarePayTextView titleView = view.findViewById(R.id.toolbar_title);
        if (toolbar != null) {
            listener.setToolbar(toolbar);
            titleView.setText(Label.getLabel("forgot_password_screen_title"));
        }

    }

    private void resetPassword() {
        if (validateEmail(emailEditText.getText().toString())) {
            viewModel.resetPassword(emailEditText.getText().toString());
        }
    }

    private boolean validateEmail(String email) {
        boolean isValid = ValidationHelper.isValidEmail(email);
        if (!isValid) {
            setEmailError(getString(com.carecloud.carepaylibrary.R.string.signin_signup_error_invalid_email));
        }
        return isValid;
    }

    private void goToNextScreen() {
        listener.addFragment(ConfirmationResetPasswordFragment
                .newInstance(emailEditText.getText().toString()), true);
    }

    private void setResetPasswordButtonEnabled(String email) {
        resetPasswordButton.setEnabled(email.length() > 0);
    }

    private void setEmailError(String error) {
        signInEmailTextInputLayout.setErrorEnabled(true);
        signInEmailTextInputLayout.setError(error);
    }
}
