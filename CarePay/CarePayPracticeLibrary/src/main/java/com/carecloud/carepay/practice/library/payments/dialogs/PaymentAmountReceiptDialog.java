package com.carecloud.carepay.practice.library.payments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class PaymentAmountReceiptDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private PaymentsModel paymentReceiptModel;

    /**
     * Constructor.
     *
     * @param context             context
     * @param paymentReceiptModel model
     */
    public PaymentAmountReceiptDialog(Context context, PaymentsModel paymentReceiptModel) {
        super(context);
        this.context = context;
        this.paymentReceiptModel = paymentReceiptModel;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_payment_amount_receipt);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setAttributes(params);

        onInitialization();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialog_close_header) {
            cancel();
        } else if (viewId == R.id.payment_receipt_location) {
            //address will add after model come
            onMapView("", "");
        } else if (viewId == R.id.payment_receipt_call) {
            //phoneCall will add after model come
            onPhoneCall("");
        } else if (viewId == R.id.payment_receipt_save) {
            onSaveButton();
        } else if (viewId == R.id.payment_receipt_share) {
            onShareButton();
        }
    }

    private void onInitialization() {
        ImageView dialogCloseHeader = (ImageView) findViewById(R.id.dialog_close_header);
        dialogCloseHeader.setOnClickListener(this);

        ImageView paymentReceiptLocationImageView = (ImageView) findViewById(R.id.payment_receipt_location);
        paymentReceiptLocationImageView.setOnClickListener(this);

        ImageView paymentReceiptDialImageView = (ImageView) findViewById(R.id.payment_receipt_call);
        paymentReceiptDialImageView.setOnClickListener(this);

        Button saveReceiptButton = (Button) findViewById(R.id.payment_receipt_save);
        saveReceiptButton.setOnClickListener(this);

        Button shareReceiptButton = (Button) findViewById(R.id.payment_receipt_share);
        shareReceiptButton.setOnClickListener(this);

        PaymentsLabelDTO paymentsLabel = paymentReceiptModel.getPaymentsMetadata().getPaymentsLabel();
        if (paymentsLabel != null) {
            ((TextView) findViewById(R.id.receipt_no_label)).setText(paymentsLabel.getPaymentReceiptNoLabel());
            ((TextView) findViewById(R.id.payment_receipt_type_label)).setText(paymentsLabel.getPaymentReceiptPaymentType());
            ((TextView) findViewById(R.id.payment_receipt_date_label)).setText("");

            ((TextView) findViewById(R.id.payment_receipt_doctor_name)).setText(StringUtil.getLabelForView(""));
            ((TextView) findViewById(R.id.payment_receipt_speciality)).setText(StringUtil.getLabelForView(""));
            ((TextView) findViewById(R.id.payment_receipt_place_name)).setText(StringUtil.getLabelForView(""));
            ((TextView) findViewById(R.id.payment_receipt_address)).setText(StringUtil.getLabelForView(""));

            ((TextView) findViewById(R.id.payment_receipt_pre_balance_label)).setText(paymentsLabel.getPaymentPreviousBalance());
            ((TextView) findViewById(R.id.payment_receipt_pre_value)).setText("");
            ((TextView) findViewById(R.id.payment_receipt_copay_label)).setText(paymentsLabel.getPaymentInsuranceCopay());
            ((TextView) findViewById(R.id.payment_receipt_copay_value)).setText("");
            ((TextView) findViewById(R.id.payment_receipt_total_payment_label)).setText(paymentsLabel.getPaymentReceiptTotalAmount());
            ((TextView) findViewById(R.id.payment_receipt_total_payment_value)).setText("");

            saveReceiptButton.setText(paymentsLabel.getPaymentReceiptSaveReceipt());
            shareReceiptButton.setText(paymentsLabel.getPaymentReceiptShareReceipt());
        }
    }

    /**
     * show device map view based on address.
     *
     * @param address the String to evaluate
     */
    private void onMapView(final String addressName, final String address) {
        if (SystemUtil.isNotEmptyString(address)) {
            Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        }
    }

    /**
     * show device phone call UI based on phone number.
     *
     * @param phoneNumber the String to evaluate
     */
    private void onPhoneCall(final String phoneNumber) {
        if (phoneNumber != null && phoneNumber.length() > 0) {
            try {
                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e("TAG", ex.getMessage());
            }
        }
    }

    private void onSaveButton() {

    }

    private void onShareButton() {

    }
}
