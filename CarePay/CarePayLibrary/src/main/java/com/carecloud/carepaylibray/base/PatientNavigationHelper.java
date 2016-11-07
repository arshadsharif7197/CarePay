package com.carecloud.carepaylibray.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;

import static com.carecloud.carepaylibray.base.PatientNavigationStateConstants.APPOINTMENTS;

import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;



/**
 * Created by Jahirul Bhuiyan on 10/10/2016.
 * Will be move to patient application module
 */
@Deprecated
public class PatientNavigationHelper {

    private static PatientNavigationHelper instance;
    private static Context context;

    private PatientNavigationHelper() {

    }

    /**
     *
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
        navigateToWorkflow(state,null);
    }

    /**
     *
     * @param state state
     * @param bundle bundle
     */
    public void navigateToWorkflow(String state, Bundle bundle) {
        Intent intent=null;
        switch (state) {
            case APPOINTMENTS: {
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
        if(bundle!=null){
            intent.putExtra(state, bundle);
        }
        context.startActivity(intent);
    }

}
