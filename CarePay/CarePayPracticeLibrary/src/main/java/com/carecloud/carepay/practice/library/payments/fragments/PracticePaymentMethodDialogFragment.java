package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;

/**
 * A simple {@link Fragment} subclass.
 */
public class PracticePaymentMethodDialogFragment extends PracticePaymentMethodFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method_dialog_practice, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        super.onViewCreated(view, icicle);
        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        TextView title = (TextView) view.findViewById(R.id.respons_toolbar_title);
        ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        title.setLayoutParams(layoutParams);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    protected void handlePaymentButton(PaymentsMethodsDTO paymentMethod, double amount){
        super.handlePaymentButton(paymentMethod, amount);
        dismiss();
    }

}