package com.carecloud.carepaylibray.media;

import android.content.Intent;

/**
 * Created by lmenendez on 6/5/17
 */

public interface MediaResultListener {
    boolean handleActivityResult(int requestCode, int resultCode, Intent data);
}
