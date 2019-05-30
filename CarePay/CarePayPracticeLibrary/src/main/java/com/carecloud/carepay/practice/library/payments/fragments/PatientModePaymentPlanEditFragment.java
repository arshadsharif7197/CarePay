package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanEditFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 12/02/18.
 */

public class PatientModePaymentPlanEditFragment extends PaymentPlanEditFragment {

    public static PatientModePaymentPlanEditFragment newInstance(PaymentsModel paymentsModel,
                                                                 PaymentPlanDTO paymentPlanDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);

        PatientModePaymentPlanEditFragment fragment = new PatientModePaymentPlanEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        paymentPlanBalanceRules = getPaymentPlanSettings(interval);
    }

    @Override
    protected void setupToolBar(View view) {
        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    @Override
    protected void setupHeader(View view) {
        super.setupHeader(view);
        TextView headerPaymentAmount = view.findViewById(R.id.headerPlanTotal);
        headerPaymentAmount.setText(currencyFormatter.format(paymentPlanAmount));

        TextView patientBalance = view.findViewById(R.id.patientBalance);
        patientBalance.setText(currencyFormatter.format(paymentPlanAmount));
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        if (view.findViewById(R.id.bottomContainer) != null) {
            view.findViewById(R.id.bottomContainer).setVisibility(View.GONE);
        }
    }

    @Override
    protected void setupButtons(View view) {
        super.setupButtons(view);
        view.findViewById(R.id.footer).setVisibility(View.GONE);
    }

    @Override
    protected Button getActionButton() {
        return editPaymentPlanButton;
    }

    @Override
    protected void showCancelPaymentPlanConfirmDialog(final boolean deletePaymentPlan) {
        String title = Label.getLabel("payment.cancelPaymentPlan.confirmation.popup.title");
        String message = Label.getLabel("payment.cancelPaymentPlan.confirmation.popup.message");
        if (deletePaymentPlan) {
            title = Label.getLabel("payment.deletePaymentPlan.confirmation.popup.title");
            message = Label.getLabel("payment.deletePaymentPlan.confirmation.popup.message");
        }
        ConfirmDialogFragment fragment = ConfirmDialogFragment.newInstance(title, message);
        fragment.setOnCancelListener(onDialogCancelListener);
        fragment.setCallback(new ConfirmationCallback() {
            @Override
            public void onConfirm() {
                cancelPaymentPlan(deletePaymentPlan);
            }
        });
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }

    @Override
    protected void onEditPaymentPlanPaymentMethod(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, true);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }
}
