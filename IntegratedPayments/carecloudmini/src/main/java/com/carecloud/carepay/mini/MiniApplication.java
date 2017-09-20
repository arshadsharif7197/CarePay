package com.carecloud.carepay.mini;

import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;

import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.models.response.Authentication;
import com.carecloud.carepay.mini.services.carepay.RestCallServiceHelper;
import com.carecloud.carepay.mini.utils.ApplicationPreferences;
import com.carecloud.carepay.mini.utils.PicassoHelper;
import com.carecloud.shamrocksdk.ShamrockSdk;
import com.orm.SugarContext;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kkannan on 5/25/17
 */

public class MiniApplication extends MultiDexApplication implements ApplicationHelper {

    private RestCallServiceHelper restHelper;

    private ApplicationPreferences applicationPreferences;
    private Authentication authentication;

    @Override
    public void onCreate() {
        super.onCreate();

        SugarContext.init(this);
        ShamrockSdk.init(BuildConfig.X_API_KEY, BuildConfig.DEEPSTREAM_URL, BuildConfig.API_BASE_URL);
        setHttpConstants();
        Picasso.setSingletonInstance(PicassoHelper.getPicassoInstance(this));
    }

    @Override
    public void onTerminate(){
        SugarContext.terminate();
        super.onTerminate();
    }

    private void setHttpConstants() {
        DeviceIdentifierDTO deviceIdentifierDTO = new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType("CloverMini");
        deviceIdentifierDTO.setDeviceSystemVersion(Build.VERSION.RELEASE);
        HttpConstants.setDeviceInformation(deviceIdentifierDTO);
        HttpConstants.setApiStartKey(BuildConfig.X_API_KEY);
        HttpConstants.setApiBaseUrl(BuildConfig.API_BASE_URL);
        HttpConstants.setEnvironment(BuildConfig.ENVIRONMENT);
    }

    private void updatePicassoHelper(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authentication.getAccessToken());
        PicassoHelper.setHeaders(headers);
    }

    @Override
    public RestCallServiceHelper getRestHelper() {
        if(restHelper == null){
            initApplicationPreferences();
            restHelper = new RestCallServiceHelper(this);
        }
        return restHelper;
    }

    @Override
    public ApplicationPreferences getApplicationPreferences() {
        initApplicationPreferences();
        return applicationPreferences;
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
        updatePicassoHelper();
    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    private void initApplicationPreferences(){
        if(applicationPreferences == null){
            applicationPreferences = new ApplicationPreferences(this);
            restHelper = new RestCallServiceHelper(this);
        }
    }

}

