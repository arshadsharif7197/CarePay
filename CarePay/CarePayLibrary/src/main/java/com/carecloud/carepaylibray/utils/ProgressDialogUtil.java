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

/**
 * Created by sudhir_pingale on 12/21/2016.
 */

public class ProgressDialogUtil {

    private Dialog progressDialog;
    private static ProgressDialogUtil mProgressDialogUtil;
    private static Context mContext;

    /**
     * Get instance progress dialog util.
     *
     * @param context the context
     * @return the progress dialog util
     */
    public static ProgressDialogUtil getInstance(Context context){
        mContext = context;
        if(mProgressDialogUtil==null){
            mProgressDialogUtil = new ProgressDialogUtil();
        }
        return mProgressDialogUtil;
    }

    /**
     * Show.
     */
    public void show(){
        if(progressDialog!=null && progressDialog.isShowing()) {
            return;
        }

        if(progressDialog == null) {
            progressDialog = new FullScreenProgressDialog(mContext, R.style.ProgressDialogFullscreenWithTitlebar);
        }
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * Dismiss.
     */
    public void dismiss(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    public static class FullScreenProgressDialog extends Dialog {

        int theme;

        public FullScreenProgressDialog(Context context) {
            this(context, R.style.ProgressDialogFullscreenWithTitlebar);
        }

        public FullScreenProgressDialog(Context context, int themeResId){
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
}
