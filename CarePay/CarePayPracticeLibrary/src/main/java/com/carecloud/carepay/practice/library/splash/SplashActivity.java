package com.carecloud.carepay.practice.library.splash;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;

public class SplashActivity extends BasePracticeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        WorkflowSessionHandler.createSession(this);

        getWorkflowServiceHelper().executeApplicationStartRequest(applicationStartCallback);

        //clear old Labels from Preferences
        SharedPreferences preferences = ((AndroidPlatform) Platform.get())
                .openSharedPreferences(AndroidPlatform.LABELS_FILE_NAME);
        preferences.edit().clear().apply();
    }

    WorkflowServiceCallback applicationStartCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            getWorkflowServiceHelper().saveLabels(workflowDTO);
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, getIntent().getExtras());
            SplashActivity.this.finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(null);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
