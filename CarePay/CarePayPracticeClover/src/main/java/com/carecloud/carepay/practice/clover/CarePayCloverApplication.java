package com.carecloud.carepay.practice.clover;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 */

public class CarePayCloverApplication extends CarePayApplication
        implements Application.ActivityLifecycleCallbacks {

    private ApplicationMode applicationMode;
    private MixpanelAPI mixpanelAPI;


    @Override
    public void onCreate() {
        super.onCreate();

        mixpanelAPI = MixpanelAPI.getInstance(this, getString(R.string.mixpanel_application_token));
        setHttpConstants();
        registerActivityLifecycleCallbacks(this);
    }

    private void setHttpConstants() {
        DeviceIdentifierDTO deviceIdentifierDTO = new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType("Clover");
        deviceIdentifierDTO.setDevicePlatform("android");
        deviceIdentifierDTO.setDeviceOSVersion(Build.VERSION.RELEASE);
        deviceIdentifierDTO.setVersion(BuildConfig.VERSION_NAME);
        HttpConstants.setDeviceInformation(deviceIdentifierDTO);
        HttpConstants.setApiBaseUrl(BuildConfig.API_BASE_URL);
        HttpConstants.setFormsUrl(BuildConfig.FORMS_BASE_URL);
        HttpConstants.setApiStartUrl(BuildConfig.API_START_URL);
        HttpConstants.setApiStartKey(BuildConfig.X_API_KEY);
        HttpConstants.setPushNotificationWebclientUrl(BuildConfig.WEBCLIENT_URL);
        HttpConstants.setUseUnifiedAuth(BuildConfig.useUnifiedAuth);
        HttpConstants.setMixpanelAPI(mixpanelAPI);
        HttpConstants.setEnvironment(BuildConfig.ENVIRONMENT);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public ApplicationMode getApplicationMode() {
        if (applicationMode == null) {
            applicationMode = new ApplicationMode();
            applicationMode.setApplicationType(ApplicationMode.ApplicationType.PRACTICE);
        }

        return applicationMode;
    }
}
