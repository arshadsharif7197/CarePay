package com.carecloud.carepay.patient.payment.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.SystemUtil;

import org.json.JSONObject;

public class PaymentAmountReceiptDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private JSONObject paymentReceiptModel;

    private ImageView dialogCloseHeader;
    private ImageView paymentReceiptLocationImageView;
    private ImageView paymentReceiptDialImageView;

    private CarePayTextView receiptAmountValueLabel;
    private CarePayTextView receiptPaymentTypeLabel;
    private CarePayTextView receiptPaymentDateLabel;
    private CarePayTextView receiptUserNameLabel;
    private CarePayTextView receiptUserTypeLabel;
    private CarePayTextView addressReceiptLevel;
    private CarePayTextView receiptPreviousTitleLabel;
    private CarePayTextView receiptPreviousValueLabel;
    private CarePayTextView receiptInsuranceTitleLabel;
    private CarePayTextView receiptInsuranceValueLabel;
    private CarePayTextView totalPaymentReceiptTitleLabel;
    private CarePayTextView totalPaymentReceiptValueLabel;
    private CarePayTextView paymentReceiptHeaderTextView;

    private Button saveReceiptButton;
    private Button shareReceiptButton;

    /**
     * Constructor.
     *
     * @param context             context
     * @param paymentReceiptModel model
     */
    public PaymentAmountReceiptDialog(Context context, JSONObject paymentReceiptModel) {
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
        onSettingStyle();
        onSetListener();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
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
        dialogCloseHeader = (ImageView) findViewById(R.id.dialog_close_header);
        paymentReceiptLocationImageView = (ImageView) findViewById(R.id.payment_receipt_location);
        paymentReceiptDialImageView = (ImageView) findViewById(R.id.payment_receipt_call);
        receiptAmountValueLabel = (CarePayTextView) findViewById(R.id.receipt_no_label);
        receiptPaymentTypeLabel = (CarePayTextView) findViewById(R.id.payment_receipt_type_label);
        receiptPaymentDateLabel = (CarePayTextView) findViewById(R.id.payment_receipt_date_label);
        receiptUserNameLabel = (CarePayTextView) findViewById(R.id.payment_receipt_doctor_name);

        receiptUserTypeLabel = (CarePayTextView) findViewById(R.id.payment_receipt_speciality);
        addressReceiptLevel = (CarePayTextView) findViewById(R.id.payment_receipt_address);
        receiptPreviousTitleLabel = (CarePayTextView) findViewById(R.id.payment_receipt_pre_balance_label);
        receiptPreviousValueLabel = (CarePayTextView) findViewById(R.id.payment_receipt_pre_value);
        receiptInsuranceTitleLabel = (CarePayTextView) findViewById(R.id.payment_receipt_copay_label);
        receiptInsuranceValueLabel = (CarePayTextView) findViewById(R.id.payment_receipt_copay_value);
        totalPaymentReceiptTitleLabel = (CarePayTextView) findViewById(R.id.payment_receipt_total_payment_label);
        totalPaymentReceiptValueLabel = (CarePayTextView) findViewById(R.id.payment_receipt_total_payment_value);
        paymentReceiptHeaderTextView = (CarePayTextView) findViewById(R.id.payment_receipt_place_name);

        saveReceiptButton = (Button) findViewById(R.id.payment_receipt_save);
        shareReceiptButton = (Button) findViewById(R.id.payment_receipt_share);

    }

    private void onSettingStyle() {
        receiptAmountValueLabel.setTextColor(ContextCompat.getColor(context, R.color.textview_default_textcolor));
        receiptPaymentTypeLabel.setTextColor(ContextCompat.getColor(context, R.color.manatee));
        receiptPaymentDateLabel.setTextColor(ContextCompat.getColor(context, R.color.textview_default_textcolor));
        receiptUserNameLabel.setTextColor(ContextCompat.getColor(context, R.color.blue_cerulian));

        receiptUserTypeLabel.setTextColor(ContextCompat.getColor(context, R.color.bermudagrey));
        addressReceiptLevel.setTextColor(ContextCompat.getColor(context, R.color.bermudagrey));
        receiptPreviousTitleLabel.setTextColor(ContextCompat.getColor(context, R.color.bermudagrey));
        receiptPreviousValueLabel.setTextColor(ContextCompat.getColor(context, R.color.bermudagrey));
        receiptInsuranceTitleLabel.setTextColor(ContextCompat.getColor(context, R.color.bermudagrey));
        receiptInsuranceValueLabel.setTextColor(ContextCompat.getColor(context, R.color.bermudagrey));
        totalPaymentReceiptTitleLabel.setTextColor(ContextCompat.getColor(context, R.color.bermudagrey));
        totalPaymentReceiptValueLabel.setTextColor(ContextCompat.getColor(context, R.color.bermudagrey));
        paymentReceiptHeaderTextView.setTextColor(ContextCompat.getColor(context, R.color.payne_gray));
    }

    private void onSetListener() {
        dialogCloseHeader.setOnClickListener(this);
        saveReceiptButton.setOnClickListener(this);
        shareReceiptButton.setOnClickListener(this);
        paymentReceiptDialImageView.setOnClickListener(this);
        paymentReceiptLocationImageView.setOnClickListener(this);
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
