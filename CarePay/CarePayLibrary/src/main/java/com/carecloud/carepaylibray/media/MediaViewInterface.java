package com.carecloud.carepaylibray.media;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by lmenendez on 4/24/17
 */

public interface MediaViewInterface extends MediaResultListener {

    void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void setCapturedBitmap(String path, View view);

    void handleStartActivityForResult(Intent intent, int requestCode);

    @Nullable
    Fragment getCallingFragment();

    void setupImageBase64();
}
