package com.carecloud.carepay.patient.payment.interfaces;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;

/**
 * Created by pjohnson on 21/03/17.
 */
public interface PaymentPatientInterface {
    void showNoPaymentsLayout(int sectionNumber);

    void loadPaymentAmountScreen(PendingBalancePayloadDTO model, PaymentsModel paymentDTO);
}
