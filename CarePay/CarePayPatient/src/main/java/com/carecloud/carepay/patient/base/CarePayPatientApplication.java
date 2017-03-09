package com.carecloud.carepay.patient.base;

import android.app.Application;
import android.os.Build;
import android.provider.Settings;

import com.carecloud.carepay.patient.BuildConfig;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepaylibray.base.IApplicationSession;


/**
 * Created by Jahirul on 8/25/2016.
 * this is the application class for the patient app
 */
public class CarePayPatientApplication extends Application implements IApplicationSession {

    private ApplicationPreferences applicationPreferences;
    private WorkflowServiceHelper workflowServiceHelper;
    private CognitoAppHelper cognitoAppHelper;
    private ApplicationMode applicationMode;

    @Override
    public void onCreate() {
        super.onCreate();
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
        HttpConstants.setPushNotificationWebclientUrl(BuildConfig.WEBCLIENT_URL);
        HttpConstants.setUseUnifiedAuth(BuildConfig.useUnifiedAuth);
    }

    @Override
    public ApplicationPreferences getApplicationPreferences() {
        if (applicationPreferences == null) {
            applicationPreferences = new ApplicationPreferences(this);
        }

        return applicationPreferences;
    }

    @Override
    public WorkflowServiceHelper getWorkflowServiceHelper() {
        if (workflowServiceHelper == null) {
            workflowServiceHelper = new WorkflowServiceHelper(getApplicationPreferences(), getApplicationMode());
        }

        return workflowServiceHelper;
    }

    @Override
    public CognitoAppHelper getCognitoAppHelper() {
        if (cognitoAppHelper == null) {
            cognitoAppHelper = new CognitoAppHelper(this, getApplicationMode());
            getWorkflowServiceHelper().setCognitoAppHelper(cognitoAppHelper);
        }

        return cognitoAppHelper;
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
