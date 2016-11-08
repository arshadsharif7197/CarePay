package com.carecloud.carepaylibray.payment.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaExtraBold;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.utils.SystemUtil;

import org.json.JSONObject;

/**
 * Created by prem_mourya on 10/7/2016.
 */

public class PaymentAmountReceiptDialog extends Dialog implements
        View.OnClickListener {

    private Context context;
    private JSONObject paymentReceiptModel;
    private ImageView dialogCloseHeader,paymentReceiptLocationImageView, paymentReceiptDialImageView;
    private CarePayTextView receiptAmountValueLabel,receiptPaymenttypeLabel,receiptPaymentDateLabel,receiptUserNameLabel;
    private CarePayTextView receiptUsertypeLabel,addressReceiptLevel,receiptPreviousTitlelabel,receiptPreviousValuelabel,receiptInsuranceTitleLabel,
            receiptInsuranceValueLabel,totalPaymentReceiptTitleLabel,totalPaymentReceiptValueLabel;
    private CarePayTextView paymentReceiptHeaderTextView;
    private Button saveOrSharereceiptButton;

    public PaymentAmountReceiptDialog(Context context, JSONObject paymentReceiptModel) {
        super(context);
        this.context = context;
        this.paymentReceiptModel = paymentReceiptModel;
    }

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
        if (viewId == R.id.dialogCloseHeader) {
            cancel();
        } else if(viewId == R.id.paymentReceiptLocationImageView){
            //address will add after model come
            onMapView("","");
        }else if(viewId == R.id.paymentReceiptDialImageView){
            //phoneCall will add after model come
            onPhoneCall("");
        }else if (viewId == R.id.saveOrSharereceiptButton) {
            onSaveShareButton();
        }
    }

    private void onInitialization(){
        dialogCloseHeader= (ImageView)findViewById(R.id.dialogCloseHeader);
        paymentReceiptLocationImageView= (ImageView)findViewById(R.id.paymentReceiptLocationImageView);
        paymentReceiptDialImageView = (ImageView)findViewById(R.id.paymentReceiptDialImageView);
        receiptAmountValueLabel = (CarePayTextView)findViewById(R.id.receiptAmountValueLabel);
        receiptPaymenttypeLabel = (CarePayTextView)findViewById(R.id.receiptPaymenttypeLabel);
        receiptPaymentDateLabel = (CarePayTextView)findViewById(R.id.receiptPaymentDateLabel);
        receiptUserNameLabel = (CarePayTextView)findViewById(R.id.receiptUserNameLabel);

        receiptUsertypeLabel=(CarePayTextView)findViewById(R.id.receiptUsertypeLabel);
        addressReceiptLevel=(CarePayTextView)findViewById(R.id.addressReceiptLevel);
        receiptPreviousTitlelabel=(CarePayTextView)findViewById(R.id.receiptPreviousTitlelabel);
        receiptPreviousValuelabel=(CarePayTextView)findViewById(R.id.receiptPreviousValuelabel);
        receiptInsuranceTitleLabel=(CarePayTextView)findViewById(R.id.receiptInsuranceTitleLabel);
        receiptInsuranceValueLabel=(CarePayTextView)findViewById(R.id.receiptInsuranceValueLabel);
        totalPaymentReceiptTitleLabel=(CarePayTextView)findViewById(R.id.totalPaymentReceiptTitleLabel);
        totalPaymentReceiptValueLabel=(CarePayTextView)findViewById(R.id.totalPaymentReceiptValueLabel);
        paymentReceiptHeaderTextView = (CarePayTextView)findViewById(R.id.paymentReceiptHeaderTextView);
        saveOrSharereceiptButton = (Button)findViewById(R.id.saveOrSharereceiptButton);

    }
    private void onSettingStyle(){
        receiptAmountValueLabel.setTextColor(ContextCompat.getColor(context,R.color.textview_default_textcolor));
        receiptPaymenttypeLabel.setTextColor(ContextCompat.getColor(context,R.color.manatee));
        receiptPaymentDateLabel.setTextColor(ContextCompat.getColor(context,R.color.textview_default_textcolor));
        receiptUserNameLabel.setTextColor(ContextCompat.getColor(context,R.color.blue_cerulian));

        receiptUsertypeLabel.setTextColor(ContextCompat.getColor(context,R.color.bermudagrey));
        addressReceiptLevel.setTextColor(ContextCompat.getColor(context,R.color.bermudagrey));
        receiptPreviousTitlelabel.setTextColor(ContextCompat.getColor(context,R.color.bermudagrey));
        receiptPreviousValuelabel.setTextColor(ContextCompat.getColor(context,R.color.bermudagrey));
        receiptInsuranceTitleLabel.setTextColor(ContextCompat.getColor(context,R.color.bermudagrey));
        receiptInsuranceValueLabel.setTextColor(ContextCompat.getColor(context,R.color.bermudagrey));
        totalPaymentReceiptTitleLabel.setTextColor(ContextCompat.getColor(context,R.color.bermudagrey));
        totalPaymentReceiptValueLabel.setTextColor(ContextCompat.getColor(context,R.color.bermudagrey));
        paymentReceiptHeaderTextView.setTextColor(ContextCompat.getColor(context,R.color.payne_gray));
    }
    private void onSetListener(){
        dialogCloseHeader.setOnClickListener(this);
        saveOrSharereceiptButton.setOnClickListener(this);
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
        if(phoneNumber !=null && phoneNumber.length() > 0)
            try {
                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
            } catch (android.content.ActivityNotFoundException ex) {

            }
    }
    private void onSaveShareButton(){

    }
}
