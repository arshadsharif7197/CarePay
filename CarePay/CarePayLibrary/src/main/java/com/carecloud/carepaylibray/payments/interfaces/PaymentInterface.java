package com.carecloud.carepaylibray.payments.interfaces;

import android.support.v4.app.DialogFragment;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentInterface extends FragmentActivityInterface {

    /**
     * Callback to launch the payment method selector
     *
     * @param amount        amount to pay
     * @param paymentsModel the payment model
     */
    void onPayButtonClicked(double amount, PaymentsModel paymentsModel);

    void navigateToWorkflow(WorkflowDTO workflowDTO);

    void displayDialogFragment(DialogFragment fragment, boolean addToBaskStack);
}
