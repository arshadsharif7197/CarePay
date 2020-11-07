package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;

import com.carecloud.carepaylibray.payments.fragments.PaymentPlanFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2019-05-03.
 */
public class PatientPaymentPlanFragment extends PaymentPlanFragment {

    public static PatientPaymentPlanFragment newInstance(PaymentsModel paymentsModel,
                                                         PendingBalanceDTO selectedBalance,
                                                         double paymentPlanAmount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);
        args.putDouble(KEY_PLAN_AMOUNT, paymentPlanAmount);

        PatientPaymentPlanFragment fragment = new PatientPaymentPlanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onAddBalanceToExistingPlan() {
        PatientValidPlansFragment fragment = PatientValidPlansFragment
                .newInstance(paymentsModel, selectedBalance, paymentPlanAmount);
        callback.replaceFragment(fragment, true);
        logPaymentPlanStartedMixPanelEvent();
    }
}
