package com.carecloud.carepay.mini.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lmenendez on 6/25/17
 */

public class Defs {

    public static final int IMAGE_STYLE_PRACTICE_LOGO = 0;
    public static final int IMAGE_STYLE_PRACTICE_INITIALS = 1;
    public static final int IMAGE_STYLE_CARECLOUD_LOGO = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IMAGE_STYLE_PRACTICE_LOGO, IMAGE_STYLE_PRACTICE_INITIALS, IMAGE_STYLE_CARECLOUD_LOGO})
    public @interface ImageStyles{}
}
