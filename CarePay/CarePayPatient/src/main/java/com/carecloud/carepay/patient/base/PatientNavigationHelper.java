package com.carecloud.carepay.patient.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepay.patient.consentforms.ConsentActivity;
import com.carecloud.carepay.patient.demographics.activities.DemographicReviewActivity;
import com.carecloud.carepay.patient.demographics.activities.DemographicsActivity;
import com.carecloud.carepay.patient.selectlanguage.SelectLanguageActivity;
import com.carecloud.carepay.patient.signinsignuppatient.SigninSignupActivity;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;


/**
 * Created by Jahirul Bhuiyan on 10/10/2016.
 * Will be move to patient application module
 */
public class PatientNavigationHelper {

    private Context context;

    private PatientNavigationHelper() {

    }

    public static PatientNavigationHelper getInstance(Context context) {
        PatientNavigationHelper instance = new PatientNavigationHelper();
        instance.context = context;
        return instance;
    }

    public void navigateToWorkflow(String state) {
        navigateToWorkflow(state, null);
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
                intent = new Intent(context, DemographicReviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            case PatientNavigationStateConstants.CONSENT_FORMS: {
                intent = new Intent(context, ConsentActivity.class);
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
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).finish();
        }
    }
}
