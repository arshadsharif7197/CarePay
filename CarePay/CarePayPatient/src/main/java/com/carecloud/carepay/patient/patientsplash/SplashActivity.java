package com.carecloud.carepay.patient.patientsplash;

import android.os.Bundle;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.patientsplash.dtos.SelectLanguageDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

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
    WorkflowServiceCallback signInCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(SplashActivity.this).navigateToWorkflow(workflowDTO);
            // end-splash activity and transition
            SplashActivity.this.finish();
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

            if (!SystemUtil.isNotEmptyString(ApplicationPreferences.Instance.getUserLanguage())) {
                PatientNavigationHelper.getInstance(SplashActivity.this).navigateToWorkflow(workflowDTO);
            } else if (SystemUtil.isNotEmptyString(ApplicationPreferences.Instance.getUserLanguage())) {
              String languageid=  ApplicationPreferences.Instance.getUserLanguage();

                // Convert to SignInSignUpDTO
                Gson gson = new Gson();
                SelectLanguageDTO signInSignUpDTO = gson.fromJson(workflowDTO.toString(), SelectLanguageDTO.class);

                Map<String, String> header = WorkflowServiceHelper.getApplicationStartHeaders();
                header.put("Accept-Language", languageid);

                WorkflowServiceHelper.getInstance().execute(signInSignUpDTO.getMetadata().getTransitions().getSignin(), signInCallback, null, null, header);
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            //   SystemUtil.showDialogMessage(SplashActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // dynamic transition
        WorkflowServiceHelper.getInstance().executeApplicationStartRequest(applicationStartCallback);

    }
}