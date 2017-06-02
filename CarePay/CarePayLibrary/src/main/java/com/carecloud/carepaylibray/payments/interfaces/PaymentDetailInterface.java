package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentDetailInterface extends PaymentInterface {
    void onDetailCancelClicked(PaymentsModel paymentsModel);
}
