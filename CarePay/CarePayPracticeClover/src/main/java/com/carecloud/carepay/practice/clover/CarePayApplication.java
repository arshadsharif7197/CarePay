package com.carecloud.carepay.practice.clover;

import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;

import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.carecloud.carepay.service.library.mode.Mode;
import com.carecloud.carepay.service.library.mode.ModeChangeable;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 */

public class CarePayApplication extends MultiDexApplication implements ModeChangeable {

    private Mode mode = Mode.MODE_PRACTICE;

    @Override
    public void onCreate() {
        super.onCreate();
        setHttpConstants();

        WorkflowServiceHelper.initialization(WorkflowServiceHelper.ApplicationType.PRACTICE);
        PracticeNavigationHelper.initInstance(this);
    }

    private void setHttpConstants() {
        DeviceIdentifierDTO deviceIdentifierDTO=new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType("Clover");
        deviceIdentifierDTO.setDeviceSystemVersion(Build.VERSION.RELEASE);
        HttpConstants.setDeviceInformation(deviceIdentifierDTO);
        HttpConstants.setApiBaseUrl(BuildConfig.API_BASE_URL);
        HttpConstants.setApiStartUrl(BuildConfig.API_START_URL);
        HttpConstants.setApiStartKey(BuildConfig.X_API_KEY);
        HttpConstants.setPushNotificationWebclientUrl(BuildConfig.WEBCLIENT_URL);
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
