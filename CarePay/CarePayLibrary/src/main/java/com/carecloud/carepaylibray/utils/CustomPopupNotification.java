package com.carecloud.carepaylibray.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.IApplicationSession;
import com.carecloud.carepaylibray.qrcodescanner.DisplayUtils;

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
    public Context context;
    public Window window;
    private CustomPopupNotificationListener callback;
    private Boolean hasStatusBar = true;

    public interface CustomPopupNotificationListener {
        void onSwipe(String swipeDirection);
    }

    /**
     * @param context          the context to inflate custom popup layout
     * @param parentView       a parent view to get the {@link android.view.View#getWindowToken()} token from
     * @param popupMessageText Sets the string value of the TextView popup message
     * @param notificationType The notification type to be displayed from CustomPopupNotification class
     */
    public CustomPopupNotification(Context context, View parentView, Window window, String popupMessageText, int notificationType, CustomPopupNotificationListener callback) {

        super(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_popup, null));
        this.context = context;
        this.parentView = parentView;
        this.callback = callback;
        this.window = window;
        this.statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        this.errorColor = ContextCompat.getColor(context, R.color.remove_red);
        this.notificationType = notificationType;

        initializeDimensions();
        setOutsideTouchable(true);

        setSwipeListener();
        View popupWindowLayout = this.getContentView();
        ImageView popupIcon = (ImageView) popupWindowLayout.findViewById(R.id.popup_icon);
        TextView popupMessageLabel = (TextView) popupWindowLayout.findViewById(R.id.popup_message_tv);

        switch (notificationType) {
            case TYPE_ALERT_NOTIFICATION:
                popupWindowLayout.setBackgroundResource(R.drawable.alert_notification_background);
                popupMessageLabel.setTextColor(ContextCompat.getColor(context, R.color.white));
                popupIcon.setImageResource(R.drawable.icn_notification_alert);
                break;
            case TYPE_TIMED_NOTIFICATION:
                popupWindowLayout.setBackgroundResource(R.drawable.timed_notification_background);
                popupMessageLabel.setTextColor(ContextCompat.getColor(context, R.color.glitter));
                popupIcon.setImageResource(R.drawable.icn_notification_basic_green);
                break;
            case TYPE_ERROR_NOTIFICATION:
                popupWindowLayout.setBackgroundResource(R.drawable.error_notification_background);
                popupMessageLabel.setTextColor(ContextCompat.getColor(context, R.color.white));
                ApplicationMode.ApplicationType appMode = ((IApplicationSession) context).getApplicationMode().getApplicationType();
                if (popupMessageText == null) {
                    popupMessageText = CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE;
                }
                this.hasStatusBar = (appMode == ApplicationMode.ApplicationType.PATIENT);

                popupIcon.setImageResource(R.drawable.icn_notification_error);
                break;
            case AUTO_DISSMISS_SUCCESS_NOTIFICATION:
                popupWindowLayout.setBackgroundResource(R.drawable.success_notification_background);
                popupMessageLabel.setTextColor(ContextCompat.getColor(context, R.color.white));
                popupIcon.setImageResource(R.drawable.icn_notification_basic_white_check);
                break;
            default:
        }

        if (Build.VERSION.SDK_INT >= 24) {
            popupMessageLabel.setText(Html.fromHtml(popupMessageText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            popupMessageLabel.setText(Html.fromHtml(popupMessageText));
        }

        SystemUtil.setTypefaceFromAssets(context, "fonts/proximanova_regular.otf", popupMessageLabel);
    }

    private void initializeDimensions() {
        if (DisplayUtils.getScreenOrientation(context) == Configuration.ORIENTATION_PORTRAIT) {
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Show pop window.
     */
    public void showPopWindow() {
        if (parentView == null && window == null) {
            return;
        }
        if (parentView == null) {
            parentView = window.getDecorView();
            if (parentView == null) {
                return;
            }
        }
        showAtLocation(parentView, Gravity.TOP, 0, 0);

        if (notificationType == TYPE_ERROR_NOTIFICATION) {
            if (hasStatusBar) {
                setStatusBarColor(errorColor);
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (hasStatusBar) {
                        setStatusBarColor(statusBarColor);
                    }
                    if (isShowing()) {
                        try {
                            dismiss();
                        } catch (IllegalArgumentException iae) {
                            //do nothing
                        }
                    }

                }

            }, CarePayConstants.CUSTOM_POPUP_AUTO_DISMISS_DURATION);
        }
    }

    private void setSwipeListener() {
        getContentView().setOnTouchListener(new SwipeGuestureListener(context) {

            public void onSwipeTop() {
                if (hasStatusBar) {
                    setStatusBarColor(statusBarColor);
                }
                callback.onSwipe(GESTURE_DIRECTION_TOP);
            }

            public void onSwipeRight() {
                if (hasStatusBar) {
                    setStatusBarColor(statusBarColor);
                }
                callback.onSwipe(GESTURE_DIRECTION_RIGHT);
            }

            public void onSwipeLeft() {
                if (hasStatusBar) {
                    setStatusBarColor(statusBarColor);
                }
                callback.onSwipe(GESTURE_DIRECTION_LEFT);
            }

            public void onSwipeBottom() {
                if (hasStatusBar) {
                    setStatusBarColor(statusBarColor);
                }
                callback.onSwipe(GESTURE_DIRECTION_BOTTOM);
            }

        });
    }

    @SuppressLint("NewApi")
    private void setStatusBarColor(int color) {
        if (window != null && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            window.setStatusBarColor(color);
        }
    }
}
