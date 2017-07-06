package com.carecloud.carepay.practice.library.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.practice.library.adhocforms.AdHocFormsActivity;
import com.carecloud.carepay.practice.library.appointments.PatientModePracticeAppointmentActivity;
import com.carecloud.carepay.practice.library.appointments.PracticeModePracticeAppointmentsActivity;
import com.carecloud.carepay.practice.library.checkin.PatientModeCheckInCheckOutActivity;
import com.carecloud.carepay.practice.library.checkin.PracticeModePracticeCheckInActivity;
import com.carecloud.carepay.practice.library.checkin.activities.HowToCheckInActivity;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.patientmode.PatientModeSplashActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckoutActivity;
import com.carecloud.carepay.practice.library.payments.PatientModePracticePaymentsActivity;
import com.carecloud.carepay.practice.library.payments.PaymentsActivity;
import com.carecloud.carepay.practice.library.signin.SigninActivity;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.utils.StringUtil;

public class PracticeNavigationHelper {

    /**
     * Navigation using application context
     *
     * @param context       activity context
     * @param workflowDTO   response DTO
     */
    public static void navigateToWorkflow(Context context, WorkflowDTO workflowDTO) {
        navigateToWorkflow(context, workflowDTO, false, 0);
    }

    /**
     * Navigation using application context
     *
     * @param context       activity context
     * @param workflowDTO   response DTO
     * @param info          extra Bundle info
     */
    public static void navigateToWorkflow(Context context, WorkflowDTO workflowDTO, Bundle info) {
        navigateToWorkflow(context, workflowDTO, false, 0, info);
    }

    /**
     * Navigation using application context
     *
     * @param context       activity context
     * @param expectsResult should launch with startActivityForResult
     * @param requestCode   RequestCode for activity Result
     * @param workflowDTO   WorkflowDTO
     */
    public static void navigateToWorkflow(Context context, WorkflowDTO workflowDTO, boolean expectsResult, int requestCode) {
        navigateToWorkflow(context, workflowDTO, expectsResult, requestCode, null);
    }

    /**
     * Navigation using application context
     *
     * @param context       activity context
     * @param expectsResult should launch with startActivityForResult
     * @param requestCode   RequestCode for activity Result
     * @param workflowDTO   WorkflowDTO
     * @param info          extra Bundle info
     */
    public static void navigateToWorkflow(Context context, WorkflowDTO workflowDTO, boolean expectsResult,
                                          int requestCode, Bundle info) {
        Intent intent = null;
        if (workflowDTO == null || StringUtil.isNullOrEmpty(workflowDTO.getState())) {
            return;
        }
        if (info == null) {
            info = new Bundle();
        }
        switch (workflowDTO.getState()) {
            case NavigationStateConstants.PRACTICE_MODE_SIGNIN: {
                intent = new Intent(context, SigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }

            case NavigationStateConstants.PRACTICE_HOME: {
                intent = new Intent(context, CloverMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                break;
            }

            case NavigationStateConstants.PATIENT_HOME: {
                intent = new Intent(context, CloverMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                break;
            }

            case NavigationStateConstants.PRACTICE_ADHOC_FORMS: {
                intent = new Intent(context, AdHocFormsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                break;
            }

            case NavigationStateConstants.PRACTICE_APPOINTMENTS: {
                intent = new Intent(context, PracticeModePracticeAppointmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }

            case NavigationStateConstants.APPOINTMENTS: {
                ApplicationPreferences applicationPreferences = ((ISession) context).getApplicationPreferences();
                @Defs.AppointmentNavigationTypeDef int navigationOption = applicationPreferences.getAppointmentNavigationOption();
                switch (navigationOption){
                    case Defs.NAVIGATE_APPOINTMENT:
                        intent = new Intent(context, PatientModePracticeAppointmentActivity.class);
                        break;
                    case Defs.NAVIGATE_CHECKIN:
                    case Defs.NAVIGATE_CHECKOUT:
                        intent = new Intent(context, PatientModeCheckInCheckOutActivity.class);
                        break;
                    default:
                        //do nothing
                }
                if(intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                break;
            }

            case NavigationStateConstants.PATIENT_MODE_SPLASH: {
                intent = new Intent(context, PatientModeSplashActivity.class);
                break;
            }

            case NavigationStateConstants.PRACTICE_CHECKIN: {
                intent = new Intent(context, PracticeModePracticeCheckInActivity.class);
                break;
            }

            case NavigationStateConstants.PATIENT_MODE_SIGNIN: {
                intent = new Intent(context, HowToCheckInActivity.class);
                break;
            }

            case NavigationStateConstants.DEMOGRAPHIC_VERIFY: {
                intent = new Intent(context, PatientModeCheckinActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case NavigationStateConstants.MEDICATION_ALLERGIES:{
                if (context instanceof PatientModeCheckinActivity) {
                    ((PatientModeCheckinActivity) context).navigateToMedicationsAllergy(workflowDTO);
                    return;
                }else{
                    intent = new Intent(context, PatientModeCheckinActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                break;
            }
            case NavigationStateConstants.CONSENT_FORMS: {
                if (context instanceof PatientModeCheckinActivity) {
                    ((PatientModeCheckinActivity) context).navigateToConsentForms(workflowDTO);
                    return;
                }else{
                    intent = new Intent(context, PatientModeCheckinActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                break;
            }

            case NavigationStateConstants.INTAKE_FORMS: {
                if (context instanceof PatientModeCheckinActivity) {
                    ((PatientModeCheckinActivity) context).navigateToIntakeForms(workflowDTO);
                    return;
                }else{
                    intent = new Intent(context, PatientModeCheckinActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                break;
            }

            case NavigationStateConstants.PAYMENTS: {
                if (context instanceof PatientModeCheckinActivity) {
                    ((PatientModeCheckinActivity) context).getPaymentInformation(workflowDTO.toString());
                    return;
                } else {
                    intent = new Intent(context, PatientModePracticePaymentsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                break;
            }

            case NavigationStateConstants.PRACTICE_PAYMENT: {
                intent = new Intent(context, PaymentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            }

            case NavigationStateConstants.PATIENT_APP_CHECKOUT:
            case NavigationStateConstants.PATIENT_FORM_CHECKOUT:
            case NavigationStateConstants.PATIENT_PAY_CHECKOUT: {
                if(context instanceof PatientModeCheckoutActivity){
                    ((PatientModeCheckoutActivity) context).initDto(workflowDTO);
                    return;
                }

                intent = new Intent(context, PatientModeCheckoutActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }

            default: {
                //do nothing, otherwise the screen will fail if it doesnt have the required DTO
                break;
            }
        }

        if (intent != null) {
            WorkFlowRecord workFlowRecord = new WorkFlowRecord(workflowDTO);
            workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(context));

            Bundle bundle = new Bundle();
            bundle.putLong(WorkflowDTO.class.getSimpleName(), workFlowRecord.save());
            intent.putExtras(bundle);
            intent.putExtra(NavigationStateConstants.EXTRA_INFO, info);

            if (expectsResult && context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, requestCode);
            } else {
                context.startActivity(intent);
            }
        }
    }
}
