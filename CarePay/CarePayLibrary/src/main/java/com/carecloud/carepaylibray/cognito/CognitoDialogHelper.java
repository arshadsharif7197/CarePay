package com.carecloud.carepaylibray.cognito;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by lsoco_user on 9/21/2016.
 */

public class CognitoDialogHelper {

    private static final String LOG_TAG = CognitoDialogHelper.class.getSimpleName();
    private Context        context;
    private ProgressDialog waitDialog;
    private AlertDialog    userDialog;

    public CognitoDialogHelper(Context context) {
        this.context = context;
    }

    public void showWaitDialog(String message) {
        if(waitDialog != null) {
            closeWaitDialog();
            Log.v(LOG_TAG, "closed progress dialog");
        } else {
            waitDialog = new ProgressDialog(context);
            waitDialog.setTitle(message);
            Log.v(LOG_TAG, "new progress dialog");
        }

        waitDialog.setTitle(message);
        waitDialog.show();
    }

    public void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(body)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    public void closeWaitDialog() {
        try {
            waitDialog.dismiss();
            waitDialog = null;
        }
        catch (Exception e) {
        }
    }

}
