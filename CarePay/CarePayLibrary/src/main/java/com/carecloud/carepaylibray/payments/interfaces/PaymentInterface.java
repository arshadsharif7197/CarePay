package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentInterface {

    /**
     * Callback to launch the payment method selector
     *
     * @param amount amount to pay
     * @param paymentsModel
     */
    void onPayButtonClicked(double amount, PaymentsModel paymentsModel);
}
