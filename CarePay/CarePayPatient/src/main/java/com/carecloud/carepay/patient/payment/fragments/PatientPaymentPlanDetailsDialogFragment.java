package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.base.ToolbarInterface;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanDetailsDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 4/2/19.
 */
public class PatientPaymentPlanDetailsDialogFragment extends PaymentPlanDetailsDialogFragment {

    private PatientPaymentPlanEditFragment paymentPlanEditFragment;

    /**
     * @param paymentsModel  the payment model
     * @param paymentPlanDTO the Payment Plan Dto
     * @return new instance of a PaymentPlanDetailsDialogFragment
     */
    public static PatientPaymentPlanDetailsDialogFragment newInstance(PaymentsModel paymentsModel,
                                                                      PaymentPlanDTO paymentPlanDTO) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);

        PatientPaymentPlanDetailsDialogFragment dialog = new PatientPaymentPlanDetailsDialogFragment();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    protected void onEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        hideDialog(true);
        paymentPlanEditFragment = PatientPaymentPlanEditFragment.newInstance(paymentsModel, paymentPlanDTO);
        paymentPlanEditFragment.setOnBackPressedListener(() -> showDialog(true));
        callback.replaceFragment(paymentPlanEditFragment, true);
        ((ToolbarInterface) callback).displayToolbar(false, null);
    }

    protected void onMakeOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        hideDialog(true);
        PatientOneTimePaymentDialog fragment = PatientOneTimePaymentDialog.newInstance(paymentsModel, paymentPlanDTO);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, false);
    }

    public Fragment getChildFragment() {
        return paymentPlanEditFragment;
    }
}
