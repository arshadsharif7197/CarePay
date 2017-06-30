package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.models.data.SignInDTO;
import com.carecloud.carepay.mini.models.data.UserDTO;
import com.carecloud.carepay.mini.models.response.RegistrationDataModel;
import com.carecloud.carepay.mini.models.response.SignInAuth;
import com.carecloud.carepay.mini.services.ServiceCallback;
import com.carecloud.carepay.mini.services.ServiceRequestDTO;
import com.carecloud.carepay.mini.services.ServiceResponseDTO;
import com.carecloud.carepay.mini.utils.DtoHelper;
import com.carecloud.carepay.mini.utils.StringUtil;
import com.carecloud.carepay.mini.views.CustomErrorToast;

/**
 * Created by lmenendez on 6/23/17
 */

public class LoginFragment extends RegistrationFragment {

    private View loginButton;
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_registration_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initProgressToolbar(view, getString(R.string.registration_login_title), 1);

        loginButton = view.findViewById(R.id.button_login);
        loginButton.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });

        emailInput = (EditText) view.findViewById(R.id.input_email);
        emailInput.setOnFocusChangeListener(emailFocusValidator);
        emailInput.addTextChangedListener(emailTextWatcher);
        emailInput.addTextChangedListener(emptyTextWatcher);

        passwordInput = (EditText) view.findViewById(R.id.input_password);
        passwordInput.addTextChangedListener(emptyTextWatcher);
    }


    private void validateFields(){
        if(!StringUtil.isNullOrEmpty(emailInput.getText().toString()) &&
                StringUtil.isValidEmail(emailInput.getText().toString()) &&
                !StringUtil.isNullOrEmpty(passwordInput.getText().toString())){
            loginButton.setVisibility(View.VISIBLE);
        }else{
            loginButton.setVisibility(View.INVISIBLE);
        }
    }


    private void signInUser(){
        UserDTO userDTO = new UserDTO();
        userDTO.setAlias(emailInput.getText().toString());
        userDTO.setPassword(passwordInput.getText().toString());

        SignInDTO signInDTO = new SignInDTO();
        signInDTO.setUser(userDTO);

        ServiceRequestDTO loginRequest = callback.getRegistrationDataModel().getMetadata().getTransitions().getSignIn();
        getServiceHelper().execute(loginRequest, signInCallback, DtoHelper.getStringDTO(signInDTO));

    }

    private void authenticateUser(){
        SignInAuth.Cognito.Authentication authentication = callback.getRegistrationDataModel().getPayloadDTO().getSignInAuth().getCognito().getAuthentication();
        if(authentication != null && authentication.getAccessToken() != null) {
            callback.setAuthentication(authentication);
        }

        ServiceRequestDTO authRequest = callback.getRegistrationDataModel().getMetadata().getTransitions().getAuthenticate();
        getServiceHelper().execute(authRequest, authenticateCallback);
    }

    private void displayNextStep(){
        RegistrationDataModel registrationDataModel = callback.getRegistrationDataModel();
        if(!registrationDataModel.getPayloadDTO().getUserPractices().isEmpty()){
            if(registrationDataModel.getPayloadDTO().getUserPractices().size() > 1){
                //show practice selection
                callback.replaceFragment(new PracticesFragment(), true);
            }else{
                //show location selection
                String practiceID = registrationDataModel.getPayloadDTO().getUserPractices().get(0).getPracticeId();
                getApplicationHelper().getApplicationPreferences().setPracticeId(practiceID);
                callback.replaceFragment(new LocationsFragment(), true);
            }
        }else{
            CustomErrorToast.showWithMessage(getContext(), getString(R.string.error_login));
        }
    }

    private void enableFields(boolean enabled){
        loginButton.setEnabled(enabled);
        emailInput.setEnabled(enabled);
        passwordInput.setEnabled(enabled);

    }

    private ServiceCallback signInCallback = new ServiceCallback() {
        @Override
        public void onPreExecute() {
            enableFields(false);
        }

        @Override
        public void onPostExecute(ServiceResponseDTO serviceResponseDTO) {
            Log.d(LoginFragment.class.getName(), serviceResponseDTO.toString());
            callback.setRegistrationDataModel(serviceResponseDTO);
            getApplicationHelper().getApplicationPreferences().setUsername(emailInput.getText().toString());
            authenticateUser();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            enableFields(true);
            Log.d(LoginFragment.class.getName(), exceptionMessage);
            CustomErrorToast.showWithMessage(getContext(), exceptionMessage);
        }
    };

    private ServiceCallback authenticateCallback = new ServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(ServiceResponseDTO serviceResponseDTO) {
            enableFields(true);
            Log.d(LoginFragment.class.getName(), serviceResponseDTO.toString());
            callback.setRegistrationDataModel(serviceResponseDTO);
            displayNextStep();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            enableFields(true);
            Log.d(LoginFragment.class.getName(), exceptionMessage);
            CustomErrorToast.showWithMessage(getContext(), exceptionMessage);
        }
    };

    private TextWatcher emptyTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(StringUtil.isNullOrEmpty(editable.toString())){
                loginButton.setVisibility(View.INVISIBLE);
            }
            validateFields();
        }
    };

    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(!StringUtil.isNullOrEmpty(editable.toString())){
                emailInput.setError(null);
            }
        }
    };

    private View.OnFocusChangeListener emailFocusValidator = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if(!hasFocus) {
                EditText editText = (EditText) view;
                if(!StringUtil.isValidEmail(editText.getText().toString())){
                    editText.setError(getString(R.string.error_email));
                }
            }
        }
    };
}
