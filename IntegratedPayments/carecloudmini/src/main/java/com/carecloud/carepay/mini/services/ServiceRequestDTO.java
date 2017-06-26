package com.carecloud.carepay.mini.services;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by kkannan on 5/23/17.
 */

public class ServiceRequestDTO {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_DELETE = "DELETE";

    private @MethodTypeDefs String method;
    private String url;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({METHOD_GET, METHOD_POST, METHOD_DELETE})
    public @interface MethodTypeDefs{}


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public @MethodTypeDefs String getMethod() {
        return method;
    }

    public void setMethod(@MethodTypeDefs String method) {
        this.method = method;
    }

    /**
     * @return true is method is GET
     */
    public boolean isGet() {
        return METHOD_GET.equalsIgnoreCase(method);
    }

    /**
     * @return true is method is POST
     */
    public boolean isPost() {
        return METHOD_POST.equalsIgnoreCase(method);
    }

    /**
     * @return true is method is DELETE
     */
    public boolean isDelete() {
        return METHOD_DELETE.equalsIgnoreCase(method);
    }


}
