package com.carecloud.carepay.practice.library.appointments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.createappointment.AvailabilityHourFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.LocationListFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.ProviderListFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.VisitTypeListFragment;
import com.carecloud.carepay.practice.library.appointments.dialogs.PatientModeRequestAppointmentDialog;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProvidersReasonDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PatientModeAppointmentActivity extends BasePracticeAppointmentsActivity {

    private UserPracticeDTO selectedPractice;
    private LocationDTO selectedLocation;
    private VisitTypeDTO selectedVisitType;
    private AppointmentResourcesItemDTO selectedResource;
    private TextView locationStepNoDataTextView;
    private TextView visitTypeStepNoDataTextView;
    private TextView providerStepNoDataTextView;
    private View visitTypeCard;
    private View locationCard;
    private View providerCard;
    private TextView availabilityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_providers_patient);
        PatientModel patient = new PatientModel();
        patient.setPatientId(ApplicationPreferences.getInstance().getPatientId());
        setPatient(patient);
        appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
        selectedPractice = appointmentsResultModel.getPayload().getUserPractices().get(0);
        setUpUI();
    }

    private void setUpUI() {
        findViewById(R.id.provider_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        findViewById(R.id.btnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        setUpSteps();
        availabilityButton = findViewById(R.id.availabilityButton);
        availabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAvailabilityService();
            }
        });

    }


    protected void callAvailabilityService() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", selectedPractice.getPracticeMgmt());
        queryMap.put("practice_id", selectedPractice.getPracticeId());
        queryMap.put("patient_id", ApplicationPreferences.getInstance().getPatientId());
        queryMap.put("visit_reason_id", selectedVisitType.getId());
        queryMap.put("resource_ids", String.valueOf(selectedResource.getId()));
        queryMap.put("location_ids", String.valueOf(selectedLocation.getId()));
        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getLinks().getAppointmentAvailability();
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                AppointmentsResultModel availabilityDto = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
                if (availabilityDto.getPayload().getAppointmentAvailability().getPayload().isEmpty()) {
                    AppointmentAvailabilityPayloadDTO payload = new AppointmentAvailabilityPayloadDTO();
                    payload.setLocation(selectedLocation);
                    payload.setResource(selectedResource);
                    ProvidersReasonDTO reasonDTO = new ProvidersReasonDTO();
                    reasonDTO.setAmount(selectedVisitType.getAmount());
                    reasonDTO.setName(selectedVisitType.getName());
                    reasonDTO.setDescription(selectedVisitType.getDescription());
                    reasonDTO.setId(selectedVisitType.getId());
                    payload.setVisitReason(reasonDTO);
                    availabilityDto.getPayload().getAppointmentAvailability().getPayload().add(payload);
                }
                availabilityDto.getPayload().getAppointmentAvailability().getPayload().get(0)
                        .getResource().getProvider().setPhoto(selectedResource.getProvider().getPhoto());
                availabilityDto.getPayload().getAppointmentAvailability().getPayload().get(0)
                        .getVisitReason().setAmount(selectedVisitType.getAmount());
                appointmentsResultModel.getPayload().setAppointmentAvailability(availabilityDto.getPayload()
                        .getAppointmentAvailability());
                showAvailabilityHourFragment();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
            }
        }, queryMap);
    }

    private void setUpSteps() {
        setUpProviderStep();
        setUpVisitTypeStep();
        setUpLocationStep();
    }

    private void setUpLocationStep() {
        View locationContainer = findViewById(R.id.locationStepContainer);
        TextView locationStepTitleTextView = locationContainer.findViewById(R.id.stepTitleTextView);
        locationStepTitleTextView.setText(Label.getLabel("add_appointment_location"));
        locationStepNoDataTextView = locationContainer.findViewById(R.id.stepNoDataTextView);
        locationStepNoDataTextView.setText(Label.getLabel("add_appointment_location_hint"));
        locationStepNoDataTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(LocationListFragment.newInstance(selectedPractice, selectedVisitType, selectedResource));
            }
        });
        locationCard = locationContainer.findViewById(R.id.stepContainer);
        ImageView profilePicImageView = locationCard.findViewById(R.id.profilePicImageView);
        profilePicImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_step_location));
    }

    private void setUpVisitTypeStep() {
        View visitTypeContainer = findViewById(R.id.visitTypeStepContainer);
        TextView visitTypeStepTitleTextView = visitTypeContainer.findViewById(R.id.stepTitleTextView);
        visitTypeStepTitleTextView.setText(Label.getLabel("add_appointment_visit_type"));
        visitTypeStepNoDataTextView = visitTypeContainer.findViewById(R.id.stepNoDataTextView);
        visitTypeStepNoDataTextView.setText(Label.getLabel("add_appointment_visit_type_hint"));
        visitTypeStepNoDataTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(VisitTypeListFragment.newInstance(selectedPractice, selectedLocation, selectedResource));
            }
        });
        visitTypeCard = visitTypeContainer.findViewById(R.id.stepContainer);
        ImageView profilePicImageView = visitTypeCard.findViewById(R.id.profilePicImageView);
        profilePicImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_visit_type_placeholder));

    }

    private void setUpProviderStep() {
        View providerContainer = findViewById(R.id.providerStepContainer);
        TextView providerStepTitleTextView = providerContainer.findViewById(R.id.stepTitleTextView);
        providerStepTitleTextView.setText(Label.getLabel("add_appointment_provider"));
        providerStepNoDataTextView = providerContainer.findViewById(R.id.stepNoDataTextView);
        providerStepNoDataTextView.setText(Label.getLabel("add_appointment_provider_hint"));
        providerStepNoDataTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(ProviderListFragment.newInstance(selectedPractice, selectedVisitType, selectedLocation));
            }
        });
        providerCard = providerContainer.findViewById(R.id.stepContainer);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {

    }

    @Override
    public void onCashSelected(PaymentsModel paymentsModel) {
        //Not Implemented
    }

    @Override
    public void onDateSelected(Date selectedDate) {
        //Not Implemented
    }

    @Override
    public DTO getDto() {
        return appointmentsResultModel;
    }

    @Override
    public void appointmentScheduledSuccessfully() {
        getSupportFragmentManager().popBackStackImmediate(AvailabilityHourFragment.class.getName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().popBackStackImmediate();
        logout();
    }

    @Override
    public void setAppointmentSlot(AppointmentsSlotsDTO slot) {
        //Not Apply
    }

    @Override
    public void setResourceProvider(AppointmentResourcesItemDTO resource) {
        selectedResource = resource;
        providerStepNoDataTextView.setVisibility(View.GONE);
        providerCard.setVisibility(View.VISIBLE);
        TextView title = providerCard.findViewById(R.id.title);
        title.setText(resource.getName());
        TextView subtitle = providerCard.findViewById(R.id.subTitle);
        subtitle.setText(resource.getProvider().getSpecialty().getName());
        ImageView resetImageView = providerCard.findViewById(R.id.resetImageView);
        TextView profileShortNameTextView = providerCard.findViewById(R.id.profileShortNameTextView);
        profileShortNameTextView.setText(StringUtil.getShortName(resource.getProvider().getSpecialty().getName()));
        profileShortNameTextView.setVisibility(View.VISIBLE);
        resetImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedResource = null;
                providerCard.setVisibility(View.GONE);
                providerStepNoDataTextView.setVisibility(View.VISIBLE);
            }
        });
        ImageView profilePicImageView = providerCard.findViewById(R.id.profilePicImageView);
        PicassoHelper.get().loadImage(this, profilePicImageView, profileShortNameTextView,
                resource.getProvider().getPhoto());
        enableAvailableButton();
    }

    @Override
    public void setVisitType(VisitTypeDTO visitTypeDTO) {
        selectedVisitType = visitTypeDTO;
        visitTypeStepNoDataTextView.setVisibility(View.GONE);
        visitTypeCard.setVisibility(View.VISIBLE);
        TextView title = visitTypeCard.findViewById(R.id.title);
        title.setText(visitTypeDTO.getName());
        TextView subtitle = visitTypeCard.findViewById(R.id.subTitle);
        if (visitTypeDTO.getAmount() > 0) {
            String subtitleText = String.format(Label
                            .getLabel("createAppointment.visitTypeList.item.label.prepaymentMessage"),
                    NumberFormat.getCurrencyInstance(Locale.US).format(visitTypeDTO.getAmount()));
            subtitle.setText(subtitleText);
        }
        ImageView resetImageView = visitTypeCard.findViewById(R.id.resetImageView);
        resetImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedVisitType = null;
                visitTypeCard.setVisibility(View.GONE);
                visitTypeStepNoDataTextView.setVisibility(View.VISIBLE);
            }
        });
        enableAvailableButton();
    }

    @Override
    public void setLocation(LocationDTO locationDTO) {
        selectedLocation = locationDTO;
        locationStepNoDataTextView.setVisibility(View.GONE);
        locationCard.setVisibility(View.VISIBLE);
        TextView title = locationCard.findViewById(R.id.title);
        title.setText(locationDTO.getName());
        TextView subtitle = locationCard.findViewById(R.id.subTitle);
        subtitle.setText(locationDTO.getAddress().geAddressStringWithShortZipWOCounty());
        ImageView resetImageView = locationCard.findViewById(R.id.resetImageView);
        resetImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLocation = null;
                locationCard.setVisibility(View.GONE);
                locationStepNoDataTextView.setVisibility(View.VISIBLE);
            }
        });
        enableAvailableButton();
    }

    @Override
    public void showAppointmentConfirmationFragment(AppointmentDTO appointmentDTO) {
        showFragment(PatientModeRequestAppointmentDialog.newInstance(appointmentDTO));
    }

    private void enableAvailableButton() {
        boolean enable = selectedLocation != null && selectedVisitType != null && selectedResource != null;
        availabilityButton.setEnabled(enable);
    }


    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        getSupportFragmentManager().popBackStackImmediate(AvailabilityHourFragment.class.getName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().popBackStackImmediate();
        logout();
    }

    private void logout() {
        goToHome(appointmentsResultModel.getMetadata().getTransitions().getLogout());
    }
}
