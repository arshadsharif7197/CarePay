
package com.carecloud.carepaylibray.payment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsDeleteCreditCardsModel {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("query_string")
    @Expose
    private PaymentCreditCardQueryStringDto queryString;

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
     *     The queryString
     */
    public PaymentCreditCardQueryStringDto getQueryString() {
        return queryString;
    }

    /**
     * 
     * @param queryString
     *     The query_string
     */
    public void setQueryString(PaymentCreditCardQueryStringDto queryString) {
        this.queryString = queryString;
    }

}
