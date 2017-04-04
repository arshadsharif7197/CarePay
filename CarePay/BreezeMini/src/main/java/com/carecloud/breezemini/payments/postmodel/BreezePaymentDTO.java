package com.carecloud.breezemini.payments.postmodel;

/**
 * Created by kkannan on 3/17/17.
 */

public class BreezePaymentDTO {

    public PaymentLineItem[] getPaymentLineItems() {
        return paymentLineItems;
    }

    public void setPaymentLineItems(PaymentLineItem[] paymentLineItems) {
        this.paymentLineItems = paymentLineItems;
    }

    private PaymentLineItem[] paymentLineItems;

    public long getAmountLong() {
        return amountLong;
    }

    public void setAmountLong(long amountLong) {
        this.amountLong = amountLong;
    }

    private long amountLong;
}
