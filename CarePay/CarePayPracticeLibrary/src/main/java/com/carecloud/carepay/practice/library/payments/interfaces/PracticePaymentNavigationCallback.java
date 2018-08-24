package com.carecloud.carepay.practice.library.payments.interfaces;

import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionEntryFragment;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.SimpleChargeItem;
import com.carecloud.carepaylibray.retail.models.RetailItemDto;

import java.util.List;

/**
 * Created by lmenendez on 3/16/17
 */

public interface PracticePaymentNavigationCallback extends PaymentNavigationCallback, PaymentMethodDialogInterface, PracticePaymentHistoryCallback {
    void lookupChargeItem(List<SimpleChargeItem> simpleChargeItems, AddPaymentItemCallback callback);

    void showAmountEntry(PaymentDistributionEntryFragment.PaymentDistributionAmountCallback callback, BalanceItemDTO balanceItemDTO, SimpleChargeItem chargeItem);

    void showPaymentPlanDashboard(PaymentsModel paymentsModel);

    void showRetailItems(PaymentsModel paymentsModel, AddPaymentItemCallback callback);

    void showRetailItemOptions(RetailItemDto retailItemDto, AddPaymentItemCallback callback);
}
