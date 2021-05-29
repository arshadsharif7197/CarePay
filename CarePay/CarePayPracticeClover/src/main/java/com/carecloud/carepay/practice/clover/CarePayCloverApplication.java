package com.carecloud.carepay.practice.clover;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.carecloud.carepay.practice.clover.utils.ChipInterceptorUtil;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.session.PracticeSessionWorker;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.session.SessionWorker;
import com.carecloud.carepaylibray.session.SessionedActivityInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.shamrocksdk.ShamrockSdk;
import com.clover.sdk.util.Platform;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.newrelic.agent.android.NewRelic;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016
 */

public class CarePayCloverApplication extends CarePayApplication
        implements Application.ActivityLifecycleCallbacks {

    private ApplicationMode applicationMode;
    private MixpanelAPI mixpanelAPI;
    public WorkManager sessionWorkManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mixpanelAPI = MixpanelAPI.getInstance(this.getApplicationContext(), BuildConfig.MIX_PANEL_TOKEN);
        setHttpConstants();
        ShamrockSdk.init(HttpConstants.getPaymentsApiKey(), HttpConstants.getDeepStreamUrl(), HttpConstants.getPaymentsUrl());
        AppCenter.start(this, "132d415e-d431-4a5e-a17f-158e9fabf624",
                Analytics.class, Crashes.class);
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
        ChipInterceptorUtil.getInstance().startIntercept(this,
                ChipInterceptorUtil.CLOVER_CARD_INTERCEPT_ACTION);
        if (activity instanceof SessionedActivityInterface
                && ((SessionedActivityInterface) activity).manageSession()
                && !PracticeSessionWorker.isServiceStarted) {
            restartSession(activity);
        } else if (activity instanceof SessionedActivityInterface
                && ((SessionedActivityInterface) activity).manageSession()
                && PracticeSessionWorker.isServiceStarted && PracticeSessionWorker.isLogoutNeeded) {
            callLogoutService(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ChipInterceptorUtil.getInstance().stopIntercept(this);
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

    private String getDeviceType() {
        if (Platform.isCloverStation()) {
            return CarePayConstants.CLOVER_DEVICE;
        } else {
            return CarePayConstants.CLOVER_2_DEVICE;
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
            PracticeSessionWorker.isLogoutNeeded = false;
            WorkManager.getInstance(getApplicationContext()).cancelAllWorkByTag("sessionWorker");
            SessionWorker.handler.removeMessages(0);
        }
    }

    private void callLogoutService(Activity activity) {
        ((CarePayApplication) getApplicationContext()).getWorkflowServiceHelper().execute(
                ((((SessionedActivityInterface) activity).getLogoutTransition())), new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        cancelSession();
                        PracticeNavigationHelper.navigateToWorkflow(getApplicationContext(), workflowDTO);
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        restartSession(activity);
                    }
                });
    }
}
