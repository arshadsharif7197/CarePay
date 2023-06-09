package com.carecloud.carepay.patient.appointments.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentCancellationFee;
import com.carecloud.carepaylibray.base.BaseDialogFragment;

/**
 * @author pjohnson
 */
public class CancelAppointmentFeeDialog extends BaseDialogFragment {

    private static CancelAppointmentFeeDialog fragment;
    private String cancellationFeeAmount;

    public interface CancelAppointmentFeeDialogListener {
        void onCancelAppointmentFeeAccepted();
    }

    private CancelAppointmentFeeDialogListener callback;

    /**
     * @param cancellationFee the cancellation fee
     */
    public static CancelAppointmentFeeDialog newInstance(AppointmentCancellationFee cancellationFee) {
        Bundle args = new Bundle();
        args.putString("cancellationFeeAmount", cancellationFee.getAmount());
        fragment = new CancelAppointmentFeeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static CancelAppointmentFeeDialog getInstance() {
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        cancellationFeeAmount = args.getString("cancellationFeeAmount");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_cancel_appointment_fee, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView cancellationFeeMessageTextView = (TextView) findViewById(R.id.cancellationFeeMessageTextView);
        cancellationFeeMessageTextView.setText(String
                .format(Label.getLabel("appointment_cancellation_fee_message"), cancellationFeeAmount));
        Button cancelAppointmentButton = (Button) findViewById(R.id.cancelAppointment);
        cancelAppointmentButton.setOnClickListener(view1 -> {
            callback.onCancelAppointmentFeeAccepted();
            hideDialog();
        });
        findViewById(R.id.dialogCloseHeaderImageView).setOnClickListener(view12 -> {
            cancel();
        });
    }

    public void setCancelFeeDialogListener(CancelAppointmentFeeDialogListener callback) {
        this.callback = callback;
    }
}