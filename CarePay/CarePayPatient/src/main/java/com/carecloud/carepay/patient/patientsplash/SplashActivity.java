package com.carecloud.carepay.patient.patientsplash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.patientsplash.dtos.SelectLanguageDTO;
import com.carecloud.carepay.patient.selectlanguage.SelectLanguageActivity;
import com.carecloud.carepay.patient.signinsignuppatient.SigninSignupActivity;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.signinsignup.dtos.SignInSignUpDTO;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jahirul Bhuiyan on 10/13/2016.
 * This is the Launcher activity for the patient app
 * Applied logic:
 * If application language not yet selected navigate to language selection screen
 * if user authentication not found navigate to login screen
 * else navigate to appointment screen
 */

public class SplashActivity extends BasePatientActivity {

    private static final int STOPSPLASH = 0;
    private static final long SPLASHTIME = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


//        Message msg = new Message();
//        msg.what = STOPSPLASH;
//        splashHandler.sendMessageDelayed(msg, SPLASHTIME);

        // dynamic transition
        WorkflowServiceHelper.getInstance().executeApplicationStartRequest(applicationStartCallback);

    }

 
    private Handler splashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
        }
    };

    WorkflowServiceCallback applicationStartCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            boolean signedIn = CognitoAppHelper.findCurrentUser(null);

            if (signedIn) {
                Intent intent = new Intent(SplashActivity.this, AppointmentsActivity.class);
                startActivity(intent);
            } else {
                if (!(ApplicationPreferences.Instance.getUserLanguage().equals("English"))) {
                    PatientNavigationHelper.instance().navigateToWorkflow(workflowDTO);
                } else if (ApplicationPreferences.Instance.getUserLanguage().equals("English")) {
//                Intent intent = new Intent(SplashActivity.this, SigninSignupActivity.class);
//                startActivity(intent);
                    // Convert to SignInSignUpDTO
                    Gson gson = new Gson();
                    SelectLanguageDTO signInSignUpDTO = gson.fromJson(workflowDTO.toString(), SelectLanguageDTO.class);

                    //WorkflowServiceHelper.getInstance().executeApplicationStartRequest(signInCallback);

                    WorkflowServiceHelper.getInstance().execute(signInSignUpDTO.getMetadata().getTransitions().getSignin(), signInCallback,null,null, WorkflowServiceHelper.getApplicationStartHeaders());
                }
            }

            // end-splash activity and transition
            SplashActivity.this.finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            //   SystemUtil.showDialogMessage(SplashActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback signInCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            int dummy = 0;
            PatientNavigationHelper.instance().navigateToWorkflow(workflowDTO.getState());
        }

        @Override
        public void onFailure(String exceptionMessage) {
        }
    };
}