package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentCompletedInterface {

    /**
     * Callback when payment process is finished... This is where any cleanup of screens and fragments should occur
     *
     * @param updatePatientBalancesDTO updated balance
     */
    void completePaymentProcess(PaymentsModel updatePatientBalancesDTO);
}
