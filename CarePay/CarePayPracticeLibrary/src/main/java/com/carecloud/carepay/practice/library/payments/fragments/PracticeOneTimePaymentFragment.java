package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.interfaces.OneTimePaymentInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by lmenendez on 2/8/18
 */

public class PracticeOneTimePaymentFragment extends PracticePartialPaymentDialogFragment {

    private PaymentPlanDTO paymentPlanDTO;
    private OneTimePaymentInterface callback;


    /**
     * @param paymentResultModel the payment model
     * @param owedAmount         fullAmount owed
     * @return an instance of PracticePartialPaymentDialogFragment
     */
    public static PracticeOneTimePaymentFragment newInstance(PaymentsModel paymentResultModel, double owedAmount, PaymentPlanDTO paymentPlanDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentResultModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putDouble(KEY_FULL_AMOUNT, owedAmount);
        PracticeOneTimePaymentFragment fragment = new PracticeOneTimePaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OneTimePaymentInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must implement OneTimePaymentInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, arguments);
        fullAmount = calculateFullAmount();
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        applyButton.setText(Label.getLabel("payment_plan_one_time_payment"));
    }

    @Override
    protected double getMinimumPayment(){
        double planTotal = paymentPlanDTO.getPayload().getAmount();
        PaymentSettingsBalanceRangeRule selectedRule = new PaymentSettingsBalanceRangeRule();
        PaymentsPayloadSettingsDTO payloadSettingsDTO = paymentsModel.getPaymentPayload().getPaymentSettings().get(0);
        for(PaymentSettingsBalanceRangeRule rule : payloadSettingsDTO.getPayload().getPaymentPlans().getBalanceRangeRules()){
            double minAmount = rule.getMinBalance().getValue();
            double maxAmount = rule.getMaxBalance().getValue();
            if(planTotal >= minAmount && planTotal <= maxAmount &&
                    minAmount > selectedRule.getMinBalance().getValue()){
                selectedRule = rule;
            }
        }
        return selectedRule.getMinPaymentRequired().getValue();
    }

    protected double calculateFullAmount() {
        return SystemUtil.safeSubtract(paymentPlanDTO.getPayload().getAmount(), paymentPlanDTO.getPayload().getAmountPaid());
    }

    @Override
    protected void onPaymentClick(double amount) {
        createPaymentModel(amount);
        callback.onStartOneTimePayment(paymentsModel, paymentPlanDTO);
        dismiss();
    }

    private void createPaymentModel(double amount){
        IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        if (postModel == null) {
            postModel = new IntegratedPaymentPostModel();
        }
        postModel.setAmount(amount);

        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);
    }


}
