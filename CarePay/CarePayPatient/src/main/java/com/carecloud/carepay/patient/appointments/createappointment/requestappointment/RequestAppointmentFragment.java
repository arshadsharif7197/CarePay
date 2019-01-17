package com.carecloud.carepay.patient.appointments.createappointment.requestappointment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.createappointment.CreateAppointmentInterface;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDataDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author pjohnson on 1/17/19.
 */
public class RequestAppointmentFragment extends BaseDialogFragment {

    private CreateAppointmentInterface callback;
    private AppointmentsResultModel appointmentModelDto;
    private AppointmentDTO appointmentDto;

    public static RequestAppointmentFragment newInstance(AppointmentDTO appointmentDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDTO);
        RequestAppointmentFragment fragment = new RequestAppointmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreateAppointmentInterface) {
            callback = (CreateAppointmentInterface) context;
        } else {
            throw new ClassCastException("context must implement CreateAppointmentInterface.");
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        appointmentModelDto = (AppointmentsResultModel) callback.getDto();
        appointmentDto = DtoHelper.getConvertedDTO(AppointmentDTO.class, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DateUtil.getInstance().setDateRaw(appointmentDto.getPayload().getStartTime());
        CarePayTextView dateTextView = view.findViewById(R.id.appointDateTextView);
        CarePayTextView timeTextView = view.findViewById(R.id.appointTimeTextView);
        timeTextView.setText(DateUtil.getInstance().getDateAsDayMonthDayOrdinal());
        dateTextView.setText(DateUtil.getInstance().getTime12Hour());

        CarePayTextView shortNameTextView = view.findViewById(R.id.appointShortnameTextView);
        shortNameTextView.setText(StringUtil.getShortName(appointmentDto.getPayload().getProvider().getName()));

        CarePayTextView nameTextView = view.findViewById(R.id.providerName);
        nameTextView.setText(appointmentDto.getPayload().getProvider().getName());

        CarePayTextView typeTextView = view.findViewById(R.id.providerSpecialty);
        typeTextView.setText(appointmentDto.getPayload().getProvider().getSpecialty().getName());

        // Appointment Place name
        final CarePayTextView addressHeaderTextView = view.findViewById(R.id.appointAddressHeaderTextView);
        final String placeName = appointmentDto.getPayload().getLocation().getName();
        addressHeaderTextView.setText(placeName);

        view.findViewById(R.id.dialogAppointDismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Appointment Place address
        final CarePayTextView addressTextView = view.findViewById(R.id.appointAddressTextView);
        String placeAddress = null;
        if (appointmentDto.getPayload().getLocation().getAddress() != null) {
            placeAddress = appointmentDto.getPayload().getLocation().getAddress().getPlaceAddressString();
            addressTextView.setText(placeAddress);
        }
        final String finalPlaceAddress = placeAddress;
        view.findViewById(R.id.appointLocationImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMapView(finalPlaceAddress);
            }
        });


        final String phoneNumber = appointmentDto.getPayload().getProvider().getPhone();
        if (StringUtil.isNullOrEmpty(phoneNumber)) {
            Drawable originalIcon = ContextCompat.getDrawable(getContext(), R.drawable.icn_appointment_card_call);
            originalIcon.setAlpha(40);
            ((ImageView) view.findViewById(R.id.appointDailImageView))
                    .setImageDrawable(originalIcon);
        }
        view.findViewById(R.id.appointDailImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isNullOrEmpty(phoneNumber)) {
                    onPhoneCall(phoneNumber);
                }
            }
        });

        if (StringUtil.isNullOrEmpty(placeAddress)) {
            Drawable originalIcon = ContextCompat.getDrawable(getContext(), R.drawable.icn_appointment_card_directions);
            originalIcon.setAlpha(40);
            ((ImageView) view.findViewById(R.id.appointLocationImageView))
                    .setImageDrawable(originalIcon);
        }

        boolean autoScheduleAppointments = getAppointmentSettings(appointmentModelDto.getPayload()
                .getAppointmentAvailability().getMetadata().getPracticeId())
                .getRequests().getAutomaticallyApproveRequests();

        view.findViewById(R.id.appointDialogButtonLayout).setVisibility(View.VISIBLE);
        Button appointmentRequestButton = view.findViewById(R.id.requestAppointmentButton);
        appointmentRequestButton.setText(Label.getLabel(autoScheduleAppointments ?
                "appointments_schedule_button" : "appointments_request_heading"));
        appointmentRequestButton.requestFocus();

        TextView reasonTextView = view.findViewById(R.id.reasonTextView);
        VisitTypeDTO visitTypeDTO = appointmentDto.getPayload().getVisitType();
        String visitReason = visitTypeDTO.getName();
        reasonTextView.setText(visitReason);
        View prepaidLayout = view.findViewById(R.id.prepaymentLayout);
        if (visitTypeDTO.getAmount() > 0) {
            prepaidLayout.setVisibility(View.VISIBLE);
            TextView prepaidAmount = view.findViewById(R.id.prepaymentAmount);
            prepaidAmount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(visitTypeDTO.getAmount()));
            appointmentRequestButton.setText(Label.getLabel("appointments_prepayment_button"));
        } else {
            prepaidLayout.setVisibility(View.GONE);
        }

        final EditText reasonForVisitEditText = view.findViewById(R.id.reasonForVisitEditText);
        appointmentRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String reasonForVisit = reasonForVisitEditText.getText().toString();
                appointmentDto.getPayload().setReasonForVisit(reasonForVisit);
                requestAppointment(appointmentDto);
            }
        });


        ImageView appointUserPicImageView = view.findViewById(R.id.appointUserPicImageView);
        ProviderDTO provider = appointmentDto.getPayload().getProvider();
        PicassoHelper.get().loadImage(getContext(), appointUserPicImageView, null, provider.getPhoto());

        final ScrollView scrollContainer = view.findViewById(R.id.containerScrollView);
        scrollContainer.post(new Runnable() {
            @Override
            public void run() {
                scrollContainer.fullScroll(View.FOCUS_UP);
            }
        });
    }

    private void requestAppointment(AppointmentDTO appointmentDto) {
        AppointmentAvailabilityDataDTO availabilityDataDTO = appointmentModelDto.getPayload().getAppointmentAvailability();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", availabilityDataDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", availabilityDataDTO.getMetadata().getPracticeId());

        ScheduleAppointmentRequestDTO appointmentRequestDto = new ScheduleAppointmentRequestDTO();
        ScheduleAppointmentRequestDTO.Appointment appointment = appointmentRequestDto.getAppointment();
        appointment.setStartTime(appointmentDto.getPayload().getStartTime());
        appointment.setEndTime(appointmentDto.getPayload().getEndTime());
        appointment.setLocationId(appointmentDto.getPayload().getLocation().getId());
        appointment.setLocationGuid(appointmentDto.getPayload().getLocation().getGuid());
        appointment.setProviderId(appointmentDto.getPayload().getProvider().getId());
        appointment.setProviderGuid(appointmentDto.getPayload().getProvider().getGuid());
        appointment.setVisitReasonId(appointmentDto.getPayload().getVisitType().getId());
        appointment.setResourceId(appointmentDto.getPayload().getResource().getId());
        appointment.setComplaint(appointmentDto.getPayload().getReasonForVisit());
        appointment.setComments(appointmentDto.getPayload().getReasonForVisit());
        appointment.getPatient().setId(getPatientId(availabilityDataDTO.getMetadata().getPracticeId()));

        double amount = appointmentDto.getPayload().getVisitType().getAmount();
        if (amount > 0) {
            startPrepaymentProcess(appointmentRequestDto, amount);
        } else {
            callRequestAppointmentService(queryMap, appointmentRequestDto);
        }
    }

    private void callRequestAppointmentService(Map<String, String> queryMap, ScheduleAppointmentRequestDTO appointmentRequestDto) {
        Gson gson = new Gson();
        TransitionDTO transitionDTO = appointmentModelDto.getMetadata().getTransitions().getMakeAppointment();
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        showProgressDialog();
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        hideProgressDialog();
                        callback.refreshAppointmentsList();
                        dismiss();
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        hideProgressDialog();
                        showErrorNotification(exceptionMessage);
                        if (isVisible()) {
                            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
                        }
                    }
                },
                gson.toJson(appointmentRequestDto), queryMap);
    }

    private String getPatientId(String practiceId) {
        PracticePatientIdsDTO[] practicePatientIdArray = ApplicationPreferences.getInstance()
                .getObjectFromSharedPreferences(CarePayConstants.KEY_PRACTICE_PATIENT_IDS,
                        PracticePatientIdsDTO[].class);
        for (PracticePatientIdsDTO practicePatientId : practicePatientIdArray) {
            if (practicePatientId.getPracticeId().equals(practiceId)) {
                return practicePatientId.getPatientId();
            }
        }
        return null;
    }

    private void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDto, double amount) {

    }

    private void onMapView(final String address) {
        if (SystemUtil.isNotEmptyString(address)) {
            Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }

    private void onPhoneCall(final String phoneNumber) {
        try {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        } catch (ActivityNotFoundException ex) {
            Log.e("error", ex.getMessage());
        }
    }

    public AppointmentsSettingDTO getAppointmentSettings(String practiceId) {
        for (AppointmentsSettingDTO settings : appointmentModelDto.getPayload().getAppointmentsSettings()) {
            if (practiceId.equals(settings.getPracticeId())) {
                return settings;
            }
        }
        return null;
    }
}
