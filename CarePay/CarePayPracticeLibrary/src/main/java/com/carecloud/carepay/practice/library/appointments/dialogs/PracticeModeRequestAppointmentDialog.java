package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;

public class PracticeModeRequestAppointmentDialog extends PatientModeRequestAppointmentDialog {


    /**
     * Constructor.
     *
     * @param context                 activity context
     * @param cancelString            String
     * @param appointmentSlot         The appointment data
     * @param appointmentResourcesDTO
     * @param visitTypeDTO
     * @param callback                for dialog actions
     */
    public PracticeModeRequestAppointmentDialog(Context context,
                                                String cancelString,
                                                AppointmentsSlotsDTO appointmentSlot,
                                                AppointmentResourcesDTO appointmentResourcesDTO,
                                                VisitTypeDTO visitTypeDTO,
                                                AppointmentNavigationCallback callback) {
        super(context, cancelString, appointmentSlot, appointmentResourcesDTO, visitTypeDTO, callback);
    }

    protected void inflateUIComponents(View view) {
        super.inflateUIComponents(view);
        TextView appointmentTimeTextView = findViewById(R.id.appointment_time);
        appointmentTimeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        View summaryHeaderContainer = findViewById(R.id.summary_header_layout);
        summaryHeaderContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark_blue));
        View toolbar = findViewById(R.id.content_view_header_title);
        toolbar.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.background_top_rounded_dark_blue));
    }


}
