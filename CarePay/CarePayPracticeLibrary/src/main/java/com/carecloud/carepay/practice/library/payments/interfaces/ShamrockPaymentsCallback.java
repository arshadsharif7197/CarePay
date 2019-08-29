package com.carecloud.carepay.practice.library.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

/**
 * Created by lmenendez on 11/9/17
 */

public interface ShamrockPaymentsCallback {

    void showPaymentConfirmation(WorkflowDTO workflowDTO);

}
