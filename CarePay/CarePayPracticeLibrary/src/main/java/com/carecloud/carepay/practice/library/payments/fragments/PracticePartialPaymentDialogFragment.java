package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by pjohnson on 23/03/17.
 */

public class PracticePartialPaymentDialogFragment extends PartialPaymentBaseDialogFragment {

    private PaymentsModel paymentsModel;
    private double amount;
    private PaymentNavigationCallback callback;

    /**
     * @param paymentResultModel the payment model
     * @param owedAmount         amount owed
     * @return an instance of PracticePartialPaymentDialogFragment
     */
    public static PracticePartialPaymentDialogFragment newInstance(PaymentsModel paymentResultModel, double owedAmount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentResultModel);
        args.putDouble("amount", owedAmount);
        PracticePartialPaymentDialogFragment fragment = new PracticePartialPaymentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PaymentNavigationCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, arguments);
        amount = arguments.getDouble("amount");
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        TextView pendingAmountTextView = (TextView) view.findViewById(R.id.pendingAmountTextView);
        pendingAmountTextView.setVisibility(View.VISIBLE);
        pendingAmountTextView.setText(Label.getLabel("payment_pending_text") + " " + amount);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if ((view.getId() == R.id.enter_amount_button) && !amountText.getText().toString().isEmpty()) {
            callback.onPayButtonClicked(Double.parseDouble(amountText.getText().toString()), paymentsModel);
            dismiss();
        }
    }
}
