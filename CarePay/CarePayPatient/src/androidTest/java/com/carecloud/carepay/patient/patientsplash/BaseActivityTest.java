package com.carecloud.carepay.patient.patientsplash;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.carecloud.carepay.patient.patientsplash.dtos.SelectLanguageDTO;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.cognito.CognitoActionCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


public class BaseActivityTest {

    private ApplicationPreferences applicationPreferences;


    public static String workFlowDtoString;
    public static String workFlowDtoStringAppointments;
    public static String user;
    public static String passowrd;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private String TAG = BaseActivityTest.class.getSimpleName();
    protected ApplicationMode applicationMode;
    protected AppAuthorizationHelper appAuthorizationHelper;
    protected WorkflowServiceHelper workflowServiceHelper;
    protected Context context;


    WorkflowServiceCallback signInCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            Log.d(TAG, "on pre execute");
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            workFlowDtoString = workflowDTO.toString();
            SignInDTO signInSignUpDTO = new Gson().fromJson(workFlowDtoString, SignInDTO.class);
//            getApplicationMode().setCognitoDTO(signInSignUpDTO.getPayload().getPatientAppSignin().getCognito());
            getAppAuthorizationHelper().signIn(user, passowrd, cognitoActionCallback);
            Log.d(TAG, "on post execute signInCallback");
        }

        @Override
        public void onFailure(String exceptionMessage) {
        }
    };

    CognitoActionCallback cognitoActionCallback = new CognitoActionCallback() {

        @Override
        public void onLoginSuccess() {
            Map<String, String> query = new HashMap<>();
            Map<String, String> header = new HashMap<>();
            header.put("Accept-Language", "en");
            TransitionDTO transitionDTO = new TransitionDTO();
            transitionDTO.setUrl("https://aze9ynjfhl.execute-api.us-east-1.amazonaws.com/dev/workflow/shamrock/patient_app/authenticate/start");
            transitionDTO.setMethod("GET");
            workflowServiceHelper.execute(transitionDTO, loginCallback, query, header);
            Log.d(TAG, "on post login success cognitoActionCallback");
        }

        @Override
        public void onBeforeLogin() {
        }

        @Override
        public void onLoginFailure(String exceptionMessage) {
            Log.d(TAG, "onLoginFailure cognitoActionCallback");
        }

    };


    WorkflowServiceCallback loginCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            workFlowDtoStringAppointments = workflowDTO.toString();
            Log.d(TAG, "onPostExecute loginCallback");
        }

        @Override
        public void onFailure(String exceptionMessage) {
        }
    };


    WorkflowServiceCallback applicationStartCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            SelectLanguageDTO signInSignUpDTO = gson.fromJson(workflowDTO.toString(), SelectLanguageDTO.class);
            Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
            header.put("Accept-Language", "en");
//            getWorkflowServiceHelper().execute(signInSignUpDTO.getMetadata().getTransitions().getSignin(), signInCallback, null, null, header);
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
            applicationPreferences = new ApplicationPreferences(getContext());
        }
        return applicationPreferences;
    }


    /**
     * get cognito helper
     */
    public AppAuthorizationHelper getAppAuthorizationHelper() {
        if (appAuthorizationHelper == null) {
            appAuthorizationHelper = new AppAuthorizationHelper(getContext(), getApplicationMode());
            getWorkflowServiceHelper().setAppAuthorizationHelper(appAuthorizationHelper);
        }
        return appAuthorizationHelper;
    }

    /**
     * Get context
     */
    public Context getContext() {
        if (context == null) {
            context = InstrumentationRegistry.getContext();
        }
        return context;
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
