package com.carecloud.carepay.mini.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.mini.HttpConstants;
import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.models.data.UserDTO;
import com.carecloud.carepay.mini.models.response.Authentication;
import com.carecloud.carepay.mini.models.response.PreRegisterDataModel;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;
import com.carecloud.carepay.mini.services.carepay.RestCallServiceCallback;
import com.carecloud.carepay.mini.utils.DtoHelper;
import com.carecloud.carepay.mini.utils.KeyboardUtil;
import com.carecloud.carepay.mini.utils.StringUtil;
import com.carecloud.carepay.mini.views.CustomErrorToast;
import com.carecloud.shamrocksdk.registrations.DeviceRegistration;
import com.carecloud.shamrocksdk.registrations.interfaces.AccountInfoAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by lmenendez on 6/23/17
 */

public class LoginFragment extends RegistrationFragment {

    private View loginButton;
    private View backButton;
    private View buttonSpacer;
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
        loginButton.setVisibility(View.GONE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });

        buttonSpacer = view.findViewById(R.id.button_spacer);
        buttonSpacer.setVisibility(loginButton.getVisibility());

        emailInput = (EditText) view.findViewById(R.id.input_email);
        emailInput.setOnFocusChangeListener(emailFocusValidator);
        emailInput.addTextChangedListener(emailTextWatcher);
        emailInput.addTextChangedListener(emptyTextWatcher);

        passwordInput = (EditText) view.findViewById(R.id.input_password);
        passwordInput.addTextChangedListener(emptyTextWatcher);
        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                KeyboardUtil.hideSoftKeyboard(getContext(), textView);
                signInUser();
                return false;
            }
        });

        backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onBackPressed();
            }
        });
    }


    private void validateFields(){
        if(!StringUtil.isNullOrEmpty(emailInput.getText().toString()) &&
                StringUtil.isValidEmail(emailInput.getText().toString()) &&
                !StringUtil.isNullOrEmpty(passwordInput.getText().toString())){
            loginButton.setVisibility(View.VISIBLE);

        }else{
            loginButton.setVisibility(View.GONE);
        }
        buttonSpacer.setVisibility(loginButton.getVisibility());
    }


    private void signInUser(){
        UserDTO userDTO = new UserDTO();
        userDTO.setAlias(emailInput.getText().toString());
        userDTO.setPassword(passwordInput.getText().toString());

        getRestHelper().executeSignIn(signInRestCallback, DtoHelper.getStringDTO(userDTO));

    }

    private void getPreRegistration(){
        String environment = HttpConstants.getEnvironment();
        if(environment.equals("Support")){
               setSupportDeviceMid();
        }else {
            DeviceRegistration.getAccountInfo(getContext(), accountInfoAdapter);
        }
    }

    protected void displayNextStep(){
        PreRegisterDataModel preRegisterDataModel = callback.getPreRegisterDataModel();
        if(!preRegisterDataModel.getUserPracticeDTOList().isEmpty()){
            if(preRegisterDataModel.getUserPracticeDTOList().size() > 1){
                //show practice selection
                callback.replaceFragment(new PracticesFragment(), true);
            }else{
                //show practice confirmation Fragment
                String practiceId = preRegisterDataModel.getUserPracticeDTOList().get(0).getPracticeId();
                getApplicationHelper().getApplicationPreferences().setPracticeId(practiceId);
                callback.replaceFragment(new ConfirmPracticesFragment(), true);
            }
        }else{
            CustomErrorToast.showWithMessage(getContext(), getString(R.string.error_login));
        }

    }

    private void stripPractices(List<UserPracticeDTO> practices){
        UserPracticeDTO practiceDTO = practices.get(0);
        practices.clear();
        practices.add(  practiceDTO);
    }

    private void enableFields(boolean enabled){
        loginButton.setEnabled(enabled);
        backButton.setEnabled(enabled);
        emailInput.setEnabled(enabled);
        passwordInput.setEnabled(enabled);

    }


    private RestCallServiceCallback signInRestCallback = new RestCallServiceCallback() {
        @Override
        public void onPreExecute() {
            enableFields(false);
        }

        @Override
        public void onPostExecute(JsonElement jsonElement) {
            Log.d(LoginFragment.class.getName(), jsonElement.toString());
            Authentication signInAuth = DtoHelper.getConvertedDTO(Authentication.class, (JsonObject) jsonElement);
            callback.setAuthentication(signInAuth);
            getApplicationHelper().getApplicationPreferences().setUsername(emailInput.getText().toString());
            getPreRegistration();
        }

        @Override
        public void onFailure(String errorMessage) {
            enableFields(true);
            Log.d(LoginFragment.class.getName(), errorMessage);
            CustomErrorToast.showWithMessage(getContext(), errorMessage);
        }
    };

    private RestCallServiceCallback preRegisterCallback = new RestCallServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(JsonElement jsonElement) {
            enableFields(true);
            Log.d(LoginFragment.class.getName(), jsonElement.toString());
            callback.setPreRegisterDataModel(jsonElement);
            displayNextStep();
        }

        @Override
        public void onFailure(String errorMessage) {
            enableFields(true);
            Log.d(LoginFragment.class.getName(), errorMessage);
            CustomErrorToast.showWithMessage(getContext(), errorMessage);
        }
    };

    private AccountInfoAdapter accountInfoAdapter = new AccountInfoAdapter() {
        @Override
        public void onRetrieveMerchantId(String merchantId){
            getRestHelper().executePreRegister(preRegisterCallback, merchantId);
        }

        @Override
        public void onAccountConnectionFailure(String errorMessage) {
            enableFields(true);
            CustomErrorToast.showWithMessage(getContext(), errorMessage);
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
                loginButton.setVisibility(View.GONE);
                buttonSpacer.setVisibility(loginButton.getVisibility());
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

    private void setSupportDeviceMid(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Title");
        builder.setMessage("Enter Client MID");

        final EditText input = new EditText(getContext());
        input.setText(getApplicationHelper().getApplicationPreferences().getSupportMid());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtil.hideSoftKeyboard(getContext(), input);
                dialog.dismiss();
                String merchantId = input.getText().toString();
                getApplicationHelper().getApplicationPreferences().setSupportMid(merchantId);
                getRestHelper().executePreRegister(preRegisterCallback, merchantId);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyboardUtil.hideSoftKeyboard(getContext(), input);
                dialog.cancel();
                enableFields(true);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                input.selectAll();
            }
        });
        dialog.show();

    }
}
