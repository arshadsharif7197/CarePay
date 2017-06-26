package com.carecloud.carepay.mini.views;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.utils.StringUtil;

/**
 * Created by lmenendez on 6/25/17
 */

public class CustomErrorToast extends Toast {

    private CustomErrorToast(Context context, String message) {
        super(context);
        View errorLayout = LayoutInflater.from(context).inflate(R.layout.error_message_toast, null, false);
        TextView textView = (TextView) errorLayout.findViewById(R.id.error_message_textview);
        if (!StringUtil.isNullOrEmpty(message)) {
            if (Build.VERSION.SDK_INT >= 24) {
                textView.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));
            } else {
                textView.setText(Html.fromHtml(message));
            }
        }
        setView(errorLayout);
        setGravity(Gravity.TOP, 0, 0);
    }

    public static void showWithMessage(Context context, String message){
        new CustomErrorToast(context, message).show();
    }

}
