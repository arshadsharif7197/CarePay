package com.carecloud.carepay.mini.interfaces;

import android.support.v4.app.Fragment;

import com.carecloud.carepay.mini.models.response.Authentication;
import com.carecloud.carepay.mini.models.response.PreRegisterDataModel;
import com.google.gson.JsonElement;

/**
 * Created by lmenendez on 6/23/17
 */

public interface RegistrationNavigationCallback {

    void replaceFragment(Fragment fragment, boolean addToBackStack);

    void onBackPressed();

    void setPreRegisterDataModel(JsonElement jsonElement);

    PreRegisterDataModel getPreRegisterDataModel();

    void setAuthentication(Authentication authentication);
}
