package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.View;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.payments.fragments.ValidPlansFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 2/13/18
 */

public class PracticeValidPlansFragment extends ValidPlansFragment {

    public static PracticeValidPlansFragment newInstance(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, double amount){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);
        args.putDouble(KEY_PLAN_AMOUNT, amount);

        PracticeValidPlansFragment fragment = new PracticeValidPlansFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupToolBar(View view){
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
    public void onPaymentPlanItemSelected(PaymentPlanDTO paymentPlan) {
        super.onPaymentPlanItemSelected(paymentPlan);
        dismiss();
    }


}
