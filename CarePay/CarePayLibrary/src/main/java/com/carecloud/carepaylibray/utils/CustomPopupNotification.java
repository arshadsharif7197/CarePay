package com.carecloud.carepaylibray.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;

/**
 * Created by sudhir_pingale on 9/22/2016.
 */
public class CustomPopupNotification extends PopupWindow {

    public static final int TYPE_TIMED_NOTIFICATION = 1;
    public static final int TYPE_ALERT_NOTIFICATION = 2;
    public static final int TYPE_ERROR_NOTIFICATION = 3;
    private static final int AUTO_DISSMISS_SUCCESS_NOTIFICATION = 4;

    public static final String GESTURE_DIRECTION_LEFT = "Left";
    public static final String GESTURE_DIRECTION_RIGHT = "Right";
    public static final String GESTURE_DIRECTION_TOP = "Top";
    public static final String GESTURE_DIRECTION_BOTTOM = "Bottom";

    private int statusBarColor;
    private int errorColor;
    private int notificationType;
    private View parentView;
    public Context context ;
    public Window window ;
    private CustomPopupNotificationListener callback;

    public interface CustomPopupNotificationListener {
        void onSwipe(String swipeDirection);
    }

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
        this.parentView = parentView;

        View popupWindowLayout = this.getContentView();
        Button popupDismissButton = (Button) popupWindowLayout.findViewById(R.id.popup_dismiss_button);
        Button popupCheckInButton = (Button) popupWindowLayout.findViewById(R.id.popup_checkin_button);
        TextView popupMessageLabel = (TextView) popupWindowLayout.findViewById(R.id.popup_message_tv);
        popupDismissButton.setText(negativeButtonCaption);
        popupCheckInButton.setText(positiveButtonCaption);
        if (Build.VERSION.SDK_INT >= 24)
        {
            popupMessageLabel.setText(Html.fromHtml(popupMessageText, Html.FROM_HTML_MODE_LEGACY));
        }
        else
        {
            popupMessageLabel.setText(Html.fromHtml(popupMessageText));

        }


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
    public CustomPopupNotification(Context context, View parentView, Window window, String popupMessageText, int notificationType, CustomPopupNotificationListener callback) {

        super(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.custom_popup, null),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.parentView = parentView;
        this.callback = callback;
        this.window = window;
        this.statusBarColor =  ContextCompat.getColor(context, R.color.colorPrimaryDark) ;
        this.errorColor = ContextCompat.getColor(context, R.color.remove_red);
        this.notificationType = notificationType;

        setOutsideTouchable(true);
        setSwipeListener();
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
                popupWindowLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.remove_red));
                popupMessageLabel.setTextColor(ContextCompat.getColor(context, R.color.white));
                popupIcon.setImageResource(R.drawable.icn_notification_error);
                break;
            case AUTO_DISSMISS_SUCCESS_NOTIFICATION:
                popupWindowLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.emerald));
                popupMessageLabel.setTextColor(ContextCompat.getColor(context, R.color.white));
                popupIcon.setImageResource(R.drawable.icn_notification_basic_white_check);
                break;
            default:
        }

        popupMessageLabel.setText(popupMessageText);

        SystemUtil.setTypefaceFromAssets(context, "fonts/proximanova_regular.otf", popupMessageLabel);
    }

    /**
     * Show pop window.
     */
    public void showPopWindow() {
        showAtLocation(parentView, Gravity.TOP, 0, 0);

        if(notificationType == TYPE_ERROR_NOTIFICATION)
        {
            setStatusBarColor(errorColor);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (isShowing()) {
                    dismiss();
                    setStatusBarColor(statusBarColor);
                }
            }

        }, CarePayConstants.CUSTOM_POPUP_AUTO_DISMISS_DURATION);
    }

    private void setSwipeListener()
    {
        getContentView().setOnTouchListener(new SwipeGuestureListener(context) {

            public void onSwipeTop() {
                callback.onSwipe(GESTURE_DIRECTION_TOP);
            }

            public void onSwipeRight() {
                callback.onSwipe(GESTURE_DIRECTION_RIGHT);
                    setStatusBarColor(statusBarColor);

            }

            public void onSwipeLeft() {
                callback.onSwipe(GESTURE_DIRECTION_LEFT);
                    setStatusBarColor(statusBarColor);

            }

            public void onSwipeBottom() {
                callback.onSwipe(GESTURE_DIRECTION_BOTTOM);
            }

        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(int color){
        if(window!= null)
        {
            window.setStatusBarColor(color);
        }
    }
}
