package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * Created by lmenendez on 5/30/17.
 */

public interface PaymentMethodDialogInterface extends PaymentMethodInterface {
    /**
     * Callback to do cleanup when dismissing PaymentMethod Dialog
     * @param paymentsModel
     */
    void onDismissPaymentMethodDialog(PaymentsModel paymentsModel);

}
