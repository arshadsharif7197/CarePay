package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.fragments.AddExistingPaymentPlanFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 2/13/18
 */

public class PatientModeAddExistingPaymentPlanFragment extends AddExistingPaymentPlanFragment {

    public static PatientModeAddExistingPaymentPlanFragment newInstance(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, PaymentPlanDTO existingPlan, double amount){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);
        DtoHelper.bundleDto(args, existingPlan);
        args.putDouble(KEY_PLAN_AMOUNT, amount);

        PatientModeAddExistingPaymentPlanFragment fragment = new PatientModeAddExistingPaymentPlanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupToolBar(View view) {
        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callback.onDismissPaymentPlan(paymentsModel);
            }
        });

    }

    @Override
    protected void setupHeader(View view) {
        super.setupHeader(view);
        TextView headerPaymentAmount = (TextView) view.findViewById(R.id.headerPlanTotal);
        headerPaymentAmount.setText(currencyFormatter.format(paymentPlanAmount));

        TextView patientBalance = (TextView) view.findViewById(R.id.patientBalance);
        patientBalance.setText(currencyFormatter.format(paymentPlanAmount));
    }

    @Override
    protected void onPlanEdited(WorkflowDTO workflowDTO){
        super.onPlanEdited(workflowDTO);
        dismiss();
    }


}
