package com.carecloud.carepay.patient.appointments.createappointment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.BaseRequestAppointmentDialogFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
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

    public static RequestAppointmentDialogFragment newInstance(AppointmentDTO appointmentDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDTO);
        RequestAppointmentDialogFragment fragment = new RequestAppointmentDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getStartTime());
        CarePayTextView dateTextView = view.findViewById(com.carecloud.carepaylibrary.R.id.appointDateTextView);
        CarePayTextView timeTextView = view.findViewById(com.carecloud.carepaylibrary.R.id.appointTimeTextView);
        timeTextView.setText(DateUtil.getInstance().getDateAsDayMonthDayOrdinal());
        dateTextView.setText(DateUtil.getInstance().getTime12Hour());

        CarePayTextView shortNameTextView = view.findViewById(com.carecloud.carepaylibrary.R.id.appointShortnameTextView);
        shortNameTextView.setText(StringUtil.getShortName(appointmentDTO.getPayload().getProvider().getName()));

        CarePayTextView nameTextView = view.findViewById(com.carecloud.carepaylibrary.R.id.providerName);
        nameTextView.setText(appointmentDTO.getPayload().getProvider().getName());

        CarePayTextView typeTextView = view.findViewById(com.carecloud.carepaylibrary.R.id.providerSpecialty);
        typeTextView.setText(appointmentDTO.getPayload().getProvider().getSpecialty().getName());

        // Appointment Place name
        final CarePayTextView addressHeaderTextView = view.findViewById(com.carecloud.carepaylibrary.R.id.appointAddressHeaderTextView);
        final String placeName = appointmentDTO.getPayload().getLocation().getName();
        addressHeaderTextView.setText(placeName);

        View dismissView = view.findViewById(com.carecloud.carepaylibrary.R.id.dialogAppointDismiss);
        if (dismissView != null) {
            dismissView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        // Appointment Place address
        final CarePayTextView addressTextView = view.findViewById(com.carecloud.carepaylibrary.R.id.appointAddressTextView);
        String placeAddress = null;
        if (appointmentDTO.getPayload().getLocation().getAddress() != null) {
            placeAddress = appointmentDTO.getPayload().getLocation().getAddress().getPlaceAddressString();
            addressTextView.setText(placeAddress);
        }
        final String finalPlaceAddress = placeAddress;
        view.findViewById(com.carecloud.carepaylibrary.R.id.appointLocationImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMapView(finalPlaceAddress);
            }
        });


        final String phoneNumber = appointmentDTO.getPayload().getProvider().getPhone();
        if (StringUtil.isNullOrEmpty(phoneNumber)) {
            Drawable originalIcon = ContextCompat.getDrawable(getContext(), com.carecloud.carepaylibrary.R.drawable.icn_appointment_card_call);
            originalIcon.setAlpha(40);
            ((ImageView) view.findViewById(com.carecloud.carepaylibrary.R.id.appointDailImageView))
                    .setImageDrawable(originalIcon);
        }
        view.findViewById(com.carecloud.carepaylibrary.R.id.appointDailImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isNullOrEmpty(phoneNumber)) {
                    onPhoneCall(phoneNumber);
                }
            }
        });

        if (StringUtil.isNullOrEmpty(placeAddress)) {
            Drawable originalIcon = ContextCompat.getDrawable(getContext(), com.carecloud.carepaylibrary.R.drawable.icn_appointment_card_directions);
            originalIcon.setAlpha(40);
            ((ImageView) view.findViewById(com.carecloud.carepaylibrary.R.id.appointLocationImageView))
                    .setImageDrawable(originalIcon);
        }

        autoScheduleAppointments = false;
        AppointmentsSettingDTO settings = getAppointmentSettings(appointmentModelDto.getPayload()
                .getAppointmentAvailability().getMetadata().getPracticeId());
        if (settings != null) {
            autoScheduleAppointments = settings.getRequests().getAutomaticallyApproveRequests();
        }

        view.findViewById(com.carecloud.carepaylibrary.R.id.appointDialogButtonLayout).setVisibility(View.VISIBLE);
        Button appointmentRequestButton = view.findViewById(com.carecloud.carepaylibrary.R.id.requestAppointmentButton);
        appointmentRequestButton.setText(Label.getLabel(autoScheduleAppointments ?
                "appointments_schedule_button" : "appointments_request_heading"));
        appointmentRequestButton.requestFocus();

        TextView reasonTextView = view.findViewById(com.carecloud.carepaylibrary.R.id.reasonTextView);
        VisitTypeDTO visitTypeDTO = appointmentDTO.getPayload().getVisitType();
        String visitReason = visitTypeDTO.getName();
        reasonTextView.setText(visitReason);
        View prepaidLayout = view.findViewById(com.carecloud.carepaylibrary.R.id.prepaymentLayout);
        if (visitTypeDTO.getAmount() > 0) {
            prepaidLayout.setVisibility(View.VISIBLE);
            TextView prepaidAmount = view.findViewById(com.carecloud.carepaylibrary.R.id.prepaymentAmount);
            prepaidAmount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(visitTypeDTO.getAmount()));
            appointmentRequestButton.setText(Label.getLabel("appointments_prepayment_button"));
        } else {
            prepaidLayout.setVisibility(View.GONE);
        }

        final EditText reasonForVisitEditText = view.findViewById(com.carecloud.carepaylibrary.R.id.reasonForVisitEditText);
        appointmentRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String reasonForVisit = reasonForVisitEditText.getText().toString();
                appointmentDTO.getPayload().setReasonForVisit(reasonForVisit);
                requestAppointment(appointmentDTO);
            }
        });

        ImageView appointUserPicImageView = view.findViewById(com.carecloud.carepaylibrary.R.id.appointUserPicImageView);
        ProviderDTO provider = appointmentDTO.getPayload().getProvider();
        PicassoHelper.get().loadImage(getContext(), appointUserPicImageView, null, provider.getPhoto());

        final ScrollView scrollContainer = view.findViewById(com.carecloud.carepaylibrary.R.id.containerScrollView);
        scrollContainer.post(new Runnable() {
            @Override
            public void run() {
                scrollContainer.fullScroll(View.FOCUS_UP);
            }
        });
    }
}
