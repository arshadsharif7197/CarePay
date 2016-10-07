package com.carecloud.carepaylibray.payment.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
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
    private LinearLayout addChildDyanmicLayou;
    private CustomProxyNovaRegularLabel prevoiusTitletextView;
    private CustomProxyNovaRegularLabel previousBalanceAmounttextView;
    private CustomProxyNovaRegularLabel insurnceCoPayTitletextView;
    private CustomProxyNovaRegularLabel insurnceCoPayAmounttextView;
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
        this.addChildDyanmicLayou = (LinearLayout) this.rootView.findViewById(R.id.addChildDyanmicLayout);
        onInitialization();
    }

    private void onInitialization() {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_payment_info, null);
        prevoiusTitletextView = (CustomProxyNovaRegularLabel) childActionView.findViewById(R.id.prevoiusTitletextView);
        previousBalanceAmounttextView = (CustomProxyNovaRegularLabel) childActionView.findViewById(R.id.previousBalanceAmounttextView);
        insurnceCoPayTitletextView = (CustomProxyNovaRegularLabel) childActionView.findViewById(R.id.insurnceCoPayTitletextView);
        insurnceCoPayAmounttextView = (CustomProxyNovaRegularLabel) childActionView.findViewById(R.id.insurnceCoPayAmounttextView);
        payNowButton = (Button) childActionView.findViewById(R.id.payNowButton);
        onSettingStyle();
        onSetListener();
        this.addChildDyanmicLayou.addView(childActionView);
    }

    private void onSettingStyle() {
        prevoiusTitletextView.setTextColor(ContextCompat.getColor(context,R.color.textview_default_textcolor));
        previousBalanceAmounttextView.setTextColor(ContextCompat.getColor(context,R.color.textview_default_textcolor));
        insurnceCoPayTitletextView.setTextColor(ContextCompat.getColor(context,R.color.textview_default_textcolor));
        insurnceCoPayAmounttextView.setTextColor(ContextCompat.getColor(context,R.color.textview_default_textcolor));
    }

    private void onSetListener() {
        payNowButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int viewId = v.getId();
        if (viewId == R.id.payNowButton) {
            onPayNow();
        }
    }

    private void onPayNow() {

    }
}
