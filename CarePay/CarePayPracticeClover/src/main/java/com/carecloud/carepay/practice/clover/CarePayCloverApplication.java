package com.carecloud.carepay.practice.clover;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.shamrocksdk.ShamrockSdk;
import com.clover.sdk.util.Platform;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.newrelic.agent.android.NewRelic;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016
 */

public class CarePayCloverApplication extends CarePayApplication
        implements Application.ActivityLifecycleCallbacks {

    private ApplicationMode applicationMode;
    private MixpanelAPI mixpanelAPI;


    @Override
    public void onCreate() {
        super.onCreate();

        mixpanelAPI = MixpanelAPI.getInstance(this.getApplicationContext(), BuildConfig.MIX_PANEL_TOKEN);
        setHttpConstants();
        registerActivityLifecycleCallbacks(this);
        ShamrockSdk.init(HttpConstants.getPaymentsApiKey(), HttpConstants.getDeepStreamUrl(), HttpConstants.getPaymentsUrl());
    }

    private void setHttpConstants() {
        DeviceIdentifierDTO deviceIdentifierDTO = new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType(getDeviceType());
        deviceIdentifierDTO.setDevicePlatform(CarePayConstants.PLATFORM_ANDROID);
        deviceIdentifierDTO.setDeviceOSVersion(Build.VERSION.RELEASE);
        deviceIdentifierDTO.setVersion(BuildConfig.VERSION_NAME);
        HttpConstants.setDeviceInformation(deviceIdentifierDTO);
        HttpConstants.setApiBaseUrl(BuildConfig.API_BASE_URL);
        HttpConstants.setFormsUrl(BuildConfig.FORMS_BASE_URL);
        HttpConstants.setApiStartUrl(BuildConfig.API_START_URL);
        HttpConstants.setApiStartKey(BuildConfig.X_API_KEY);
        HttpConstants.setPushNotificationWebclientUrl(BuildConfig.WEBCLIENT_URL);
        HttpConstants.setMixpanelAPI(mixpanelAPI);
        HttpConstants.setEnvironment(BuildConfig.ENVIRONMENT);
        HttpConstants.setDeepStreamUrl(BuildConfig.DEEPSTREAM_URL);
        HttpConstants.setPaymentsUrl(BuildConfig.PAYMENTS_BASE_URL);
        HttpConstants.setPaymentsApiKey(BuildConfig.PAYMENTS_API_KEY);
        HttpConstants.setRetailUrl(BuildConfig.RETAIL_URL);
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
    public void onAtomicRestart(){
        super.onAtomicRestart();
        applicationMode.clearUserPracticeDTO();
        applicationMode = null;
    }

    @Override
    public void setNewRelicInteraction(String interactionName) {
        NewRelic.setInteractionName(interactionName);
    }

    @Override
    public ApplicationMode getApplicationMode() {
        if (applicationMode == null) {
            applicationMode = new ApplicationMode();
            applicationMode.setApplicationType(ApplicationMode.ApplicationType.PRACTICE);
        }

        return applicationMode;
    }

    private String getDeviceType(){
        if(Platform.isCloverStation()){
            return CarePayConstants.CLOVER_DEVICE;
        }else{
            return CarePayConstants.CLOVER_2_DEVICE;
        }
    }
}
