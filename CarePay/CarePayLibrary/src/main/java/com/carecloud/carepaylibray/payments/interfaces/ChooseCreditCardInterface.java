package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 29/05/17.
 */

public interface ChooseCreditCardInterface extends PaymentConfirmationInterface {

    /**
     * Callback to add new card
     *
     * @param amount amount to pay
     */
    void showAddCard(double amount, PaymentsModel paymentsModel);


    void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod);
}
