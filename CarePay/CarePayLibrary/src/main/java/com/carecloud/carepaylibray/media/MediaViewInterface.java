package com.carecloud.carepaylibray.media;

import android.content.Intent;

import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;

/**
 * Created by lmenendez on 4/24/17.
 */

public interface MediaViewInterface {

    void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    CarePayCameraCallback getCameraCallback();

    void startActivityForResult(Intent intent, int requestCode);

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
