package com.carecloud.carepay.practice.library.payments.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.PaymentItemsListAdapter;
import com.carecloud.carepaylibray.base.IApplicationSession;
import com.carecloud.carepaylibray.customdialogs.BasePaymentDetailsFragmentDialog;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

public class PaymentDetailsFragmentDialog extends BasePaymentDetailsFragmentDialog {


    /**
     * @param paymentsModel  the payment model
     * @param paymentPayload the PendingBalancePayloadDTO
     * @return new instance of a PaymentDetailsFragmentDialog
     */
    public static PaymentDetailsFragmentDialog newInstance(PaymentsModel paymentsModel,
                                                           PendingBalancePayloadDTO paymentPayload) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPayload);

        PaymentDetailsFragmentDialog dialog = new PaymentDetailsFragmentDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitialization(view);
    }

    protected void onInitialization(View view) {

        Button payNowButton = (Button) view.findViewById(R.id.payment_details_pay_now_button);
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onPayButtonClicked(paymentPayload.getAmount(), paymentReceiptModel);
                dismiss();
            }
        });

        if (paymentReceiptModel != null) {
            String totalAmount = StringUtil.getFormattedBalanceAmount(paymentPayload.getAmount());
            String amountBalanceLabel = Label.getLabel("payment_details_patient_balance_label");
            String name = paymentReceiptModel.getPaymentPayload().getPatientBalances().get(0).getDemographics().getPayload().getPersonalDetails().getFirstName();
            String lastname = paymentReceiptModel.getPaymentPayload().getPatientBalances().get(0).getDemographics().getPayload().getPersonalDetails().getLastName();

            ((TextView) view.findViewById(R.id.patient_full_name)).setText(name + " " + lastname);
            ((TextView) view.findViewById(R.id.payment_details_total_paid)).setText(amountBalanceLabel + " " + totalAmount);
            ((TextView) view.findViewById(R.id.avTextView)).setText(StringUtil.getShortName(name + " " + lastname));

            payNowButton.setText(Label.getLabel("payment_details_pay_now"));

            ImageView dialogCloseHeader;

            ApplicationMode.ApplicationType appMode = ((IApplicationSession) getContext().getApplicationContext()).getApplicationMode().getApplicationType();
            if (appMode == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE || appMode == ApplicationMode.ApplicationType.PRACTICE) {

                dialogCloseHeader = (ImageView) view.findViewById(R.id.payment_close_button);
                if (dialogCloseHeader != null) {
                    dialogCloseHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                        }
                    });
                    view.findViewById(R.id.payment_close_Layout).setVisibility(View.VISIBLE);
                }
            } else {
                dialogCloseHeader = (ImageView) view.findViewById(R.id.dialog_close_header);
                if (dialogCloseHeader != null) {
                    dialogCloseHeader.setVisibility(View.VISIBLE);
                    dialogCloseHeader.setOnClickListener(this);
                }

            }
        }

        RecyclerView paymentDetailsRecyclerView = ((RecyclerView) view.findViewById(R.id.payment_receipt_details_view));
        paymentDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PaymentItemsListAdapter adapter = new PaymentItemsListAdapter(getContext(), paymentPayload.getDetails());
        paymentDetailsRecyclerView.setAdapter(adapter);
    }

    @Override
    protected int getCancelImageResource() {
        return 0;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_dialog_payment_details;
    }

}
