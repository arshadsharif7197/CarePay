package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

/**
 * Created by lmenendez on 4/17/18
 */
@Deprecated
public class PracticeModeAddExistingPaymentPlanFragment extends PatientModeAddExistingPaymentPlanFragment {

    public static PracticeModeAddExistingPaymentPlanFragment newInstance(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, PaymentPlanDTO existingPlan, double amount){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);
        DtoHelper.bundleDto(args, existingPlan);
        args.putDouble(KEY_PLAN_AMOUNT, amount);

        PracticeModeAddExistingPaymentPlanFragment fragment = new PracticeModeAddExistingPaymentPlanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupHeader(View view){
        super.setupHeader(view);
        TextView parameters = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.paymentPlanParametersTextView);
        parameters.setVisibility(View.INVISIBLE);
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

        if (StringUtil.isNullOrEmpty(installmentsEditText.getText().toString())) {
            if (isUserInteraction) {
                setError(R.id.paymentMonthCountInputLayout, Label.getLabel("validation_required_field")
                        , isUserInteraction);
                return false;
            } else {
                clearError(R.id.paymentMonthCountInputLayout);
            }
        } else if (installments < 2) {
            setError(R.id.paymentMonthCountInputLayout,
                    String.format(Label.getLabel("payment_plan_min_months_error_temporal"),
                            String.valueOf(2))
                    , isUserInteraction);
            clearError(R.id.paymentAmountInputLayout);
            return false;
        } else {
            clearError(R.id.paymentMonthCountInputLayout);
        }

        if (StringUtil.isNullOrEmpty(amountPaymentEditText.getText().toString())) {
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
