
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsHistoryDTO {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("query_string")
    @Expose
    private PaymentsQueryDTO paymentsQuery = new PaymentsQueryDTO();

    /**
     * 
     * @return
     *     The method
     */
    public String getMethod() {
        return method;
    }

    /**
     * 
     * @param method
     *     The method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 
     * @return
     *     The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return
     *     The paymentsQuery
     */
    public PaymentsQueryDTO getPaymentsQuery() {
        return paymentsQuery;
    }

    /**
     * 
     * @param paymentsQuery
     *     The query_string
     */
    public void setPaymentsQuery(PaymentsQueryDTO paymentsQuery) {
        this.paymentsQuery = paymentsQuery;
    }

}
