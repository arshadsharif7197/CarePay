package com.carecloud.carepay.patient.payment.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adapters.PaymentItemsListAdapter;
import com.carecloud.carepaylibray.customdialogs.BasePaymentDetailsFragmentDialog;
import com.carecloud.carepaylibray.payments.models.PatientStatementDTO;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsSettingsPaymentPlansDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.StatementDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.PdfUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

public class PaymentDetailsFragmentDialog extends BasePaymentDetailsFragmentDialog {

    private PendingBalanceDTO selectedBalance;

    private boolean mustAddToExisting = false;

    /**
     * @param paymentsModel      the payment model
     * @param paymentPayload     the PendingBalancePayloadDTO
     * @param showPaymentButtons show payments button
     * @return new instance of a PaymentDetailsFragmentDialog
     */
    public static PaymentDetailsFragmentDialog newInstance(PaymentsModel paymentsModel,
                                                           PendingBalancePayloadDTO paymentPayload,
                                                           PendingBalanceDTO selectedBalance,
                                                           boolean showPaymentButtons) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPayload);
        DtoHelper.bundleDto(args, selectedBalance);
        args.putBoolean("showPaymentButtons", showPaymentButtons);

        PaymentDetailsFragmentDialog dialog = new PaymentDetailsFragmentDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        selectedBalance = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, getArguments());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onInitialization(View view) {
        View payNowButton = view.findViewById(R.id.payment_details_pay_now_button);
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.onPayButtonClicked(paymentPayload.getAmount(), paymentReceiptModel);
            }
        });

        View partialPaymentButton = view.findViewById(R.id.make_partial_payment_button);
        partialPaymentButton.setVisibility(isPartialPayAvailable(selectedBalance.getMetadata().getPracticeId(), paymentPayload.getAmount())
                ? View.VISIBLE : View.GONE);
        partialPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.onPartialPaymentClicked(paymentPayload.getAmount(), selectedBalance);
            }
        });

        TextView paymentPlanButton = (TextView) view.findViewById(R.id.createPaymentPlanButton);
        paymentPlanButton.setVisibility(isPaymentPlanAvailable(selectedBalance.getMetadata().getPracticeId(), paymentPayload.getAmount())
                ? View.VISIBLE : View.GONE);
        paymentPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.onPaymentPlanAction(paymentReceiptModel);
            }
        });
        if(mustAddToExisting) {
            paymentPlanButton.setText(Label.getLabel("payment_plan_add_existing"));
        }

        boolean canMakePayments = false;
        if (paymentReceiptModel != null) {
            String practiceName = selectedBalance.getMetadata().getPracticeName();
            String totalAmount = StringUtil.getFormattedBalanceAmount(paymentPayload.getAmount());
            ((TextView) view.findViewById(R.id.payment_details_total_paid)).setText(totalAmount);
            ((TextView) view.findViewById(R.id.payment_receipt_title)).setText(practiceName);
            ((TextView) view.findViewById(R.id.payment_receipt_total_label))
                    .setText(Label.getLabel("payment_details_patient_balance_label"));
            ((TextView) view.findViewById(R.id.payment_receipt_total_value)).setText(totalAmount);
            ((TextView) view.findViewById(R.id.avTextView)).setText(StringUtil.getShortName(practiceName));

            ImageView dialogCloseHeader = (ImageView) view.findViewById(R.id.dialog_close_header);
            if (dialogCloseHeader != null) {
                dialogCloseHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
            }

            canMakePayments = paymentReceiptModel.getPaymentPayload()
                    .canMakePayments(selectedBalance.getMetadata().getPracticeId());
        }

        RecyclerView paymentDetailsRecyclerView = ((RecyclerView) view
                .findViewById(R.id.payment_receipt_details_view));
        paymentDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PaymentItemsListAdapter adapter = new PaymentItemsListAdapter(getContext(),
                paymentPayload.getDetails());
        paymentDetailsRecyclerView.setAdapter(adapter);

        boolean showPaymentButtons = getArguments().getBoolean("showPaymentButtons", false);
        if (showPaymentButtons && canMakePayments) {
            view.findViewById(R.id.paymentButtonsContainer).setVisibility(View.VISIBLE);
            view.findViewById(R.id.planButtonsContainer).setVisibility(View.VISIBLE);
        }

        List<PatientStatementDTO> patientStatementList = paymentReceiptModel.getPaymentPayload().getPatientStatements();
        PatientStatementDTO statement = null;
        for (PatientStatementDTO patientStatementDTO : patientStatementList) {
            if (patientStatementDTO.getMetadata().getPracticeId()
                    .equals(selectedBalance.getMetadata().getPracticeId())) {
                statement = patientStatementDTO;
                break;
            }
        }
        if (statement != null && !statement.getStatements().isEmpty()) {
            View statementButton = view.findViewById(R.id.statement_button);
            statementButton.setVisibility(View.VISIBLE);
            final PatientStatementDTO finalStatement = statement;
            statementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadStatementPdf(finalStatement.getStatements().get(0));
                }
            });
        }
    }

    private void downloadStatementPdf(StatementDTO statementDTO) {
        String url = String.format("%s?%s=%s", paymentReceiptModel.getPaymentsMetadata()
                        .getPaymentsLinks().getPatientStatements().getUrl(), "statement_id",
                String.valueOf(statementDTO.getId()));
        String fileName = String.format("%s %s", "Statement - ", selectedBalance.getMetadata().getPracticeName());
        PdfUtil.downloadPdf(getContext(), url, fileName, ".pdf", statementDTO.getStatementDate());
    }

    @Override
    protected int getCancelImageResource() {
        return 0;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_patient_dialog_payment_details;
    }

    protected boolean isPartialPayAvailable(String practiceId, double total) {
        if (practiceId != null) {
            for (PaymentsPayloadSettingsDTO payloadSettingsDTO : paymentReceiptModel.getPaymentPayload().getPaymentSettings()) {
                if (practiceId.equals(payloadSettingsDTO.getMetadata().getPracticeId())) {
                    if (payloadSettingsDTO.getPayload().getRegularPayments().isAllowPartialPayments()) {
                        double minBalance = payloadSettingsDTO.getPayload().getRegularPayments().getPartialPaymentsThreshold();
                        double minPayment = payloadSettingsDTO.getPayload().getRegularPayments().getMinimumPartialPaymentAmount();
                        return total >= minBalance && total >= minPayment;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean isPaymentPlanAvailable(String practiceId, double balance) {
        if (practiceId != null) {
            for (PaymentsPayloadSettingsDTO payloadSettingsDTO : paymentReceiptModel.getPaymentPayload().getPaymentSettings()) {
                if (practiceId.equals(payloadSettingsDTO.getMetadata().getPracticeId())) {
                    PaymentsSettingsPaymentPlansDTO paymentPlanSettings = payloadSettingsDTO.getPayload().getPaymentPlans();
                    if (paymentPlanSettings.isPaymentPlansEnabled()) {
                        for (PaymentSettingsBalanceRangeRule rule : paymentPlanSettings.getBalanceRangeRules()) {
                            if (balance >= rule.getMinBalance().getValue() &&
                                    balance <= rule.getMaxBalance().getValue()) {
                                if(paymentReceiptModel.getPaymentPayload().getPatientPaymentPlans().isEmpty()){
                                    return true;
                                }else if(paymentPlanSettings.isCanHaveMultiple()){//need to check if multiple plans is enabled
                                    return true;
                                }else if(paymentPlanSettings.isAddBalanceToExisting()){//check if balance can be added to existing
                                    mustAddToExisting = true;
                                    return true;
                                }
                                return false;
                            }
                        }

                    }
                }
            }
        }
        return false;
    }

}
