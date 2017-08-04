package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;

import java.text.NumberFormat;

/**
 * Created by pjohnson on 3/23/17
 */
public abstract class PartialPaymentBaseDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    protected TextView amountTextView;
    protected TextView amountSymbol;
    protected Button applyButton;
    protected String numberStr = "";
    private boolean numberIsDecimal;
    private boolean putADecimal;
    protected NumberFormat currencyFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.dialog_fragment_partial_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        amountTextView = (TextView) view.findViewById(R.id.enter_amount_text);

        currencyFormat = NumberFormat.getCurrencyInstance();
        String symbol = currencyFormat.getCurrency().getSymbol();
        amountSymbol = (TextView) view.findViewById(R.id.amountSymbolTextView);
        amountSymbol.setText(symbol);
        applyButton = (Button) view.findViewById(R.id.enter_amount_button);
        applyButton.setOnClickListener(this);
        View close = view.findViewById(R.id.closeViewLayout);
        close.setOnClickListener(this);
        setEntryListeners(view);

        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(0);
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
                if (!numberStr.isEmpty()) {
                    numberStr = numberStr.substring(0, numberStr.length() - 1);
                    if (numberStr.isEmpty()) {
                        numberIsDecimal = false;
                        putADecimal = false;
                        currencyFormat.setMinimumFractionDigits(0);
                        amountSymbol.setTextColor(getResources().getColor(R.color.white_transparent));
                    } else if (!numberStr.contains(".")) {
                        numberIsDecimal = false;
                        putADecimal = false;
                        currencyFormat.setMinimumFractionDigits(0);
                    }
                    if (numberStr.isEmpty()) {
                        amountTextView.setText(numberStr);
                    } else {
                        amountTextView.setText(currencyFormat.format(Double.parseDouble(numberStr)));
                    }
                }
            } else if ((view.getId() == R.id.key_blank) ||
                    (view.getId() == R.id.key_zero && numberStr.isEmpty())) {
                if (numberStr.isEmpty()) {
                    putADecimal = true;
                    currencyFormat.setMinimumFractionDigits(2);
                } else if (!numberIsDecimal) {
                    putADecimal = true;
                    currencyFormat.setMinimumFractionDigits(2);
                    amountTextView.setText(currencyFormat.format(Double.parseDouble(numberStr)));
                }
            } else {
                if (numberStr.length() < 8 || numberIsDecimal || putADecimal) {
                    String input = ((TextView) view).getText().toString();
                    if (numberStr.isEmpty() && putADecimal) {
                        input = "0." + input;
                        numberIsDecimal = true;
                        putADecimal = false;
                    } else if (putADecimal) {
                        input = "." + input;
                        numberIsDecimal = true;
                        putADecimal = false;
                    }
                    if (!numberStr.contains(".") || (numberStr.length() - numberStr.indexOf(".") < 3)) {
                        numberStr = numberStr + input;
                        amountTextView.setText(currencyFormat.format(Double.parseDouble(numberStr)));
                    }

                    amountSymbol.setTextColor(getResources().getColor(R.color.white));
                }
            }
            if (numberStr.length() < 5) {
                amountTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80);
            } else if (numberStr.length() < 7) {
                amountTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 65);
            } else if (numberStr.length() < 10) {
                amountTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
            }
            
            updateLayout();
        }
    };

    protected abstract void updateLayout();

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.closeViewLayout) {
            dismiss();
        }
    }

}
