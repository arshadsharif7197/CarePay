package com.carecloud.carepay.mini.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.fragments.StartRegistrationFragment;
import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.interfaces.RegistrationNavigationCallback;
`import com.carecloud.carepay.mini.models.response.PreRegisterDataModel;
import com.carecloud.carepay.mini.models.response.RegistrationDataModel;
import com.carecloud.carepay.mini.models.response.SignInAuth;
import com.carecloud.carepay.mini.services.ServiceResponseDTO;
import com.carecloud.carepay.mini.utils.DtoHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by lmenendez on 6/23/17
 */

public class RegistrationActivity extends AppCompatActivity implements RegistrationNavigationCallback {

    private RegistrationDataModel registrationDataModel;
    private PreRegisterDataModel preRegisterDataModel;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        setContentView(R.layout.activity_registration);

        replaceFragment(new StartRegistrationFragment(), false);
    }

    private void replaceFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String tag = fragment.getClass().getName();
        transaction.replace(containerId, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void setRegistrationDataModel(ServiceResponseDTO serviceResponseDTO) {
        if(registrationDataModel == null) {
            registrationDataModel = DtoHelper.getConvertedDTO(RegistrationDataModel.class, serviceResponseDTO);
        }else{
            registrationDataModel.merge(DtoHelper.getConvertedDTO(RegistrationDataModel.class, serviceResponseDTO));
        }
    }

    @Override
    public void setPreRegisterDataModel(JsonElement jsonElement) {
        preRegisterDataModel = DtoHelper.getConvertedDTO(PreRegisterDataModel.class, (JsonObject) jsonElement);
    }

    @Override
    public RegistrationDataModel getRegistrationDataModel() {
        return registrationDataModel;
    }

    @Override
    public PreRegisterDataModel getPreRegisterDataModel() {
        return preRegisterDataModel;
    }

    @Override
    public void setAuthentication(SignInAuth.Cognito.Authentication authentication) {
        ((ApplicationHelper) getApplication()).setAuthentication(authentication);
    }

}
