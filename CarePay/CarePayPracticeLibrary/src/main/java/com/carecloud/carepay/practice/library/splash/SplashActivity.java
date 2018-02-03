package com.carecloud.carepay.practice.library.splash;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends BasePracticeActivity {

    private WorkflowDTO initialWorkFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        WorkflowSessionHandler.createSession(this);

        getWorkflowServiceHelper().executeApplicationStartRequest(applicationStartCallback);

        Label.clearLabels();

        setUncaughtExceptionHandler();
    }

    WorkflowServiceCallback applicationStartCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            initialWorkFlow = workflowDTO;
            SignInDTO signInDTO = DtoHelper.getConvertedDTO(SignInDTO.class, workflowDTO);
            callLanguageEndpoint(signInDTO.getMetadata().getLinks().getLanguage(),
                    getApplicationPreferences().getUserLanguage());
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(null);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void callLanguageEndpoint(TransitionDTO transition, String languageCode) {
        Map<String, String> query = new HashMap<>();
        query.put("language", languageCode);
        getWorkflowServiceHelper().execute(transition, languageCallback, null, query,
                getWorkflowServiceHelper().getApplicationStartHeaders());
    }

    protected WorkflowServiceCallback languageCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            if (workflowDTO.getPayload().has("language_metadata")) {
                JsonObject labels = workflowDTO.getPayload().getAsJsonObject("language_metadata")
                        .getAsJsonObject("metadata").getAsJsonObject("labels");
                getWorkflowServiceHelper().saveLabels(labels);
                //set default labels for patient.
                getWorkflowServiceHelper().saveLabels(labels, CarePayConstants.PATIENT_MODE_LABELS_PREFIX);
                getApplicationPreferences().setUserLanguage(workflowDTO.getPayload()
                        .getAsJsonObject("language_metadata").get("code").getAsString());
            }
            PracticeNavigationHelper.navigateToWorkflow(getContext(), initialWorkFlow, getIntent().getExtras());
            SplashActivity.this.finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
