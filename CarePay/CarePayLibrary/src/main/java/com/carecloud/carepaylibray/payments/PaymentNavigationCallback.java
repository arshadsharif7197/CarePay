package com.carecloud.carepaylibray.payments;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;

/**
 * Created by lmenendez on 2/28/17.
 */

public interface PaymentNavigationCallback {
    /**
     * Callback to launch the partial payment view
     */
    void startPartialPayment();

    /**
     * Callback to launch the payment method selector
     * @param amount amount to pay
     */
    void onPayButtonClicked(double amount, PaymentsModel paymentsModel);

    /**
     * Callback to proceed to select card view once payment method is selected
     * @param selectedPaymentMethod payment method type
     * @param amount amount to pay
     */
    void onPaymentMethodAction(String selectedPaymentMethod, double amount, PaymentsModel paymentsModel);

    /**
     * Callback to start payment plan workflow
     */
    void onPaymentPlanAction();

    /**
     * Callback to display receipt
     * @param paymentsModel receipt model
     */
    void showReceipt(PaymentsModel paymentsModel);

    /**
     * Callback to add new card
     * @param amount amount to pay
     */
    void showAddCard(double amount, PaymentsModel paymentsModel);

}
