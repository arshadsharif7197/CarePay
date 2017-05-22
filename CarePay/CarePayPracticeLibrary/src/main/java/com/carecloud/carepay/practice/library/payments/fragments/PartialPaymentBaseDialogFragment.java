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
import com.carecloud.carepaylibray.base.BaseDialogFragment;

import java.text.NumberFormat;

/**
 * Created by pjohnson on 3/23/17
 */
public abstract class PartialPaymentBaseDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    protected EditText amountText;
    protected TextView amountSymbol;
    protected Button apply;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.dialog_partial_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        amountText = (EditText) view.findViewById(R.id.enter_amount_text);
        amountText.addTextChangedListener(payAmountTextFormatter);
        String symbol = NumberFormat.getCurrencyInstance().getCurrency().getSymbol();
        amountSymbol = (TextView) view.findViewById(R.id.amountSymbolTextView);
        amountSymbol.setText(symbol);
        apply = (Button) view.findViewById(R.id.enter_amount_button);
        apply.setOnClickListener(this);
        View close = view.findViewById(R.id.closeViewLayout);
        close.setOnClickListener(this);
        setEntryListeners(view);
    }

    private void setEntryListeners(View view) {
        view.findViewById(R.id.key_one).setOnClickListener(digitsClickListener);
        view.findViewById(R.id.key_two).setOnClickListener(digitsClickListener);
        view.findViewById(R.id.key_three).setOnClickListener(digitsClickListener);
        view.findViewById(R.id.key_four).setOnClickListener(digitsClickListener);
        view.findViewById(R.id.key_five).setOnClickListener(digitsClickListener);
        view.findViewById(R.id.key_six).setOnClickListener(digitsClickListener);
        view.findViewById(R.id.key_seven).setOnClickListener(digitsClickListener);
        view.findViewById(R.id.key_eighth).setOnClickListener(digitsClickListener);
        view.findViewById(R.id.key_nine).setOnClickListener(digitsClickListener);
        view.findViewById(R.id.key_zero).setOnClickListener(digitsClickListener);
        view.findViewById(R.id.key_blank).setOnClickListener(digitsClickListener);
        view.findViewById(R.id.key_clear).setOnClickListener(digitsClickListener);
    }

    View.OnClickListener digitsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.key_clear) {
                String currentText = amountText.getText().toString();
                if (currentText.length() > 0) {
                    amountText.setText(currentText.substring(0, currentText.length() - 1));
                }
            } else {
                int start = amountText.getSelectionStart();
                String input = ((TextView) view).getText().toString();
                if (start < amountText.length()) {
                    amountText.getText().insert(start, input);
                } else {
                    amountText.append(input);
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.closeViewLayout) {
            dismiss();
        }
    }


    private TextWatcher payAmountTextFormatter = new TextWatcher() {
        private boolean amountChangeFlag = true;
        private String balanceBeforeTextChange;

        @Override
        public void afterTextChanged(Editable str) {
            if (str.length() > 0) {
                amountSymbol.setTextColor(getResources().getColor(R.color.white));
                amountText.setHint(null);
            } else {
                amountSymbol.setTextColor(getResources().getColor(R.color.white_transparent));
                amountText.setHint("0.00");
            }
        }

        @Override
        public void beforeTextChanged(CharSequence str, int start, int count, int after) {
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
