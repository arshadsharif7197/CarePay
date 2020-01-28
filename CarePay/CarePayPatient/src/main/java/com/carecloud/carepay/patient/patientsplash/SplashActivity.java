package com.carecloud.carepay.patient.patientsplash;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.carecloud.carepay.patient.BuildConfig;
import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.patientsplash.dtos.SelectLanguageDTO;
import com.carecloud.carepay.patient.payment.androidpay.AndroidPayQueueUploadService;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.base.models.DeviceVersionModel;
import com.carecloud.carepaylibray.base.models.LatestVersionDTO;
import com.carecloud.carepaylibray.base.models.LatestVersionModel;
import com.carecloud.carepaylibray.fcm.RegistrationIntentService;
import com.carecloud.carepaylibray.utils.DtoHelper;
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

        RegistrationIntentService.enqueueWork(getContext(), new Intent());

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
                getApplicationPreferences().setUserLanguage(workflowDTO.getPayload()
                        .getAsJsonObject("language_metadata").get("code").getAsString());
            }

            Gson gson = new Gson();
            SelectLanguageDTO selectLanguageDTO = gson.fromJson(workflowDTO.toString(), SelectLanguageDTO.class);
            checkLatestVersion(selectLanguageDTO.getMetadata().getLinks().getCheckLatestVersion());
            Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
            header.put("Accept-Language", getApplicationPreferences().getUserLanguage());
            Map<String, String> query = new HashMap<>();
            query.put("language", getApplicationPreferences().getUserLanguage());
            getWorkflowServiceHelper().execute(selectLanguageDTO.getMetadata().getTransitions().getSignin(),
                    signInCallback, null, query, header);
        }

        @Override
        public void onFailure(ServerErrorDTO serverErrorDto) {
            showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
            Log.e(getString(R.string.alert_title_server_error), serverErrorDto.getMessage().getBody().getError().getMessage());
        }
    };

    WorkflowServiceCallback signInCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            if (getIntent().getData() != null) {
                String inviteId = getIntent().getData().getQueryParameter("i");
                getIntent().putExtra("inviteId", inviteId);
            }
            navigateToWorkflow(workflowDTO, getIntent().getExtras());
            // end-splash activity and transition
            SplashActivity.this.finish();
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
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                Log.d(getClass().getName(), serverErrorDto.getMessage().getBody().getError().getMessage());
            }
        };
    }

    @Override
    public boolean manageSession() {
        return false;
    }
}