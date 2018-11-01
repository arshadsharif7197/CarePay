package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;

import java.text.NumberFormat;
import java.util.Locale;

public class RequestAppointmentDialog extends BaseDoctorInfoDialog {

    private final AppointmentsSlotsDTO appointmentSlot;
    private Context context;
    private VisitTypeDTO visitTypeDTO;

    private AppointmentNavigationCallback callback;

    /**
     * Constructor.
     *
     * @param context          activity context
     * @param appointmentDTO   appointment model
     * @param appointmentsSlot The appointment slot
     */
    public RequestAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                    AppointmentsSlotsDTO appointmentsSlot,
                                    VisitTypeDTO visitTypeDTO) {

        super(context, appointmentDTO, true);
        this.context = context;
        this.appointmentSlot = appointmentsSlot;
        this.visitTypeDTO = visitTypeDTO;
        setupCallback();
    }

    private void setupCallback() {
        try {
            if (context instanceof AppointmentViewHandler) {
                callback = ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (AppointmentNavigationCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided Context must implement AppointmentNavigationCallback");
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setActionButton();
    }

    @SuppressLint("InflateParams")
    private void setActionButton() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_request_appointment, null);

        boolean autoScheduleAppointments = callback.getAppointmentsSettings().getRequests().getAutomaticallyApproveRequests();
        View view = findViewById(R.id.appointDialogButtonLayout);
        view.setVisibility(View.VISIBLE);
        Button appointmentRequestButton = (Button) findViewById(R.id.requestAppointmentButton);
        appointmentRequestButton.setText(Label.getLabel(autoScheduleAppointments ?
                "appointments_schedule_button" : "appointments_request_heading"));
        appointmentRequestButton.requestFocus();

        TextView reasonTextView = (TextView) childActionView.findViewById(R.id.reasonTextView);
        String visitReason = visitTypeDTO.getName();
        reasonTextView.setText(visitReason);

        final EditText reasonForVisitEditText = (EditText) childActionView.findViewById(R.id.reasonForVisitEditText);

        View prepaidLayout = childActionView.findViewById(R.id.prepaymentLayout);
        if (visitTypeDTO.getAmount() > 0) {
            prepaidLayout.setVisibility(View.VISIBLE);
            TextView prepaidAmount = (TextView) childActionView.findViewById(R.id.prepaymentAmount);
            prepaidAmount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(visitTypeDTO.getAmount()));
            appointmentRequestButton.setText(Label.getLabel("appointments_prepayment_button"));
        } else {
            prepaidLayout.setVisibility(View.GONE);
        }

        appointmentRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                final String reasonForVisit = reasonForVisitEditText.getText().toString();
                callback.requestAppointment(appointmentSlot, reasonForVisit);
            }
        });

        ((ViewGroup) getAddActionChildView()).addView(childActionView);
    }
}
