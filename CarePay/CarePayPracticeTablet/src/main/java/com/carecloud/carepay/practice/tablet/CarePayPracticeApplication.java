package com.carecloud.carepay.practice.tablet;

import android.app.Activity;
import android.os.Build;
import android.provider.Settings;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.carecloud.carepay.practice.library.homescreen.AppointmentCountUpdateService;
import com.carecloud.carepay.practice.library.session.PracticeSessionWorker;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.session.SessionWorker;
import com.carecloud.carepaylibray.session.SessionedActivityInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.shamrocksdk.ShamrockSdk;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.newrelic.agent.android.NewRelic;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016
 */

public class CarePayPracticeApplication extends CarePayApplication {

    private ApplicationMode applicationMode;
    private MixpanelAPI mixpanelAPI;
    public WorkManager sessionWorkManager;

    @Override
    public void onCreate() {
        super.onCreate();
        start();
        this.onTerminate();
        AppCenter.start(this, "bae2f03a-0b4e-4565-9ddd-720141369b1c",
                Analytics.class, Crashes.class);
    }

    @Override
    public void onTerminate() {
        AppointmentCountUpdateService.cancelScheduledServiceRun(this);
        super.onTerminate();
    }

    /**
     * init app
     */
    public void start() {
        mixpanelAPI = MixpanelAPI.getInstance(getApplicationContext(), BuildConfig.MIX_PANEL_TOKEN);
        setHttpConstants();
        ShamrockSdk.init(HttpConstants.getPaymentsApiKey(), HttpConstants.getDeepStreamUrl(), HttpConstants.getPaymentsUrl());
    }

    private void setHttpConstants() {
        DeviceIdentifierDTO deviceIdentifierDTO = new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType(CarePayConstants.ANDROID_DEVICE);
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
            applicationMode.setApplicationType(ApplicationMode.ApplicationType.PRACTICE);
        }
        return applicationMode;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        if (activity instanceof SessionedActivityInterface
                && ((SessionedActivityInterface) activity).manageSession()
                && !PracticeSessionWorker.isServiceStarted) {
            restartSession(activity);
        }
    }

    @Override
    public void restartSession(Activity activity) {
        cancelSession();
        Data.Builder builder = new Data.Builder();
        builder.putString("logout_transition",
                DtoHelper.getStringDTO(((SessionedActivityInterface) activity).getLogoutTransition()));

        OneTimeWorkRequest sessionWorkerRequest = new OneTimeWorkRequest.Builder(PracticeSessionWorker.class)
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
        if (sessionWorkManager != null) {
            PracticeSessionWorker.isServiceStarted = false;
            WorkManager.getInstance(getApplicationContext()).cancelAllWorkByTag("sessionWorker");
            SessionWorker.handler.removeMessages(0);
        }
    }
}
