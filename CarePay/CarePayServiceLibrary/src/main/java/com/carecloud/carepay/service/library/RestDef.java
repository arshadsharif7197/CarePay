package com.carecloud.carepay.service.library;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lmenendez on 6/28/17
 */

public class RestDef {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";

    @Retention(RetentionPolicy.CLASS)
    @StringDef({GET, POST, DELETE, PUT})
    @interface RestMethod{}
}
