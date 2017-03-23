package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.payments.models.SimpleChargeItem;

/**
 * Created by lmenendez on 3/17/17.
 */
public class PaymentDistributionEntryFragment extends PartialPaymentBaseDialogFragment implements View.OnClickListener {
    public interface PaymentDistributionAmountCallback {
        void applyNewDistributionAmount(double amount);

        void applyAmountToBalanceItem(double amount, BalanceItemDTO balanceItemDTO);

        void addNewCharge(double amount, SimpleChargeItem chargeItem);
    }

    private PaymentDistributionAmountCallback callback;
    private BalanceItemDTO balanceItem;
    private SimpleChargeItem chargeItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.dialog_enter_amount, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        TextView header = (TextView) view.findViewById(R.id.enter_amount_header);
        if (chargeItem != null) {
            apply.setText(Label.getLabel("payment_add_item"));
            header.setText(chargeItem.getDescription());
            amountText.setText(String.valueOf(chargeItem.getAmount()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.enter_amount_button && callback != null) {
            try {
                if (balanceItem != null) {
                    callback.applyAmountToBalanceItem(Double.parseDouble(amountText.getText().toString()), balanceItem);
                } else if (chargeItem != null) {
                    callback.addNewCharge(Double.parseDouble(amountText.getText().toString()), chargeItem);
                } else {
                    callback.applyNewDistributionAmount(Double.parseDouble(amountText.getText().toString()));
                }
                dismiss();
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }

    }

    public void setCallback(PaymentDistributionAmountCallback callback) {
        this.callback = callback;
    }

    public void setBalanceItem(BalanceItemDTO balanceItem) {
        this.balanceItem = balanceItem;
    }

    public void setChargeItem(SimpleChargeItem chargeItem) {
        this.chargeItem = chargeItem;
    }


}
