package com.carecloud.carepaylibray.carepaycamera;

import android.graphics.Bitmap;

/**
 * Created by Jahirul Bhuiyan on 11/10/2016.
 */

public interface CarePayCameraCallback {
    void onCapturedSuccess(Bitmap bitmap);

    void onCaptureFail();

    void onChangeCamera(int currentCameraId);
}
