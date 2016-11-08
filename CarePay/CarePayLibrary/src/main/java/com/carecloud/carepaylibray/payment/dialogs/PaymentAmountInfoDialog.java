package com.carecloud.carepaylibray.payment.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.customdialogs.BaseAmountInfoDialog;

import org.json.JSONObject;

/**
 * Created by prem_mourya on 10/6/2016.
 */

public class PaymentAmountInfoDialog extends BaseAmountInfoDialog {

    private JSONObject jsonObject;
    private Context context;
    private View rootView;
    private LinearLayout addChildDynamicLayout;
    private CarePayTextView previousTitleTextView;
    private CarePayTextView previousBalanceAmountTextView;
    private CarePayTextView insuranceCoPayTitleTextView;
    private CarePayTextView insuranceCoPayAmountTextView;
    private Button payNowButton;

    public PaymentAmountInfoDialog(Context context, JSONObject jsonObject) {
        super(context, jsonObject);
        this.context = context;
        this.jsonObject = jsonObject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.rootView = getRootView();
        this.addChildDynamicLayout = (LinearLayout) this.rootView.findViewById(R.id.addChildDynamicLayout);
        onInitialization();
    }

    private void onInitialization() {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_payment_info, null);
        previousTitleTextView = (CarePayTextView) childActionView.findViewById(R.id.previousTitleTextView);
        previousBalanceAmountTextView = (CarePayTextView) childActionView.findViewById(R.id.previousBalanceAmountTextView);
        insuranceCoPayTitleTextView = (CarePayTextView) childActionView.findViewById(R.id.insuranceCoPayTitleTextView);
        insuranceCoPayAmountTextView = (CarePayTextView) childActionView.findViewById(R.id.insuranceCoPayAmountTextView);
        payNowButton = (Button) childActionView.findViewById(R.id.payNowButton);
        onSettingStyle();
        onSetListener();
        this.addChildDynamicLayout.addView(childActionView);
    }

    private void onSettingStyle() {
        previousTitleTextView.setTextColor(ContextCompat.getColor(context,R.color.textview_default_textcolor));
        previousBalanceAmountTextView.setTextColor(ContextCompat.getColor(context,R.color.textview_default_textcolor));
        insuranceCoPayTitleTextView.setTextColor(ContextCompat.getColor(context,R.color.textview_default_textcolor));
        insuranceCoPayAmountTextView.setTextColor(ContextCompat.getColor(context,R.color.textview_default_textcolor));
    }

    private void onSetListener() {
        payNowButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.payNowButton) {
            onPayNow();
        }
    }

    private void onPayNow() {

    }
}
