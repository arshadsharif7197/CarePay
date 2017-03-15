package com.carecloud.carepaylibray.base;

/**
 * Created by pjohnson on 15/03/17.
 */

public class Platform {

    private static Platform PLATFORM;

    public static Platform get() {
        if (PLATFORM == null) {
            PLATFORM = new Platform();
        }
        return PLATFORM;
    }

    public static void setPlatform(Platform platform) {
        PLATFORM = platform;
    }

    protected Platform() {
    }
}
