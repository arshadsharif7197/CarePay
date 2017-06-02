package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
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
     * @return practice info ssociated to current patient payment
     */
    UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel);

}
