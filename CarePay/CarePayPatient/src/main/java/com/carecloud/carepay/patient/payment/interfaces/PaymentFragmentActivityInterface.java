package com.carecloud.carepay.patient.payment.interfaces;

import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsBalancesItem;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 27/04/17.
 */

public interface PaymentFragmentActivityInterface extends FragmentActivityInterface, PaymentNavigationCallback {

    void loadPaymentAmountScreen(PaymentsBalancesItem selectedBalancesItem, PaymentsModel paymentDTO);

    void displayToolbar(boolean visible, String title);
}
