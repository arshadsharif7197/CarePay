package com.carecloud.carepay.practice.library.payments.interfaces;

import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.SimpleChargeItem;

import java.util.List;

/**
 * Created by lmenendez on 3/16/17
 */

public interface PracticePaymentNavigationCallback extends PaymentNavigationCallback, PaymentMethodDialogInterface, PracticePaymentHistoryCallback {
}
