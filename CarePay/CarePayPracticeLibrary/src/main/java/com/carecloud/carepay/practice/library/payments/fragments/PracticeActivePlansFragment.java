package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.fragments.ActivePlansFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 2/13/18
 */

public class PracticeActivePlansFragment extends ActivePlansFragment {

    public static PracticeActivePlansFragment newInstance(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);

        PracticeActivePlansFragment fragment = new PracticeActivePlansFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupToolBar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        toolbar.setTitle("");
        TextView title = (TextView) toolbar.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_title);
        title.setText(Label.getLabel("payment_plan_active_plan"));

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
