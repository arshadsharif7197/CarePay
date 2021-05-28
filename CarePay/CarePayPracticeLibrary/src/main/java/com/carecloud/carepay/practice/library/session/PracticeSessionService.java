package com.carecloud.carepay.practice.library.session;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.session.SessionService;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2019-07-03.
 */
public class PracticeSessionService extends SessionService {

    private static TransitionDTO logoutTransition;
    public static boolean isServiceStarted;
    private CountDownTimer logoutTimer;
    private Context context;

    @Override
    public void onCreate() {
        context = this;
        isServiceStarted = true;
        super.onCreate();
        if (((IApplicationSession) getApplication()).getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            sessionTimeout = Long.parseLong(((IApplicationSession) getApplication()).getApplicationPreferences().getPatientSessionTime());
            sessionTimeout = 1000 * 60 * (sessionTimeout - 1); // minus 1 because of 1 minute expiry time for popup dialog
        } else {
            sessionTimeout = Long.parseLong(((IApplicationSession) getApplication()).getApplicationPreferences().getPracticeSessionTime());
            sessionTimeout = 1000 * 60 * (sessionTimeout - 1); // minus 1 because of 1 minute expiry time for popup dialog
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int val = super.onStartCommand(intent, flags, startId);
        if (intent != null && intent.getExtras() != null && logoutTransition == null) {
            logoutTransition = DtoHelper.getConvertedDTO(TransitionDTO.class, intent.getExtras());
        }

        handleLogoutTimer();
        return START_STICKY;
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

    @Override
    protected void logout() {
        logoutTimer.start();
    }

    private void handleLogoutTimer() {
        if (logoutTimer != null) {
            logoutTimer.cancel();
        } else {
            logoutTimer = new CountDownTimer(60 * 1000, 1000) {
                @Override
                public void onTick(long timePassed) {
                    if (((CarePayApplication) getApplicationContext()).isForeground()) {
                        Intent intent = new Intent(context, PracticeWarningSessionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (logoutTransition != null) {
                            Bundle bundle = new Bundle();
                            DtoHelper.bundleDto(bundle, logoutTransition);
                            intent.putExtras(bundle);
                        }
                        intent.putExtra("countdown", (timePassed / 1000) + "");
                        startActivity(intent);
                        cancel();
                    }
                }

                @Override
                public void onFinish() {
                    if (!(((CarePayApplication) getApplicationContext()).isForeground())) {
                        callLogoutService();
                    }
                }
            };
        }
    }

    private void callLogoutService() {
        ((CarePayApplication) getApplicationContext()).getWorkflowServiceHelper().execute(logoutTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                PracticeNavigationHelper.navigateToWorkflow(getApplicationContext(), workflowDTO);
            }

            @Override
            public void onFailure(String exceptionMessage) {

            }
        });
    }
}

