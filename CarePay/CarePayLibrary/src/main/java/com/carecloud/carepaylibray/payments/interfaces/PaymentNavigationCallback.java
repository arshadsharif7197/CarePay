package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;

/**
 * Created by lmenendez on 2/28/17.
 */

public interface PaymentNavigationCallback extends ResponsibilityPaymentInterface, PaymentDetailInterface,
        PaymentMethodInterface, PaymentConfirmationInterface, ChooseCreditCardInterface, PaymentCompletedInterface {

    /**
     * Start payment process.. This is where we should init the initial dialog to show responsibility and details
     *
     * @param paymentsModel payment model dto
     */
    void startPaymentProcess(PaymentsModel paymentsModel);

}
