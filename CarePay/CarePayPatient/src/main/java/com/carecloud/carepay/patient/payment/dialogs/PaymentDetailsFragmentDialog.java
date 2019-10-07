package com.carecloud.carepay.patient.payment.dialogs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.ToolbarInterface;
import com.carecloud.carepay.patient.payment.fragments.PatientPartialPaymentDialog;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentPlanAmountDialog;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
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
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PaymentDetailsFragmentDialog extends BasePaymentDetailsFragmentDialog {

    private static final int MY_PERMISSIONS_STATEMENT_WRITE_EXTERNAL_STORAGE = 100;
    private PendingBalanceDTO selectedBalance;

    private boolean mustAddToExisting = false;
    private PatientStatementDTO finalStatement;
    private NumberFormat currencyFormat;
    boolean showStatementButton;

    /**
     * @param paymentsModel      the payment model
     * @param paymentPayload     the PendingBalancePayloadDTO
     * @param showPaymentButtons show payments button
     * @return new instance of a PaymentDetailsFragmentDialog
     */
    public static PaymentDetailsFragmentDialog newInstance(PaymentsModel paymentsModel,
                                                           PendingBalancePayloadDTO paymentPayload,
                                                           PendingBalanceDTO selectedBalance,
                                                           boolean showPaymentButtons,
                                                           boolean showStatementButton) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPayload);
        DtoHelper.bundleDto(args, selectedBalance);
        args.putBoolean("showPaymentButtons", showPaymentButtons);
        args.putBoolean("showStatementButton", showStatementButton);

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_payment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

            UserPracticeDTO practice = paymentReceiptModel.getPaymentPayload()
                    .getUserPractice(selectedBalance.getMetadata().getPracticeId());
            if (!StringUtil.isNullOrEmpty(practice.getPracticePhoto())) {
                PicassoHelper.get().loadImage(getContext(), view.findViewById(R.id.practiceImageView),
                        view.findViewById(R.id.avTextView), practice.getPracticePhoto());
            }

            ImageView dialogCloseHeader = view.findViewById(R.id.dialog_close_header);
            if (dialogCloseHeader != null) {
                dialogCloseHeader.setOnClickListener(view1 -> dismiss());
            }

            canMakePayments = paymentReceiptModel.getPaymentPayload()
                    .canMakePayments(selectedBalance.getMetadata().getPracticeId());
        }
        showStatementButton = getArguments().getBoolean("showStatementButton", false);

        setUpStatementButton(view);
        setUpDetails(view);
        if (paymentReceiptModel.getPaymentPayload().havePermissionsToMakePayments(selectedBalance
                .getMetadata().getPracticeId())) {
            setUpBottomSheet(view, canMakePayments);
        } else {
            view.findViewById(R.id.consolidatedPaymentButton).setVisibility(View.INVISIBLE);
        }
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
        if (statement != null && !statement.getStatements().isEmpty()
                && paymentReceiptModel.getPaymentPayload().canSeeStatement(selectedBalance.getMetadata().getPracticeId())) {
            View statementButton = view.findViewById(R.id.statement_button);
            statementButton.setVisibility(showStatementButton ? View.VISIBLE : View.GONE);
            finalStatement = statement;
            statementButton.setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PermissionChecker.PERMISSION_GRANTED && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_STATEMENT_WRITE_EXTERNAL_STORAGE);
                } else {
                    downloadStatementPdf();
                }

            });
        }
    }

    private void setUpDetails(View view) {
        if (paymentReceiptModel.getPaymentPayload().canViewBalanceDetails(selectedBalance.getMetadata().getPracticeId())) {
            RecyclerView paymentDetailsRecyclerView = view.findViewById(R.id.payment_receipt_details_view);
            paymentDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            PaymentItemsListAdapter adapter = new PaymentItemsListAdapter(getContext(),
                    paymentPayload.getDetails());
            paymentDetailsRecyclerView.setAdapter(adapter);
        }
    }

    private void setUpBottomSheet(View view, boolean canMakePayments) {
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

        View payTotalAmountContainer = view.findViewById(R.id.payTotalAmountContainer);
        payTotalAmountContainer.setOnClickListener(view1 -> {
            hideDialog(true);
            PatientPaymentMethodFragment fragment = PatientPaymentMethodFragment
                    .newInstance(paymentReceiptModel, paymentPayload.getAmount(), false);
            fragment.setOnBackPressedListener(() -> showDialog(true));
            callback.replaceFragment(fragment, true);
            logFullPaymentMixPanelEvent();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            ((ToolbarInterface) callback).displayToolbar(false, null);
        });

        View partialPaymentContainer = view.findViewById(R.id.partialPaymentContainer);
        partialPaymentContainer.setVisibility(isPartialPayAvailable(selectedBalance.getMetadata()
                .getPracticeId(), paymentPayload.getAmount())
                ? View.VISIBLE : View.GONE);
        partialPaymentContainer.setOnClickListener(view12 -> {
            PatientPartialPaymentDialog fragment = PatientPartialPaymentDialog
                    .newInstance(paymentReceiptModel, selectedBalance);
            fragment.setOnCancelListener(dialogInterface -> showDialog(true));
            callback.displayDialogFragment(fragment, true);
            hideDialog(true);
            MixPanelUtil.logEvent(getString(R.string.event_payment_make_partial_payment),
                    getString(R.string.param_practice_id),
                    selectedBalance.getMetadata().getPracticeId());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        View paymentPlanContainer = view.findViewById(R.id.paymentPlanContainer);
        paymentPlanContainer.setVisibility(isPaymentPlanAvailable(selectedBalance.getMetadata()
                .getPracticeId(), paymentPayload.getAmount())
                && paymentReceiptModel.getPaymentPayload().getDelegate() == null //TODO: SHMRK-9463 Take out last validation when MW handle PP
                ? View.VISIBLE : View.GONE);
        paymentPlanContainer.setOnClickListener(view13 -> {
            PendingBalanceDTO reducedBalancesItem = paymentReceiptModel.getPaymentPayload()
                    .reduceBalanceItems(selectedBalance, false);
            PatientPaymentPlanAmountDialog fragment = PatientPaymentPlanAmountDialog
                    .newInstance(paymentReceiptModel, reducedBalancesItem);
            fragment.setOnCancelListener(dialogInterface -> showDialog(true));
            callback.displayDialogFragment(fragment, true);
            hideDialog(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
        if (mustAddToExisting) {
            TextView paymentPlanTextView = paymentPlanContainer.findViewById(R.id.paymentPlanTextView);
            paymentPlanTextView.setText(Label.getLabel("payment_plan_add_existing"));
        }

        boolean showPaymentButtons = getArguments().getBoolean("showPaymentButtons", false);
        view.findViewById(R.id.consolidatedPaymentButton).setVisibility((showPaymentButtons && canMakePayments)
                ? View.VISIBLE : View.GONE);


        Button consolidatedPaymentButton = view.findViewById(R.id.consolidatedPaymentButton);
        consolidatedPaymentButton.setOnClickListener(v -> bottomSheetBehavior
                .setState(BottomSheetBehavior.STATE_EXPANDED));

        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));

        view.findViewById(R.id.payLaterContainer).setVisibility(View.GONE);

        TextView totalPatientResponsibilityValue = view.findViewById(R.id.totalPatientResponsibilityValue);
        totalPatientResponsibilityValue.setText(currencyFormat.format(paymentPayload.getAmount()));
    }

    private void logFullPaymentMixPanelEvent() {
        String[] params = {getString(R.string.param_balance_amount),
                getString(R.string.param_practice_id)
        };

        Object[] values = {paymentPayload.getAmount(),
                selectedBalance.getMetadata().getPracticeId()
        };
        MixPanelUtil.logEvent(getString(R.string.event_payment_make_full_payment), params, values);
    }

    private void downloadStatementPdf() {
        StatementDTO statementDTO = finalStatement.getStatements().get(0);
        String url = String.format("%s?%s=%s", paymentReceiptModel.getPaymentsMetadata()
                        .getPaymentsLinks().getPatientStatements().getUrl(), "statement_id",
                String.valueOf(statementDTO.getId()));
        String fileName = String.format("%s %s", "Statement - ", selectedBalance.getMetadata().getPracticeName());
        FileDownloadUtil.downloadPdf(getContext(), url, fileName, ".pdf", statementDTO.getStatementDate());
    }

    private boolean isPartialPayAvailable(String practiceId, double total) {
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

    private boolean isPaymentPlanAvailable(String practiceId, double balance) {
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
