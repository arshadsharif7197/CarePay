package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentDetailInterface extends ResponsibilityPaymentInterface {
    void onDetailCancelClicked(PaymentsModel paymentsModel);

    void onPaymentPlanAction(PaymentsModel paymentsModel);

}
