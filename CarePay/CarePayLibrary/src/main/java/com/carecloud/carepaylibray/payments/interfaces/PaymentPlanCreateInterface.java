package com.carecloud.carepaylibray.payments.interfaces;

import android.support.v4.app.Fragment;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;

public interface PaymentPlanCreateInterface extends ChooseCreditCardInterface, PaymentMethodInterface,
        FragmentActivityInterface {

    void onStartPaymentPlan(PaymentsModel paymentsModel,
                            PaymentPlanPostModel paymentPlanPostModel);

    void onSubmitPaymentPlan(WorkflowDTO workflowDTO);

    void onPaymentPlanAddedExisting(WorkflowDTO workflowDTO);

    void navigateToFragment(Fragment fragment, boolean addToBackStack);

}
