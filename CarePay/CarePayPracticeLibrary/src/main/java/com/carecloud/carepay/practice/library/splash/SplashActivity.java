package com.carecloud.carepay.practice.library.splash;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.selectlanguage.SelectLangaugeActivity;
import com.carecloud.carepaylibray.signinsignup.SigninSignupActivity;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class SplashActivity extends BasePracticeActivity {

    private static final int STOPSPLASH = 0;
    private static final long SPLASHTIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        WorkflowServiceHelper.getInstance().executeApplicationStartRequest(applicationStartCallback);
    }

    WorkflowServiceCallback applicationStartCallback=new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PracticeNavigationHelper.getInstance().navigateToWorkflow(workflowDTO);
            SplashActivity.this.finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(SplashActivity.this,getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
