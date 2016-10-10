
package com.carecloud.carepaylibray.payment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsHistoryModel {

    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("query_string")
    @Expose
    private PaymentQueryStringDto paymentQueryStringDto;

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
     *     The paymentQueryStringDto
     */
    public PaymentQueryStringDto getPaymentQueryStringDto() {
        return paymentQueryStringDto;
    }

    /**
     * 
     * @param paymentQueryStringDto
     *     The query_string
     */
    public void setPaymentQueryStringDto(PaymentQueryStringDto paymentQueryStringDto) {
        this.paymentQueryStringDto = paymentQueryStringDto;
    }

}
