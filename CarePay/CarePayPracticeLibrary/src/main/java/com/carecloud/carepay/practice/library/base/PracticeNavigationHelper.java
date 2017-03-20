package com.carecloud.carepay.practice.library.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.practice.library.checkin.PatientModePracticeCheckInActivity;
import com.carecloud.carepay.practice.library.appointments.PracticeModePracticeAppointmentsActivity;
import com.carecloud.carepay.practice.library.appointments.PatientModePracticeAppointmentActivity;
import com.carecloud.carepay.practice.library.checkin.PracticeModePracticeCheckInActivity;
import com.carecloud.carepay.practice.library.checkin.activities.HowToCheckInActivity;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.patientmode.PatientModeSplashActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.payments.PatientModePracticePaymentsActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinPreregisterActivity;
import com.carecloud.carepay.practice.library.payments.PaymentsActivity;
import com.carecloud.carepay.practice.library.signin.SigninActivity;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.utils.StringUtil;


/**
 * Created by Jahirul Bhuiyan on 10/10/2016.
 * Dynamic screen navigation helper
 * Singleton
 * initialize from application class
 */

public class PracticeNavigationHelper {

    private static boolean shouldExpectResult = false;
    private static int expectRequestCode = -1;

    /**
     * Navigation using application context
     *
     * @param context       activity context
     * @param workflowDTO   response DTO
     * @param expectsResult should launch with startActivityForResult
     * @param requestCode   RequestCode for activity Result
     */
    public static void navigateToWorkflow(Context context, WorkflowDTO workflowDTO, boolean expectsResult, int requestCode) {
        shouldExpectResult = expectsResult;
        expectRequestCode = requestCode;
        navigateToWorkflow(context, workflowDTO);
    }

    /**
     * Navigation using application context
     *
     * @param context     activity context
     * @param workflowDTO WorkflowDTO
     */
    public static void navigateToWorkflow(Context context, WorkflowDTO workflowDTO) {
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                break;
            }

            case PracticeNavigationStateConstants.PATIENT_HOME: {
                intent = new Intent(context, CloverMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                break;
            }

            case PracticeNavigationStateConstants.PRACTICE_APPOINTMENTS: {
                intent = new Intent(context, PracticeModePracticeAppointmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }

            case PracticeNavigationStateConstants.PATIENT_APPOINTMENTS: {
                ApplicationPreferences applicationPreferences = ((ISession) context).getApplicationPreferences();
                intent = new Intent(context,  applicationPreferences.isNavigatingToAppointments()
                                ? PatientModePracticeAppointmentActivity.class : PatientModePracticeCheckInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }

            case PracticeNavigationStateConstants.PATIENT_MODE_SPLASH: {
                intent = new Intent(context, PatientModeSplashActivity.class);
                break;
            }

            case PracticeNavigationStateConstants.PRACTICE_CHECKIN: {
                intent = new Intent(context, PracticeModePracticeCheckInActivity.class);
                break;
            }

            case PracticeNavigationStateConstants.PATIENT_MODE_SIGNIN: {
                intent = new Intent(context, HowToCheckInActivity.class);
                break;
            }

            case PracticeNavigationStateConstants.PATIENT_MODE_CHECKIN_SUBFLOW: {
                intent = new Intent(context, PatientModeCheckinPreregisterActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }
            case PracticeNavigationStateConstants.MEDICATION_ALLERGIES:{
                if (context instanceof PatientModeCheckinPreregisterActivity) {
                    ((PatientModeCheckinPreregisterActivity) context).loadMedicationsAllergy(workflowDTO.toString());
                    return;
                }
                break;
            }
            case PracticeNavigationStateConstants.CONSENT_FORMS: {
                if (context instanceof PatientModeCheckinPreregisterActivity) {
                    ((PatientModeCheckinPreregisterActivity) context).getConsentFormInformation(workflowDTO.toString());
                    return;
                }
                break;
            }

            case PracticeNavigationStateConstants.INTAKE_FORMS: {
                if (context instanceof PatientModeCheckinPreregisterActivity) {
                    ((PatientModeCheckinPreregisterActivity) context).startIntakeForms(workflowDTO.toString());
                    return;
                }
                break;
            }

            case PracticeNavigationStateConstants.PAYMENTS: {
                if (context instanceof PatientModeCheckinPreregisterActivity) {
                    ((PatientModeCheckinPreregisterActivity) context).getPaymentInformation(workflowDTO.toString());
                    return;
                } else {
                    intent = new Intent(context, PatientModePracticePaymentsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                break;
            }

            case PracticeNavigationStateConstants.PRACTICE_PAYMENT: {
                intent = new Intent(context, PaymentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        bundle.putSerializable(WorkflowDTO.class.getSimpleName(), workflowDTO.toString());
        if (intent != null) {
            intent.putExtras(bundle);
            if (shouldExpectResult && context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, expectRequestCode);
                shouldExpectResult = false;
                expectRequestCode = -1;
            } else {
                context.startActivity(intent);
            }
        }
    }
}
