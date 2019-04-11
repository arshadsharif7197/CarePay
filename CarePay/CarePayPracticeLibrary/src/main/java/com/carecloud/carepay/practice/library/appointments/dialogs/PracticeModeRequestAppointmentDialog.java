package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

public class PracticeModeRequestAppointmentDialog extends PatientModeRequestAppointmentDialog {

    private PatientModel patientModel;

    public static PracticeModeRequestAppointmentDialog newInstance(AppointmentDTO appointmentDto,
                                                                   PatientModel patientModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDto);
        DtoHelper.bundleDto(args, patientModel);
        PracticeModeRequestAppointmentDialog fragment = new PracticeModeRequestAppointmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        patientModel = DtoHelper.getConvertedDTO(PatientModel.class, getArguments());
        patientId = patientModel.getPatientId();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView appointmentTimeTextView = view.findViewById(R.id.appointment_time);
        appointmentTimeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        View summaryHeaderContainer = view.findViewById(R.id.summary_header_layout);
        summaryHeaderContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark_blue));

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getStartTime());
        TextView toolbar = view.findViewById(R.id.content_view_header_title);
        toolbar.setText(dateUtil.getDateAsDayMonthDayOrdinalYear(Label.getLabel("appointments_web_today_heading"), Label.getLabel("add_appointment_tomorrow")));
        toolbar.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.background_top_rounded_dark_blue));

        TextView providerImageTextView = view.findViewById(R.id.provider_short_name);
        providerImageTextView.setText(StringUtil.getShortName(patientModel.getFullName()));
        TextView patientNameTextView = view.findViewById(R.id.provider_doctor_name);
        if (StringUtil.isNullOrEmpty(patientModel.getPhoneNumber())) {
            patientNameTextView.setText(StringUtil.capitalize(patientModel.getFullName()));
        } else {
            patientNameTextView.setText(String
                    .format("%s | %s", StringUtil.capitalize(patientModel.getFullName()),
                            StringUtil.formatPhoneNumber(patientModel.getPhoneNumber())));
        }

        TextView providerNameTextView = view.findViewById(R.id.providerName);
        providerNameTextView.setVisibility(View.VISIBLE);
        providerNameTextView.setText(StringUtil.capitalize(appointmentDTO.getPayload().getResource().getProvider().getFullName()));
        view.findViewById(R.id.provider_place_address).setVisibility(View.GONE);
        view.findViewById(R.id.provider_place_name).setVisibility(View.GONE);
        view.findViewById(R.id.addressSeparator).setVisibility(View.GONE);

        ImageView picImageView = view.findViewById(R.id.picImageView);
        PicassoHelper.get().loadImage(getContext(), picImageView, providerImageTextView, patientModel.getProfilePhoto());
    }


}
