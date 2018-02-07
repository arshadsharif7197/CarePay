package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;

/**
 * Created by lmenendez on 1/23/18
 */

public interface PaymentPlanInterface extends PaymentMethodInterface {

    void onStartPaymentPlan(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel);

    void onDismissPaymentPlan(PaymentsModel paymentsModel);

    void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod, PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel);

    void onAddPaymentPlanCard(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel);

    void onDisplayPaymentPlanTerms(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel);

    void onSubmitPaymentPlan(WorkflowDTO workflowDTO);

    void onMakeOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO);

    void displayBalanceDetails(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem, PendingBalanceDTO selectedBalance);
}