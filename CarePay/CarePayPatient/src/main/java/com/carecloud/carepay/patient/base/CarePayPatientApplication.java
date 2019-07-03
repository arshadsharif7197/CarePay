package com.carecloud.carepay.patient.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.carecloud.carepay.patient.BuildConfig;
import com.carecloud.carepay.patient.session.PatientSessionService;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.session.SessionService;
import com.carecloud.carepaylibray.session.SessionedActivityInterface;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.newrelic.agent.android.NewRelic;


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
        mixpanelAPI = MixpanelAPI.getInstance(this.getApplicationContext(), BuildConfig.MIX_PANEL_TOKEN);
        setHttpConstants();
    }

    /**
     * Http constants initialization
     */
    private void setHttpConstants() {
        DeviceIdentifierDTO deviceIdentifierDTO = new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType(CarePayConstants.ANDROID_DEVICE);
        deviceIdentifierDTO.setDevicePlatform(CarePayConstants.PLATFORM_ANDROID);
        deviceIdentifierDTO.setDeviceOSVersion(Build.VERSION.RELEASE);
        deviceIdentifierDTO.setVersion(BuildConfig.VERSION_NAME);
//        deviceIdentifierDTO.setVersion("1.0.6");
        HttpConstants.setDeviceInformation(deviceIdentifierDTO);
        HttpConstants.setApiBaseUrl(BuildConfig.API_BASE_URL);
        HttpConstants.setFormsUrl(BuildConfig.FORMS_BASE_URL);
        HttpConstants.setApiStartUrl(BuildConfig.API_START_URL);
        HttpConstants.setApiStartKey(BuildConfig.X_API_KEY);
        HttpConstants.setMessagingBaseUrl(BuildConfig.MESSAGING_BASE_URL);
        HttpConstants.setPushNotificationWebclientUrl(BuildConfig.WEBCLIENT_URL);
        HttpConstants.setMixpanelAPI(mixpanelAPI);
        HttpConstants.setEnvironment(BuildConfig.ENVIRONMENT);
        HttpConstants.setRetailUrl(BuildConfig.RETAIL_URL);
    }

    @Override
    public void onAtomicRestart() {
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
            applicationMode.setApplicationType(ApplicationMode.ApplicationType.PATIENT);
        }

        return applicationMode;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        if (activity instanceof SessionedActivityInterface
                && ((SessionedActivityInterface) activity).manageSession()) {
            startService(new Intent(this, PatientSessionService.class));
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        super.onActivityPaused(activity);
    }
}
