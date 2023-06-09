package com.carecloud.carepay.patient.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.carecloud.carepay.patient.BuildConfig;
import com.carecloud.carepay.patient.session.PatientSessionWorker;
import com.carecloud.carepay.patient.signinsignuppatient.SigninSignupActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.session.SessionWorker;
import com.carecloud.carepaylibray.session.SessionedActivityInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.newrelic.agent.android.NewRelic;


/**
 * Created by Jahirul on 8/25/2016.
 * this is the application class for the patient app
 */
public class CarePayPatientApplication extends CarePayApplication {

    private ApplicationMode applicationMode;
    private MixpanelAPI mixpanelAPI;
    private WorkManager sessionWorkManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mixpanelAPI = MixpanelAPI.getInstance(this.getApplicationContext(), BuildConfig.MIX_PANEL_TOKEN);
        setHttpConstants();
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectNonSdkApiUsage()
                    .penaltyLog()
                    .build());
        }
        AppCenter.start(this, "a450a247-935b-4110-a19e-047ac2562830",
                Analytics.class, Crashes.class);
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
                && ((SessionedActivityInterface) activity).manageSession()
                && !PatientSessionWorker.isServiceStarted) {
            restartSession(activity);
        } else if (activity instanceof SigninSignupActivity) {
            cancelSession();
        } else if (activity instanceof SessionedActivityInterface
                && ((SessionedActivityInterface) activity).manageSession()
                && PatientSessionWorker.isServiceStarted && PatientSessionWorker.isLogoutNeeded) {
            callLogoutService(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        super.onActivityPaused(activity);
    }

    @Override
    public void restartSession(Activity activity) {
        cancelSession();
        Data.Builder builder = new Data.Builder();
        builder.putString("logout_transition",
                DtoHelper.getStringDTO(((SessionedActivityInterface) activity).getLogoutTransition()));

        OneTimeWorkRequest sessionWorkerRequest = new OneTimeWorkRequest.Builder(PatientSessionWorker.class)
                .setInputData(builder.build())
                .addTag("sessionWorker")
                .build();

        sessionWorkManager = WorkManager.getInstance(getApplicationContext());
        sessionWorkManager.enqueueUniqueWork(
                "sessionWorker",
                ExistingWorkPolicy.REPLACE,
                sessionWorkerRequest);
    }

    public void cancelSession() {
        PatientSessionWorker.isServiceStarted = false;
        PatientSessionWorker.isLogoutNeeded = false;
        if (PatientSessionWorker.logoutTimer != null) PatientSessionWorker.logoutTimer.cancel();
        if (SessionWorker.handler != null) {
            WorkManager.getInstance(getApplicationContext()).cancelAllWorkByTag("sessionWorker");
            SessionWorker.handler.removeMessages(0);
        }
    }

    private void callLogoutService(Activity activity) {
        cancelSession();

        getApplicationMode().clearUserPracticeDTO();
        AppAuthorizationHelper authHelper = getAppAuthorizationHelper();
        authHelper.setUser(null);
        authHelper.setAccessToken(null);
        authHelper.setIdToken(null);
        authHelper.setRefreshToken(null);
        onAtomicRestart();
        activity.finishAffinity();
    }
}
