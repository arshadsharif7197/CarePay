package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 29/05/17.
 */

public interface ResponsibilityPaymentInterface extends PaymentInterface {

    /**
     * Callback when payment process is canceled... This is where any cleanup of screens or resetting views should occur
     *
     * @param paymentsModel payment model
     */
    void onPayLaterClicked(PaymentsModel paymentsModel);

    /**
     * Callback to launch the partial payment view
     *
     * @param owedAmount the owed amount
     */
    void onPartialPaymentClicked(double owedAmount);
}
