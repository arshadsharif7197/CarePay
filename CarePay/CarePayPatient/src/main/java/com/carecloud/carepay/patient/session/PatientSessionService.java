package com.carecloud.carepay.patient.session;

import android.content.Intent;

import com.carecloud.carepaylibray.session.SessionService;

/**
 * @author pjohnson on 2019-07-02.
 */
public class PatientSessionService extends SessionService {

    @Override
    public void onCreate() {
        super.onCreate();
        sessionTimeout = PATIENT_SESSION_TIMEOUT;
    }

    @Override
    protected void callWarningActivity() {
        Intent intent = new Intent(this, PatientWarningSessionActivity.class);
        startActivity(intent);
    }
}
