package com.carecloud.carepaylibray.payment.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedBookLabel;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.utils.SystemUtil;

import org.json.JSONObject;

/**
 * Created by prem_mourya on 10/4/2016.
 */

public class PartialPaymentDialog extends Dialog implements View.OnClickListener, TextWatcher {

    private Context context;
    private JSONObject paymentModel;
    private EditText enterPartialAmountEditText;
    private CustomProxyNovaSemiBoldLabel partialPaymentTotalAmountTitle;
    private CustomGothamRoundedMediumLabel amountSymbolTextView;
    private CustomGothamRoundedBookLabel partialPaymentPayingToday;
    private Button payPartialButton;
    //changes are needed when model will come
    private double fullAmount = 108.00;
    private String amountMsg = "Pending amount: ";
    private String amountSymbol = "$";

    /**
     * Contractor for partial payment dialog.
     *
     * @param context the String to evaluate
     * @param paymentModel the String to evaluate
     */
    public PartialPaymentDialog(Context context, JSONObject paymentModel) {
        super(context);
        this.context = context;
        this.paymentModel = paymentModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_partial_payment);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setAttributes(params);

        findViewById(R.id.dialogCloseImageView).setOnClickListener(this);
        enterPartialAmountEditText = (EditText) findViewById(R.id.enterPartialAmountEditText);
        partialPaymentTotalAmountTitle = (CustomProxyNovaSemiBoldLabel) findViewById(R.id.partialPaymentTotalAmountTitle);
        payPartialButton = (Button) findViewById(R.id.payPartialButton);
        amountSymbolTextView = (CustomGothamRoundedMediumLabel) findViewById(R.id.amountSymbolTextView);
        partialPaymentPayingToday = (CustomGothamRoundedBookLabel) findViewById(R.id.partialPaymentPayingToday);
        enterPartialAmountEditText.addTextChangedListener(this);
        partialPaymentTotalAmountTitle.setText(amountMsg + amountSymbol + fullAmount);
        partialPaymentTotalAmountTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        partialPaymentTotalAmountTitle.setTextColor(context.getResources().getColor(R.color.payne_gray));
        payPartialButton.setOnClickListener(this);
        payPartialButton.setEnabled(false);
        amountSymbolTextView.setText(amountSymbol);
        amountSymbolTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
        amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white_transparent));
        partialPaymentPayingToday.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        partialPaymentPayingToday.setTextColor(context.getResources().getColor(R.color.glitter));
        SystemUtil.setGothamRoundedMediumTypeface(context, enterPartialAmountEditText);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialogCloseImageView) {
            cancel();
        } else if (viewId == R.id.payPartialButton) {
            onPaymentClick();
        }
    }

    @Override
    public void afterTextChanged(Editable stringValue) {
    }

    @Override
    public void beforeTextChanged(CharSequence stringValue, int start, int count, int after) {
        amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white));
    }

    @Override
    public void onTextChanged(CharSequence stringValue, int start, int before, int count) {
        String amountEditText = enterPartialAmountEditText.getText().toString();
        onPendingAmountValidation(amountEditText);
    }

    private void onPendingAmountValidation(String amountEditText) {
        if (amountEditText != null && amountEditText.length() > 0) {
            if (amountEditText.length() == 1 && amountEditText.equalsIgnoreCase(".")) {
                amountEditText = "0.";
            }
            double amountPay = Double.parseDouble(amountEditText);
            if ((amountPay > 0) && (amountPay <= fullAmount)) {
                partialPaymentTotalAmountTitle.setText(amountMsg + amountSymbol + (double) Math.round((fullAmount - amountPay) * 100) / 100);
                payPartialButton.setEnabled(true);
            } else {
                payPartialButton.setEnabled(false);
                partialPaymentTotalAmountTitle.setText(amountMsg + amountSymbol + fullAmount);
            }
        } else {
            amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white_transparent));
            payPartialButton.setEnabled(false);
            partialPaymentTotalAmountTitle.setText(amountMsg + amountSymbol + fullAmount);
        }
    }

    private void onPaymentClick() {

    }
}
