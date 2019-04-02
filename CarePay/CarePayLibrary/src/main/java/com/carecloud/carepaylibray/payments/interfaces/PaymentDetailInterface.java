package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentDetailInterface extends ResponsibilityPaymentInterface {

    void onPaymentPlanAction(PaymentsModel paymentsModel);

    void onPaymentPlanAmount(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, double amount);

}
