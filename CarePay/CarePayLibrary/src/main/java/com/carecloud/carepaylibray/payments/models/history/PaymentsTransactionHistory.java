package com.carecloud.carepaylibray.payments.models.history;

import com.carecloud.carepaylibray.base.models.Paging;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 9/28/17
 */

public class PaymentsTransactionHistory {

    @SerializedName("page_details")
    private Paging pageDetails = new Paging();

    @SerializedName("results")
    private List<PaymentHistoryItem> paymentHistoryList = new ArrayList<>();

    public Paging getPageDetails() {
        return pageDetails;
    }

    public void setPageDetails(Paging pageDetails) {
        this.pageDetails = pageDetails;
    }

    public List<PaymentHistoryItem> getPaymentHistoryList() {
        return paymentHistoryList;
    }

    public void setPaymentHistoryList(List<PaymentHistoryItem> paymentHistoryList) {
        this.paymentHistoryList = paymentHistoryList;
    }
}
