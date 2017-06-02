package com.carecloud.carepaylibray.media;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by lmenendez on 4/24/17
 */

public interface MediaViewInterface {

    void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    boolean handleActivityResult(int requestCode, int resultCode, Intent data);

    void setCapturedBitmap(Bitmap bitmap, String path, View view);

    void handleStartActivityForResult(Intent intent, int requestCode);
}
