package com.carecloud.carepaylibray.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

/**
 * Created by sudhir_pingale on 12/21/2016.
 */

public class ProgressDialogUtil {

    private ProgressDialog mProgressDialog;
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
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    /**
     * Dismiss.
     */
    public void dismiss(){
        if(mProgressDialog!=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
