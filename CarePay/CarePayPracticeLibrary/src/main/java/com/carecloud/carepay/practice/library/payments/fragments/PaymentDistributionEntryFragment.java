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
import com.carecloud.carepaylibray.utils.StringUtil;

/**
 * Created by lmenendez on 3/17/17
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
            if(chargeItem.getAmount() > 0) {
                amountText.setText(String.valueOf(chargeItem.getAmount()));
            }
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
            if (balanceItem != null) {
                callback.applyAmountToBalanceItem(getDoubleAmountForEntry(), balanceItem);
            } else if (chargeItem != null) {
                callback.addNewCharge(getDoubleAmountForEntry(), chargeItem);
            } else {
                callback.applyNewDistributionAmount(getDoubleAmountForEntry());
            }
            dismiss();
        }

    }

    private double getDoubleAmountForEntry(){
        String entry = amountText.getText().toString();
        if(StringUtil.isNullOrEmpty(entry)){
            return 0D;
        }
        try {
            return Double.parseDouble(entry);
        }catch (NumberFormatException nfe){
            nfe.printStackTrace();
        }
        return 0D;
    }

    /**
     * sets the callback
     * @param callback the callback
     */
    public void setCallback(PaymentDistributionAmountCallback callback) {
        this.callback = callback;
    }

    /**
     * sets the balance item
     * @param balanceItem the balance item
     */
    public void setBalanceItem(BalanceItemDTO balanceItem) {
        this.balanceItem = balanceItem;
    }

    /**
     * sets the charge item
     * @param chargeItem charge item
     */
    public void setChargeItem(SimpleChargeItem chargeItem) {
        this.chargeItem = chargeItem;
    }


}
