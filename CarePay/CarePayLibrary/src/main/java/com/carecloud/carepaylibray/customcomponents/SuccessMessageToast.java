package com.carecloud.carepaylibray.customcomponents;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.StringUtil;

/**
 * Created by kkannan on 2/23/17.
 */
public class SuccessMessageToast extends Toast {

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public SuccessMessageToast(Context context, String successMessage) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.success_message_toast, null);
        if(!StringUtil.isNullOrEmpty(successMessage))
        {
            TextView successTextView = (TextView) layout.findViewById(R.id.success_message_toast_textview);
            successTextView.setText(successMessage);
        }
        setView(layout);
        setGravity(Gravity.FILL_HORIZONTAL|Gravity.TOP, 0, 0);
        setDuration(Toast.LENGTH_SHORT);
    }


}
