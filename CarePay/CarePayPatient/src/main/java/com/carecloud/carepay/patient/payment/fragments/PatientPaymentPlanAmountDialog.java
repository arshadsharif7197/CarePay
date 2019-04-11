package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.EditText;
import android.widget.Toast;

import com.carecloud.carepay.patient.base.ToolbarInterface;
import com.carecloud.carepay.patient.payment.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanAmountDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanFragment;
import com.carecloud.carepaylibray.payments.fragments.ValidPlansFragment;
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
            BaseDialogFragment fragment;
            if (paymentsModel.getPaymentPayload().mustAddToExisting(amount, selectedBalance)) {
                fragment = ValidPlansFragment.newInstance(paymentsModel, selectedBalance, amount);
                addExisting = true;
            } else {
                fragment = PaymentPlanFragment.newInstance(paymentsModel, selectedBalance, amount);
                fragment.setOnCancelListener(onDialogCancelListener);
            }
            logPaymentPlanStartedMixPanelEvent(addExisting);
            getFragmentManager().popBackStackImmediate(PaymentDetailsFragmentDialog.class.getName(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            callback.replaceFragment(fragment, true);
            ((ToolbarInterface) callback).displayToolbar(false, null);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Toast.makeText(getContext(), "Please enter valid amount!", Toast.LENGTH_LONG).show();
        }
    }
}
