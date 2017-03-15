package com.carecloud.carepaylibray.base;

import android.content.Context;

/**
 * Created by pjohnson on 15/03/17.
 */

public class AndroidPlatform extends Platform {
    private final Context context;

    public AndroidPlatform(Context context) {
        super();
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
