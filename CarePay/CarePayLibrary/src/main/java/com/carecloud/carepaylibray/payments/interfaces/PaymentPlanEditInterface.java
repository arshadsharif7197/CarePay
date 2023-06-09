package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.interfaces.DTOInterface;

/**
 * Created by lmenendez on 1/23/18
 */

public interface PaymentPlanEditInterface extends PaymentPlanCompletedInterface, PaymentPlanCreateInterface,
        OneTimePaymentInterface, DTOInterface {

    void onPaymentPlanEdited(WorkflowDTO workflowDTO);

    void onPaymentPlanCanceled(WorkflowDTO workflowDTO, boolean isDeleted);

    void showCancelPaymentPlanConfirmDialog(ConfirmationCallback confirmationCallback, boolean isGoingToDelete);

}
