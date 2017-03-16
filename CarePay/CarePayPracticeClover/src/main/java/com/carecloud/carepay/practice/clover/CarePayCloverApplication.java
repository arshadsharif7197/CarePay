package com.carecloud.carepay.practice.clover;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.carecloud.carepay.practice.library.signin.SigninActivity;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.carecloud.carepaylibray.CarePlayApplication;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 */

public class CarePayCloverApplication extends CarePlayApplication
        implements Application.ActivityLifecycleCallbacks {

    private ApplicationMode applicationMode;

    @Override
    public void onCreate() {
        super.onCreate();
        setHttpConstants();
        registerActivityLifecycleCallbacks(this);
    }

    private void setHttpConstants() {
        DeviceIdentifierDTO deviceIdentifierDTO = new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType("Clover");
        deviceIdentifierDTO.setDeviceSystemVersion(Build.VERSION.RELEASE);
        HttpConstants.setDeviceInformation(deviceIdentifierDTO);
        HttpConstants.setApiBaseUrl(BuildConfig.API_BASE_URL);
        HttpConstants.setApiStartUrl(BuildConfig.API_START_URL);
        HttpConstants.setApiStartKey(BuildConfig.X_API_KEY);
        HttpConstants.setPushNotificationWebclientUrl(BuildConfig.WEBCLIENT_URL);
        HttpConstants.setUseUnifiedAuth(BuildConfig.useUnifiedAuth);
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
        if (activity instanceof SigninActivity) {
            // log out previous user from Cognito
            Log.v(this.getClass().getSimpleName(), "sign out Cognito");
            //getAppAuthorizationHelper().getPool().getUser().signOut();
            //getAppAuthorizationHelper().setUser(null);
        }
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
