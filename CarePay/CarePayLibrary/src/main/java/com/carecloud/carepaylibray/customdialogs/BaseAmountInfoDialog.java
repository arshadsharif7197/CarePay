package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by prem_mourya on 10/5/2016.
 */

public class BaseAmountInfoDialog extends Dialog implements View.OnClickListener {

    private static final String LOG_TAG = BaseAmountInfoDialog.class.getSimpleName();

    private Context context;
    protected CarePayTextView paymentAmountTextView;
    private CarePayTextView userShortnameTextView;
    private CarePayTextView userNameTextView;

    private ImageView dialogCloseHeader;
    protected PatientBalanceDTO model;
    private View rootLayout;

    /**
     * Constructor.
     * @param context context
     * @param paymentModel payment model
     */
    public BaseAmountInfoDialog(Context context, PatientBalanceDTO paymentModel) {
        super(context);
        this.context = context;
        this.model = paymentModel;
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

        userNameTextView.setText(CarePayConstants.NOT_DEFINED);
        onSetListener();
    }

    private void onInitialization() {
        dialogCloseHeader = (ImageView) findViewById(R.id.dialog_close_header);
        paymentAmountTextView = (CarePayTextView) findViewById(R.id.paymentAmountTextView);
        ImageView paymentUserPicImageView = (ImageView) findViewById(R.id.paymentUserPicImageView);
        userShortnameTextView = (CarePayTextView) findViewById(R.id.userShortnameTextView);
        userNameTextView = (CarePayTextView) findViewById(R.id.userNameTextView);
        
        rootLayout = findViewById(R.id.rootDialogPaymentLayout);
        Resources res = context.getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        //mProgress.setProgress(40);   // Main Progress
        //mProgress.setSecondaryProgress(100); // Secondary Progress
        //mProgress.setMax(100); // Maximum Progress
        //mProgress.setProgressDrawable(drawable);
    }

    private void onSettingStyle() {
        paymentAmountTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
        userShortnameTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
        userNameTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }

    private void onSetListener() {
        dialogCloseHeader.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialog_close_header) {
            cancel();
        }
    }

    /**
     * show device map view based on address.
     * @param addressName place name
     * @param address place address
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
     * @param phoneNumber phone number
     */
    private void onPhoneCall(final String phoneNumber) {
        if (phoneNumber != null && phoneNumber.length() > 0) {
            try {
                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e(LOG_TAG, ex.getMessage());
            }
        }
    }

    protected View getRootView() {
        return rootLayout;
    }
}
