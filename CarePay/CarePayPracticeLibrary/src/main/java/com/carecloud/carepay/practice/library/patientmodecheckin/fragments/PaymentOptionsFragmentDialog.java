package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsSettingsPaymentPlansDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author pjohnson on 11/20/18.
 */
public class PaymentOptionsFragmentDialog extends BaseDialogFragment {

    public final static int PAYMENT_OPTION_TOTAL_AMOUNT = 100;
    public final static int PAYMENT_OPTION_PARTIAL_AMOUNT = 101;
    public final static int PAYMENT_OPTION_PAYMENT_PLAN = 102;
    public final static int PAYMENT_OPTION_PAY_LATER = 103;

    private PaymentOptionsInterface callback;
    private PaymentsModel paymentsModel;

    public static PaymentOptionsFragmentDialog newInstance(PaymentsModel paymentModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentModel);
        PaymentOptionsFragmentDialog fragment = new PaymentOptionsFragmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_options, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View closeButton = findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        List<PendingBalanceDTO> paymentList = paymentsModel.getPaymentPayload()
                .getPatientBalances().get(0).getBalances();
        double total = 0;
        double nonBalanceTotal = 0;
        for (PendingBalancePayloadDTO payment : paymentList.get(0).getPayload()) {
            total = SystemUtil.safeAdd(total, payment.getAmount());
            if (!payment.getType().equals(PendingBalancePayloadDTO.PATIENT_BALANCE)) {
                //not an amount that can be added to a plan
                nonBalanceTotal = SystemUtil.safeAdd(nonBalanceTotal, payment.getAmount());
            }
        }

        final PendingBalanceDTO selectedBalance = paymentsModel.getPaymentPayload()
                .getPatientBalances().get(0).getBalances().get(0);
        if (total > 0) {
            TextView balanceTextView = view.findViewById(R.id.balanceTextView);
            NumberFormat numForm = NumberFormat.getCurrencyInstance(Locale.US);
            balanceTextView.setText(numForm.format(total));
            View payTotalAmountContainer = view.findViewById(R.id.payTotalAmountContainer);
            payTotalAmountContainer.setVisibility(View.VISIBLE);
            payTotalAmountContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    callback.onOptionSelected(PAYMENT_OPTION_TOTAL_AMOUNT);
                }
            });

            View partialPaymentContainer = view.findViewById(R.id.partialPaymentContainer);
            partialPaymentContainer.setVisibility(isPartialPayAvailable(selectedBalance
                    .getMetadata().getPracticeId(), total) ? View.VISIBLE : View.GONE);
            partialPaymentContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    callback.onOptionSelected(PAYMENT_OPTION_PARTIAL_AMOUNT);
                }
            });

        }

        boolean paymentPlanEnabled = !paymentsModel.getPaymentPayload().isPaymentPlanCreated() &&
                isPaymentPlanAvailable(selectedBalance.getMetadata().getPracticeId(), total, nonBalanceTotal);
        View paymentPlanContainer = view.findViewById(R.id.paymentPlanContainer);
        paymentPlanContainer.setVisibility(paymentPlanEnabled ? View.VISIBLE : View.GONE);
        paymentPlanContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callback.onOptionSelected(PAYMENT_OPTION_PAYMENT_PLAN);
            }
        });

        if (!paymentPlanEnabled
                && mustAddToExistingPlan(selectedBalance.getMetadata().getPracticeId(), total, nonBalanceTotal)) {
            TextView paymentPlanTextView = paymentPlanContainer.findViewById(R.id.paymentPlanTextView);
            paymentPlanTextView.setText(Label.getLabel("payment_plan_add_existing_short"));
        }
    }

    private boolean isPaymentPlanAvailable(String practiceId, double balance, double nonBalanceTotal) {
        double adjustedBalance = SystemUtil.safeSubtract(balance, nonBalanceTotal);
        if (adjustedBalance <= 0) {
            return false;
        }
        PaymentsPayloadSettingsDTO payloadSettingsDTO = paymentsModel.getPaymentPayload().getPaymentSetting(practiceId);
        PaymentsSettingsPaymentPlansDTO paymentPlanSettings = payloadSettingsDTO.getPayload().getPaymentPlans();
        if (!paymentPlanSettings.isPaymentPlansEnabled()) {
            return false;
        }

        double maxAllowablePayment = paymentsModel.getPaymentPayload().getMaximumAllowablePlanAmount(practiceId);
        if (maxAllowablePayment > adjustedBalance) {
            maxAllowablePayment = adjustedBalance;
        }
        for (PaymentSettingsBalanceRangeRule rule : paymentPlanSettings.getBalanceRangeRules()) {
            if (maxAllowablePayment >= rule.getMinBalance().getValue() &&
                    maxAllowablePayment <= rule.getMaxBalance().getValue()) {
                //found a valid rule that covers this balance
                if (paymentsModel.getPaymentPayload().getActivePlans(practiceId).isEmpty()) {
                    //don't already have an existing plan so this is the first plan
                    return true;
                } else if (paymentPlanSettings.isCanHaveMultiple()) {
                    // already have a plan so need to see if I can create a new one
                    return true;
                }
                break;//don't need to continue going through these rules
            }
        }

        return false;
    }

    private boolean mustAddToExistingPlan(String practiceId, double balance, double nonBalanceTotal) {
        PaymentsPayloadSettingsDTO payloadSettingsDTO = paymentsModel.getPaymentPayload().getPaymentSetting(practiceId);
        PaymentsSettingsPaymentPlansDTO paymentPlanSettings = payloadSettingsDTO.getPayload().getPaymentPlans();

        double adjustedBalance = SystemUtil.safeSubtract(balance, nonBalanceTotal);
        if (!paymentPlanSettings.isPaymentPlansEnabled() || adjustedBalance <= 0) {
            return false;
        }
        //check if balance can be added to existing
        double minAllowablePayment = paymentsModel.getPaymentPayload().getMinimumAllowablePlanAmount(practiceId);
        if (minAllowablePayment > adjustedBalance) {
            minAllowablePayment = adjustedBalance;
        }
        return paymentPlanSettings.isAddBalanceToExisting() &&
                !paymentsModel.getPaymentPayload().getValidPlans(practiceId, minAllowablePayment).isEmpty();
    }


    protected boolean isPartialPayAvailable(String practiceId, double total) {
        PaymentsPayloadSettingsDTO payloadSettingsDTO = paymentsModel.getPaymentPayload().getPaymentSetting(practiceId);
        if (payloadSettingsDTO.getPayload().getRegularPayments().isAllowPartialPayments()) {
            double minBalance = payloadSettingsDTO.getPayload().getRegularPayments().getPartialPaymentsThreshold();
            double minPayment = payloadSettingsDTO.getPayload().getRegularPayments().getMinimumPartialPaymentAmount();
            return total >= minBalance && total >= minPayment;
        }
        return false;
    }

    public void setCallback(PaymentOptionsInterface callback) {
        this.callback = callback;
    }

    public interface PaymentOptionsInterface {
        void onOptionSelected(int option);
    }
}
