package com.carecloud.carepay.practice.library.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.practice.library.appointments.AppointmentsActivity;
import com.carecloud.carepay.practice.library.appointments.PracticeAppointmentsActivity;
import com.carecloud.carepay.practice.library.appointments.ScheduleAppointmentActivity;
import com.carecloud.carepay.practice.library.checkin.CheckInActivity;
import com.carecloud.carepay.practice.library.checkin.activities.HowToCheckInActivity;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.patientmode.PatientModeSplashActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
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

    /**
     * Navigation using application context
     *
     *  @param context    activity context
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
                intent = new Intent(context, PracticeAppointmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }

            case PracticeNavigationStateConstants.PATIENT_APPOINTMENTS: {
                ApplicationPreferences applicationPreferences = ((ISession) context).getApplicationPreferences();
                intent = new Intent(context,  applicationPreferences.isNavigatingToAppointments()
                                ? ScheduleAppointmentActivity.class : AppointmentsActivity.class);
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
                if (context instanceof PatientModeCheckinActivity) {
                    ((PatientModeCheckinActivity) context).startIntakeForms(workflowDTO.toString());
                    return;
                }
                break;
            }

            case PracticeNavigationStateConstants.PAYMENTS: {
                if (context instanceof PatientModeCheckinActivity) {
                    ((PatientModeCheckinActivity) context).getPaymentInformation(workflowDTO.toString());
                    return;
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
            context.startActivity(intent);
        }
    }
}
