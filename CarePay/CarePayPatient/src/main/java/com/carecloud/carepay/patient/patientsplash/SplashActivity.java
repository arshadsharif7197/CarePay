package com.carecloud.carepay.patient.patientsplash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.carecloud.carepay.patient.BuildConfig;
import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.patientsplash.dtos.SelectLanguageDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
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

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Toast.makeText(getContext(), Label.getLabel("crash_handled_error_message"), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SplashActivity.this, SplashActivity.class);
                intent.putExtra("crash", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(SplashActivity.this.getBaseContext(),
                        0, intent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager mgr = (AlarmManager) SplashActivity.this.getBaseContext()
                        .getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1, pendingIntent);
                finish();
                System.exit(2);
            }
        });
    }

    WorkflowServiceCallback applicationStartCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {

            if (!SystemUtil.isNotEmptyString(getApplicationPreferences().getUserLanguage())) {
                navigateToWorkflow(workflowDTO);
            } else if (SystemUtil.isNotEmptyString(getApplicationPreferences().getUserLanguage())) {
                String languageid = getApplicationPreferences().getUserLanguage();

                // Convert to SignInSignUpDTO
                Gson gson = new Gson();
                SelectLanguageDTO signInSignUpDTO = gson.fromJson(workflowDTO.toString(), SelectLanguageDTO.class);

                Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
                header.put("Accept-Language", languageid);

                getWorkflowServiceHelper().execute(signInSignUpDTO.getMetadata().getTransitions().getSignin(),
                        signInCallback, null, null, header);
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
            getWorkflowServiceHelper().saveLabels(workflowDTO);
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