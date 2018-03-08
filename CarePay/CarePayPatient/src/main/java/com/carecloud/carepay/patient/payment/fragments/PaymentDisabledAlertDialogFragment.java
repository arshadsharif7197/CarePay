package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsBalancesItem;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 10/9/17
 */
public class PaymentDisabledAlertDialogFragment extends BaseDialogFragment {

    public interface DisabledPaymentAlertCallback {
        void onContinueClicked();

        void onAlertClicked();
    }

    private DisabledPaymentAlertCallback callback;

    public static PaymentDisabledAlertDialogFragment newInstance(String title, String message,
                                                                 PaymentsBalancesItem selectedBalancesItem) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        DtoHelper.bundleDto(args, selectedBalancesItem);
        PaymentDisabledAlertDialogFragment fragment = new PaymentDisabledAlertDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DisabledPaymentAlertCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement DisabledPaymentAlertCallback");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.dialog_alert_payment_disabled, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        View close = view.findViewById(R.id.dialogCloseHeaderImageView);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        View cancel = view.findViewById(R.id.continueButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callback.onContinueClicked();
            }
        });

        View proceed = view.findViewById(R.id.alertProviderButton);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callback.onAlertClicked();
            }
        });
        Bundle arguments = getArguments();
        PaymentsBalancesItem selectedBalance = DtoHelper.getConvertedDTO(PaymentsBalancesItem.class, arguments);

        TextView titleTextView = (TextView) view.findViewById(R.id.dialogTitle);
        titleTextView.setText(String.format(arguments.getString("title"),
                selectedBalance.getMetadata().getPracticeName()));
        TextView messageTextView = (TextView) view.findViewById(R.id.dialogMessage);
        messageTextView.setText(arguments.getString("message"));
    }
}
