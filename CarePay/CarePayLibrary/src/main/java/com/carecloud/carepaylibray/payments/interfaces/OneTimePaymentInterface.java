package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * Created by lmenendez on 2/7/18
 */

public interface OneTimePaymentInterface extends PaymentMethodInterface{
    void onMakeOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO);

    void onStartOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO);

    void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod, PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO, boolean onlySelectMode);

    void onAddPaymentPlanCard(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO, boolean onlySelectMode);

}
