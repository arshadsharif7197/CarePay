package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.payments.fragments.AddExistingPaymentPlanFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 2/13/18
 */

public class PatientModeAddExistingPaymentPlanFullFragment extends AddExistingPaymentPlanFragment {

    public static PatientModeAddExistingPaymentPlanFullFragment newInstance(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, PaymentPlanDTO existingPlan, double amount){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);
        DtoHelper.bundleDto(args, existingPlan);
        args.putDouble(KEY_PLAN_AMOUNT, amount);

        PatientModeAddExistingPaymentPlanFullFragment fragment = new PatientModeAddExistingPaymentPlanFullFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_plan_full, container, false);
    }

    @Override
    protected void setupToolBar(View view) {
        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
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

}
