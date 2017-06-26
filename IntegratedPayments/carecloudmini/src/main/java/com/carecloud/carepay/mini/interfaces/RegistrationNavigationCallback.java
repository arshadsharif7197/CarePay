package com.carecloud.carepay.mini.interfaces;

import android.support.v4.app.Fragment;

import com.carecloud.carepay.mini.models.response.RegistrationDataModel;
import com.carecloud.carepay.mini.services.ServiceResponseDTO;

/**
 * Created by lmenendez on 6/23/17
 */

public interface RegistrationNavigationCallback {

    void replaceFragment(Fragment fragment, boolean addToBackStack);

    void onBackPressed();

    void setRegistrationDataModel(ServiceResponseDTO serviceResponseDTO);

    RegistrationDataModel getRegistrationDataModel();
}
