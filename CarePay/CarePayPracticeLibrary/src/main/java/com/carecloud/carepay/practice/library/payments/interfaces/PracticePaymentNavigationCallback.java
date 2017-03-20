package com.carecloud.carepay.practice.library.payments.interfaces;

import com.carecloud.carepay.practice.library.payments.fragments.AddPaymentItemFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionEntryFragment;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;

import java.util.List;

/**
 * Created by lmenendez on 3/16/17.
 */

public interface PracticePaymentNavigationCallback extends PaymentNavigationCallback {
    void lookupChargeItem(List<BalanceItemDTO> simpleChargeItems, AddPaymentItemFragment.AddItemCallback callback);

    void showAmountEntry(PaymentDistributionEntryFragment.PaymentDistributionAmountCallback callback);

}
