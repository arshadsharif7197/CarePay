package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

/**
 * @author pjohnson on 29/05/17.
 */
public interface PaymentCompletedInterface {

    /**
     * Callback when payment process is finished... This is where any cleanup of screens and fragments should occur
     *
     * @param workflowDTO updated balance
     */
    void completePaymentProcess(WorkflowDTO workflowDTO);
}
