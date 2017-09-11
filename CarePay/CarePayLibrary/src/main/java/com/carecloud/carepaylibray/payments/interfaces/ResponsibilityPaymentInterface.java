package com.carecloud.carepaylibray.payments.interfaces;

import android.support.annotation.Nullable;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;

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

    @Nullable
    AppointmentDTO getAppointment();
}
