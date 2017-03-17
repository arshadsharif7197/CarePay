package com.carecloud.carepaylibray.customdialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class RequestAppointmentDialog extends BaseDoctorInfoDialog {

    private Context context;
    private LinearLayout mainLayout;
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentDTO appointmentDTO;

    private AppointmentNavigationCallback callback;

    /**
     * Constructor.
     * @param context activity context
     * @param appointmentDTO appointment model
     * @param appointmentsResultModel appointments result model
     */
    public RequestAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                    AppointmentsResultModel appointmentsResultModel) {

        super(context, appointmentDTO, true);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.appointmentsResultModel = appointmentsResultModel;
        setupCallback();
    }

    private void setupCallback(){
        try{
            callback = (AppointmentNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Provided Context must implement AppointmentNavigationCallback");
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mainLayout = (LinearLayout) getAddActionChildView();
        setActionButton();
    }

    @SuppressLint("InflateParams")
    private void setActionButton() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_request_appointment, null);

        Button appointmentRequestButton = (Button) childActionView.findViewById(R.id.requestAppointmentButton);
        appointmentRequestButton.setText(appointmentsResultModel.getMetadata().getLabel().getAppointmentsRequestHeading());
        appointmentRequestButton.setOnClickListener(this);
        appointmentRequestButton.requestFocus();

        CarePayTextView reasonTypeTextView = (CarePayTextView)
                childActionView.findViewById(R.id.reasonTypeTextView);
        SystemUtil.setProximaNovaRegularTypeface(context, reasonTypeTextView);
        reasonTypeTextView.setText(appointmentsResultModel.getMetadata().getLabel().getVisitTypeHeading());

        CarePayTextView reasonTextView = (CarePayTextView)
                childActionView.findViewById(R.id.reasonTextView);
        SystemUtil.setProximaNovaRegularTypeface(context, reasonTextView);

        String visitReason = (String) appointmentDTO.getPayload().getChiefComplaint();
        if (SystemUtil.isNotEmptyString(visitReason)) {
            reasonTextView.setText(visitReason);
        }

        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.requestAppointmentButton) {
            dismiss();
            callback.requestAppointment(appointmentDTO.getPayload().getStartTime(), appointmentDTO.getPayload().getEndTime(), "");
        }
    }
}
