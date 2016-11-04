package com.carecloud.carepay.practice.clover;

import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;

import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 */

public class CarePayApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        DeviceIdentifierDTO deviceIdentifierDTO=new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType("Clover");
        deviceIdentifierDTO.setDeviceSystemVersion(Build.VERSION.RELEASE);
        HttpConstants.setDeviceInformation(deviceIdentifierDTO);
        WorkflowServiceHelper.initialization(WorkflowServiceHelper.ApplicationType.PRACTICE);
        PracticeNavigationHelper.initInstance(this);
    }
}
