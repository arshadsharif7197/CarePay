package com.carecloud.carepay.patient.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepay.patient.demographics.activities.DemographicReviewActivity;
import com.carecloud.carepay.patient.selectlanguage.SelectLanguageActivity;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.google.gson.Gson;


/**
 * Created by Jahirul Bhuiyan on 10/10/2016.
 * Will be move to patient application module
 */
public class PatientNavigationHelper {

    private static PatientNavigationHelper instance;
    private static Context context;

    private PatientNavigationHelper() {

    }

    /**
     * @param context context
     */
    public static void initInstance(Context context) {
        PatientNavigationHelper.context = context;
        if (instance == null) {
            instance = new PatientNavigationHelper();
        }
    }

    public static PatientNavigationHelper instance() {
        return instance;
    }

    public void navigateToWorkflow(String state) {
        navigateToWorkflow(state, null);
    }

    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        Bundle bundle = new Bundle();
        bundle.putString(PatientNavigationHelper.class.getSimpleName(), workflowDTO.toString());
        navigateToWorkflow(workflowDTO.getState(), bundle);
    }

    /**
     * @param state  state
     * @param bundle bundle
     */
    public void navigateToWorkflow(String state, Bundle bundle) {
        Intent intent = null;
        switch (state) {
            case PatientNavigationStateConstants.LANGUAGE_SELECTION:
                intent = new Intent(context, SelectLanguageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;

            case PatientNavigationStateConstants.APPOINTMENTS: {
                intent = new Intent(context, DemographicReviewActivity.class);
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
