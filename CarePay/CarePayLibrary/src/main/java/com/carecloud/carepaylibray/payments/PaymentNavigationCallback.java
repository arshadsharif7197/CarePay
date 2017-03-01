package com.carecloud.carepaylibray.payments;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;

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
     * @param amount
     */
    void onPayButtonClicked(double amount);

    /**
     * Callback to proceed to select card view once payment method is selected
     * @param selectedPaymentMethod
     * @param amount
     */
    void onPaymentMethodAction(String selectedPaymentMethod, double amount);

    /**
     * Callback to start payment plan workflow
     */
    void onPaymentPlanAction();

    /**
     * Callback to display receipt
     * @param paymentsModel
     */
    void showReceipt(PaymentsModel paymentsModel);

    /**
     * Callback to add new card
     * @param amount
     */
    void showAddCard(double amount);
}
