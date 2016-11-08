package com.carecloud.carepay.patient.base;

import android.app.Application;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;

import com.carecloud.carepay.patient.BuildConfig;
import com.carecloud.carepay.patient.patientsplash.SplashActivity;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;
import com.carecloud.carepaylibray.utils.SystemUtil;


/**
 * Created by Jahirul on 8/25/2016.
 * this is the application class for the patient app
 */
public class CarePayPatientApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setHttpConstants();
        ApplicationPreferences.createPreferences(this);
        registerActivityLifecycleCallbacks(new CarePayActivityLifecycleCallbacks());
        CognitoAppHelper.init(getApplicationContext());
     //   WorkflowServiceHelper.getInstance().executeApplicationStartRequest(applicationStartCallback);
    }
    WorkflowServiceCallback applicationStartCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.instance().navigateToWorkflow(workflowDTO.getState());

            // end-splash activity and transition
        }

        @Override
        public void onFailure(String exceptionMessage) {
         //   SystemUtil.showDialogMessage(SplashActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

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
    }
}
