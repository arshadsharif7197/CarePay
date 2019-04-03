package com.carecloud.carepaylibray.payments.interfaces;

import android.support.annotation.Nullable;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentMethodInterface extends FragmentActivityInterface {

    /**
     * Callback to proceed to select card view once payment method is selected
     *
     * @param selectedPaymentMethod Selected Payment Method
     * @param amount                amount to pay
     */
    void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel);

    @Nullable
    String getAppointmentId();

    @Nullable
    AppointmentDTO getAppointment();

    UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel);

    void onPaymentCashFinished();
}
