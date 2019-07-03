package com.carecloud.carepay.patient.session;

import android.content.Intent;

import com.carecloud.carepay.patient.patientsplash.SplashActivity;
import com.carecloud.carepaylibray.session.WarningSessionActivity;

/**
 * @author pjohnson on 2019-07-02.
 */
public class PatientWarningSessionActivity extends WarningSessionActivity {

    @Override
    protected void onContinueButton() {
        Intent serviceIntent = new Intent(this, PatientSessionService.class);
        startService(serviceIntent);
        finish();
    }

    @Override
    protected void restartApp() {
        onAtomicRestart();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
