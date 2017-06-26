package com.carecloud.carepay.mini;

import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;

import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.services.ServiceHelper;
import com.carecloud.carepay.mini.utils.ApplicationPreferences;

/**
 * Created by kkannan on 5/25/17
 */

public class MiniApplication extends MultiDexApplication implements ApplicationHelper {

    private ServiceHelper serviceHelper;
    private ApplicationPreferences applicationPreferences;

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

    private void initApplicationPreferences(){
        if(applicationPreferences == null){
            applicationPreferences = new ApplicationPreferences(this);
            serviceHelper = new ServiceHelper(applicationPreferences);
        }
    }
}

