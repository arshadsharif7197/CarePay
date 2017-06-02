package com.carecloud.carepay.practice.library.payments.interfaces;

import com.carecloud.carepay.practice.library.payments.fragments.AddPaymentItemFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionEntryFragment;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.SimpleChargeItem;

import java.util.List;

/**
 * Created by lmenendez on 3/16/17
 */

public interface PracticePaymentNavigationCallback extends PaymentNavigationCallback, PaymentMethodDialogInterface {
    void lookupChargeItem(List<SimpleChargeItem> simpleChargeItems, AddPaymentItemFragment.AddItemCallback callback);

    void showAmountEntry(PaymentDistributionEntryFragment.PaymentDistributionAmountCallback callback, BalanceItemDTO balanceItemDTO, SimpleChargeItem chargeItem);

}
