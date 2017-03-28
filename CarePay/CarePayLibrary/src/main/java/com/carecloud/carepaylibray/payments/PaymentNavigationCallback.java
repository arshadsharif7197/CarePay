package com.carecloud.carepaylibray.payments;

import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;

/**
 * Created by lmenendez on 2/28/17.
 */

public interface PaymentNavigationCallback {
    /**
     * Callback to launch the partial payment view
     * @param owedAmount the owed amount
     */
    void startPartialPayment(double owedAmount);

    /**
     * Callback to launch the payment method selector
     * @param amount amount to pay
     */
    void onPayButtonClicked(double amount, PaymentsModel paymentsModel);

    /**
     * Callback to proceed to select card view once payment method is selected
     * @param selectedPaymentMethod Selected Payment Method
     * @param amount amount to pay
     */
    void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel);

    /**
     * Callback to start payment plan workflow
     */
    void onPaymentPlanAction();

    /**
     * Callback to display receipt
     * @param paymentsModel receipt model
     */
    void showPaymentConfirmation(PaymentsModel paymentsModel);

    /**
     * Callback to add new card
     * @param amount amount to pay
     */
    void showAddCard(double amount, PaymentsModel paymentsModel);

    /**
     * Callback when payment process is finished... This is where any cleanup of screens and fragments should occur
     * @param updatePatientBalancesDTO updated balance
     */
    void completePaymentProcess(UpdatePatientBalancesDTO updatePatientBalancesDTO);


    /**
     * Callback when payment process is canceled... This is where any cleanup of screens or resetting views should occur
     * @param paymentsModel payment model
     */
    void cancelPaymentProcess(PaymentsModel paymentsModel);
}
