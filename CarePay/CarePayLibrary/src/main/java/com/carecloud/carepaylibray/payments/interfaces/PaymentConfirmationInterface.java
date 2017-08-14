package com.carecloud.carepaylibray.payments.interfaces;

import android.support.annotation.Nullable;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentConfirmationInterface extends PaymentInterface {

    /**
     * Callback to display receipt
     *
     * @param workflowDTO receipt model
     */
    void showPaymentConfirmation(WorkflowDTO workflowDTO);

    /**
     * Return the practice info associated to the current payment flow
     * Should fill the PracticeID, PracticeMgmt, & PatientID
     * @return practice info associated to current patient payment
     */
    UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel);

    /**
     * Get the appointment id associated with this payment event
     * @return appointment id or null if no appointment linked to this event
     */
    @Nullable
    String getAppointmentId();

    /**
     * Get the appointment object associated with this payment event
     * @return appointment object or null if no appointment linked to this event
     */
    @Nullable
    AppointmentDTO getAppointment();


}
