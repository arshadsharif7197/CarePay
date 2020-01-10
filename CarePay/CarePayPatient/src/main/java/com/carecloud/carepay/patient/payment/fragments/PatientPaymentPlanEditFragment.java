package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;

import com.carecloud.carepaylibray.payments.fragments.PaymentPlanEditFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 4/2/19.
 */
public class PatientPaymentPlanEditFragment extends PaymentPlanEditFragment {

    /**
     * @param paymentsModel  the payment model
     * @param paymentPlanDTO the plan to be edited
     * @return an PaymentPlanFragment instance with the payment plan data filled for editing a payment plan
     */
    public static PatientPaymentPlanEditFragment newInstance(PaymentsModel paymentsModel,
                                                      PaymentPlanDTO paymentPlanDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);

        PatientPaymentPlanEditFragment fragment = new PatientPaymentPlanEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onEditPaymentPlanPaymentMethod(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        callback.addFragment(PaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, true), true);
        hideDialog();
    }

}
