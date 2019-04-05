package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.BaseRequestAppointmentDialogFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.NumberFormat;
import java.util.Locale;

public class PatientModeRequestAppointmentDialog extends BaseRequestAppointmentDialogFragment {

    public static PatientModeRequestAppointmentDialog newInstance(AppointmentDTO appointmentDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDTO);
        PatientModeRequestAppointmentDialog fragment = new PatientModeRequestAppointmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_request_appointment_patient_mode, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestAppointmentButton = view.findViewById(R.id.requestAppointmentButton);
        ApplicationMode.ApplicationType applicationType = getApplicationMode().getApplicationType();
        boolean autoScheduleAppointments = getAutomaticallyApproveRequests();

        final EditText reasonForVisitEditText = view.findViewById(R.id.reasonForVisitEditText);
        requestAppointmentButton.setText(Label.getLabel(applicationType == ApplicationMode.ApplicationType.PRACTICE ||
                autoScheduleAppointments ? "schedule_appointment_button_label" : "appointments_request_heading"));
        requestAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String reasonForVisit = reasonForVisitEditText.getText().toString();
                appointmentDTO.getPayload().setReasonForVisit(reasonForVisit);
                requestAppointment(appointmentDTO);
            }
        });

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getStartTime());

//        setDialogTitle(dateUtil.getDateAsDayMonthDayOrdinalYear(Label.getLabel("appointments_web_today_heading")));

        TextView appointment_date = view.findViewById(R.id.content_view_header_title);
        appointment_date.setText(dateUtil.getDateAsDayMonthDayOrdinalYear(Label.getLabel("appointments_web_today_heading")));

        TextView appointmentTimeTextView = view.findViewById(R.id.appointment_time);
        appointmentTimeTextView.setText(dateUtil.getTime12Hour());

        TextView providerImageTextView = view.findViewById(R.id.provider_short_name);
        ProviderDTO provider = appointmentDTO.getPayload().getResource().getProvider();
        providerImageTextView.setText(StringUtil.getShortName(provider.getName()));

        TextView appointmentDoctorNameTextView = view.findViewById(R.id.provider_doctor_name);
        appointmentDoctorNameTextView.setText(String
                .format("%s | %s", provider.getName(), provider.getSpecialty().getName()));

        ImageView picImageView = view.findViewById(R.id.picImageView);
        PicassoHelper.get().loadImage(getContext(), picImageView, null, provider.getPhoto());


        TextView appointmentPlaceNameTextView = view.findViewById(R.id.provider_place_name);
        appointmentPlaceNameTextView.setText(appointmentDTO.getPayload().getLocation().getName());

        TextView appointmentAddressTextView = view.findViewById(R.id.provider_place_address);
        appointmentAddressTextView.setText(appointmentDTO.getPayload().getLocation().getAddress()
                .geAddressStringWithShortZipWOCounty2Lines());

        VisitTypeDTO visitTypeDTO = appointmentDTO.getPayload().getVisitType();
        TextView visitTypeTextView = view.findViewById(R.id.reasonTextView);
        visitTypeTextView.setText(visitTypeDTO.getName());

        View videoVisitIndicator = view.findViewById(R.id.visit_type_video);
        videoVisitIndicator.setVisibility(appointmentDTO.getPayload().getVisitType()
                .hasVideoOption() ? View.VISIBLE : View.GONE);

        View prepaidLayout = findViewById(R.id.prepaymentLayout);
        if (visitTypeDTO.getAmount() > 0) {
            prepaidLayout.setVisibility(View.VISIBLE);
            TextView prepaidAmount = view.findViewById(R.id.prepaymentAmount);
            prepaidAmount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(visitTypeDTO.getAmount()));
            requestAppointmentButton.setText(Label.getLabel("appointments_prepayment_button"));
        } else {
            prepaidLayout.setVisibility(View.GONE);
        }

        view.findViewById(R.id.closeViewLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final ScrollView scrollContainer = view.findViewById(R.id.scrollContainer);
        scrollContainer.post(new Runnable() {
            @Override
            public void run() {
                scrollContainer.fullScroll(View.FOCUS_UP);
                SystemUtil.hideSoftKeyboard(getContext(), view);
            }
        });
    }

    private boolean getAutomaticallyApproveRequests() {
        AppointmentsSettingDTO appointmentsSettingDTO = appointmentModelDto.getPayload()
                .getAppointmentsSetting(selectedPractice.getPracticeId());
        if (appointmentsSettingDTO == null) {
            return false;
        }
        return appointmentsSettingDTO.getRequests().getAutomaticallyApproveRequests();
    }

    @Override
    protected void logMixPanelAppointmentRequestedEvent(AppointmentDTO appointmentRequestDto) {
        ApplicationMode.ApplicationType applicationType = getApplicationMode().getApplicationType();
        String[] params = {getString(R.string.param_appointment_type),
                getString(R.string.param_practice_id),
                getString(R.string.param_practice_name),
                getString(R.string.param_patient_id)};
        String[] values = {appointmentRequestDto.getPayload().getVisitType().getName(),
                getApplicationMode().getUserPracticeDTO().getPracticeId(),
                getApplicationMode().getUserPracticeDTO().getPracticeName(),
                patientId};
        MixPanelUtil.logEvent(getString(applicationType == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE ?
                R.string.event_appointment_requested : R.string.event_appointment_scheduled), params, values);
        if (applicationType == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            MixPanelUtil.incrementPeopleProperty(getString(R.string.count_appointment_requested), 1);
        }
    }

}
