package com.carecloud.carepaylibray.payments.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;

public interface PaymentPlanCreateInterface extends ChooseCreditCardInterface, PaymentMethodInterface {
    void onStartPaymentPlan(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel);

    void onDismissPaymentPlan(PaymentsModel paymentsModel);

    void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod,
                                   PaymentsModel paymentsModel,
                                   PaymentPlanPostModel paymentPlanPostModel,
                                   boolean onlySelectMode);

    void onAddPaymentPlanCard(PaymentsModel paymentsModel,
                              PaymentPlanPostModel paymentPlanPostModel,
                              boolean onlySelectMode);

    void onDisplayPaymentPlanTerms(PaymentsModel paymentsModel,
                                   PaymentPlanPostModel paymentPlanPostModel);

    void onSubmitPaymentPlan(WorkflowDTO workflowDTO);

    void onAddBalanceToExistingPlan(PaymentsModel paymentsModel,
                                    PendingBalanceDTO selectedBalance,
                                    double amount);

    void onSelectedPlanToAdd(PaymentsModel paymentsModel,
                             PendingBalanceDTO selectedBalance,
                             PaymentPlanDTO selectedPlan,
                             double amount);

    void displayBalanceDetails(PaymentsModel paymentsModel,
                               PendingBalancePayloadDTO paymentLineItem,
                               PendingBalanceDTO selectedBalance);

    void onPaymentPlanAddedExisting(WorkflowDTO workflowDTO);

}
