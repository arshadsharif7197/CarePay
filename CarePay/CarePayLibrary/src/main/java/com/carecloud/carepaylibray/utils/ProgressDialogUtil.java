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

    private int theme;
    private static final int FULLSCREEN_VALUE = 0x10000000;
    public boolean isPracticeAppPatientMode;

    public ProgressDialogUtil(Context context, Boolean isPracticeAppPatientMode) {
        this(context, R.style.ProgressDialogFullscreenWithTitlebar);
        this.isPracticeAppPatientMode = isPracticeAppPatientMode;
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
        typedArray.recycle();

        if(keepStatusBar){
            adjustForStatusBar();
        }else if(isPracticeAppPatientMode){
            setNavigationBarVisibility();
        }

    }

    /**
     * Updates layout so in clover and devices with navigation bar is on screen don't hide content
     * */
    public void setNavigationBarVisibility(){

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | FULLSCREEN_VALUE;
        decorView.setSystemUiVisibility(uiOptions);
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
