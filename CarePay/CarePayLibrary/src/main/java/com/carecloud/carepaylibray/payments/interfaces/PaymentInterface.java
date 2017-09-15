package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentInterface {

    /**
     * Callback to launch the payment method selector
     *
     * @param amount amount to pay
     * @param paymentsModel the payment model
     */
    void onPayButtonClicked(double amount, PaymentsModel paymentsModel);

    void navigateToWorkflow(WorkflowDTO workflowDTO);
}
