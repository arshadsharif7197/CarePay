package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;

import com.carecloud.carepay.patient.base.ToolbarInterface;
import com.carecloud.carepaylibray.payments.fragments.OneTimePaymentDialog;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.Date;

/**
 * @author pjohnson on 4/2/19.
 */
public class PatientOneTimePaymentDialog extends OneTimePaymentDialog {

    /**
     * @param paymentsDTO    payment model
     * @param paymentPlanDTO payment plan
     */
    public static PatientOneTimePaymentDialog newInstance(PaymentsModel paymentsDTO,
                                                          PaymentPlanDTO paymentPlanDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        PatientOneTimePaymentDialog dialog = new PatientOneTimePaymentDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    protected void onStartOneTimePayment(PaymentsModel paymentsDTO, PaymentPlanDTO paymentPlanDTO) {
        hideDialog(true);
        PaymentPlanPaymentMethodFragment fragment = PaymentPlanPaymentMethodFragment
                .newInstance(paymentsDTO, paymentPlanDTO, false);
        fragment.setOnBackPressedListener(new OnBackPressedInterface() {
            @Override
            public void onBackPressed() {
                showDialog(true);
            }
        });
        callback.replaceFragment(fragment, true);
        ((ToolbarInterface) callback).displayToolbar(false, null);
        logPaymentPlanOneTimePaymentMixPanelEvent(paymentPlanDTO);
    }

    @Override
    protected void onScheduleOneTimePayment(PaymentsModel paymentsDTO, PaymentPlanDTO paymentPlanDTO, Date paymentDate) {
        hideDialog(true);
        PaymentPlanPaymentMethodFragment fragment = PaymentPlanPaymentMethodFragment
                .newInstance(paymentsDTO, paymentPlanDTO, false, paymentDate);
        fragment.setOnBackPressedListener(new OnBackPressedInterface() {
            @Override
            public void onBackPressed() {
                showDialog(true);
            }
        });
        callback.replaceFragment(fragment, true);
        ((ToolbarInterface) callback).displayToolbar(false, null);
    }
}
