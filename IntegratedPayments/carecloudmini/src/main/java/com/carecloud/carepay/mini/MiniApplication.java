package com.carecloud.carepay.mini;

import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;

import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.models.response.SignInAuth;
import com.carecloud.carepay.mini.services.ServiceHelper;
import com.carecloud.carepay.mini.utils.ApplicationPreferences;
import com.carecloud.carepay.mini.utils.PicassoHelper;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kkannan on 5/25/17
 */

public class MiniApplication extends MultiDexApplication implements ApplicationHelper {

    private ServiceHelper serviceHelper;
    private ApplicationPreferences applicationPreferences;
    private SignInAuth.Cognito.Authentication authentication;

    @Override
    public void onCreate() {
        super.onCreate();

        setHttpConstants();
    }

    private void setHttpConstants() {
        DeviceIdentifierDTO deviceIdentifierDTO = new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType("CloverMini");
        deviceIdentifierDTO.setDeviceSystemVersion(Build.VERSION.RELEASE);
        HttpConstants.setDeviceInformation(deviceIdentifierDTO);
        HttpConstants.setApiStartUrl(BuildConfig.API_START_URL);
        HttpConstants.setApiStartKey(BuildConfig.X_API_KEY);
        HttpConstants.setApiBaseUrl(BuildConfig.API_BASE_URL);
    }

    private void initPicassoHelper(){
        Map<String, String> headers = new HashMap<>();
        headers.put("AccessToken", authentication.getAccessToken());
        Picasso.setSingletonInstance(PicassoHelper.getPicassoInstance(this, headers));
    }

    @Override
    public ServiceHelper getServiceHelper(){
        if(serviceHelper == null){
            if(applicationPreferences == null){
                applicationPreferences = new ApplicationPreferences(this);
            }
            serviceHelper = new ServiceHelper(applicationPreferences);
        }
        return serviceHelper;
    }

    @Override
    public ApplicationPreferences getApplicationPreferences() {
        initApplicationPreferences();
        return applicationPreferences;
    }

    @Override
    public void setAuthentication(SignInAuth.Cognito.Authentication authentication) {
        this.authentication = authentication;
        initPicassoHelper();
    }

    private void initApplicationPreferences(){
        if(applicationPreferences == null){
            applicationPreferences = new ApplicationPreferences(this);
            serviceHelper = new ServiceHelper(applicationPreferences);
        }
    }
}

