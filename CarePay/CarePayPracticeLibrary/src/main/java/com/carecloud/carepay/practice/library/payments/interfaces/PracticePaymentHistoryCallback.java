package com.carecloud.carepay.practice.library.payments.interfaces;

import com.carecloud.carepaylibray.payments.interfaces.PaymentInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;

/**
 * Created by lmenendez on 9/29/17
 */

public interface PracticePaymentHistoryCallback extends PaymentInterface {
    void completeRefundProcess(PaymentHistoryItem historyItem, PaymentsModel paymentsModel);
}

