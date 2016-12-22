package com.carecloud.carepaylibray.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

/**
 * Created by sudhir_pingale on 12/21/2016.
 */

public class ProgressDialogUtil {

    private ProgressDialog progressDialog;
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
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setIndeterminate(false);
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
}
