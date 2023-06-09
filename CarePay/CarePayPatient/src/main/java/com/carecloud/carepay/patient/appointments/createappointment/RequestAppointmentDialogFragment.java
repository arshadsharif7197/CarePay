package com.carecloud.carepay.patient.appointments.createappointment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.BaseRequestAppointmentDialogFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author pjohnson on 1/17/19.
 */
public class RequestAppointmentDialogFragment extends BaseRequestAppointmentDialogFragment {


    private static RequestAppointmentDialogFragment fragment;

    public static RequestAppointmentDialogFragment newInstance(AppointmentDTO appointmentDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDTO);
        fragment = new RequestAppointmentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static RequestAppointmentDialogFragment getInstance() {
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getStartTime());
        TextView dateTextView = view.findViewById(R.id.appointDateTextView);
        TextView timeTextView = view.findViewById(R.id.appointTimeTextView);
        timeTextView.setText(DateUtil.getInstance().getDateAsWeekdayMonthDayYear());
        dateTextView.setText(DateUtil.getInstance().getTime12Hour());

        TextView shortNameTextView = view.findViewById(R.id.appointShortnameTextView);
        shortNameTextView.setText(StringUtil.getShortName(appointmentDTO.getPayload().getProvider().getName()));

        TextView nameTextView = view.findViewById(R.id.providerName);
        nameTextView.setText(appointmentDTO.getPayload().getProvider().getName());

        TextView typeTextView = view.findViewById(R.id.providerSpecialty);
        typeTextView.setText(appointmentDTO.getPayload().getProvider().getSpecialty().getName());

        View videoVisitIndicator = view.findViewById(R.id.visit_type_video);
        videoVisitIndicator.setVisibility(appointmentDTO.getPayload().getVisitType()
                .hasVideoOption() ? View.VISIBLE : View.GONE);

        // Appointment Place name
        final TextView addressHeaderTextView = view.findViewById(R.id.appointAddressHeaderTextView);
        final String placeName = appointmentDTO.getPayload().getLocation().getName();
        addressHeaderTextView.setText(placeName);

        View dismissView = view.findViewById(R.id.dialogAppointDismiss);
        if (dismissView != null) {
            dismissView.setOnClickListener(v -> dismiss());
        }

        // Appointment Place address
        final TextView addressTextView = view.findViewById(R.id.appointAddressTextView);
        String placeAddress = null;
        if (appointmentDTO.getPayload().getLocation().getAddress() != null) {
            placeAddress = appointmentDTO.getPayload().getLocation().getAddress().getPlaceAddressString();
            addressTextView.setText(placeAddress);
        }
        final String finalPlaceAddress = placeAddress;
        view.findViewById(R.id.appointLocationImageView).setOnClickListener(v -> onMapView(finalPlaceAddress));


        final String phoneNumber = appointmentDTO.getPayload().getProvider().getPhone();
        if (StringUtil.isNullOrEmpty(phoneNumber)) {
            Drawable originalIcon = ContextCompat.getDrawable(getContext(), R.drawable.icn_appointment_card_call);
            originalIcon.setAlpha(40);
            ((ImageView) view.findViewById(R.id.appointDailImageView))
                    .setImageDrawable(originalIcon);
        }
        view.findViewById(R.id.appointDailImageView).setOnClickListener(v -> {
            if (!StringUtil.isNullOrEmpty(phoneNumber)) {
                onPhoneCall(phoneNumber);
            }
        });

        if (StringUtil.isNullOrEmpty(placeAddress)) {
            Drawable originalIcon = ContextCompat.getDrawable(getContext(), R.drawable.icn_appointment_card_directions);
            originalIcon.setAlpha(40);
            ((ImageView) view.findViewById(R.id.appointLocationImageView))
                    .setImageDrawable(originalIcon);
        }

        autoScheduleAppointments = false;
        AppointmentsSettingDTO settings = getAppointmentSettings(appointmentModelDto.getPayload()
                .getAppointmentAvailability().getMetadata().getPracticeId());
        if (settings != null) {
            autoScheduleAppointments = settings.getRequests().getAutomaticallyApproveRequests();
        }

        view.findViewById(R.id.appointDialogButtonLayout).setVisibility(View.VISIBLE);
        requestAppointmentButton = view.findViewById(R.id.requestAppointmentButton);
        requestAppointmentButton.setText(Label.getLabel(autoScheduleAppointments ?
                "appointments_schedule_button" : "appointments_request_heading").toUpperCase());
        requestAppointmentButton.requestFocus();

        TextView reasonTextView = view.findViewById(R.id.reasonTextView);
        VisitTypeDTO visitTypeDTO = appointmentDTO.getPayload().getVisitType();
        String visitReason = visitTypeDTO.getName();
        reasonTextView.setText(visitReason);
        View prepaidLayout = view.findViewById(R.id.prepaymentLayout);
        if (visitTypeDTO.getAmount() > 0) {
            prepaidLayout.setVisibility(View.VISIBLE);
            TextView prepaidAmount = view.findViewById(R.id.prepaymentAmount);
            prepaidAmount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(visitTypeDTO.getAmount()));
            requestAppointmentButton.setText(Label.getLabel("appointments_prepayment_button").toUpperCase());
        } else {
            prepaidLayout.setVisibility(View.GONE);
        }

        final EditText reasonForVisitEditText = view.findViewById(R.id.reasonForVisitEditText);
        requestAppointmentButton.setOnClickListener(v -> {
            final String reasonForVisit = reasonForVisitEditText.getText().toString();
            appointmentDTO.getPayload().setReasonForVisit(reasonForVisit);
            requestAppointment(appointmentDTO);
        });

        ImageView appointUserPicImageView = view.findViewById(R.id.appointUserPicImageView);
        ProviderDTO provider = appointmentDTO.getPayload().getProvider();
        PicassoHelper.get().loadImage(getContext(), appointUserPicImageView, null, provider.getPhoto());

        final ScrollView scrollContainer = view.findViewById(R.id.containerScrollView);
        scrollContainer.post(() -> scrollContainer.fullScroll(View.FOCUS_UP));
    }
}
