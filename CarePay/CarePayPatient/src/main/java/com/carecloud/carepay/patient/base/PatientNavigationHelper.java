package com.carecloud.carepay.patient.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.patient.notification.activities.NotificationActivity;
import com.carecloud.carepay.patient.purchases.activities.PurchaseActivity;
import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepay.patient.consentforms.ConsentActivity;
import com.carecloud.carepay.patient.demographics.activities.DemographicsActivity;
import com.carecloud.carepay.patient.demographics.activities.DemographicsSettingsActivity;
import com.carecloud.carepay.patient.demographics.activities.NewReviewDemographicsActivity;
import com.carecloud.carepay.patient.intakeforms.activities.InTakeWebViewActivity;
import com.carecloud.carepay.patient.payment.PaymentActivity;
import com.carecloud.carepay.patient.payment.activities.ViewPaymentBalanceHistoryActivity;
import com.carecloud.carepay.patient.selectlanguage.SelectLanguageActivity;
import com.carecloud.carepay.patient.signinsignuppatient.SigninSignupActivity;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;


/**
 * Created by Jahirul Bhuiyan on 10/10/2016.
 * Will be move to patient application module
 */
public class PatientNavigationHelper {

    private Context context;

    private static boolean accessPaymentsBalances;

    private PatientNavigationHelper() {

    }

    /**
     * Get the customized instance of the helper
     * @param context The context from which the helper has been invoked
     * @return The instance holding the fresh context
     */
    public static PatientNavigationHelper getInstance(Context context) {
        PatientNavigationHelper instance = new PatientNavigationHelper();
        instance.context = context;
        return instance;
    }

    public void navigateToWorkflow(String state) {
        navigateToWorkflow(state, null);
    }

    /**
     * Access pending payments and history payments from menu.
     * We got same state on json to responsibility and pending payments and need to
     * handle it manually
     * */
    public static void setAccessPaymentsBalances(boolean accessPaymentsBalances) {
        PatientNavigationHelper.accessPaymentsBalances = accessPaymentsBalances;
    }

    /**
     * @param workflowDTO workflowdto
     */
        public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        Bundle bundle = new Bundle();
        bundle.putString(PatientNavigationHelper.class.getSimpleName(), workflowDTO.toString());
        navigateToWorkflow(workflowDTO.getState(), bundle);
    }

    /**
     * @param state  state
     * @param bundle bundle
     */
    private void navigateToWorkflow(String state, Bundle bundle) {
        Intent intent;
        switch (state) {
            case PatientNavigationStateConstants.LANGUAGE_SELECTION:
                intent = new Intent(context, SelectLanguageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;

            case PatientNavigationStateConstants.APPOINTMENTS: {
                intent = new Intent(context, AppointmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case PatientNavigationStateConstants.SIGNIN_SIGNUP: {
                intent = new Intent(context, SigninSignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case PatientNavigationStateConstants.DEMOGRAPHICS: {
                intent = new Intent(context, DemographicsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case PatientNavigationStateConstants.DEMOGRAPHIC_VERIFY: {
                intent = new Intent(context, NewReviewDemographicsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case PatientNavigationStateConstants.CONSENT_FORMS: {
                intent = new Intent(context, ConsentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case PatientNavigationStateConstants.INTAKE_FORMS: {
                intent = new Intent(context, InTakeWebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case PatientNavigationStateConstants.PAYMENTS: {
                intent = new Intent(context, accessPaymentsBalances?ViewPaymentBalanceHistoryActivity.class:PaymentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case PatientNavigationStateConstants.PROFILE_UPDATE: {
                intent = new Intent(context, DemographicsSettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case PatientNavigationStateConstants.PURCHASE: {
                intent = new Intent(context, PurchaseActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case PatientNavigationStateConstants.NOTIFICATION: {
                intent = new Intent(context, NotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            default: {
                intent = new Intent(context, AppointmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
        }

        if (bundle != null) {
            intent.putExtra(PatientNavigationHelper.class.getSimpleName(), bundle);
        }
        context.startActivity(intent);
    }
}
