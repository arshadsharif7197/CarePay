package com.carecloud.carepaylibray.utils;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.carecloud.carepaylibrary.R;

public class ProgressDialogUtil extends Dialog {

    private int theme;

    public ProgressDialogUtil(Context context) {
        this(context, R.style.ProgressDialogFullscreenWithTitlebar);
    }

    private ProgressDialogUtil(Context context, int themeResId){
        super(context, themeResId);
        this.theme = themeResId;
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);

        int[] attributes = {R.attr.keepStatusBar};
        TypedArray typedArray = getContext().obtainStyledAttributes(theme, attributes);
        boolean keepStatusBar = typedArray.getBoolean(0, false);
        if(keepStatusBar){
            adjustForStatusBar();
        }
    }

    private void adjustForStatusBar(){
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        setStatusBarColor();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
    }
}
