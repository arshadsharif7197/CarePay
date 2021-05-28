package com.carecloud.carepay.practice.library.session;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.session.SessionWorker;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.common.util.concurrent.ListenableFuture;

public class PracticeSessionWorker extends SessionWorker {

    private static TransitionDTO logoutTransition;
    public static boolean isServiceStarted;
    private CountDownTimer logoutTimer;

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public PracticeSessionWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        this.logoutTransition = DtoHelper.getConvertedDTO(TransitionDTO.class, getInputData().getString("logout_transition"));
        isServiceStarted = true;
        if (((IApplicationSession) getApplicationContext()).getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            sessionTimeout = Long.parseLong(((IApplicationSession) getApplicationContext()).getApplicationPreferences().getPatientSessionTime());
            sessionTimeout = 1000 * 60 * (sessionTimeout - 1); // minus 1 because of 1 minute expiry time for popup dialog
        } else {
            sessionTimeout = Long.parseLong(((IApplicationSession) getApplicationContext()).getApplicationPreferences().getPracticeSessionTime());
            sessionTimeout = 1000 * 60 * (sessionTimeout - 1); // minus 1 because of 1 minute expiry time for popup dialog
        }

    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        handleLogoutTimer();
        return super.startWork();
    }

    @Override
    protected void callWarningActivity() {
        if (isServiceStarted) {
            Intent intent = new Intent(getApplicationContext(), PracticeWarningSessionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (logoutTransition != null) {
                Bundle bundle = new Bundle();
                DtoHelper.bundleDto(bundle, logoutTransition);
                intent.putExtras(bundle);
            }
            getApplicationContext().startActivity(intent);
        }
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
                        if (isServiceStarted) {
                            Intent intent = new Intent(getApplicationContext(), PracticeWarningSessionActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (logoutTransition != null) {
                                Bundle bundle = new Bundle();
                                DtoHelper.bundleDto(bundle, logoutTransition);
                                intent.putExtras(bundle);
                            }
                            intent.putExtra("countdown", (timePassed / 1000) + "");
                            getApplicationContext().startActivity(intent);
                        }
                        cancel();
                    }
                }

                @Override
                public void onFinish() {
                    if (!(((CarePayApplication) getApplicationContext()).isForeground())
                            && isServiceStarted) {
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
                ((CarePayApplication) getApplicationContext()).cancelSession();
                PracticeNavigationHelper.navigateToWorkflow(getApplicationContext(), workflowDTO);
            }

            @Override
            public void onFailure(String exceptionMessage) {

            }
        });
    }
}

