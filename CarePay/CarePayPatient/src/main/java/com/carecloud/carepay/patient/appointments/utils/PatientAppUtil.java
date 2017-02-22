package com.carecloud.carepay.patient.appointments.utils;

import android.content.Context;
import android.view.View;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.utils.CustomPopupNotification;

/**
 * Created by kkannan on 2/10/17.
 */

public class PatientAppUtil {

    public static void showSuccessNotification(Context context, View parentView) {

        showSuccessNotification(context, parentView, context.getString(R.string.success));
    }

    public static void showSuccessNotification(Context context, View parentView, String successMessage) {

        if (null == context || parentView == null || (context instanceof BaseActivity &&
                !((BaseActivity) context).isVisible())) {
            return;
        }

        new CustomPopupNotification(context, parentView, successMessage, 4)
                .showPopWindow();

    }

}
