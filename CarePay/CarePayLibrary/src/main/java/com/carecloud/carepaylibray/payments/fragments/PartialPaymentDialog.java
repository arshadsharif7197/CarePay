package com.carecloud.carepaylibray.payments.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.carecloud.carepaylibray.payments.interfaces.PaymentInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by lmenendez on 2/28/17
 */

public class PartialPaymentDialog extends Dialog implements View.OnClickListener, TextWatcher {
    private Context context;

    private EditText amountText;
    private TextView partialPaymentTotalAmountTitle;
    private TextView amountSymbol;
    private Button payPartialButton;

    private double fullAmount = 0.00;
    private PaymentsModel paymentsDTO;

    private double insuranceCopay;
    private double patientBalance;
    private boolean amountChangeFlag = true;
    private String balanceBeforeTextChange;

    private PaymentInterface payNowClickListener;

    /**
     * Contructor
     *
     * @param context     context must implement PayNowClickListener
     * @param paymentsDTO payment model
     */
    public PartialPaymentDialog(Context context, PaymentsModel paymentsDTO) {
        super(context);
        this.context = context;
        this.paymentsDTO = paymentsDTO;

        try {
            if (context instanceof PaymentViewHandler) {
                payNowClickListener = ((PaymentViewHandler) context).getPaymentPresenter();
            } else {
                payNowClickListener = (PaymentInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Dialog Context must implement PaymentInterface for callback");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_partial_payment);
        setCancelable(false);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
            getWindow().setAttributes(params);
        }
        initViews();
    }

    private void initViews() {
        amountText = (EditText) findViewById(R.id.enterPartialAmountEditText);
        partialPaymentTotalAmountTitle = (TextView) findViewById(R.id.partialPaymentTotalAmountTitle);
        payPartialButton = (Button) findViewById(R.id.payPartialButton);
        amountSymbol = (TextView) findViewById(R.id.amountSymbolTextView);

        amountText.addTextChangedListener(this);
        payPartialButton.setOnClickListener(this);
        payPartialButton.setEnabled(false);

        String symbol = NumberFormat.getCurrencyInstance().getCurrency().getSymbol();
        amountSymbol.setText(symbol);

        View closeView = findViewById(com.carecloud.carepaylibrary.R.id.closeViewLayout);
        closeView.setOnClickListener(this);

        calculateFullAmount(partialPaymentTotalAmountTitle);

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.closeViewLayout) {
            SystemUtil.hideSoftKeyboard(context, view);
            cancel();
        } else if (viewId == R.id.payPartialButton) {
            onPaymentClick(amountText, paymentsDTO);
        }
    }

    @Override
    public void afterTextChanged(Editable str) {
        if (str.length() > 0) {
            amountSymbol.setTextColor(ContextCompat.getColor(context, R.color.white));
            amountText.setHint(null);
        } else {
            amountSymbol.setTextColor(ContextCompat.getColor(context, R.color.white_transparent));
            amountText.setHint("0.00");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence str, int start, int count, int after) {
        //amountSymbol.setTextColor(context.getResources().getColor(R.color.white));
        balanceBeforeTextChange = str.toString();
    }

    @Override
    public void onTextChanged(CharSequence str, int start, int before, int count) {
        String amountEditText = amountText.getText().toString();
        try {
            if (amountChangeFlag) {
                // flag to avoid the onTextChanged listener call after setText manipulated number
                amountChangeFlag = false;
                //when deleting a decimal point, delete any non-integral portion of the number
                if (balanceBeforeTextChange.contains(".") && !amountEditText.contains(".")) {
                    amountEditText = balanceBeforeTextChange.substring(0, balanceBeforeTextChange.indexOf("."));
                    amountText.setText(amountEditText);
                    amountText.setSelection(amountEditText.length());
                } else
                    // user cannot enter amount less than 1
                    if (amountEditText.equalsIgnoreCase(".") || Double.parseDouble(amountEditText) < 1) {
                        amountEditText = "0";
                        amountText.setText(amountEditText);
                    } else
                        // Only when user enters dot, we should show the decimal values as 00
                        if (amountEditText.endsWith(".")) {
                            amountEditText = amountEditText + "00";
                            amountText.setText(amountEditText);
                            amountText.setSelection(amountEditText.length() - 2);
                            // When user removes dot, we should show the integer before that
                        } else if (amountEditText.endsWith(".0")) {
                            amountText.setText(amountEditText.substring(0, amountEditText.length() - 2));
                            amountText.setSelection(amountText.length());
                            // When user enters number, we should simply append the number entered
                            // Also adjusting the cursor position after removing DOT and appending number
                        } else {
                            if (amountEditText.contains(".") && amountEditText.length() - 3 > amountEditText.indexOf(".")) {
                                //amountText.setText(new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER).format(Double.parseDouble(amountEditText)));
                                amountEditText = amountEditText.substring(0, amountEditText.indexOf(".") + 3);
                                amountText.setText(amountEditText);
                            } else {
                                amountText.setText(amountEditText);
                            }
                            if (amountText.getText().toString().endsWith("0") && amountEditText.contains(".")) {
                                amountText.setSelection(amountText.length() - 1);
                            } else {
                                amountText.setSelection(amountText.length());
                            }
                        }
            } else {
                amountChangeFlag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Calculating the remaining amount after entering partial payment amount
        onPendingAmountValidation(amountEditText, payPartialButton, partialPaymentTotalAmountTitle, amountSymbol);
    }

    private void onPendingAmountValidation(String amountEditText, Button payPartialButton, TextView partialPaymentTotalAmountTitle, TextView amountSymbolTextView) {
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
            //amountSymbol.setTextColor(context.getResources().getColor(R.color.white_transparent));
            payPartialButton.setEnabled(false);
            partialPaymentTotalAmountTitle.setText(Label.getLabel("payment_pending_text") + " " + StringUtil.getFormattedBalanceAmount(fullAmount));
        }
    }

    private void onPaymentClick(EditText enterPartialAmountEditText, PaymentsModel paymentsDTO) {
        try {
            payNowClickListener.onPayButtonClicked(Double.parseDouble(enterPartialAmountEditText.getText().toString()), paymentsDTO);
            cancel();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Toast.makeText(context, "Please enter valid amount!", Toast.LENGTH_LONG).show();
        }
    }

    private void calculateFullAmount(TextView partialPaymentTotalAmountTitle) {
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