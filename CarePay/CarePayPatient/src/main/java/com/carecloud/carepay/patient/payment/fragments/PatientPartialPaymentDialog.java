package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.carecloud.carepay.patient.base.ToolbarInterface;
import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 4/11/19.
 */
public class PatientPartialPaymentDialog extends PartialPaymentDialog {


    public static PatientPartialPaymentDialog newInstance(PaymentsModel paymentsDTO, PendingBalanceDTO selectedBalance) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        DtoHelper.bundleDto(args, selectedBalance);
        PatientPartialPaymentDialog dialog = new PatientPartialPaymentDialog();
        dialog.setArguments(args);
        return dialog;
    }

    protected void onPaymentClick(EditText enterPartialAmountEditText) {
        try {
            double amount = Double.parseDouble(enterPartialAmountEditText.getText().toString());
            createPaymentModel(amount);
//            getFragmentManager().popBackStackImmediate(PaymentDetailsFragmentDialog.class.getName(),
//                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            hideDialog(true);
            PatientPaymentMethodFragment fragment = PatientPaymentMethodFragment
                    .newInstance(paymentsDTO, amount, false);
            fragment.setOnBackPressedListener(new OnBackPressedInterface() {
                @Override
                public void onBackPressed() {
                    showDialog(true);
                }
            });
            payListener.replaceFragment(fragment, true);
            ((ToolbarInterface) payListener).displayToolbar(false, null);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Toast.makeText(getContext(), "Please enter valid amount!", Toast.LENGTH_LONG).show();
        }
    }
}
