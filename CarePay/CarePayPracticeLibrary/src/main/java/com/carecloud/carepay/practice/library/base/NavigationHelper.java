package com.carecloud.carepay.practice.library.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.signin.SigninActivity;
import com.carecloud.carepaylibray.demographics.models.DemographicDTO;

import static com.carecloud.carepay.practice.library.base.NavigationStateConstants.PRACTICE_HOME;

/**
 * Created by Jahirul Bhuiyan on 10/10/2016.
 */

public class NavigationHelper {

    private static NavigationHelper instance = null;
    private static Context context;

    private NavigationHelper() {

    }

    public static void initInstance(Context context) {
        NavigationHelper.context = context;
        if (instance == null) {
            instance = new NavigationHelper();
        }
    }

    public static NavigationHelper Instance() {
        return instance;
    }

    public void navigateToWorkflow(String state) {
        navigateToWorkflow(state,null);
    }

    public void navigateToWorkflow(String state, Bundle bundle) {
        Intent intent=null;
        switch (state) {
            case PRACTICE_HOME: {
                intent = new Intent(context, CloverMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
            default: {
                intent = new Intent(context, SigninActivity.class);
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
