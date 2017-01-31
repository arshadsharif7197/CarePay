package com.carecloud.carepay.patient.appointments.utils;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by sudhir_pingale on 9/22/2016.
 */

public class CustomPopupNotification extends PopupWindow {

    public static final int TYPE_TIMED_NOTIFICATION = 1;
    public static final int TYPE_ALERT_NOTIFICATION = 2;
    public static final int TYPE_ERROR_NOTIFICATION = 3;
    private View parentView;
    private CustomPopupNotification customPopupNotificationInstance;

    /**
     * @param context the context to inflate custom popup layout
     * @param parentView a parent view to get the {@link android.view.View#getWindowToken()} token from
     * @param positiveButtonCaption Sets the string value of the positive action button
     * @param negativeButtonCaption Sets the string value of the positive action button
     * @param popupMessageText Sets the string value of the TextView popup message
     * @param positiveAction callback to be invoked when positive action button is clicked
     * @param negativeAction callback to be invoked when negative action button is clicked
     */
    public CustomPopupNotification(Context context, View parentView, String positiveButtonCaption,
                                   String negativeButtonCaption, String popupMessageText,
                                   View.OnClickListener positiveAction, View.OnClickListener negativeAction) {

        super(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.custom_popup_with_action, null),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customPopupNotificationInstance = this;
        this.parentView = parentView;

        View popupWindowLayout = this.getContentView();
        Button popupDismissButton = (Button) popupWindowLayout.findViewById(R.id.popup_dismiss_button);
        Button popupCheckInButton = (Button) popupWindowLayout.findViewById(R.id.popup_checkin_button);
        TextView popupMessageLabel = (TextView) popupWindowLayout.findViewById(R.id.popup_message_tv);
        popupDismissButton.setText(negativeButtonCaption);
        popupCheckInButton.setText(positiveButtonCaption);
        popupMessageLabel.setText(popupMessageText);

        SystemUtil.setTypefaceFromAssets(context, "fonts/proximanova_regular.otf", popupMessageLabel);
        SystemUtil.setTypefaceFromAssets(context, "fonts/gotham_rounded_medium.otf", popupDismissButton);
        SystemUtil.setTypefaceFromAssets(context, "fonts/gotham_rounded_medium.otf", popupCheckInButton);

        popupDismissButton.setOnClickListener(negativeAction);
        popupCheckInButton.setOnClickListener(positiveAction);
    }

    /**
     * @param context the context to inflate custom popup layout
     * @param parentView a parent view to get the {@link android.view.View#getWindowToken()} token from
     * @param popupMessageText Sets the string value of the TextView popup message
     * @param notificationType The notification type to be displayed from CustomPopupNotification class
     */
    public CustomPopupNotification(Context context, View parentView, String popupMessageText, int notificationType) {

        super(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.custom_popup, null),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        customPopupNotificationInstance = this;
        this.parentView = parentView;

        setOutsideTouchable(true);
        View popupWindowLayout = this.getContentView();
        ImageView popupIcon = (ImageView) popupWindowLayout.findViewById(R.id.popup_icon);
        TextView popupMessageLabel = (TextView) popupWindowLayout.findViewById(R.id.popup_message_tv);

        switch (notificationType) {
            case TYPE_ALERT_NOTIFICATION:
                popupWindowLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.yellowGreen));
                popupMessageLabel.setTextColor(ContextCompat.getColor(context, R.color.white));
                popupIcon.setImageResource(R.drawable.icn_notification_alert);
                break;
            case TYPE_TIMED_NOTIFICATION:
                popupWindowLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.charcoal));
                popupMessageLabel.setTextColor(ContextCompat.getColor(context, R.color.glitter));
                popupIcon.setImageResource(R.drawable.icn_notification_basic_green);
                break;
            case TYPE_ERROR_NOTIFICATION:
                popupWindowLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.cardinal));
                popupMessageLabel.setTextColor(ContextCompat.getColor(context, R.color.white));
                popupIcon.setImageResource(R.drawable.icn_notification_error);
                break;
            default:
        }

        popupMessageLabel.setText(popupMessageText);

        SystemUtil.setTypefaceFromAssets(context, "fonts/proximanova_regular.otf", popupMessageLabel);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (customPopupNotificationInstance.isShowing()) {
                    customPopupNotificationInstance.dismiss();
                }
            }

        }, CarePayConstants.CUSTOM_POPUP_AUTO_DISMISS_DURATION);
    }

    public void showPopWindow() {
        showAtLocation(parentView, Gravity.TOP, 0, 0);
    }
}