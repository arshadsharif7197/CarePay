package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaExtraBold;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.utils.SystemUtil;

import org.json.JSONObject;

/**
 * Created by prem_mourya on 10/5/2016.
 */

public class BaseAmountInfoDialog extends Dialog implements
        View.OnClickListener {
    private Context context;
    protected CustomGothamRoundedMediumLabel paymentAmountTextView;
    private CustomProxyNovaSemiBoldLabel userShortnameTextView;
    private CustomProxyNovaSemiBoldLabel userNameTextView;
    private CustomProxyNovaRegularLabel userTypeTextView;
    private CustomProxyNovaSemiBoldLabel paymentDatetextView;
    private CustomProxyNovaExtraBold paymentAddressHeaderTextView;
    private CustomProxyNovaRegularLabel addressAmountLevel;

    private ImageView dialogCloseHeader, paymentUserPicImageView, paymentLocationImageView, paymentDialImageView;
    private JSONObject paymentModel;
    private View rootLayout;

    public BaseAmountInfoDialog(Context context,JSONObject paymentModel) {
        super(context);
        this.context = context;
        this.paymentModel = paymentModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_base_amount_info);
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

    private void onInitialization() {
        dialogCloseHeader = (ImageView) findViewById(R.id.dialogCloseHeader);
        paymentAmountTextView = (CustomGothamRoundedMediumLabel) findViewById(R.id.paymentAmountTextView);
        paymentUserPicImageView = (ImageView) findViewById(R.id.paymentUserPicImageView);
        userShortnameTextView = (CustomProxyNovaSemiBoldLabel) findViewById(R.id.userShortnameTextView);
        userNameTextView = (CustomProxyNovaSemiBoldLabel) findViewById(R.id.userNameTextView);
        userTypeTextView = (CustomProxyNovaRegularLabel) findViewById(R.id.userTypeTextView);
        paymentDatetextView = (CustomProxyNovaSemiBoldLabel) findViewById(R.id.paymentDatetextView);
        paymentAddressHeaderTextView = (CustomProxyNovaExtraBold) findViewById(R.id.paymentAddressHeaderTextView);
        addressAmountLevel = (CustomProxyNovaRegularLabel) findViewById(R.id.addressAmountLevel);
        paymentLocationImageView = (ImageView) findViewById(R.id.paymentLocationImageView);
        paymentDialImageView = (ImageView) findViewById(R.id.paymentDailImageView);
        if(TextUtils.isEmpty("")){
            Drawable originalIcon = context.getResources().getDrawable(R.drawable.icn_appointment_card_call);
            ((ImageView)findViewById(R.id.paymentDailImageView)).setImageDrawable(SystemUtil.convertDrawableToGrayScale(originalIcon));
        }
        if(!SystemUtil.isNotEmptyString(addressAmountLevel.getText().toString())){
            Drawable originalIcon = context.getResources().getDrawable(R.drawable.icn_appointment_card_directions);
            ((ImageView)findViewById(R.id.paymentLocationImageView)).setImageDrawable(SystemUtil.convertDrawableToGrayScale(originalIcon));
        }
        rootLayout = findViewById(R.id.rootDialogPaymentLayout);

    }

    private void onSettingStyle() {
        paymentAmountTextView.setTextColor(ContextCompat.getColor(context,R.color.white));
        userShortnameTextView.setTextColor(ContextCompat.getColor(context,R.color.white));
        userNameTextView.setTextColor(ContextCompat.getColor(context,R.color.button_bright_cerulean));
        userTypeTextView.setTextColor(ContextCompat.getColor(context,R.color.lightSlateGray));
        paymentDatetextView.setTextColor(ContextCompat.getColor(context,R.color.payne_gray));
        paymentAddressHeaderTextView.setTextColor(ContextCompat.getColor(context,R.color.payne_gray));
        addressAmountLevel.setTextColor(ContextCompat.getColor(context,R.color.optionl_gray));
    }

    private void onSetListener() {
        dialogCloseHeader.setOnClickListener(this);
        paymentLocationImageView.setOnClickListener(this);
        paymentDialImageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int viewId= view.getId();
        if(viewId == R.id.dialogCloseHeader){
            cancel();
        }else if(viewId == R.id.paymentLocationImageView){
            //address will add after model come
            onMapView("","");
        }else if(viewId == R.id.paymentDailImageView){
            //phoneCall will add after model come
            onPhoneCall("");
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
        if(phoneNumber !=null && phoneNumber.length() > 0)
        try {
            context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        } catch (android.content.ActivityNotFoundException ex) {
                System.out.print(ex.getMessage());
        }
    }
    protected View getRootView() {
        return rootLayout;
    }

}
