package com.carecloud.carepaylibray.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import static com.carecloud.carepaylibray.base.NavigationStateConstants.APPOINTMENTS;

import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;



/**
 * Created by Jahirul Bhuiyan on 10/10/2016.
 */

public class NavigationHelper {

    private static NavigationHelper instance = null;
    private static Context context;

    private NavigationHelper() {

    }

    /**
     *
     * @param context
     */
    public static void initInstance(Context context) {
        NavigationHelper.context = context;
        if (instance == null) {
            instance = new NavigationHelper();
        }
    }

    public static NavigationHelper instance() {
        return instance;
    }

    public void navigateToWorkflow(String state) {
        navigateToWorkflow(state,null);
    }

    /**
     *
     * @param state
     * @param bundle
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
