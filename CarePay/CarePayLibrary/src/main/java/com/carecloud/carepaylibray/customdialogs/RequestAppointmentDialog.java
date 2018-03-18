package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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
     * @param context           activity context
     * @param appointmentDTO    appointment model
     * @param appointmentsSlot  The appointment slot
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
            if(context instanceof AppointmentViewHandler){
                callback = ((AppointmentViewHandler) context).getAppointmentPresenter();
            }else {
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

        Button appointmentRequestButton = (Button) childActionView.findViewById(R.id.requestAppointmentButton);
        appointmentRequestButton.setText(Label.getLabel("appointments_request_heading"));
        appointmentRequestButton.setOnClickListener(this);
        appointmentRequestButton.requestFocus();

        TextView reasonTextView = (TextView) childActionView.findViewById(R.id.reasonTextView);

        String visitReason = visitTypeDTO.getName();// (String) appointmentDTO.getPayload().getChiefComplaint();
        reasonTextView.setText(visitReason);

        View prepaidLayout = childActionView.findViewById(R.id.prepaymentLayout);
        if(visitTypeDTO.getAmount() > 0){
            prepaidLayout.setVisibility(View.VISIBLE);
            TextView prepaidAmount = (TextView) childActionView.findViewById(R.id.prepaymentAmount);
            prepaidAmount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(visitTypeDTO.getAmount()));
            appointmentRequestButton.setText(Label.getLabel("appointments_prepayment_button"));
        }else{
            prepaidLayout.setVisibility(View.GONE);
        }

        ((ViewGroup) getAddActionChildView()).addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.requestAppointmentButton) {
            dismiss();
            callback.requestAppointment(appointmentSlot, "");
        }
    }
}
