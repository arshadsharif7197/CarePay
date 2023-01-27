package com.carecloud.shamrocksdk.services;



import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Service Request model for retrofit
 */

public class ServiceRequest {

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_DELETE = "DELETE";

    @StringDef({METHOD_GET, METHOD_POST, METHOD_DELETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MethodType{}

    private @MethodType String method;
    private String url;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public @MethodType String getMethod() {
        return method;
    }

    public void setMethod(@MethodType String method) {
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
