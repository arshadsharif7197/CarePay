package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

/**
 * Created by lmenendez on 3/30/18
 */

public interface PaymentPlanCompletedInterface {

    void completePaymentPlanProcess(WorkflowDTO workflowDTO);

}
