package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.utils.StringUtil;

/**
 * Created by lmenendez on 6/24/17
 */

public class DeviceFragment extends RegistrationFragment {

    private View nextButton;
    private View buttonSpacer;
    private EditText nameInput;
//    private EditText welcomeInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_registration_device, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initProgressToolbar(view, getString(R.string.registration_device_title), 4);

        nextButton = view.findViewById(R.id.button_next);
        nextButton.setVisibility(View.GONE);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDeviceDetails();
            }
        });

        buttonSpacer = view.findViewById(R.id.button_spacer);
        buttonSpacer.setVisibility(nextButton.getVisibility());

        View backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onBackPressed();
            }
        });

        nameInput = (EditText) view.findViewById(R.id.input_device_name);
        nameInput.addTextChangedListener(emptyTextWatcher);

        String selectedName = getApplicationHelper().getApplicationPreferences().getDeviceName();
        if(!StringUtil.isNullOrEmpty(selectedName)){
            nameInput.setText(selectedName);
        }

//        welcomeInput = (EditText) view.findViewById(R.id.input_welcome);
//        welcomeInput.addTextChangedListener(emptyTextWatcher);

    }

    private boolean validateForm(){
        return !StringUtil.isNullOrEmpty(nameInput.getText().toString());
    }

    private void setDeviceDetails(){
        getApplicationHelper().getApplicationPreferences().setDeviceName(nameInput.getText().toString());
//        getApplicationHelper().getApplicationPreferences().setWelcomeMessage(welcomeInput.getText().toString());

        callback.replaceFragment(new ImageSelectFragment(), true);
    }

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
                nextButton.setVisibility(View.GONE);
            }else if(validateForm()){
                nextButton.setVisibility(View.VISIBLE);
            }
            buttonSpacer.setVisibility(nextButton.getVisibility());
        }
    };


}
