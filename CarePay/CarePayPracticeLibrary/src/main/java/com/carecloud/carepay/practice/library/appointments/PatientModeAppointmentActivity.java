package com.carecloud.carepay.practice.library.appointments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.createappointment.AvailabilityHourFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.IntelligentSchedulerFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.LocationListFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.ProviderListFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.VisitTypeListFragment;
import com.carecloud.carepay.practice.library.appointments.dialogs.PatientModeRequestAppointmentDialog;
import com.carecloud.carepay.practice.library.appointments.interfaces.IntelligentSchedulerCallback;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.signin.SigninActivity;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.availabilityhour.BaseAvailabilityHourFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.IntelligentSchedulerDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeQuestions;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PatientModeAppointmentActivity extends BasePracticeAppointmentsActivity implements IntelligentSchedulerCallback, ConfirmationCallback {

    private UserPracticeDTO selectedPractice;
    private LocationDTO selectedLocation;
    private VisitTypeDTO selectedVisitType;
    private AppointmentResourcesItemDTO selectedResource;
    private TextView locationStepNoDataTextView;
    private TextView visitTypeStepNoDataTextView;
    private TextView providerStepNoDataTextView;
    private TextView provider_screen_sub_header;
    private View visitTypeCard;
    private View locationCard;
    private View providerCard;
    private View visitTypeContainer;
    private TextView availabilityButton;
    private TextView provider_screen_header;
    private IntelligentSchedulerFragment fragment;


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
        startIntelligentScheduler();

    }

    private void startIntelligentScheduler() {
        IntelligentSchedulerDTO intelligentSchedulerDTO = getIntelligentScheduler(selectedPractice);
        if (selectedPractice != null &&
                intelligentSchedulerDTO != null &&
                intelligentSchedulerDTO.isSchedulerEnabled() &&
                intelligentSchedulerDTO.getIntelligent_scheduler_questions() != null &&
                isNewUser(appointmentsResultModel.getPayload().getAppointments())) {
            visitTypeContainer.setVisibility(View.GONE);
            String allQuestions = new Gson().toJson(appointmentsResultModel.getPayload().getIntelligent_scheduler().get(0).getIntelligent_scheduler_questions().get(0));
            fragment = IntelligentSchedulerFragment.newInstance(allQuestions);
            new Handler().postDelayed(() -> {
                showFragment(fragment);
            }, 500);
            // showFragment(fragment);
            //  fragment.setCancelable(false);
            // isSchedulerStarted = true;
            // requireActivity().startActivityForResult(new Intent(requireContext(), IntelligentSchedulerActivity.class)
            //              .putExtra(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY, new Gson().toJson(intelligentSchedulerDTO.getIntelligent_scheduler_questions().get(0))),
            //      CarePayConstants.INTELLIGENT_SCHEDULER_REQUEST);
        } else {
            visitTypeContainer.setVisibility(View.VISIBLE);
            provider_screen_header.setText(Label.getLabel("patientMode.appointmentCreation.title.label.create"));
        }
        //for testing without intelligent scheduler check
        //   visitTypeContainer.setVisibility(View.VISIBLE);
        //  provider_screen_header.setText(Label.getLabel("patientMode.appointmentCreation.title.label.create"));
    }

    private IntelligentSchedulerDTO getIntelligentScheduler(UserPracticeDTO selectedPractice) {
        for (IntelligentSchedulerDTO intelligentSchedulerDTO : appointmentsResultModel.getPayload().getIntelligent_scheduler()) {
            if (intelligentSchedulerDTO.getPractice_id().equals(selectedPractice.getPracticeId())) {
                return intelligentSchedulerDTO;
            }
        }
        return null;
    }


    private void setUpUI() {
        findViewById(R.id.provider_logout).setOnClickListener(v -> logout());
        findViewById(R.id.btnHome).setOnClickListener(v -> logout());

        setUpSteps();
        availabilityButton = findViewById(R.id.availabilityButton);
        provider_screen_header = findViewById(R.id.provider_screen_header);
        provider_screen_sub_header = findViewById(R.id.provider_screen_sub_header);
        availabilityButton.setOnClickListener(v -> callAvailabilityService());


    }

   /* @Override
    public void showFragment(DialogFragment fragment) {
        String tag = fragment.getClass().getName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(tag);
        fragment.show(ft, tag);
    }*/

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
                    payload.setVisitReason(selectedVisitType);
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
        locationContainer.setContentDescription(getString(R.string.content_description_choose_location));
        TextView locationStepTitleTextView = locationContainer.findViewById(R.id.stepTitleTextView);
        locationStepTitleTextView.setText(Label.getLabel("add_appointment_location"));
        locationStepNoDataTextView = locationContainer.findViewById(R.id.stepNoDataTextView);
        locationStepNoDataTextView.setText(Label.getLabel("add_appointment_location_hint"));
        locationStepNoDataTextView.setOnClickListener(v -> {
            if (selectedResource == null)
                return;
            showFragment(LocationListFragment.newInstance(selectedPractice, selectedVisitType, selectedResource));
        });

        locationCard = locationContainer.findViewById(R.id.stepContainer);
        locationCard.setOnClickListener(v -> showFragment(LocationListFragment.newInstance(selectedPractice, selectedVisitType, selectedResource)));
        ImageView profilePicImageView = locationCard.findViewById(R.id.profilePicImageView);
        profilePicImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_step_location));
    }

    private void setUpVisitTypeStep() {
        visitTypeContainer = findViewById(R.id.visitTypeStepContainer);
        if (appointmentsResultModel.getPayload().getAppointments() != null && appointmentsResultModel.getPayload().getAppointments().size() != 0) {
            visitTypeContainer.setVisibility(View.GONE);
        }
        visitTypeContainer.setContentDescription(getString(R.string.content_description_choose_visit_type));
        TextView visitTypeStepTitleTextView = visitTypeContainer.findViewById(R.id.stepTitleTextView);
        visitTypeStepTitleTextView.setText(Label.getLabel("add_appointment_visit_type"));
        visitTypeStepNoDataTextView = visitTypeContainer.findViewById(R.id.stepNoDataTextView);
        visitTypeStepNoDataTextView.setText(Label.getLabel("add_appointment_visit_type_hint"));
        visitTypeStepNoDataTextView.setOnClickListener(v -> showFragment(VisitTypeListFragment.newInstance(selectedPractice, selectedLocation, selectedResource)));
        visitTypeCard = visitTypeContainer.findViewById(R.id.stepContainer);
        visitTypeCard.setOnClickListener(v -> showFragment(VisitTypeListFragment.newInstance(selectedPractice, selectedLocation, selectedResource)));
        ImageView profilePicImageView = visitTypeCard.findViewById(R.id.profilePicImageView);
        profilePicImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_visit_type_placeholder));

    }

    private void setUpProviderStep() {
        View providerContainer = findViewById(R.id.providerStepContainer);
        providerContainer.setContentDescription(getString(R.string.content_description_choose_provider));
        TextView providerStepTitleTextView = providerContainer.findViewById(R.id.stepTitleTextView);
        providerStepTitleTextView.setText(Label.getLabel("add_appointment_provider"));
        providerStepNoDataTextView = providerContainer.findViewById(R.id.stepNoDataTextView);
        providerStepNoDataTextView.setText(Label.getLabel("add_appointment_provider_hint"));
        providerStepNoDataTextView.setOnClickListener(v ->
        {
            if (selectedVisitType == null)
                return;
            showFragment(ProviderListFragment.newInstance(selectedPractice, selectedVisitType, selectedLocation));
        });
        providerCard = providerContainer.findViewById(R.id.stepContainer);
        providerCard.setOnClickListener(v ->
        {
            if (selectedVisitType == null)
                return;
            showFragment(ProviderListFragment.newInstance(selectedPractice, selectedVisitType, selectedLocation));
        });
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {
        //Works only when chooseCreditCardFragment is used in selectMode
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
    public void appointmentScheduledSuccessfully(AppointmentDTO appointmentDTO) {
        // empty implementation
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
        title.setText(StringUtil.capitalize(resource.getName()));
        TextView subtitle = providerCard.findViewById(R.id.subTitle);
        subtitle.setText(StringUtil.capitalize(resource.getProvider().getSpecialty().getName()));
        ImageView resetImageView = providerCard.findViewById(R.id.resetImageView);
        TextView profileShortNameTextView = providerCard.findViewById(R.id.profileShortNameTextView);
        profileShortNameTextView.setText(StringUtil.getShortName(resource.getProvider().getSpecialty().getName()));
        profileShortNameTextView.setVisibility(View.VISIBLE);
        resetImageView.setOnClickListener(v -> {
            resetLocation();
            selectedResource = null;
            providerCard.setVisibility(View.GONE);
            providerStepNoDataTextView.setVisibility(View.VISIBLE);
        });
        ImageView profilePicImageView = providerCard.findViewById(R.id.profilePicImageView);
        PicassoHelper.get().loadImage(this, profilePicImageView, profileShortNameTextView,
                resource.getProvider().getPhoto());
        enableAvailableButton();
    }

    private void resetLocation() {

        selectedLocation = null;
        locationCard.setVisibility(View.GONE);
        locationStepNoDataTextView.setVisibility(View.VISIBLE);


    }

    private void resetProvider() {
        selectedResource = null;
        providerCard.setVisibility(View.GONE);
        providerStepNoDataTextView.setVisibility(View.VISIBLE);
    }


    @Override
    public void setVisitType(VisitTypeDTO visitTypeDTO) {
        selectedVisitType = visitTypeDTO;
        visitTypeStepNoDataTextView.setVisibility(View.GONE);
        visitTypeCard.setVisibility(View.VISIBLE);
        TextView title = visitTypeCard.findViewById(R.id.title);
        title.setText(StringUtil.capitalize(visitTypeDTO.getName()));
        TextView subtitle = visitTypeCard.findViewById(R.id.subTitle);
        if (visitTypeDTO.getAmount() > 0) {
            String subtitleText = String.format(Label
                            .getLabel("createAppointment.visitTypeList.item.label.prepaymentMessage"),
                    NumberFormat.getCurrencyInstance(Locale.US).format(visitTypeDTO.getAmount()));
            subtitle.setText(subtitleText);
        } else {
            subtitle.setText(Label.getLabel("createAppointment.visitTypeList.item.label.noPrepaymentMessage"));
        }
        ImageView resetImageView = visitTypeCard.findViewById(R.id.resetImageView);
        resetImageView.setOnClickListener(v -> {
            selectedVisitType = null;
            visitTypeCard.setVisibility(View.GONE);
            visitTypeStepNoDataTextView.setVisibility(View.VISIBLE);
            resetProvider();
            resetLocation();
        });
        enableAvailableButton();
    }

    @Override
    public void setLocation(LocationDTO locationDTO) {
        selectedLocation = locationDTO;
        locationStepNoDataTextView.setVisibility(View.GONE);
        locationCard.setVisibility(View.VISIBLE);
        TextView title = locationCard.findViewById(R.id.title);
        title.setText(StringUtil.capitalize(locationDTO.getName()));
        TextView subtitle = locationCard.findViewById(R.id.subTitle);
        subtitle.setText(locationDTO.getAddress().geAddressStringWithShortZipWOCounty2Lines());
        ImageView resetImageView = locationCard.findViewById(R.id.resetImageView);
        resetImageView.setOnClickListener(v -> {
            selectedLocation = null;
            locationCard.setVisibility(View.GONE);
            locationStepNoDataTextView.setVisibility(View.VISIBLE);
        });
        enableAvailableButton();
    }

    @Override
    public void showAppointmentConfirmationFragment(AppointmentDTO appointmentDTO,
                                                    final BaseAvailabilityHourFragment baseAvailabilityHourFragment) {
        PatientModeRequestAppointmentDialog fragment = PatientModeRequestAppointmentDialog.newInstance(appointmentDTO);
        fragment.setOnCancelListener(dialogInterface -> baseAvailabilityHourFragment.showDialog());
        showFragment(fragment);
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

    @Override
    public void onPaymentCashFinished() {
        //NA
    }

    @Override
    public boolean manageSession() {
        return true;
    }

    @Override
    public TransitionDTO getLogoutTransition() {
        return appointmentsResultModel.getMetadata().getTransitions().getLogout();
    }

    @Override
    public void onPaymentCancel() {

    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void onVisitTypeSelected(VisitTypeDTO visitTypeDTO) {
        selectedVisitType = visitTypeDTO;
        provider_screen_sub_header.setText(Label.getLabel("intelligent_scheduler_auto_selected_label") + " " + visitTypeDTO.getName());
        fragment.dismiss();
    }

    @Override
    public void onOptionSelected(VisitTypeQuestions visitTypeQuestion) {
        fragment.onVisitOptionSelected(visitTypeQuestion);
    }

    @Override
    public void onExit() {
        ConfirmDialogFragment fragment = ConfirmDialogFragment
                .newInstance(Label.getLabel("intelligent_scheduler_cancel_popup_title"),
                        Label.getLabel("intelligent_scheduler_cancel_popup_label"),
                        Label.getLabel("button_no"),
                        Label.getLabel("button_yes"));
        fragment.setCallback(this);


        displayDialogFragment(fragment, false);
    }

    @Override
    public void onBack() {
        fragment.onBack();
    }

    private boolean isNewUser(List<AppointmentDTO> appointments) {
        int appointmentSize = 0;
        for (AppointmentDTO appointmentDTO : appointments) {
            String appointmentCode = appointmentDTO.getPayload().getAppointmentStatus().getCode();
            if (appointmentCode.equals(CarePayConstants.PENDING) ||
                    appointmentCode.equals(CarePayConstants.REQUESTED) ||
                    appointmentCode.equals(CarePayConstants.DENIED) ||
                    appointmentCode.equals(CarePayConstants.CANCELLED) ||
                    appointmentDTO.getPayload().isAppointmentOver()) {
                continue;
            } else {
                appointmentSize++;
                break;
            }
        }
        return appointmentSize > 0 ? false : true;
    }

    @Override
    public void onConfirm() {
        finish();
    }
}
