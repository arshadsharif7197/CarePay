package com.carecloud.breezemini.base;

import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;

import com.carecloud.breezemini.BuildConfig;
import com.carecloud.breezemini.DeviceIdentifierDTO;
import com.carecloud.breezemini.HttpConstants;

/**
 * Created by kkannan on 5/25/17.
 */

public class ShamrockLite extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        setHttpConstants();

    }

    private void setHttpConstants() {
        DeviceIdentifierDTO deviceIdentifierDTO = new DeviceIdentifierDTO();
        deviceIdentifierDTO.setDeviceIdentifier(Settings.Secure.ANDROID_ID);
        deviceIdentifierDTO.setDeviceType("CloverMini");
        deviceIdentifierDTO.setDeviceSystemVersion(Build.VERSION.RELEASE);
        HttpConstants.setDeviceInformation(deviceIdentifierDTO);
        HttpConstants.setApiStartUrl(BuildConfig.API_START_URL);
        HttpConstants.setApiStartKey(BuildConfig.X_API_KEY);
        HttpConstants.setApiBaseUrl(BuildConfig.API_BASE_URL);
    }

}

