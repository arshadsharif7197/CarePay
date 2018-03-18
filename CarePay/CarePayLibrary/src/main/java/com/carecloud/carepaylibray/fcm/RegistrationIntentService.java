package com.carecloud.carepaylibray.fcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by pjohnson on 4/04/17.
 * DOC: http://stackoverflow.com/questions/37711082/how-to-handle-notification-when-app-in-background-in-firebase
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "Breeze";


    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            ((AndroidPlatform) Platform.get()).openDefaultSharedPreferences().edit().putString("deviceToken", token).apply();
        }
        Log.d(TAG, "FCM Registration Token: " + token);
    }
}
