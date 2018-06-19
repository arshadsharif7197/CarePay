package com.carecloud.carepaylibray.payments.interfaces;

import android.support.annotation.Nullable;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;

/**
 * @author pjohnson on 29/05/17.
 */

public interface ResponsibilityPaymentInterface extends PaymentInterface {

    /**
     * Callback when payment process is canceled... This is where any cleanup of screens or resetting views should occur
     *
     * @param pendingBalanceDTO the selected pending balance
     */
    void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO);

    /**
     * Callback to launch the partial payment view
     *
     * @param owedAmount the owed amount
     */
    void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance);

    /**
     * Callback to launch payment plan flow
     *
     * @param paymentsModel payment model
     */
    void onPaymentPlanAction(PaymentsModel paymentsModel);

    /**
     * Callback to display balance details
     *
     * @param paymentsModel payment model
     * @param paymentLineItem line item
     * @param selectedBalance selected balance
     */
    void displayBalanceDetails(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem, PendingBalanceDTO selectedBalance);

    @Nullable
    AppointmentDTO getAppointment();
}
