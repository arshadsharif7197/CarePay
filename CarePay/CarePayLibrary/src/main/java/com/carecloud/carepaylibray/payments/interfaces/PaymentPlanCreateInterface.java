package com.carecloud.carepaylibray.payments.interfaces;

import android.support.v4.app.Fragment;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;

public interface PaymentPlanCreateInterface extends ChooseCreditCardInterface, PaymentMethodInterface,
        FragmentActivityInterface {

    void onStartPaymentPlan(PaymentsModel paymentsModel,
                            PaymentPlanPostModel paymentPlanPostModel);

    void onDismissPaymentPlan(PaymentsModel paymentsModel);

    void onSubmitPaymentPlan(WorkflowDTO workflowDTO);

    void displayBalanceDetails(PaymentsModel paymentsModel,
                               PendingBalancePayloadDTO paymentLineItem,
                               PendingBalanceDTO selectedBalance);

    void onPaymentPlanAddedExisting(WorkflowDTO workflowDTO);

    void navigateToFragment(Fragment fragment, boolean addToBackStack);

}
