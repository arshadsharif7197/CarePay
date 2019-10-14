package com.carecloud.carepay.patient.patientsplash;

import android.content.Context;
import android.util.Log;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

import java.util.Map;


public class BaseActivityTest {

    private ApplicationPreferences applicationPreferences;


    public static String workFlowDtoString;
    public static String workFlowDtoStringAppointments;
    public static String user;
    public static String passowrd;
    private String TAG = BaseActivityTest.class.getSimpleName();
    protected ApplicationMode applicationMode;
    protected AppAuthorizationHelper appAuthorizationHelper;
    protected WorkflowServiceHelper workflowServiceHelper;
    protected Context context;


    WorkflowServiceCallback applicationStartCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
            header.put("Accept-Language", "en");
            Log.d(TAG, "onPostExecute applicationStartCallback");
        }

        @Override
        public void onFailure(String exceptionMessage) {
        }
    };


    /**
     * Get workflow service helper
     */
    public WorkflowServiceHelper getWorkflowServiceHelper() {

        if (workflowServiceHelper == null) {
            workflowServiceHelper = new WorkflowServiceHelper(getApplicationPreferences(), getApplicationMode());
        }
        return workflowServiceHelper;

    }


    /**
     * Get application mode
     */
    public ApplicationMode getApplicationMode() {
        if (applicationMode == null) {
            applicationMode = new ApplicationMode();
            applicationMode.setApplicationType(ApplicationMode.ApplicationType.PATIENT);
        }

        return applicationMode;
    }


    /**
     * Get application preferences
     */
    public ApplicationPreferences getApplicationPreferences() {
        if (applicationPreferences == null) {
            applicationPreferences = ApplicationPreferences.getInstance();
        }
        return applicationPreferences;
    }

    /**
     * Pause execution
     */
    public void delay(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
