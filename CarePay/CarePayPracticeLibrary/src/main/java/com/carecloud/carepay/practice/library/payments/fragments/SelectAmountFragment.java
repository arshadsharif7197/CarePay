package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;

import java.text.NumberFormat;
import java.util.Locale;

public class SelectAmountFragment extends BaseDialogFragment {

    private double maxAmount;
    private NumberFormat currencyFormat;
    private SelectAmountInterface callback;
    protected String numberStr = "";
    private boolean numberIsDecimal;
    private boolean putADecimal;
    private TextView amountTextView;
    private TextView amountSymbol;
    private Button applyAmountButton;

    /**
     * @return an instance of SelectAmountFragment
     */
    public static SelectAmountFragment newInstance(double maxAmount) {
        Bundle args = new Bundle();
        args.putDouble("maxAmount", maxAmount);
        SelectAmountFragment fragment = new SelectAmountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        maxAmount = args.getDouble("maxAmount");
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.dialog_fragment_select_amount, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        TextView header = (TextView) findViewById(R.id.dialogTitle);
        header.setText(Label.getLabel("payment.selectAmount.title.label.title"));
        TextView footer = (TextView) findViewById(R.id.maxAmountTextView);
        footer.setText(String.format(Label.getLabel("payment.selectAmount.label.label.maxAmount"),
                currencyFormat.format(maxAmount)));
        applyAmountButton = view.findViewById(R.id.enterAmountButton);
        applyAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callback.onAmountSelected(Double.parseDouble(numberStr));
            }
        });
        amountTextView = view.findViewById(R.id.enterAmountTextView);
        String symbol = currencyFormat.getCurrency().getSymbol();
        amountSymbol = view.findViewById(R.id.amountSymbolTextView);
        amountSymbol.setText(symbol);
        setEntryListeners(view);
        View close = view.findViewById(R.id.closeViewLayout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setCallback(SelectAmountInterface callback) {
        this.callback = callback;
    }

    public interface SelectAmountInterface {
        void onAmountSelected(double amount);
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
                amountTextView.setTextSize(getResources().getDimension(R.dimen.amountCalculatorEntryTextSizeBig));
            } else if (numberStr.length() < 7) {
                amountTextView.setTextSize(getResources().getDimension(R.dimen.amountCalculatorEntryTextSize65));
            } else if (numberStr.length() < 10) {
                amountTextView.setTextSize(getResources().getDimension(R.dimen.amountCalculatorEntryTextSize50));
            }
            enableApplyAmountButton(numberStr);
        }
    };

    private void enableApplyAmountButton(String numberStr) {
        boolean enable = !numberStr.isEmpty();
        if (!enable) {
            applyAmountButton.setEnabled(false);
            return;
        }

        double amount = Double.valueOf(numberStr);
        applyAmountButton.setEnabled(amount <= maxAmount);
    }

}
