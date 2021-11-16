package com.carecloud.carepay.practice.library.session;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.session.SessionedActivityInterface;
import com.carecloud.carepaylibray.session.WarningSessionActivity;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2019-07-03.
 */
public class PracticeWarningSessionActivity extends WarningSessionActivity {

    private static final int FULLSCREEN_VALUE = 0x1000000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("countdown")) {
            countDown = Integer.parseInt(intent.getStringExtra("countdown"));
        }
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | FULLSCREEN_VALUE;
        decorView.setSystemUiVisibility(uiOptions);
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean manageSession() {
        return false;
    }

    @Override
    protected void onContinueButton() {
        ((CarePayApplication) getApplication()).cancelSession();
        TransitionDTO logoutTransition = DtoHelper.getConvertedDTO(TransitionDTO.class, getIntent().getExtras());
        Data.Builder builder = new Data.Builder();
        builder.putString("logout_transition",
                DtoHelper.getStringDTO(logoutTransition));

        OneTimeWorkRequest sessionWorkerRequest = new OneTimeWorkRequest.Builder(PracticeSessionWorker.class)
                .setInputData(builder.build())
                .addTag("sessionWorker")
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork(
                "sessionWorker",
                ExistingWorkPolicy.REPLACE,
                sessionWorkerRequest);
        finish();
    }

    @Override
    protected void restartApp() {
        ((CarePayApplication) getApplication()).cancelSession();
        TransitionDTO logoutTransition = DtoHelper.getConvertedDTO(TransitionDTO.class, getIntent().getExtras());
        logOut(logoutTransition, false);
    }

    private void logOut(TransitionDTO transition, boolean shouldContinue) {
        ((CarePayApplication) getApplication()).cancelSession();
        getWorkflowServiceHelper().execute(transition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                getAppAuthorizationHelper().setUser(null);
                if (shouldContinue) {
                    PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
                } else {
                    TransitionDTO patientModeTransition = ApplicationPreferences.getInstance().getPatientModeTransition();
                    logOut(patientModeTransition, true);
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
            }
        });
    }
}
