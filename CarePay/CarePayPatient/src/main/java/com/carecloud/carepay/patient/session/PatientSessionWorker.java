package com.carecloud.carepay.patient.session;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.session.SessionWorker;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.common.util.concurrent.ListenableFuture;

public class PatientSessionWorker extends SessionWorker {

    public static TransitionDTO logoutTransition;
    public static boolean isServiceStarted;
    public static boolean isLogoutNeeded;
    public static CountDownTimer logoutTimer;

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public PatientSessionWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        this.logoutTransition = DtoHelper.getConvertedDTO(TransitionDTO.class, getInputData().getString("logout_transition"));
        isServiceStarted = true;
        isLogoutNeeded = false;
        sessionTimeout = PATIENT_SESSION_TIMEOUT;
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
            Intent intent = new Intent(getApplicationContext(), PatientWarningSessionActivity.class);
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
                            Intent intent = new Intent(getApplicationContext(), PatientWarningSessionActivity.class);
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
                        isLogoutNeeded = true;
                    }
                }
            };
        }
    }

}

