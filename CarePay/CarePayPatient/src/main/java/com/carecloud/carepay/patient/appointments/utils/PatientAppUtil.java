package com.carecloud.carepay.patient.appointments.utils;

import android.content.Context;
import android.view.View;
import android.view.Window;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.customcomponents.SuccessMessageToast;
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

        new CustomPopupNotification(context, parentView, null, successMessage, 4, null)
                .showPopWindow();

    }

    public static void showSuccessToast(Context context) {

        showSuccessToast(context, null);
    }

    /**
     * Show success toast.
     *
     * @param context        the context
     * @param successMessage the success message
     */
    public static void showSuccessToast(Context context, String successMessage) {

        if (null == context) {
            return;
        }

        new SuccessMessageToast(context, successMessage)
                .show();

    }


}
