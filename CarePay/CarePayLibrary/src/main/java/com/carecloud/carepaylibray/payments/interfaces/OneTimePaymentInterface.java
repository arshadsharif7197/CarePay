package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentPayload;

import java.util.Date;

/**
 * Created by lmenendez on 2/7/18
 */

public interface OneTimePaymentInterface extends PaymentMethodInterface {
    void onMakeOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO);

    void onStartOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO);

    void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod, PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO, boolean onlySelectMode, Date paymentDate);

    void onAddPaymentPlanCard(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO, boolean onlySelectMode, Date paymentDate);

    void onScheduleOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO, Date paymentDate);

    void showScheduledPaymentConfirmation(WorkflowDTO workflowDTO);

    void showDeleteScheduledPaymentConfirmation(WorkflowDTO workflowDTO, ScheduledPaymentPayload scheduledPaymentPayload);

    void showPaymentConfirmation(WorkflowDTO workflowDTO, boolean isOneTimePayment);

}
