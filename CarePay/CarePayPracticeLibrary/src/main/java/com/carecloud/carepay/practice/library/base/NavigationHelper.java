package com.carecloud.carepay.practice.library.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.practice.library.appointments.AppointmentsActivity;
import com.carecloud.carepay.practice.library.checkin.CheckInActivity;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.patientmode.PatientModeSplashActivity;
import com.carecloud.carepay.practice.library.signin.SigninActivity;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;


/**
 * Created by Jahirul Bhuiyan on 10/10/2016.
 * Dynamic screen navigation helper
 */

public class NavigationHelper {

    private static NavigationHelper instance = null;
    private static Context context;

    private NavigationHelper() {

    }

    public static void initInstance(Context context) {
        NavigationHelper.context = context;
        if (instance == null) {
            instance = new NavigationHelper();
        }
    }

    public static NavigationHelper getInstance() {
        return instance;
    }

    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        Intent intent = null;
        if (workflowDTO == null) {
            return;
        }
        switch (workflowDTO.getState()) {
            case NavigationStateConstants.PRACTICE_MODE_SIGNIN: {
                intent = new Intent(context, SigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }
            case NavigationStateConstants.PRACTICE_HOME: {
                intent = new Intent(context, CloverMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }
            case NavigationStateConstants.PRACTICE_APPOINTMENTS: {
                intent = new Intent(context, AppointmentsActivity.class);
                break;
            }
            case NavigationStateConstants.PATIENT_MODE_SPLASH: {
                intent = new Intent(context, PatientModeSplashActivity.class);
                break;
            }
            case NavigationStateConstants.PRACTICE_CHECKIN: {
                intent = new Intent(context, CheckInActivity.class);
                break;
            }
            default: {
                intent = new Intent(context, SigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;

            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putSerializable(context.getClass().getSimpleName(), workflowDTO.toString());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
