package com.carecloud.carepay.practice.tablet.base;

import android.app.Application;
import android.os.Build;
import android.provider.Settings;

import com.carecloud.carepay.practice.library.base.NavigationHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * Application class for the practice app tablet
 * Use for initialize application global variables
 */

public class CarePayPracticeTabletApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DeviceIdentifierDTO deviceIdentifierDTO=new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType("Android Tablet");
        deviceIdentifierDTO.setDeviceSystemVersion(Build.VERSION.RELEASE);
        HttpConstants.setDeviceInformation(deviceIdentifierDTO);
        NavigationHelper.initInstance(this);
    }
}
