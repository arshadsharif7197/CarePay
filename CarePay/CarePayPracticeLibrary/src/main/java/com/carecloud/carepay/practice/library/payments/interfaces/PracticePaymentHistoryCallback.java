package com.carecloud.carepay.practice.library.payments.interfaces;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;

/**
 * Created by lmenendez on 9/29/17
 */

public interface PracticePaymentHistoryCallback {
    void showPaymentHistory(PaymentsModel paymentsModel);

    void onDismissPaymentHistory(PaymentsModel paymentsModel);

    void displayHistoryItemDetails(PaymentHistoryItem item);
}

