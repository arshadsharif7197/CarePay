package com.carecloud.carepaylibray.payments.interfaces;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentMethodInterface {

    /**
     * Callback to start payment plan workflow
     */
    void onPaymentPlanAction(PaymentsModel paymentsModel);

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

    void displayDialogFragment(DialogFragment fragment, boolean addToBackStack);
}
