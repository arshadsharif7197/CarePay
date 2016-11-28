package com.carecloud.carepay.practice.library.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.carecloud.carepay.practice.library.appointments.AppointmentsActivity;
import com.carecloud.carepay.practice.library.checkin.CheckInActivity;
import com.carecloud.carepay.practice.library.checkin.activities.HowToCheckInActivity;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.patientmode.PatientModeSplashActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.intakeforms.activities.InTakeWebViewActivity;
import com.carecloud.carepay.practice.library.signin.SigninActivity;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.utils.StringUtil;


/**
 * Created by Jahirul Bhuiyan on 10/10/2016.
 * Dynamic screen navigation helper
 * Singleton
 * initialize from application class
 */

public class PracticeNavigationHelper {

    private static PracticeNavigationHelper instance;
    private static Context context;

    private PracticeNavigationHelper() {

    }

    /**
     * PracticeNavigationHelper singleton initialization from Application class
     *
     * @param context application context
     */
    public static void initInstance(Context context) {
        PracticeNavigationHelper.context = context;
        if (instance == null) {
            instance = new PracticeNavigationHelper();
        }
    }

    public static PracticeNavigationHelper getInstance() {
        return instance;
    }

    /**
     * Navigation using activity context
     *
     * @param context     activity context
     * @param workflowDTO WorkflowDTO
     */
    public void navigateToWorkflow(Context context, WorkflowDTO workflowDTO) {
        Intent intent = null;
        if (workflowDTO == null || StringUtil.isNullOrEmpty(workflowDTO.getState())) {
            return;
        }
        switch (workflowDTO.getState()) {
            case PracticeNavigationStateConstants.PRACTICE_MODE_SIGNIN: {
                intent = new Intent(context, SigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }

            case PracticeNavigationStateConstants.PRACTICE_HOME: {
                intent = new Intent(context, CloverMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }
            case PracticeNavigationStateConstants.PATIENT_HOME: {
                intent = new Intent(context, CloverMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }
            case PracticeNavigationStateConstants.PRACTICE_APPOINTMENTS: {
                intent = new Intent(context, AppointmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }
            case PracticeNavigationStateConstants.PATIENT_MODE_SPLASH: {
                intent = new Intent(context, PatientModeSplashActivity.class);
                break;
            }
            case PracticeNavigationStateConstants.PRACTICE_CHECKIN: {
                intent = new Intent(context, CheckInActivity.class);
                break;
            }
            case PracticeNavigationStateConstants.PATIENT_MODE_SIGNIN: {
                intent = new Intent(context, HowToCheckInActivity.class);
                break;
            }

            case PracticeNavigationStateConstants.PATIENT_MODE_CHECKIN_SUBFLOW: {
                intent = new Intent(context, PatientModeCheckinActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }
            case PracticeNavigationStateConstants.CONSENT_FORMS: {
                if (context instanceof PatientModeCheckinActivity) {
                    ((PatientModeCheckinActivity) context).getConsentFormInformation(workflowDTO.toString());
                    return;
                }
                break;
            }
            case PracticeNavigationStateConstants.INTAKE_FORMS: {
                Log.d("FormIntake","In Intake form");
                intent = new Intent(context, InTakeWebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

              /*  if (context instanceof PatientModeCheckinActivity) {
                    ((PatientModeCheckinActivity) context).getIntakeFormInformation(workflowDTO.toString());
                    return;
                }*/
                break;
            }
            default: {
                intent = new Intent(context, SigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;

            }
        }
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PracticeNavigationHelper.context.getClass().getSimpleName(), workflowDTO.toString());
        if(bundle!=null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    /**
     * Navigation using application context
     *
     * @param workflowDTO WorkflowDTO
     */
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        navigateToWorkflow(PracticeNavigationHelper.context, workflowDTO);
    }
}
