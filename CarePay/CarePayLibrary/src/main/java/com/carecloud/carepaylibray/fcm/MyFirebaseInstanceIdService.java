package com.carecloud.carepaylibray.fcm;

import android.util.Log;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by pjohnson on 4/04/17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "Breeze";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        saveTokenToSP(refreshedToken);
    }

    private void saveTokenToSP(String refreshedToken) {
        ((AndroidPlatform) Platform.get()).openDefaultSharedPreferences()
                .edit().putString(CarePayConstants.FCM_TOKEN, refreshedToken).apply();
    }

}
