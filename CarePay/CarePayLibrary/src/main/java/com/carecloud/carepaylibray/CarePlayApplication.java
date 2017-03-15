package com.carecloud.carepaylibray;

import android.support.multidex.MultiDexApplication;

import com.carecloud.carepaylibray.base.AndroidPlatform;
import com.carecloud.carepaylibray.base.Platform;

/**
 * Created by pjohnson on 15/03/17.
 */

public class CarePlayApplication extends MultiDexApplication{
    @Override
    public void onCreate() {
        super.onCreate();
        Platform.setPlatform(new AndroidPlatform(this));
    }
}
