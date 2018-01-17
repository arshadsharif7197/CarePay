package com.carecloud.carepay.patient.payment.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adapters.PaymentItemsListAdapter;
import com.carecloud.carepaylibray.customdialogs.BasePaymentDetailsFragmentDialog;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

public class PaymentDetailsFragmentDialog extends BasePaymentDetailsFragmentDialog {

    private PendingBalanceDTO selectedBalance;

    /**
     * @param paymentsModel  the payment model
     * @param paymentPayload the PendingBalancePayloadDTO
     * @return new instance of a PaymentDetailsFragmentDialog
     */
    public static PaymentDetailsFragmentDialog newInstance(PaymentsModel paymentsModel,
                                                           PendingBalancePayloadDTO paymentPayload,
                                                           PendingBalanceDTO selectedBalance) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPayload);
        DtoHelper.bundleDto(args, selectedBalance);

        PaymentDetailsFragmentDialog dialog = new PaymentDetailsFragmentDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        selectedBalance = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, getArguments());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onInitialization(View view) {
        view.findViewById(R.id.closeViewLayout).setVisibility(View.GONE);
        Button payNowButton = (Button) view.findViewById(R.id.payment_details_pay_now_button);
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPayButtonClicked(paymentPayload.getAmount(), paymentReceiptModel);
                dismiss();
            }
        });

        if (paymentReceiptModel != null) {
            String practiceName = selectedBalance.getMetadata().getPracticeName();
            String totalAmount = StringUtil.getFormattedBalanceAmount(paymentPayload.getAmount());
            ((TextView) view.findViewById(R.id.payment_details_total_paid)).setText(totalAmount);
            ((TextView) view.findViewById(R.id.payment_receipt_title)).setText(practiceName);
            ((TextView) view.findViewById(R.id.payment_receipt_total_label))
                    .setText(Label.getLabel("payment_details_patient_balance_label"));
            ((TextView) view.findViewById(R.id.payment_receipt_total_value)).setText(totalAmount);
            ((TextView) view.findViewById(R.id.avTextView)).setText(StringUtil.getShortName(practiceName));

            payNowButton.setText(Label.getLabel("payment_details_pay_now"));

            ImageView dialogCloseHeader = (ImageView) view.findViewById(R.id.dialog_close_header);
            if (dialogCloseHeader != null) {
                dialogCloseHeader.setVisibility(View.VISIBLE);
                dialogCloseHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
            }

            boolean canMakePayments = paymentReceiptModel.getPaymentPayload().canMakePayments(selectedBalance.getMetadata().getPracticeId());
            payNowButton.setVisibility(canMakePayments ? View.VISIBLE : View.GONE);

        }

        RecyclerView paymentDetailsRecyclerView = ((RecyclerView) view
                .findViewById(R.id.payment_receipt_details_view));
        paymentDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PaymentItemsListAdapter adapter = new PaymentItemsListAdapter(getContext(),
                paymentPayload.getDetails());
        paymentDetailsRecyclerView.setAdapter(adapter);
    }

    @Override
    protected int getCancelImageResource() {
        return 0;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_patient_dialog_payment_details;
    }

}
