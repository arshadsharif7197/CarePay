package com.carecloud.carepay.practice.library.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * Created by lmenendez on 11/9/17
 */

public interface ShamrockPaymentsCallback {
    void showChooseDeviceList(PaymentsModel paymentsModel, double paymentAmount);

    void dismissChooseDeviceList(double amount, PaymentsModel paymentsModel);

    void showPaymentConfirmation(WorkflowDTO workflowDTO);

}
