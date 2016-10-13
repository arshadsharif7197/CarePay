package com.carecloud.carepay.patient.base;

import android.app.Application;

import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;


/**
 * Created by Jahirul on 8/25/2016.
 * this is the application class for the patient app
 */
public class CarePayPatientApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationPreferences.createPreferences(this);
        registerActivityLifecycleCallbacks(new CarePayActivityLifecycleCallbacks());
        CognitoAppHelper.init(getApplicationContext());
    }
}
