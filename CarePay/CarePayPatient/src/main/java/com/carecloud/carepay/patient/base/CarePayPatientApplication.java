package com.carecloud.carepay.patient.base;

import android.os.Build;
import android.provider.Settings;

import com.carecloud.carepay.patient.BuildConfig;
import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.mixpanel.android.mpmetrics.MixpanelAPI;


/**
 * Created by Jahirul on 8/25/2016.
 * this is the application class for the patient app
 */
public class CarePayPatientApplication extends CarePayApplication {

    private ApplicationMode applicationMode;
    private MixpanelAPI mixpanelAPI;

    @Override
    public void onCreate() {
        super.onCreate();

        mixpanelAPI = MixpanelAPI.getInstance(this, getString(R.string.mixpanel_application_token));
        setHttpConstants();
        registerActivityLifecycleCallbacks(new CarePayActivityLifecycleCallbacks());
    }

    /**
     * Http constants initialization
     */
    private void setHttpConstants() {
        DeviceIdentifierDTO deviceIdentifierDTO=new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType("Android");
        deviceIdentifierDTO.setDeviceSystemVersion(Build.VERSION.RELEASE);
        HttpConstants.setDeviceInformation(deviceIdentifierDTO);
        HttpConstants.setApiBaseUrl(BuildConfig.API_BASE_URL);
        HttpConstants.setApiStartUrl(BuildConfig.API_START_URL);
        HttpConstants.setApiStartKey(BuildConfig.X_API_KEY);
        HttpConstants.setMessagingBaseUrl(BuildConfig.MESSAGING_BASE_URL);
        HttpConstants.setPushNotificationWebclientUrl(BuildConfig.WEBCLIENT_URL);
        HttpConstants.setUseUnifiedAuth(BuildConfig.useUnifiedAuth);
        HttpConstants.setMixpanelAPI(mixpanelAPI);
    }



    @Override
    public ApplicationMode getApplicationMode() {
        if (applicationMode == null) {
            applicationMode = new ApplicationMode();
            applicationMode.setApplicationType(ApplicationMode.ApplicationType.PATIENT);
        }

        return applicationMode;
    }
}
