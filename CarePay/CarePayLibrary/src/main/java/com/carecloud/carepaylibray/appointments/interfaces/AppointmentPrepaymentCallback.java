package com.carecloud.carepaylibray.appointments.interfaces;

import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.payments.interfaces.ChooseCreditCardInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentCompletedInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodInterface;

/**
 * Created by lmenendez on 8/21/17
 */

public interface AppointmentPrepaymentCallback extends PaymentMethodInterface, ChooseCreditCardInterface, PaymentCompletedInterface {

    void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDTO, double amount);

}
