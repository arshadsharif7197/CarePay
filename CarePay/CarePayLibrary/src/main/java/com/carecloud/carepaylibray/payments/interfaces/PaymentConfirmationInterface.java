package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentConfirmationInterface extends PaymentInterface {

    /**
     * Callback to display receipt
     *
     * @param workflowDTO receipt model
     */
    void showPaymentConfirmation(WorkflowDTO workflowDTO);
}
