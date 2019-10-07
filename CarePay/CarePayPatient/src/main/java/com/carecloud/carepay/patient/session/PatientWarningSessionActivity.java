package com.carecloud.carepay.patient.session;

import android.content.Intent;

import com.carecloud.carepay.patient.patientsplash.SplashActivity;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepaylibray.session.WarningSessionActivity;

/**
 * @author pjohnson on 2019-07-02.
 */
public class PatientWarningSessionActivity extends WarningSessionActivity {

    private boolean isInForeground;

    @Override
    protected void onResume() {
        super.onResume();
        isInForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForeground = false;
    }

    @Override
    protected void onContinueButton() {
        Intent serviceIntent = new Intent(this, PatientSessionService.class);
        startService(serviceIntent);
        finish();
    }

    @Override
    protected void restartApp() {
        if (isInForeground) {
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
        } else {
            onAtomicRestart();
            stopSessionService();
            finishAffinity();
            System.exit(0);
        }
    }
}
