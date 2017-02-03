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
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.payments.adapter.PaymentDetailsListAdapter;
import com.carecloud.carepaylibray.payments.models.PaymentDetailsItemDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentAmountReceiptDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private PaymentsModel paymentReceiptModel;
    private PaymentsModel intakeBundle;

    /**
     * Constructor.
     *
     * @param context             context
     * @param paymentReceiptModel model
     */
    public PaymentAmountReceiptDialog(Context context, PaymentsModel paymentReceiptModel, PaymentsModel intakeBundle) {
        super(context);
        this.context = context;
        this.paymentReceiptModel = paymentReceiptModel;
        this.intakeBundle = intakeBundle;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_practice_payment_receipt);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        onInitialization();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.payment_receipt_share_button) {
            onSaveButton();
        }
    }

    private void onInitialization() {
        Button shareReceiptButton = (Button) findViewById(R.id.payment_receipt_share_button);
        shareReceiptButton.setOnClickListener(this);

        PaymentsLabelDTO paymentsLabel = paymentReceiptModel.getPaymentsMetadata().getPaymentsLabel();
        if (paymentsLabel != null) {
            ((TextView) findViewById(R.id.receipt_no_label)).setText(paymentsLabel.getPaymentReceiptNoLabel());
            ((TextView) findViewById(R.id.payment_receipt_type_label)).setText(paymentsLabel.getPaymentReceiptPaymentType());

            String receiptDate = DateUtil.getInstance().setDateRaw(new Date().toString()).toStringWithFormatMmSlashDdSlashYyyy();
            ((TextView) findViewById(R.id.payment_receipt_date_label)).setText(receiptDate);
            ((TextView) findViewById(R.id.payment_receipt_practice_title)).setText(paymentsLabel.getPaymentReceiptTitle());

            shareReceiptButton.setText(paymentsLabel.getPaymentReceiptSaveReceipt());
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

        RecyclerView paymentDetailsRecyclerView = ((RecyclerView) findViewById(R.id.payment_receipt_details_view));
        paymentDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        PaymentDetailsListAdapter adapter = new PaymentDetailsListAdapter(context, true, detailsList, paymentsLabel);
        paymentDetailsRecyclerView.setAdapter(adapter);
    }

    private void onSaveButton() {
        cancel();
        if(context instanceof PatientModeCheckinActivity){
            ((PatientModeCheckinActivity)context).getPaymentInformation(new Gson().toJson(intakeBundle,PaymentsModel.class));
        }
    }
}
