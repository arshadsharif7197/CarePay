package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;


/**
 * Created by prem_mourya on 10/4/2016.
 */
public class PracticePartialPaymentDialog extends PartialPaymentDialog {
    private EditText enterPartialAmountEditText;
    private Context context;
    private PaymentsModel paymentsDTO;
    private CarePayTextView partialPaymentTotalAmountTitle;
    private CarePayTextView amountSymbolTextView;
    private CarePayTextView partialPaymentPayingToday;
    private Button payPartialButton;
    private double fullAmount = 0.00;
    private boolean amountChangeFlag = true;
    private String balanceBeforeTextChange;

    /**
     *
     * @param context the context
     * @param paymentsDTO the paymentDTO
     */
    public PracticePartialPaymentDialog(Context context, PaymentsModel paymentsDTO) {
        super(context, paymentsDTO);
        this.context = context;
        this.paymentsDTO = paymentsDTO;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_practice_partial_payment);
        getPartialPaymentLabels();
        initViews();
        onSetListener();
    }

    private void initViews() {
        enterPartialAmountEditText = (EditText) findViewById(R.id.enterPartialAmountEditText);
        partialPaymentTotalAmountTitle = (CarePayTextView) findViewById(R.id.partialPaymentTotalAmountTitle);
        payPartialButton = (Button) findViewById(R.id.payPartialButton);
        amountSymbolTextView = (CarePayTextView) findViewById(R.id.amountSymbolTextView);
        partialPaymentPayingToday = (CarePayTextView) findViewById(R.id.partialPaymentPayingToday);
        enterPartialAmountEditText.addTextChangedListener(this);
        partialPaymentTotalAmountTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        partialPaymentTotalAmountTitle.setTextColor(context.getResources().getColor(R.color.payne_gray));
        payPartialButton.setText(paymentPartialButtonLabel);
        payPartialButton.setOnClickListener(this);
        payPartialButton.setEnabled(false);
        amountSymbolTextView.setText(CarePayConstants.DOLLAR);
        amountSymbolTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
        amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white_transparent));
        partialPaymentPayingToday.setText(paymentTitle);
        partialPaymentPayingToday.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        partialPaymentPayingToday.setTextColor(context.getResources().getColor(R.color.glitter));

        calculateFullAmount(partialPaymentTotalAmountTitle);
    }

    /**
     * set listner for keypad components .
     */
    private void onSetListener() {
        (findViewById(R.id.key_one)).setOnClickListener(this);
        (findViewById(R.id.key_two)).setOnClickListener(this);
        (findViewById(R.id.key_three)).setOnClickListener(this);
        (findViewById(R.id.key_four)).setOnClickListener(this);
        (findViewById(R.id.key_five)).setOnClickListener(this);
        (findViewById(R.id.key_six)).setOnClickListener(this);
        (findViewById(R.id.key_seven)).setOnClickListener(this);
        (findViewById(R.id.key_eighth)).setOnClickListener(this);
        (findViewById(R.id.key_nine)).setOnClickListener(this);
        (findViewById(R.id.key_zero)).setOnClickListener(this);
        (findViewById(R.id.key_blank)).setOnClickListener(this);
        (findViewById(R.id.key_clear)).setOnClickListener(this);
        (findViewById(R.id.closeViewLayout)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.key_clear) {
            onEnterNumberClear();
        } else if(viewId == R.id.closeViewLayout){
            dismiss();
        } else if (viewId == R.id.payPartialButton) {
            onPaymentClick(enterPartialAmountEditText, paymentsDTO);
        } else {
            String buttonValue = ((Button) view).getText().toString();
            onEnterNumber(buttonValue);
        }
    }

    private void onEnterNumber(String numberStr) {
        String actualValue = enterPartialAmountEditText.getText().toString();
        numberStr = actualValue + numberStr;
        enterPartialAmountEditText.setText(numberStr);
    }

    private void onEnterNumberClear() {
        String actualValue = enterPartialAmountEditText.getText().toString();
        if (actualValue.length() > 0) {
            actualValue = actualValue.substring(0, actualValue.length() - 1);
            enterPartialAmountEditText.setText(actualValue);
        }
    }

    @Override
    public void afterTextChanged(Editable str) {
    }

    @Override
    public void beforeTextChanged(CharSequence str, int start, int count, int after) {
        amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white));
        balanceBeforeTextChange = str.toString();
    }

    @Override
    public void onTextChanged(CharSequence str, int start, int before, int count) {
        String amountEditText = enterPartialAmountEditText.getText().toString();
       try {
            if (amountChangeFlag) {
                // flag to avoid the onTextChanged listener call after setText manipulated number
                amountChangeFlag = false;
                //when deleting a decimal point, delete any non-integral portion of the number
                if (balanceBeforeTextChange.contains(".") && !amountEditText.contains(".")) {
                    amountEditText = balanceBeforeTextChange.substring(0, balanceBeforeTextChange.indexOf("."));
                    enterPartialAmountEditText.setText(amountEditText);
                    enterPartialAmountEditText.setSelection(amountEditText.length());
                } else
                    // user cannot enter amount less than 1
                    if (amountEditText.equalsIgnoreCase(".") || Double.parseDouble(amountEditText) < 1) {
                        amountEditText = "0";
                        enterPartialAmountEditText.setText(amountEditText);
                    } else
                        // Only when user enters dot, we should show the decimal values as 00
                        if (amountEditText.endsWith(".")) {
                            amountEditText = amountEditText + "00";
                            enterPartialAmountEditText.setText(amountEditText);
                            enterPartialAmountEditText.setSelection(amountEditText.length() - 2);
                            // When user removes dot, we should show the integer before that
                        } else if (amountEditText.endsWith(".0")) {
                            enterPartialAmountEditText.setText(amountEditText.substring(0, amountEditText.length() - 2));
                            enterPartialAmountEditText.setSelection(enterPartialAmountEditText.length());
                            // When user enters number, we should simply append the number entered
                            // Also adjusting the cursor position after removing DOT and appending number
                        } else {
                            if (amountEditText.contains(".") && amountEditText.length() - 3 > amountEditText.indexOf(".")) {
                                //enterPartialAmountEditText.setText(new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER).format(Double.parseDouble(amountEditText)));
                                amountEditText = amountEditText.substring(0, amountEditText.indexOf(".") + 3);
                                enterPartialAmountEditText.setText(amountEditText);
                            } else {
                                enterPartialAmountEditText.setText(amountEditText);
                            }
                            if (enterPartialAmountEditText.getText().toString().endsWith("0") && amountEditText.contains(".")) {
                                enterPartialAmountEditText.setSelection(enterPartialAmountEditText.length() - 1);
                            } else {
                                enterPartialAmountEditText.setSelection(enterPartialAmountEditText.length());
                            }
                        }
            } else {
                amountChangeFlag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Calculating the remaining amount after entering partial payment amount
       onPendingAmountValidation(amountEditText, payPartialButton, partialPaymentTotalAmountTitle, amountSymbolTextView);
    }
}