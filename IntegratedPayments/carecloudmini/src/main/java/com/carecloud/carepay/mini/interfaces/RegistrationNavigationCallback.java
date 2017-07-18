package com.carecloud.carepay.mini.interfaces;

import android.support.v4.app.Fragment;

import com.carecloud.carepay.mini.models.response.PreRegisterDataModel;
import com.carecloud.carepay.mini.models.response.RegistrationDataModel;
import com.carecloud.carepay.mini.models.response.SignInAuth;
import com.carecloud.carepay.mini.services.ServiceResponseDTO;
import com.google.gson.JsonElement;

/**
 * Created by lmenendez on 6/23/17
 */

public interface RegistrationNavigationCallback {

    void replaceFragment(Fragment fragment, boolean addToBackStack);

    void onBackPressed();

    void setRegistrationDataModel(ServiceResponseDTO serviceResponseDTO);

    void setPreRegisterDataModel(JsonElement jsonElement);

    RegistrationDataModel getRegistrationDataModel();

    PreRegisterDataModel getPreRegisterDataModel();

    void setAuthentication(SignInAuth.Cognito.Authentication authentication);
}
