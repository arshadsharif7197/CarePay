package com.carecloud.carepaylibray.utils;

import android.app.Dialog;
import android.content.Context;

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
        if(progressDialog!=null && progressDialog.isShowing())
            return;

        if(progressDialog == null) {
            progressDialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            progressDialog.setContentView(R.layout.dialog_progress);
        }
//        progressDialog.setMessage("Loading");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        progressDialog.setIndeterminate(true);
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
