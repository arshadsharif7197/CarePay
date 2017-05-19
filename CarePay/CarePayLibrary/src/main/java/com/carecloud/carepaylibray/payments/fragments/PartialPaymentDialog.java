package com.carecloud.carepaylibray.payments.fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

/**
 * Created by lmenendez on 2/28/17
 */

public class PartialPaymentDialog extends Dialog implements View.OnClickListener, TextWatcher {
    private Context context;
    //private JSONObject paymentModel;
    private EditText enterPartialAmountEditText;
    private CarePayTextView partialPaymentTotalAmountTitle;
    private CarePayTextView amountSymbolTextView;
    private CarePayTextView partialPaymentPayingToday;
    private Button payPartialButton;
    //changes are needed when model will come
    private double fullAmount = 0.00;
    private String amountSymbol = "$";
    private PaymentsModel paymentsDTO;

    private double insuranceCopay;
    private double patientBalance;
    private boolean amountChangeFlag = true;
    private String balanceBeforeTextChange;

    //private boolean deviceLarge;
    protected PaymentNavigationCallback payNowClickListener;

    /**
     * Contructor
     * @param context context must implement PayNowClickListener
     * @param paymentsDTO payment model
     */
    public PartialPaymentDialog(Context context, PaymentsModel paymentsDTO) {
        super(context);
        this.context = context;
        this.paymentsDTO = paymentsDTO;

        try {
            if(context instanceof PaymentViewHandler){
                payNowClickListener = ((PaymentViewHandler) context).getPaymentPresenter();
            }else {
                payNowClickListener = (PaymentNavigationCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Dialog Context must implement PayNowClickListener for callback");
        }

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
        //onSetListener();

        initViews();
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
        payPartialButton.setOnClickListener(this);
        payPartialButton.setEnabled(false);
        amountSymbolTextView.setText(CarePayConstants.DOLLAR);
        amountSymbolTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
        amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white_transparent));
        LinearLayout closeLinearLayout = (LinearLayout) findViewById(com.carecloud.carepaylibrary.R.id.closeViewLayout);
        closeLinearLayout.setOnClickListener(this);
        partialPaymentPayingToday.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        partialPaymentPayingToday.setTextColor(context.getResources().getColor(R.color.glitter));
        calculateFullAmount(partialPaymentTotalAmountTitle);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.closeViewLayout) {
            cancel();
        } else if(viewId == R.id.key_clear) {
            onEnterNumberClear();

        }else if (viewId == R.id.payPartialButton) {
            onPaymentClick(enterPartialAmountEditText, paymentsDTO);

        }else {
            String buttonValue = ((Button) view).getText().toString();
            onEnterNumber(buttonValue);
        }
    }

    @Override
    public void afterTextChanged(Editable str) {
    }

    @Override
    public void beforeTextChanged(CharSequence str, int start, int count, int after) {
        //amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white));
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

    private void onPendingAmountValidation(String amountEditText, Button payPartialButton, TextView partialPaymentTotalAmountTitle,TextView amountSymbolTextView) {
        if (amountEditText != null && amountEditText.length() > 0) {
            if (amountEditText.length() == 1 && amountEditText.equalsIgnoreCase(".")) {
                amountEditText = "0.";
            }
            double amountPay = Double.parseDouble(amountEditText);
            if ((amountPay > 0) && (amountPay <= fullAmount)) {
                payPartialButton.setEnabled(true);
            } else {
                payPartialButton.setEnabled(false);
            }
            partialPaymentTotalAmountTitle.setText(Label.getLabel("payment_pending_text") + " " + StringUtil.getFormattedBalanceAmount((double) Math.round((fullAmount - amountPay) * 100) / 100));
        } else {
            //amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white_transparent));
            payPartialButton.setEnabled(false);
            partialPaymentTotalAmountTitle.setText(Label.getLabel("payment_pending_text") + " " + StringUtil.getFormattedBalanceAmount(fullAmount));
        }
    }

    private void onPaymentClick(EditText enterPartialAmountEditText, PaymentsModel paymentsDTO) {
        try {
            payNowClickListener.onPayButtonClicked(Double.parseDouble(enterPartialAmountEditText.getText().toString()), paymentsDTO);
            cancel();
        }catch (NumberFormatException nfe){
            nfe.printStackTrace();
            Toast.makeText(context, "Please enter valid amount!", Toast.LENGTH_LONG).show();
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

    private void calculateFullAmount(TextView partialPaymentTotalAmountTitle){
        if (paymentsDTO != null && !paymentsDTO.getPaymentPayload().getPatientBalances().isEmpty()) {
            List<PendingBalancePayloadDTO> paymentList = paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getPayload();

            if (paymentList != null && paymentList.size() > 0) {
                for (PendingBalancePayloadDTO payment : paymentList) {
                    if (payment.getType().equalsIgnoreCase(CarePayConstants.PATIENT_BALANCE)) {
                        patientBalance = payment.getAmount();
                    } else if (payment.getType().equalsIgnoreCase(CarePayConstants.INSURANCE_COPAY)) {
                        insuranceCopay = payment.getAmount();
                    }
                }

                fullAmount = patientBalance + insuranceCopay;
                partialPaymentTotalAmountTitle.setText(Label.getLabel("payment_pending_text") + " " + StringUtil.getFormattedBalanceAmount(fullAmount));
            }
        }
    }

}