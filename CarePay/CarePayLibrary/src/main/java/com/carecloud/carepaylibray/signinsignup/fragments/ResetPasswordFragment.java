package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 03/12/2017
 */
public class ResetPasswordFragment extends BaseFragment {

    private TransitionDTO passwordTransition;
    private EditText emailEditText;
    private FragmentActivityInterface listener;
    private Button resetPasswordButton;
    private TextInputLayout signInEmailTextInputLayout;
    public static final int GO_TO_HOME = 200;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (FragmentActivityInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Attached Context must implement DTOInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passwordTransition = ((SignInDTO) listener.getDto()).getMetadata().getTransitions().getForgotPassword();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpUi(view);
    }

    private void setUpUi(View view) {
        emailEditText = (EditText) view.findViewById(R.id.emailEditText);
        resetPasswordButton = (Button) view.findViewById(R.id.resetPasswordButton);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
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
        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                resetPassword();
                return false;
            }
        });

        Button goBackButton = (Button) view.findViewById(R.id.goBackButton);
        if (goBackButton != null) {
            goBackButton.setVisibility(View.VISIBLE);
            goBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        }

        ImageView signInHome = (ImageView) view.findViewById(R.id.signInHome);
        if (signInHome != null) {
            signInHome.setVisibility(View.VISIBLE);
            signInHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().setResult(GO_TO_HOME);
                    getActivity().finish();
                }
            });
        }

        signInEmailTextInputLayout = (TextInputLayout) view.findViewById(R.id.signInEmailTextInputLayout);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        CarePayTextView titleView = (CarePayTextView) view.findViewById(R.id.toolbar_title);
        if (toolbar != null) {
            listener.setToolbar(toolbar);
            titleView.setText(Label.getLabel("forgot_password_screen_title"));
        }

    }

    private void resetPassword() {
        if (validateEmail(emailEditText.getText().toString())) {
            resetPassword(passwordTransition, emailEditText.getText().toString());
        }
    }

    private void resetPassword(TransitionDTO resetPasswordTransition, String email) {
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("username", email);
        JsonObject userObject = new JsonObject();
        userObject.add("user", jsonObject2);
        Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        Map<String, String> queryParams = new HashMap<>();
        getWorkflowServiceHelper().execute(resetPasswordTransition, resetPasswordCallback,
                userObject.toString(), queryParams, headers);
    }

    private WorkflowServiceCallback resetPasswordCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            goToNextScreen();
        }

        @Override
        public void onFailure(String exceptionMessage)  {
            listener.showErrorToast(exceptionMessage);
            hideProgressDialog();
        }
    };

    private boolean validateEmail(String email) {
        boolean isValid = ValidationHelper.isValidEmail(email);
        if (!isValid) {
            setEmailError(getString(com.carecloud.carepaylibrary.R.string.signin_signup_error_invalid_email));
        }
        return isValid;
    }

    public void goToNextScreen() {
        listener.replaceFragment(ConfirmationResetPasswordFragment
                .newInstance(emailEditText.getText().toString()), true);
    }

    public void setResetPasswordButtonEnabled(String email) {
        resetPasswordButton.setEnabled(email.length() > 0);
    }

    public void setEmailError(String error) {
        signInEmailTextInputLayout.setErrorEnabled(true);
        signInEmailTextInputLayout.setError(error);
    }
}
