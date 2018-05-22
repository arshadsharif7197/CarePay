package com.carecloud.carepay.patient.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepay.patient.checkout.AppointmentCheckoutActivity;
import com.carecloud.carepay.patient.consentforms.ConsentFormsActivity;
import com.carecloud.carepay.patient.demographics.activities.DemographicsSettingsActivity;
import com.carecloud.carepay.patient.demographics.activities.NewDemographicsActivity;
import com.carecloud.carepay.patient.demographics.activities.ReviewDemographicsActivity;
import com.carecloud.carepay.patient.messages.activities.MessagesActivity;
import com.carecloud.carepay.patient.myhealth.MyHealthActivity;
import com.carecloud.carepay.patient.notifications.activities.NotificationActivity;
import com.carecloud.carepay.patient.payment.activities.PaymentActivity;
import com.carecloud.carepay.patient.payment.activities.ViewPaymentBalanceHistoryActivity;
import com.carecloud.carepay.patient.retail.activities.RetailActivity;
import com.carecloud.carepay.patient.selectlanguage.SelectLanguageActivity;
import com.carecloud.carepay.patient.signinsignuppatient.SigninSignupActivity;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.utils.StringUtil;

public class PatientNavigationHelper {

    private static boolean accessPaymentsBalances = false;

    /**
     * Access pending payments and history payments from menu.
     * We got same state on json to responsibility and pending payments and need to
     * handle it manually
     */
    public static void setAccessPaymentsBalances(boolean accessPaymentsBalances) {
        PatientNavigationHelper.accessPaymentsBalances = accessPaymentsBalances;
    }

    /**
     * Navigation using application context
     *
     * @param context     activity context
     * @param workflowDTO response DTO
     */
    public static void navigateToWorkflow(Context context, WorkflowDTO workflowDTO) {
        navigateToWorkflow(context, workflowDTO, false, 0);
    }

    public static void navigateToWorkflow(Context context, WorkflowDTO workflowDTO, Bundle info) {
        navigateToWorkflow(context, workflowDTO, false, 0, info);
    }

    public static void navigateToWorkflow(Context context, WorkflowDTO workflowDTO,
                                          boolean expectsResult, int requestCode) {
        navigateToWorkflow(context, workflowDTO, expectsResult, requestCode, null);
    }

    /**
     * Navigation using application context
     *
     * @param context       activity context
     * @param expectsResult should launch with startActivityForResult
     * @param requestCode   RequestCode for activity Result
     * @param workflowDTO   WorkflowDTO
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
            case NavigationStateConstants.LANGUAGE_SELECTION:
                intent = new Intent(context, SelectLanguageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;

            case NavigationStateConstants.APPOINTMENTS: {
                intent = new Intent(context, AppointmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case NavigationStateConstants.PATIENT_APP_SIGNIN: {
                intent = new Intent(context, SigninSignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case NavigationStateConstants.DEMOGRAPHICS: {
                intent = new Intent(context, NewDemographicsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case NavigationStateConstants.DEMOGRAPHIC_VERIFY: {
                intent = new Intent(context, ReviewDemographicsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case NavigationStateConstants.CONSENT_FORMS: {
                if (context instanceof ReviewDemographicsActivity) {
                    ((ReviewDemographicsActivity) context).navigateToConsentForms(workflowDTO);
                    return;
                } else {
                    intent = new Intent(context, ReviewDemographicsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                break;
            }
            case NavigationStateConstants.INTAKE_FORMS: {
                if (context instanceof ReviewDemographicsActivity) {
                    ((ReviewDemographicsActivity) context).navigateToIntakeForms(workflowDTO);
                    return;
                } else {
                    intent = new Intent(context, ReviewDemographicsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                break;
            }
            case NavigationStateConstants.PAYMENTS: {
                if (context instanceof ReviewDemographicsActivity) {
                    ((ReviewDemographicsActivity) context).getPaymentInformation(workflowDTO.toString());
                    return;
                }
                intent = new Intent(context, accessPaymentsBalances ? ViewPaymentBalanceHistoryActivity.class
                        : PaymentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case NavigationStateConstants.PROFILE_UPDATE: {
                intent = new Intent(context, DemographicsSettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case NavigationStateConstants.PURCHASE: {
                intent = new Intent(context, RetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case NavigationStateConstants.NOTIFICATION: {
                intent = new Intent(context, NotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case NavigationStateConstants.MEDICATION_ALLERGIES: {
                if (context instanceof ReviewDemographicsActivity) {
                    ((ReviewDemographicsActivity) context).navigateToMedicationsAllergy(workflowDTO);
                    return;
                } else {
                    intent = new Intent(context, ReviewDemographicsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
                break;
            }
            case NavigationStateConstants.PATIENT_APP_CHECKOUT:
            case NavigationStateConstants.PATIENT_FORM_CHECKOUT:
            case NavigationStateConstants.PATIENT_PAY_CHECKOUT: {
                if (context instanceof AppointmentCheckoutActivity) {
                    ((AppointmentCheckoutActivity) context).initDto(workflowDTO);
                    return;
                }

                intent = new Intent(context, AppointmentCheckoutActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                info.putString("state", workflowDTO.getState());
                break;
            }
            case NavigationStateConstants.FORMS_HISTORY: {
                intent = new Intent(context, ConsentFormsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case NavigationStateConstants.MESSAGES:
                intent = new Intent(context, MessagesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                return;

            case NavigationStateConstants.PATIENT_MY_HEALTH:
                intent = new Intent(context, MyHealthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;

            default: {
                intent = new Intent(context, AppointmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
        }

        WorkFlowRecord workFlowRecord = new WorkFlowRecord(workflowDTO);
        workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(context));

        Bundle bundle = new Bundle();
        bundle.putLong(WorkflowDTO.class.getName(), workFlowRecord.save());
        intent.putExtras(bundle);
        intent.putExtra(NavigationStateConstants.EXTRA_INFO, info);

        if (expectsResult && context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }
}
