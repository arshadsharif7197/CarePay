package com.carecloud.carepay.practice.tablet;

import android.app.Application;
import android.os.Build;
import android.provider.Settings;

import com.carecloud.carepay.practice.library.base.NavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 */

public class CarePayApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DeviceIdentifierDTO deviceIdentifierDTO=new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType("Android Tablet");
        deviceIdentifierDTO.setDeviceSystemVersion(Build.VERSION.RELEASE);
        HttpConstants.setDeviceInformation(deviceIdentifierDTO);
        WorkflowServiceHelper.initialization(WorkflowServiceHelper.ApplicationType.PRACTICE);
        NavigationHelper.initInstance(this);
    }
}
