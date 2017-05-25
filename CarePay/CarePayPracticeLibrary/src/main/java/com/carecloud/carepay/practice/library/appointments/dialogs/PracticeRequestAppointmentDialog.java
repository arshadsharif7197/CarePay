package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.customdialog.BasePracticeDialog;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class PracticeRequestAppointmentDialog extends BasePracticeDialog {

    private final AppointmentsSlotsDTO appointmentSlot;
    private Context context;
    private final LayoutInflater inflater;
    private final AppointmentResourcesDTO appointmentResourcesDTO;
    private VisitTypeDTO visitTypeDTO;
    private final AppointmentNavigationCallback callback;
    private TextView visitTypeTextView;

    /**
     * Constructor.
     *
     * @param context         activity context
     * @param cancelString    String
     * @param appointmentSlot The appointment data
     * @param callback        for dialog actions
     */
    public PracticeRequestAppointmentDialog(Context context, String cancelString,
                                            AppointmentsSlotsDTO appointmentSlot,
                                            AppointmentResourcesDTO appointmentResourcesDTO,
                                            VisitTypeDTO visitTypeDTO,
                                            AppointmentNavigationCallback callback) {

        super(context, cancelString, false);
        this.context = context;
        this.appointmentSlot = appointmentSlot;
        this.appointmentResourcesDTO = appointmentResourcesDTO;
        this.visitTypeDTO = visitTypeDTO;
        this.callback = callback;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        onAddContentView(inflater);
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onAddContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_request_appointment_summary, null);
        ((FrameLayout) findViewById(com.carecloud.carepay.practice.library.R.id.base_dialog_content_layout)).addView(view);
        inflateUIComponents(view);
    }

    @Override
    protected void onAddFooterView(LayoutInflater inflater) {

    }

    private void inflateUIComponents(View view) {
        Button requestAppointmentButton = (Button)
                view.findViewById(R.id.requestAppointmentButton);
        requestAppointmentButton.setText(Label.getLabel("appointments_request_heading"));
        requestAppointmentButton.setOnClickListener(requestAppointmentClickListener);
        requestAppointmentButton.requestFocus();
        SystemUtil.setGothamRoundedBookTypeface(context, requestAppointmentButton);

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentSlot.getStartTime());

        setDialogTitle(dateUtil.getDateAsDayMonthDayOrdinalYear(Label.getLabel("appointments_web_today_heading")));

        CarePayTextView appointmentTimeTextView = (CarePayTextView) view.findViewById(R.id.appointment_time);
        appointmentTimeTextView.setText(dateUtil.getTime12Hour());
        SystemUtil.setGothamRoundedBoldTypeface(context, appointmentTimeTextView);

        CarePayTextView providerImageTextView = (CarePayTextView) view.findViewById(R.id.provider_short_name);
        providerImageTextView.setText(StringUtil.getShortName(appointmentResourcesDTO.getResource().getProvider().getName()));
        CarePayTextView appointmentDoctorNameTextView = (CarePayTextView) view.findViewById(R.id.provider_doctor_name);
        appointmentDoctorNameTextView.setText(appointmentResourcesDTO.getResource().getProvider().getName());
        CarePayTextView appointmentDoctorSpecialityTextView = (CarePayTextView) view.findViewById(R.id.provider_doctor_speciality);
        appointmentDoctorSpecialityTextView.setText(appointmentResourcesDTO.getResource().getProvider().getSpecialty().getName());

        CarePayTextView appointmentPlaceNameTextView = (CarePayTextView) view.findViewById(R.id.provider_place_name);
        appointmentPlaceNameTextView.setText(appointmentSlot.getLocation().getName());
        SystemUtil.setProximaNovaExtraboldTypeface(context, appointmentPlaceNameTextView);
        CarePayTextView appointmentAddressTextView = (CarePayTextView) view.findViewById(R.id.provider_place_address);
        appointmentAddressTextView.setText(appointmentSlot.getLocation().getAddress().getPlaceAddressString());
        CarePayTextView visitTypeLabel = (CarePayTextView) view.findViewById(R.id.visitTypeLabel);
        visitTypeLabel.setText(Label.getLabel("visit_type_heading"));

        initializeVisitTypeTextView(view);

        setCancelImage(R.drawable.icn_arrow_left);
        setCancelable(false);
    }

    private void initializeVisitTypeTextView(View view) {
        visitTypeTextView = (TextView) view.findViewById(R.id.reasonTextView);

        if (null == visitTypeDTO) {
            dismiss();
            return;
        }

        String visitReason = visitTypeDTO.getName();
        if (SystemUtil.isNotEmptyString(visitReason)) {
            visitTypeTextView.setText(visitReason);
        }
    }

    /**
     * Click listener for request appointment button
     */
    private View.OnClickListener requestAppointmentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (null != callback) {
                callback.requestAppointment(appointmentSlot,
                        visitTypeTextView.getText().toString().trim());
            }
            dismiss();
        }
    };

    @Override
    protected void onDialogCancel() {
        super.onDialogCancel();

        if (null != callback) {
            callback.onAppointmentUnconfirmed();
        }

        dismiss();
    }
}
