package com.carecloud.carepaylibray.fcm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by pjohnson on 4/04/17.
 * DOC: http://stackoverflow.com/questions/37711082/how-to-handle-notification-when-app-in-background-in-firebase
 */
public class RegistrationIntentService extends JobIntentService {

    private static final String TAG = "Breeze";
    public static final int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, RegistrationIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            ((AndroidPlatform) Platform.get()).openDefaultSharedPreferences().edit().putString("deviceToken", token).apply();
        }
        Log.d(TAG, "FCM Registration Token: " + token);
    }
}
