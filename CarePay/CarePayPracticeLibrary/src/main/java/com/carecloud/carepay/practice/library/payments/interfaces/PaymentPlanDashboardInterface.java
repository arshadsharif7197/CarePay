package com.carecloud.carepay.practice.library.payments.interfaces;

import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 17/05/18.
 */
public interface PaymentPlanDashboardInterface extends PaymentPlanCreateInterface {

    void onAddBalanceToExistingPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlan);

    void showPaymentPlanDetail(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlan);
}
