package com.carecloud.carepay.patient.payment.dialogs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.carecloud.carepaylibray.utils.FileDownloadUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PaymentDetailsFragmentDialog extends BasePaymentDetailsFragmentDialog {

    private static final int MY_PERMISSIONS_STATEMENT_WRITE_EXTERNAL_STORAGE = 100;
    private PendingBalanceDTO selectedBalance;

    private boolean mustAddToExisting = false;
    private PatientStatementDTO finalStatement;
    private NumberFormat currencyFormat;

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
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar_Fullscreen);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onInitialization(View view) {

        boolean canMakePayments = false;
        if (paymentReceiptModel != null) {
            String practiceName = selectedBalance.getMetadata().getPracticeName();
            String totalAmount = currencyFormat.format(paymentPayload.getAmount());
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

        setUpStatementButton(view);
        setUpDetails(view);
        setUpBottomSheet(view, canMakePayments);
    }

    private void setUpStatementButton(View view) {
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
            finalStatement = statement;
            statementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PermissionChecker.PERMISSION_GRANTED && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_STATEMENT_WRITE_EXTERNAL_STORAGE);
                    } else {
                        downloadStatementPdf();
                    }

                }
            });
        }
    }

    private void setUpDetails(View view) {
        RecyclerView paymentDetailsRecyclerView = ((RecyclerView) view
                .findViewById(R.id.payment_receipt_details_view));
        paymentDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PaymentItemsListAdapter adapter = new PaymentItemsListAdapter(getContext(),
                paymentPayload.getDetails());
        paymentDetailsRecyclerView.setAdapter(adapter);
    }

    private void setUpBottomSheet(View view, boolean canMakePayments) {
        View payTotalAmountContainer = view.findViewById(R.id.payTotalAmountContainer);
        payTotalAmountContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.onPayButtonClicked(paymentPayload.getAmount(), paymentReceiptModel);
            }
        });

        View partialPaymentContainer = view.findViewById(R.id.partialPaymentContainer);
        partialPaymentContainer.setVisibility(isPartialPayAvailable(selectedBalance.getMetadata().getPracticeId(), paymentPayload.getAmount())
                ? View.VISIBLE : View.GONE);
        partialPaymentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.onPartialPaymentClicked(paymentPayload.getAmount(), selectedBalance);
            }
        });

        View paymentPlanContainer = view.findViewById(R.id.paymentPlanContainer);
        paymentPlanContainer.setVisibility(isPaymentPlanAvailable(selectedBalance.getMetadata().getPracticeId(), paymentPayload.getAmount())
                ? View.VISIBLE : View.GONE);
        paymentPlanContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.onPaymentPlanAction(paymentReceiptModel);
            }
        });
        if (mustAddToExisting) {
            TextView paymentPlanTextView = (TextView) paymentPlanContainer.findViewById(R.id.paymentPlanTextView);
            paymentPlanTextView.setText(Label.getLabel("payment_plan_add_existing"));
        }

        boolean showPaymentButtons = getArguments().getBoolean("showPaymentButtons", false);
        if (showPaymentButtons && canMakePayments) {
            view.findViewById(R.id.consolidatedPaymentButton).setVisibility(View.VISIBLE);
        }

        final View shadow = view.findViewById(R.id.shadow);
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                shadow.setAlpha(slideOffset);
            }
        });
        Button consolidatedPaymentButton = (Button) view.findViewById(R.id.consolidatedPaymentButton);
        consolidatedPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        view.findViewById(R.id.payLaterContainer).setVisibility(View.GONE);

        TextView totalPatientResponsibilityValue = (TextView) view.findViewById(R.id.totalPatientResponsibilityValue);
        totalPatientResponsibilityValue.setText(currencyFormat.format(paymentPayload.getAmount()));
    }

    private void downloadStatementPdf() {
        StatementDTO statementDTO = finalStatement.getStatements().get(0);
        String url = String.format("%s?%s=%s", paymentReceiptModel.getPaymentsMetadata()
                        .getPaymentsLinks().getPatientStatements().getUrl(), "statement_id",
                String.valueOf(statementDTO.getId()));
        String fileName = String.format("%s %s", "Statement - ", selectedBalance.getMetadata().getPracticeName());
        FileDownloadUtil.downloadPdf(getContext(), url, fileName, ".pdf", statementDTO.getStatementDate());
    }

    @Override
    protected int getCancelImageResource() {
        return 0;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_dialog_payment_details;
    }

    protected boolean isPartialPayAvailable(String practiceId, double total) {
        if (practiceId != null) {
            PaymentsPayloadSettingsDTO payloadSettingsDTO = paymentReceiptModel.getPaymentPayload().getPaymentSetting(practiceId);
            if (practiceId.equals(payloadSettingsDTO.getMetadata().getPracticeId())) {
                if (payloadSettingsDTO.getPayload().getRegularPayments().isAllowPartialPayments()) {
                    double minBalance = payloadSettingsDTO.getPayload().getRegularPayments().getPartialPaymentsThreshold();
                    double minPayment = payloadSettingsDTO.getPayload().getRegularPayments().getMinimumPartialPaymentAmount();
                    return total >= minBalance && total >= minPayment;
                }
                return false;
            }
        }
        return true;
    }

    protected boolean isPaymentPlanAvailable(String practiceId, double balance) {
        if (practiceId != null) {
            PaymentsPayloadSettingsDTO payloadSettingsDTO = paymentReceiptModel.getPaymentPayload().getPaymentSetting(practiceId);
            PaymentsSettingsPaymentPlansDTO paymentPlanSettings = payloadSettingsDTO.getPayload().getPaymentPlans();
            if (!paymentPlanSettings.isPaymentPlansEnabled()) {
                return false;
            }

            double maxAllowablePayment = paymentReceiptModel.getPaymentPayload().getMaximumAllowablePlanAmount(practiceId);
            if (maxAllowablePayment > balance) {
                maxAllowablePayment = balance;
            }
            for (PaymentSettingsBalanceRangeRule rule : paymentPlanSettings.getBalanceRangeRules()) {
                if (maxAllowablePayment >= rule.getMinBalance().getValue() &&
                        maxAllowablePayment <= rule.getMaxBalance().getValue()) {
                    //found a valid rule that covers this balance
                    if (paymentReceiptModel.getPaymentPayload().getActivePlans(practiceId).isEmpty()) {
                        //don't already have an existing plan so this is the first plan
                        return true;
                    } else if (paymentPlanSettings.isCanHaveMultiple()) {
                        // already have a plan so need to see if I can create a new one
                        return true;
                    }
//                    break;//don't need to continue going through these rules
                }
//                    break;//don't need to continue going through these rules
            }

            //check if balance can be added to existing
            double minAllowablePayment = paymentReceiptModel.getPaymentPayload().getMinimumAllowablePlanAmount(practiceId);
            if (minAllowablePayment > balance) {
                minAllowablePayment = balance;
            }
            if (paymentPlanSettings.isAddBalanceToExisting() &&
                    !paymentReceiptModel.getPaymentPayload().getValidPlans(practiceId, minAllowablePayment).isEmpty()) {
                mustAddToExisting = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_STATEMENT_WRITE_EXTERNAL_STORAGE
                && (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            downloadStatementPdf();
        }
    }

}
