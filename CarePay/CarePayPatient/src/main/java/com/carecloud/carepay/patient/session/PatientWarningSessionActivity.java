package com.carecloud.carepay.patient.session;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.patientsplash.SplashActivity;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.session.WarningSessionActivity;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2019-07-02.
 */
public class PatientWarningSessionActivity extends WarningSessionActivity {

    private boolean isInForeground;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("countdown")) {
            countDown = Integer.parseInt(intent.getStringExtra("countdown"));
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInForeground = true;
    }

    @Override
    public boolean manageSession() {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForeground = false;
    }

    @Override
    protected void onContinueButton() {
        ((CarePayApplication) getApplication()).cancelSession();
        TransitionDTO logoutTransition = DtoHelper.getConvertedDTO(TransitionDTO.class, getIntent().getExtras());
        Data.Builder builder = new Data.Builder();
        builder.putString("logout_transition",
                DtoHelper.getStringDTO(logoutTransition));

        OneTimeWorkRequest sessionWorkerRequest = new OneTimeWorkRequest.Builder(PatientSessionWorker.class)
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

        getApplicationMode().clearUserPracticeDTO();
        AppAuthorizationHelper authHelper = getAppAuthorizationHelper();
        authHelper.setUser(null);
        authHelper.setAccessToken(null);
        authHelper.setIdToken(null);
        authHelper.setRefreshToken(null);
        onAtomicRestart();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
