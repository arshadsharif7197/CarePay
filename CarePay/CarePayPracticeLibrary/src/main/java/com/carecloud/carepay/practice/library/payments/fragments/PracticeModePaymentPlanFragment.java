package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

/**
 * Created by lmenendez on 4/17/18
 */

public class PracticeModePaymentPlanFragment extends PatientModePaymentPlanFragment {

    public static PracticeModePaymentPlanFragment newInstance(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);

        PracticeModePaymentPlanFragment fragment = new PracticeModePaymentPlanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupHeader(View view){
        super.setupHeader(view);
        TextView parameters = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.paymentPlanParametersTextView);
        if(enableAddToExisting()){
            parameters.setVisibility(View.GONE);
        }else {
            parameters.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected boolean enableAddToExisting() {
        return !paymentsModel.getPaymentPayload().getFilteredPlans(
                selectedBalance.getMetadata().getPracticeId()).isEmpty();
    }

    @Override
    protected boolean validateFields(boolean isUserInteraction) {
        int paymentDay = 0;
        try {
            paymentDay = Integer.parseInt(paymentDateOption.getName());
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        if (paymentDay < 1 || paymentDay > 30) {
            if (isUserInteraction) {
                setError(R.id.paymentDrawDayInputLayout, Label.getLabel("validation_required_field")
                        , isUserInteraction);
            }
            return false;
        } else {
            clearError(R.id.paymentDrawDayInputLayout);
        }

        if (StringUtil.isNullOrEmpty(numberPaymentsEditText.getText().toString())) {
            if (isUserInteraction) {
                setError(R.id.paymentMonthCountInputLayout, Label.getLabel("validation_required_field")
                        , isUserInteraction);
                return false;
            } else {
                clearError(R.id.paymentMonthCountInputLayout);
            }
        } else if (monthlyPaymentCount < 2) {
            setError(R.id.paymentMonthCountInputLayout,
                    String.format(Label.getLabel("payment_plan_min_months_error"),
                            String.valueOf(2))
                    , isUserInteraction);
            clearError(R.id.paymentAmountInputLayout);
            return false;
        } else {
            clearError(R.id.paymentMonthCountInputLayout);
        }

        if (StringUtil.isNullOrEmpty(monthlyPaymentEditText.getText().toString())) {
            if (isUserInteraction) {
                setError(R.id.paymentAmountInputLayout, Label.getLabel("validation_required_field")
                        , isUserInteraction);
                return false;
            } else {
                clearError(R.id.paymentAmountInputLayout);
            }
        } else {
            clearError(R.id.paymentAmountInputLayout);
        }

        return true;
    }

}
