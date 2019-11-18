package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentPayload;

/**
 * Created by lmenendez on 2/7/18
 */

public interface OneTimePaymentInterface extends PaymentMethodInterface {

    void showScheduledPaymentConfirmation(WorkflowDTO workflowDTO);

    void showDeleteScheduledPaymentConfirmation(WorkflowDTO workflowDTO, ScheduledPaymentPayload scheduledPaymentPayload);

    void showPaymentConfirmation(WorkflowDTO workflowDTO, boolean isOneTimePayment);

}
