package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 2/2/18
 */

public class PatientModePaymentPlanFragment extends PaymentPlanFragment {


    public static PatientModePaymentPlanFragment newInstance(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);

        PatientModePaymentPlanFragment fragment = new PatientModePaymentPlanFragment();
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
    protected void createPaymentPlan(){
        super.createPaymentPlan();
        if(validateFields(false)){
            dismiss();
        }
    }

    @Override
    protected void addBalanceToExisting(){
        super.addBalanceToExisting();
        dismiss();
    }


}