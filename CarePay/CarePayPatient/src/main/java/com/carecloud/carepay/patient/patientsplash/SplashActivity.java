package com.carecloud.carepay.patient.patientsplash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.carecloud.carepay.patient.BuildConfig;
import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.patientsplash.dtos.SelectLanguageDTO;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.fcm.RegistrationIntentService;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.newrelic.agent.android.NewRelic;

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
            getWorkflowServiceHelper().saveLabels(workflowDTO);
            navigateToWorkflow(workflowDTO);
            // end-splash activity and transition
            SplashActivity.this.finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);

        }
    };

    WorkflowServiceCallback applicationStartCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {

            if (!SystemUtil.isNotEmptyString(getApplicationPreferences().getUserLanguage())) {
                navigateToWorkflow(workflowDTO);
            } else if (SystemUtil.isNotEmptyString(getApplicationPreferences().getUserLanguage())) {
              String languageid=  getApplicationPreferences().getUserLanguage();

                // Convert to SignInSignUpDTO
                Gson gson = new Gson();
                SelectLanguageDTO signInSignUpDTO = gson.fromJson(workflowDTO.toString(), SelectLanguageDTO.class);

                Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
                header.put("Accept-Language", languageid);

                getWorkflowServiceHelper().execute(signInSignUpDTO.getMetadata().getTransitions().getSignin(), signInCallback, null, null, header);
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        WorkflowSessionHandler.createSession(this);

        // dynamic transition
        getWorkflowServiceHelper().executeApplicationStartRequest(applicationStartCallback);

        String newRelicId = BuildConfig.NEW_RELIC_ID;
        NewRelic.withApplicationToken(newRelicId).start(this.getApplication());

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}