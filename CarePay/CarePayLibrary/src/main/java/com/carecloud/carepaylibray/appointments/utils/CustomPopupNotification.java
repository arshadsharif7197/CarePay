package com.carecloud.carepaylibray.appointments.utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by sudhir_pingale on 9/22/2016.
 */

public class CustomPopupNotification extends PopupWindow {

    public static final int TYPE_TIMED_NOTIFICATION = 1;
    public static final int TYPE_ALERT_NOTIFICATION = 2;
    public static final int TYPE_ERROR_NOTIFICATION = 3;
    private View mParentView;
    private CustomPopupNotification mCustomPopupNotification;

    /**
     * @param context
     * @param parentView
     * @param positiveButtonCaption
     * @param negativeButtonCaption
     * @param popupMessageText
     * @param positiveAction
     * @param negativeAction
     */
    public CustomPopupNotification(Context context, View parentView, String positiveButtonCaption,
                                   String negativeButtonCaption, String popupMessageText,
                                   View.OnClickListener positiveAction, View.OnClickListener negativeAction) {

        super(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_popup_with_action, null),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mCustomPopupNotification = this;
        this.mParentView = parentView;

        if (Build.VERSION.SDK_INT >= 21) {
            this.setElevation(5.0f);
        }

        View view = this.getContentView();
        Button popup_dismiss_button = (Button) view.findViewById(R.id.popup_dismiss_button);
        Button popup_checkin_button = (Button) view.findViewById(R.id.popup_checkin_button);
        TextView popup_message_tv = (TextView) view.findViewById(R.id.popup_message_tv);
        popup_dismiss_button.setText(negativeButtonCaption);
        popup_checkin_button.setText(positiveButtonCaption);
        popup_message_tv.setText(popupMessageText);

        SystemUtil.setTypefaceFromAssets(context, "fonts/proximanova_regular.otf", popup_message_tv);
        SystemUtil.setTypefaceFromAssets(context, "fonts/gotham_rounded_medium.otf", popup_dismiss_button);
        SystemUtil.setTypefaceFromAssets(context, "fonts/gotham_rounded_medium.otf", popup_checkin_button);

        popup_dismiss_button.setOnClickListener(negativeAction);
        popup_checkin_button.setOnClickListener(positiveAction);
    }

    /**
     * @param context
     * @param parentView
     * @param popupMessageText
     * @param notificationType The notification type to be displayed from CustomPopupNotification class
     */
    public CustomPopupNotification(Context context, View parentView, String popupMessageText, int notificationType) {

        super(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.custom_popup, null),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mCustomPopupNotification = this;
        this.mParentView = parentView;

        if (Build.VERSION.SDK_INT >= 21) {
            this.setElevation(5.0f);
        }
        setOutsideTouchable(true);
        View view = this.getContentView();
        ImageView popup_iv = (ImageView) view.findViewById(R.id.popup_icon) ;
        TextView popup_message_tv = (TextView) view.findViewById(R.id.popup_message_tv);

        switch (notificationType){
            case TYPE_ALERT_NOTIFICATION:
                if (Build.VERSION.SDK_INT >= 23) {
                    view.setBackgroundColor(context.getColor(R.color.yellowGreen));
                    popup_message_tv.setTextColor(context.getColor(R.color.white));
                }else{
                    view.setBackgroundColor(context.getResources().getColor(R.color.yellowGreen));
                    popup_message_tv.setTextColor(context.getResources().getColor(R.color.white));
                }
                popup_iv.setImageResource(R.drawable.icn_notification_alert);
                break;
            case TYPE_TIMED_NOTIFICATION:
                if (Build.VERSION.SDK_INT >= 23) {
                    view.setBackgroundColor(context.getColor(R.color.charcoal));
                    popup_message_tv.setTextColor(context.getColor(R.color.glitter));
                }else{
                    view.setBackgroundColor(context.getResources().getColor(R.color.charcoal));
                    popup_message_tv.setTextColor(context.getResources().getColor(R.color.glitter));
                }
                popup_iv.setImageResource(R.drawable.icn_notification_basic_green);
                break;
            case TYPE_ERROR_NOTIFICATION:
                if (Build.VERSION.SDK_INT >= 23) {
                    view.setBackgroundColor(context.getColor(R.color.cardinal));
                    popup_message_tv.setTextColor(context.getColor(R.color.white));
                }else{
                    view.setBackgroundColor(context.getResources().getColor(R.color.cardinal));
                    popup_message_tv.setTextColor(context.getResources().getColor(R.color.white));
                }
                popup_iv.setImageResource(R.drawable.icn_notification_error);
                break;
            default:
        }

        popup_message_tv.setText(popupMessageText);

        SystemUtil.setTypefaceFromAssets(context, "fonts/proximanova_regular.otf", popup_message_tv);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mCustomPopupNotification.isShowing()) {
                    mCustomPopupNotification.dismiss();
                }
            }

        }, CarePayConstants.CUSTOM_POPUP_AUTO_DISMISS_DURATION);
    }

    public void showPopWindow() {
        showAtLocation(mParentView, Gravity.TOP, 0, 0);
    }
}
