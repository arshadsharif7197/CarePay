package com.carecloud.carepay.practice.library.payments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.payments.adapter.PaymentDetailsListAdapter;
import com.carecloud.carepaylibray.payments.models.PaymentDetailsItemDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

import java.util.ArrayList;
import java.util.List;

public class PaymentDetailsDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private PaymentsModel paymentReceiptModel;
    private PayNowClickListener listener;

    /**
     * Constructor.
     *
     * @param context             context
     * @param paymentReceiptModel model
     * @param listener listener
     */
    public PaymentDetailsDialog(Context context, PaymentsModel paymentReceiptModel, PayNowClickListener listener) {
        super(context);
        this.context = context;
        this.paymentReceiptModel = paymentReceiptModel;
        this.listener = listener;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.carecloud.carepaylibrary.R.layout.dialog_payment_details);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        onInitialization();
    }

    private void onInitialization() {
        ImageView dialogCloseHeader = (ImageView) findViewById(com.carecloud.carepaylibrary.R.id.dialog_close_header);
        dialogCloseHeader.setOnClickListener(this);

        Button payNowButton = (Button) findViewById(com.carecloud.carepaylibrary.R.id.payment_details_pay_now_button);
        payNowButton.setOnClickListener(this);
        PaymentsLabelDTO paymentsLabel = paymentReceiptModel.getPaymentsMetadata().getPaymentsLabel();
        if (paymentsLabel != null) {
            ((TextView) findViewById(com.carecloud.carepaylibrary.R.id.payment_details_total_paid)).setText("");

            ((TextView) findViewById(com.carecloud.carepaylibrary.R.id.payment_receipt_title))
                    .setText(paymentsLabel.getPaymentReceiptTitle());
            ((TextView) findViewById(com.carecloud.carepaylibrary.R.id.payment_receipt_total_label))
                    .setText(paymentsLabel.getPaymentDetailsPatientBalanceLabel());
            ((TextView) findViewById(com.carecloud.carepaylibrary.R.id.payment_receipt_total_value)).setText("");

            payNowButton.setText(paymentsLabel.getPaymentDetailsPayNow());
        }

        // Temporary added till endpoint gets ready
        String[] labels = {"Deluxe Frame", "Sph Cyl Bif Pl To 400", "Eye Exam. New Patient", "Refraction"};
        String[] values = {"$69.00", "$99.00", "$77.01", "$49.00"};

        List<PaymentDetailsItemDTO> detailsList = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            PaymentDetailsItemDTO paymentDetails = new PaymentDetailsItemDTO();
            paymentDetails.setLabel(labels[i]);
            paymentDetails.setValue(values[i]);
            detailsList.add(paymentDetails);
        }

        RecyclerView paymentDetailsRecyclerView = ((RecyclerView) findViewById(com.carecloud.carepaylibrary.R.id.payment_receipt_details_view));
        paymentDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        PaymentDetailsListAdapter adapter = new PaymentDetailsListAdapter(context, false, detailsList,paymentsLabel);
        paymentDetailsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == com.carecloud.carepaylibrary.R.id.dialog_close_header) {
            cancel();
        } else if (viewId == com.carecloud.carepaylibrary.R.id.payment_details_pay_now_button) {
            listener.onPayNowButtonClicked();
            dismiss();
        }
    }

    public interface PayNowClickListener {
        void onPayNowButtonClicked();
    }
}
