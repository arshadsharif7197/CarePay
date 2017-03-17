package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;

import java.text.NumberFormat;

/**
 * Created by lmenendez on 3/17/17.
 */

public class PaymentDistributionEntryFragment extends BaseDialogFragment implements View.OnClickListener {
    public interface PaymentDistributionAmountCallback{
        void applyNewDistributionAmount(double amount);
    }

    private PaymentDistributionAmountCallback callback;

    private EditText amountText;
    private TextView amountSymbol;
    private Button apply;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.dialog_partial_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        amountText = (EditText) view.findViewById(R.id.enterPartialAmountEditText);
        amountText.addTextChangedListener(textWatcher);

        String symbol = NumberFormat.getCurrencyInstance().getCurrency().getSymbol();
        amountSymbol = (TextView) view.findViewById(R.id.amountSymbolTextView);
        amountSymbol.setText(symbol);

        apply = (Button) view.findViewById(R.id.payPartialButton);
        apply.setText(Label.getLabelForView("payment_apply_amount"));
        apply.setOnClickListener(this);

        View close = view.findViewById(R.id.closeViewLayout);
        close.setOnClickListener(this);

        view.findViewById(R.id.partialPaymentTotalAmountTitle).setVisibility(View.GONE);

        setEntryListeners(view);
    }

    private void setEntryListeners(View view){
        view.findViewById(R.id.key_one).setOnClickListener(this);
        view.findViewById(R.id.key_two).setOnClickListener(this);
        view.findViewById(R.id.key_three).setOnClickListener(this);
        view.findViewById(R.id.key_four).setOnClickListener(this);
        view.findViewById(R.id.key_five).setOnClickListener(this);
        view.findViewById(R.id.key_six).setOnClickListener(this);
        view.findViewById(R.id.key_seven).setOnClickListener(this);
        view.findViewById(R.id.key_eighth).setOnClickListener(this);
        view.findViewById(R.id.key_nine).setOnClickListener(this);
        view.findViewById(R.id.key_zero).setOnClickListener(this);
        view.findViewById(R.id.key_blank).setOnClickListener(this);
        view.findViewById(R.id.key_clear).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.closeViewLayout){
            dismiss();
        }else if(id == R.id.payPartialButton && callback!=null){
            try {
                callback.applyNewDistributionAmount(Double.parseDouble(amountText.getText().toString()));
                dismiss();
            }catch (NumberFormatException nfe){
                nfe.printStackTrace();
            }
        }else if(id == R.id.key_clear){
            String currentText = amountText.getText().toString();
            if(currentText.length()>0) {
                amountText.setText(currentText.substring(0, currentText.length() - 1));
            }
        }else{
            int start = amountText.getSelectionStart();
            String input = ((TextView) view).getText().toString();
            if(start<amountText.length()) {
                amountText.getText().insert(start, input);
            }else{
                amountText.append(input);
            }
        }

    }

    public void setCallback(PaymentDistributionAmountCallback callback) {
        this.callback = callback;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        private boolean amountChangeFlag = true;
        private String balanceBeforeTextChange;

        @Override
        public void afterTextChanged(Editable str) {
        }

        @Override
        public void beforeTextChanged(CharSequence str, int start, int count, int after) {
//            amountSymbolTextView.setTextColor(getResources().getColor(R.color.white));
            balanceBeforeTextChange = str.toString();
        }

        @Override
        public void onTextChanged(CharSequence str, int start, int before, int count) {
            String amountString = str.toString();
            try {
                if (amountChangeFlag) {
                    // flag to avoid the onTextChanged listener call after setText manipulated number
                    amountChangeFlag = false;
                    //when deleting a decimal point, delete any non-integral portion of the number
                    if (balanceBeforeTextChange.contains(".") && !amountString.contains(".")) {
                        amountString = balanceBeforeTextChange.substring(0, balanceBeforeTextChange.indexOf("."));
                        amountText.setText(amountString);
                        amountText.setSelection(amountString.length());
                    } else
                        // user cannot enter amountText less than 1
                        if (amountString.equalsIgnoreCase(".") || Double.parseDouble(amountString) < 1) {
                            amountString = "0";
                            amountText.setText(amountString);
                        } else
                            // Only when user enters dot, we should show the decimal values as 00
                            if (amountString.endsWith(".")) {
                                amountString = amountString + "00";
                                amountText.setText(amountString);
                                amountText.setSelection(amountString.length() - 2);
                                // When user removes dot, we should show the integer before that
                            } else if (amountString.endsWith(".0")) {
                                amountText.setText(amountString.substring(0, amountString.length() - 2));
                                amountText.setSelection(amountText.length());
                                // When user enters number, we should simply append the number entered
                                // Also adjusting the cursor position after removing DOT and appending number
                            } else {
                                if (amountString.contains(".") && amountString.length() - 3 > amountString.indexOf(".")) {
                                    //enterPartialAmountEditText.setText(new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER).format(Double.parseDouble(amountString)));
                                    amountString = amountString.substring(0, amountString.indexOf(".") + 3);
                                    amountText.setText(amountString);
                                } else {
                                    amountText.setText(amountString);
                                }
                                if (amountText.getText().toString().endsWith("0") && amountString.contains(".")) {
                                    amountText.setSelection(amountString.length() - 1);
                                } else {
                                    amountText.setSelection(amountString.length());
                                }
                            }
                } else {
                    amountChangeFlag = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
