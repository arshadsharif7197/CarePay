package com.carecloud.breezemini.services;

/**
 * Created by kkannan on 5/23/17.
 */

public class ServiceRequestDTO {

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";

    private String method;
    private String url;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
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
