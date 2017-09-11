package com.carecloud.carepay.patient.appointments.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentCancellationFee;

/**
 * @author pjohnson
 */
public class CancelAppointmentFeeDialog extends Dialog {

    private final AppointmentCancellationFee cancellationFee;
    private Context context;


    public interface CancelAppointmentFeeDialogListener {
        void onCancelAppointmentFeeAccepted();
    }

    private CancelAppointmentFeeDialogListener callback;

    /**
     * @param context         the context
     * @param cancellationFee the cancellation fee
     * @param callback        the callback
     */
    public CancelAppointmentFeeDialog(Context context,
                                      AppointmentCancellationFee cancellationFee,
                                      CancelAppointmentFeeDialogListener callback) {
        super(context);
        this.context = context;
        this.cancellationFee = cancellationFee;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cancel_appointment_fee);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setAttributes(params);

        TextView cancellationFeeMessageTextView = (TextView) findViewById(R.id.cancellationFeeMessageTextView);
        cancellationFeeMessageTextView.setText(String
                .format(Label.getLabel("appointment_cancellation_fee_message"), cancellationFee.getAmount()));


        Button cancelAppointmentButton = (Button) findViewById(R.id.cancelAppointment);
        cancelAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancelAppointmentFeeAccepted();
                cancel();
            }
        });

        findViewById(R.id.dialogCloseHeaderImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }
}