package com.carecloud.carepaylibray.customcomponents;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.qrcodescanner.DisplayUtils;
import com.carecloud.carepaylibray.utils.StringUtil;

/**
 * Created by kkannan on 2/23/17.
 */
public class SuccessMessageToast extends Toast {

    private final Context context;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context        The context to use.  Usually your {@link Application}                or {@link Activity} object.
     * @param successMessage the success message
     */
    public SuccessMessageToast(Context context, String successMessage) {
        super(context);
        this.context = context;
        int orientation = DisplayUtils.getScreenOrientation(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.success_message_toast, null);
        if(!StringUtil.isNullOrEmpty(successMessage))
        {
            TextView successTextView = (TextView) layout.findViewById(R.id.success_message_toast_textview);
            if (Build.VERSION.SDK_INT >= 24){
                successTextView.setText(Html.fromHtml(successMessage, Html.FROM_HTML_MODE_LEGACY));
            } else{
                successTextView.setText(Html.fromHtml(successMessage));
            }
        }
        setView(layout);
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            setGravity(Gravity.FILL_HORIZONTAL|Gravity.TOP, 0, 0);
        }else {
            setGravity(Gravity.TOP, 0, 0);
        }
        setDuration(Toast.LENGTH_SHORT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((BaseActivity) context).getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.emerald));
        }

    }

    @Override
    public void show() {
        super.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((BaseActivity) context).getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
            }
        }, 3000);

    }
}
