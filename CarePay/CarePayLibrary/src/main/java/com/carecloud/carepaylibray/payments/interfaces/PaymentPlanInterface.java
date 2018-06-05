package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.interfaces.DTOInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * Created by lmenendez on 1/23/18
 */

public interface PaymentPlanInterface extends PaymentPlanCompletedInterface, PaymentPlanCreateInterface,
        OneTimePaymentInterface, DTOInterface {

    void onEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO);

    void onPaymentPlanEdited(WorkflowDTO workflowDTO);

    void onDismissEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO);

    void onEditPaymentPlanPaymentMethod(PaymentsModel paymentsModel);
}
