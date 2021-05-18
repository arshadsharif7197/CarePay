package com.carecloud.carepay.practice.library.session;

import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepaylibray.session.SessionService;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2019-07-03.
 */
public class PracticeSessionService extends SessionService {

    private static TransitionDTO logoutTransition;

    @Override
    public void onCreate() {
        super.onCreate();
        if (((IApplicationSession) getApplication()).getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            sessionTimeout = Long.parseLong(((IApplicationSession) getApplication()).getApplicationPreferences().getPatientSessionTime());
            sessionTimeout = 1000 * 60 * (sessionTimeout-1); // minus 1 because of 1 minute expiry time for popup dialog
        } else {
            sessionTimeout = Long.parseLong(((IApplicationSession) getApplication()).getApplicationPreferences().getPracticeSessionTime());
            sessionTimeout = 1000 * 60 * (sessionTimeout-1); // minus 1 because of 1 minute expiry time for popup dialog
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int val = super.onStartCommand(intent, flags, startId);
        if (intent != null && intent.getExtras() != null && logoutTransition == null) {
            logoutTransition = DtoHelper.getConvertedDTO(TransitionDTO.class, intent.getExtras());
        }
        return val;
    }

    @Override
    protected void callWarningActivity() {
        Intent intent = new Intent(this, PracticeWarningSessionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (logoutTransition != null) {
            Bundle bundle = new Bundle();
            DtoHelper.bundleDto(bundle, logoutTransition);
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
}
