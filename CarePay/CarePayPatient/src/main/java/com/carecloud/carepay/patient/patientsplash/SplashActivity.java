package com.carecloud.carepay.patient.patientsplash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.carecloud.carepay.patient.BuildConfig;
import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.patientsplash.dtos.SelectLanguageDTO;
import com.carecloud.carepay.patient.payment.androidpay.AndroidPayQueueUploadService;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.fcm.RegistrationIntentService;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.newrelic.agent.android.NewRelic;

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

        Intent queueIntent = new Intent(getContext(), AndroidPayQueueUploadService.class);
        startService(queueIntent);//send any pending android pay payments

        setUncaughtExceptionHandler();
    }

    WorkflowServiceCallback applicationStartCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            if (workflowDTO.getPayload().has("language_metadata")) {
                getWorkflowServiceHelper().saveLabels(workflowDTO.getPayload()
                        .getAsJsonObject("language_metadata").getAsJsonObject("metadata")
                        .getAsJsonObject("labels"));
            }
            if (SystemUtil.isNotEmptyString(getApplicationPreferences().getUserLanguage())) {
                String languageId = getApplicationPreferences().getUserLanguage();
                Gson gson = new Gson();
                SelectLanguageDTO selectLanguageDTO = gson.fromJson(workflowDTO.toString(), SelectLanguageDTO.class);
                Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
                header.put("Accept-Language", languageId);
                Map<String, String> query = new HashMap<>();
                if (getApplicationPreferences().getUserLanguage() != null) {
                    query.put("language", getApplicationPreferences().getUserLanguage());
                }
                getWorkflowServiceHelper().execute(selectLanguageDTO.getMetadata().getTransitions().getSignin(),
                        signInCallback, null, query, header);
            } else {
                navigateToWorkflow(workflowDTO);
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback signInCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            navigateToWorkflow(workflowDTO, getIntent().getExtras());
            // end-splash activity and transition
            SplashActivity.this.finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);

        }
    };
}