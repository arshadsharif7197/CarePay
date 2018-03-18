package com.carecloud.carepaylibray.utils;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.carecloud.carepaylibrary.R;

public class ProgressDialogUtil extends Dialog {

    private static final int FULLSCREEN_VALUE = 0x10000000;
    private final boolean isPracticeAppPatientMode;
    private final int theme;

    public ProgressDialogUtil(boolean isPracticeAppPatientMode, Context context) {
        this(isPracticeAppPatientMode, context, R.style.ProgressDialogFullscreenWithTitlebar);
    }

    private ProgressDialogUtil(boolean isPracticeAppPatientMode, Context context, int themeResId) {
        super(context, themeResId);
        this.isPracticeAppPatientMode = isPracticeAppPatientMode;
        this.theme = themeResId;
    }

    @Override
    public void onCreate(Bundle icicle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        int[] attributes = {R.attr.keepStatusBar};
        TypedArray typedArray = getContext().obtainStyledAttributes(theme, attributes);
        boolean keepStatusBar = typedArray.getBoolean(0, false);
        typedArray.recycle();

        if (keepStatusBar) {
            adjustForStatusBar();
        }

        if (isPracticeAppPatientMode) {
            setNavigationBarVisibility();
        }
        setContentView(R.layout.dialog_progress);
    }

    /**
     * Updates layout so in clover and devices with navigation bar is on screen don't hide content
     */
    private void setNavigationBarVisibility() {

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | FULLSCREEN_VALUE;
        decorView.setSystemUiVisibility(uiOptions);
    }


    private void adjustForStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        setStatusBarColor();
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
        }
    }
}
