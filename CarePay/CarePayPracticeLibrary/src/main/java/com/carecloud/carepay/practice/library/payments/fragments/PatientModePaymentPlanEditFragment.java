package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.ApplicationPreferences;
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
        getPaymentPlanSettings(ApplicationPreferences.getInstance().getPracticeId());
    }

    @Override
    protected void setupToolBar(View view) {
        View closeButton = view.findViewById(com.carecloud.carepaylibrary.R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callback.onDismissEditPaymentPlan(paymentsModel, paymentPlanDTO);
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
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        view.findViewById(R.id.bottomContainer).setVisibility(View.GONE);
    }
}