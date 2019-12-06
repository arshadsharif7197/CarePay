package com.carecloud.carepay.practice.library.splash;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.base.models.DeviceVersionModel;
import com.carecloud.carepaylibray.base.models.LatestVersionDTO;
import com.carecloud.carepaylibray.base.models.LatestVersionModel;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;
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
        public void onFailure(ServerErrorDTO serverErrorDto) {
            showErrorNotification(null);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), serverErrorDto.getMessage().getBody().getError().getMessage());
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

            LatestVersionDTO latestVersionDTO = DtoHelper.getConvertedDTO(LatestVersionDTO.class, workflowDTO);
            checkLatestVersion(latestVersionDTO.getMetadata().getLinks().getCheckLatestVersion());
        }

        @Override
        public void onFailure(ServerErrorDTO serverErrorDto) {
            showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
            Log.e(getString(R.string.alert_title_server_error), serverErrorDto.getMessage().getBody().getError().getMessage());
        }
    };

    private void checkLatestVersion(TransitionDTO versionCheckLink) {
        try {
            PackageInfo packageInfo = getContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0);
            DeviceVersionModel versionModel = new DeviceVersionModel();
            versionModel.setApplicationName(packageInfo.packageName);
            versionModel.setVersionName(packageInfo.versionName);
            versionModel.setVersionNumber(packageInfo.versionCode);
            versionModel.setDeviceType(HttpConstants.getDeviceInformation().getDeviceType());

            Gson gson = new Gson();
            String payload = gson.toJson(versionModel);
            getWorkflowServiceHelper().execute(versionCheckLink, getVersionCheckCallback(versionModel), payload, null, getWorkflowServiceHelper().getApplicationStartHeaders());
        } catch (PackageManager.NameNotFoundException nfe) {
            nfe.printStackTrace();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), initialWorkFlow, getIntent().getExtras());
            SplashActivity.this.finish();

        }
    }

    private WorkflowServiceCallback getVersionCheckCallback(final DeviceVersionModel deviceVersionModel) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                LatestVersionDTO latestVersionDTO = DtoHelper.getConvertedDTO(LatestVersionDTO.class, workflowDTO);
                LatestVersionModel latestVersionModel = latestVersionDTO.getPayload().getVersionModel();
                getApplicationPreferences().setLatestVersion(deviceVersionModel.getVersionNumber() >= latestVersionModel.getVersionNumber());
                if (getApplicationPreferences().getLastVersionNum() != latestVersionModel.getVersionNumber()) {
                    getApplicationPreferences().setLastVersionNum(latestVersionModel.getVersionNumber());
                }
                getApplicationPreferences().setForceUpdate(deviceVersionModel.getVersionNumber() <
                        latestVersionModel.getVersionNumber() && latestVersionModel.isForceUpdate());
                PracticeNavigationHelper.navigateToWorkflow(getContext(), initialWorkFlow, getIntent().getExtras());
                SplashActivity.this.finish();
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                Log.d(getClass().getName(), serverErrorDto.getMessage().getBody().getError().getMessage());
                PracticeNavigationHelper.navigateToWorkflow(getContext(), initialWorkFlow, getIntent().getExtras());
                SplashActivity.this.finish();
            }
        };
    }

}
