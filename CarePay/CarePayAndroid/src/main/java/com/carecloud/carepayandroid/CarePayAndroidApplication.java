package com.carecloud.carepayandroid;

import android.app.Application;

import com.carecloud.carepayandroid.base.CarePayActivityLifecycleCallbacks;
import com.carecloud.carepaylibray.cognito.CognitoAppHelper;
import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;


/**
 * Created by lsoco_user on 8/25/2016.
 */
public class CarePayAndroidApplication extends Application {
    private DemographicModel demographicModel; // hold the demographics model

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationPreferences.createPreferences(this);
        registerActivityLifecycleCallbacks(new CarePayActivityLifecycleCallbacks());
        CognitoAppHelper.init(getApplicationContext());
    }

    public DemographicModel getDemographicModel() {
        return demographicModel;
    }

    public void setDemographicModel(DemographicModel demographicModel) {
        this.demographicModel = demographicModel;
    }
}
