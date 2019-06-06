package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.carecloud.carepay.patient.base.ToolbarInterface;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanAmountDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 4/2/19.
 */
public class PatientPaymentPlanAmountDialog extends PaymentPlanAmountDialog {

    public static PatientPaymentPlanAmountDialog newInstance(PaymentsModel paymentsModel,
                                                             PendingBalanceDTO selectedBalance) {

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);
        PatientPaymentPlanAmountDialog dialog = new PatientPaymentPlanAmountDialog();
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    protected void onPaymentClick(EditText enterPartialAmountEditText) {
        try {
            double amount = Double.parseDouble(enterPartialAmountEditText.getText().toString());
            boolean addExisting = false;
            hideDialog(true);
            BaseDialogFragment fragment;
            if (paymentsModel.getPaymentPayload().mustAddToExisting(amount, selectedBalance)) {
                fragment = PatientValidPlansFragment.newInstance(paymentsModel, selectedBalance, amount);
                addExisting = true;
            } else {
                fragment = PatientPaymentPlanFragment.newInstance(paymentsModel, selectedBalance, amount);
                fragment.setOnCancelListener(onDialogCancelListener);
            }
            fragment.setOnBackPressedListener(new OnBackPressedInterface() {
                @Override
                public void onBackPressed() {
                    showDialog(true);
                }
            });
            logPaymentPlanStartedMixPanelEvent(addExisting);
            callback.replaceFragment(fragment, true);
            if (callback instanceof ToolbarInterface) {
                ((ToolbarInterface) callback).displayToolbar(false, null);
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Toast.makeText(getContext(), "Please enter valid amount!", Toast.LENGTH_LONG).show();
        }
    }
}
