package com.carecloud.carepaylibray.appointments.utils;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by sudhir_pingale on 9/22/2016.
 */

public class PopupNotificationWithAction extends PopupWindow {

    private View mParentView;

    public PopupNotificationWithAction(Context context, View parentView, String positiveButtonCaption,
                                       String negativeButtonCaption, String popupMessageText,
                                       View.OnClickListener positiveAction, View.OnClickListener negativeAction) {

        super(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.popup_appointment_reminder, null),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        this.mParentView = parentView;

        if (Build.VERSION.SDK_INT >= 21) {
            this.setElevation(5.0f);
        }

        View view = this.getContentView();
        Button apt_popup_dismiss_button = (Button) view.findViewById(R.id.apt_popup_dismiss_button);
        Button apt_popup_checkin_button = (Button) view.findViewById(R.id.apt_popup_checkin_button);
        TextView apt_popup_message_tv = (TextView) view.findViewById(R.id.apt_popup_message_tv);
        apt_popup_dismiss_button.setText(negativeButtonCaption);
        apt_popup_checkin_button.setText(positiveButtonCaption);
        apt_popup_message_tv.setText(popupMessageText);

        SystemUtil.setTypefaceFromAssets(context, "fonts/proximanova_regular.otf", apt_popup_message_tv);
        SystemUtil.setTypefaceFromAssets(context, "fonts/gotham_rounded_medium.otf", apt_popup_dismiss_button);
        SystemUtil.setTypefaceFromAssets(context, "fonts/gotham_rounded_medium.otf", apt_popup_checkin_button);

        apt_popup_dismiss_button.setOnClickListener(negativeAction);
        apt_popup_checkin_button.setOnClickListener(positiveAction);
    }

    public void showPopWindow() {
        showAtLocation(mParentView, Gravity.TOP, 0, 0);
    }
}
