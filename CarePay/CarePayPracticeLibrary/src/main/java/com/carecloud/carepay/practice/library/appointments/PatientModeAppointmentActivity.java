package com.carecloud.carepay.practice.library.appointments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.createappointment.AvailabilityHourFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.IntelligentSchedulerFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.LocationListFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.ProviderListFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.QuestionAnswerTallyFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.VisitTypeListFragment;
import com.carecloud.carepay.practice.library.appointments.dialogs.PatientModeRequestAppointmentDialog;
import com.carecloud.carepay.practice.library.appointments.interfaces.IntelligentSchedulerCallback;
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
import com.carecloud.carepaylibray.appointments.models.AppointmentsPopUpDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.GetPatientTypeResponse;
import com.carecloud.carepaylibray.appointments.models.IntelligentSchedulerDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.SchedulerAnswerTally;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeQuestions;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.text.NumberFormat;
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
    private View locationContainer;
    private View providerContainer;
    private LinearLayout lastProviderContainer;
    private LinearLayout lastLocationContainer;
    private CarePayTextView lastLocationMessage;
    private CarePayTextView lastProviderMessage;
    private CarePayTextView lastLocationTitle;
    private CarePayTextView lastLocationSubTitle;
    private CarePayTextView lastProviderTitle;
    private CarePayTextView lastProviderSubTitle;
    private LinearLayout autoVisitTypeContainer;
    private CarePayTextView autoVisitTypeTitle;
    private CarePayTextView autoVisitTypeMessage;

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
        checkPracticeAlert();
    }

    private void checkPracticeAlert() {
        AppointmentsPopUpDTO appointmentsPopUpDTO = appointmentsResultModel.getPayload().getAppointmentsSetting(selectedPractice.getPracticeId()).getAppointmentsPopUpDTO();
        if (appointmentsPopUpDTO != null && appointmentsPopUpDTO.isEnabled()) {
            ConfirmDialogFragment fragment = ConfirmDialogFragment
                    .newInstance("",
                            appointmentsPopUpDTO.getText(),
                            Label.getLabel("ok"),
                            false,
                            R.layout.fragment_alert_dialog_single_action);
            fragment.setCallback(() -> getPatientType());

            displayDialogFragment(fragment, false);
        } else {
            // Intelligent Scheduler flow
            getPatientType();
        }
    }

    private void startIntelligentScheduler() {
        IntelligentSchedulerDTO intelligentSchedulerDTO = getIntelligentScheduler(selectedPractice);

        if (selectedPractice != null && intelligentSchedulerDTO != null && getPatientTypeResponse != null) {

            if (intelligentSchedulerDTO.isSchedulerEnabled() &&
                    intelligentSchedulerDTO.getIntelligent_scheduler_questions() != null &&
                    getPatientTypeResponse.getPatientType().getNewPatient()) {

                visitTypeContainer.setVisibility(View.GONE);
                String allQuestions = new Gson().toJson(intelligentSchedulerDTO.getIntelligent_scheduler_questions().get(0));
                fragment = IntelligentSchedulerFragment.newInstance(allQuestions);
                new Handler().postDelayed(() -> {
                    showFragment(fragment);
                }, 500);

            } else if (intelligentSchedulerDTO.isEstablishedPatientSchedulerEnabled() &&
                    intelligentSchedulerDTO.getEstablishedPatientIntelligentSchedulerQuestions() != null &&
                    !getPatientTypeResponse.getPatientType().getNewPatient()) {

                visitTypeContainer.setVisibility(View.GONE);
                String allQuestions = new Gson().toJson(intelligentSchedulerDTO.getEstablishedPatientIntelligentSchedulerQuestions().get(0));
                fragment = IntelligentSchedulerFragment.newInstance(allQuestions);
                new Handler().postDelayed(() -> {
                    showFragment(fragment);
                }, 500);
            }

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

        lastProviderContainer = findViewById(R.id.last_provider_container);
        lastProviderMessage = findViewById(R.id.last_provider_msg);
        lastProviderTitle = findViewById(R.id.last_provider_title);
        lastProviderSubTitle = findViewById(R.id.last_provider_subTitle);

        lastLocationContainer = findViewById(R.id.last_location_container);
        lastLocationMessage = findViewById(R.id.last_location_msg);
        lastLocationTitle = findViewById(R.id.last_location_title);
        lastLocationSubTitle = findViewById(R.id.last_location_subTitle);

        autoVisitTypeContainer = findViewById(R.id.auto_visit_type_container);
        autoVisitTypeMessage = findViewById(R.id.auto_visit_type_msg);
        autoVisitTypeTitle = findViewById(R.id.auto_visit_type_title);

        availabilityButton.setOnClickListener(v -> callAvailabilityService());
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
        locationContainer = findViewById(R.id.locationStepContainer);
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
        providerContainer = findViewById(R.id.providerStepContainer);
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

    private void getPatientType() {
        WorkflowServiceCallback getPatientTypeCallback =
                new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        showProgressDialog();
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        hideProgressDialog();
                        getPatientTypeResponse = DtoHelper.getConvertedDTO(GetPatientTypeResponse.class, workflowDTO.getPayload());
                        startIntelligentScheduler();

                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        showErrorNotification(exceptionMessage);
                        hideProgressDialog();
                        Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
                    }
                };

        Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
        Map<String, String> query = new HashMap<>();
        query.put("practice_mgmt", selectedPractice.getPracticeMgmt());
        query.put("practice_id", selectedPractice.getPracticeId());
        query.put("patient_id", ApplicationPreferences.getInstance().getPatientId());
        getWorkflowServiceHelper().execute(appointmentsResultModel.getMetadata().getLinks().getGetPatientType(),
                getPatientTypeCallback, null, query, header);
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
        title.setText(StringUtil.capitalize(resource.getProvider().getName()));
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
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onVisitTypeSelected(VisitTypeQuestions autoVisitType) {
        selectedVisitType = autoVisitType.getVisittype();
        provider_screen_sub_header.setVisibility(View.GONE);
        autoVisitTypeTitle.setText(selectedVisitType.getName());
        autoVisitTypeContainer.setVisibility(View.VISIBLE);

        if (autoVisitType.getCheckbox() != null && getPatientTypeResponse != null) {
            if (autoVisitType.getCheckbox().getProviderSameAsLast() &&
                    autoVisitType.getCheckbox().getLocationSameAsLast() &&
                    getPatientTypeResponse != null &&
                    getPatientTypeResponse.getLastAppointment() != null) {

                setLocation(getPatientTypeResponse.getLastAppointment().getPayload().getLocation());
                locationCard.setEnabled(false);
                locationCard.findViewById(R.id.resetImageView).setVisibility(View.GONE);

                selectedResource = new AppointmentResourcesItemDTO();
                selectedResource.setProvider(getPatientTypeResponse.getLastAppointment().getPayload().getProvider());
                selectedResource.setResource_id(getPatientTypeResponse.getLastAppointment().getPayload().getResourceId());
                setResourceProvider(selectedResource);
                providerCard.setEnabled(false);
                providerCard.findViewById(R.id.resetImageView).setVisibility(View.GONE);

            } else {
                if (autoVisitType.getCheckbox().getLocationSameAsLast() && getPatientTypeResponse != null && getPatientTypeResponse.getLastAppointment() != null) {

                    selectedLocation = getPatientTypeResponse.getLastAppointment().getPayload().getLocation();
                    setLocation(selectedLocation);
                    locationCard.setEnabled(false);
                    locationCard.findViewById(R.id.resetImageView).setVisibility(View.GONE);

                } else if (autoVisitType.getCheckbox().getLocationPickList()) {
                  /*  if (!isLocationOnTop && selectedResource == null) {
                        locationNoDataTextView.setEnabled(false);
                    } else {
                        locationNoDataTextView.setEnabled(true);
                    }*/

                    lastLocationContainer.setVisibility(View.GONE);
                    locationContainer.setVisibility(View.VISIBLE);
                }

                if (autoVisitType.getCheckbox().getProviderSameAsLast() && getPatientTypeResponse != null && getPatientTypeResponse.getLastAppointment() != null) {
                    selectedResource = new AppointmentResourcesItemDTO();
                    selectedResource.setProvider(getPatientTypeResponse.getLastAppointment().getPayload().getProvider());
                    selectedResource.setResource_id(getPatientTypeResponse.getLastAppointment().getPayload().getResourceId());

                    setResourceProvider(selectedResource);
                    providerCard.setEnabled(false);
                    providerCard.findViewById(R.id.resetImageView).setVisibility(View.GONE);

                    /*if (selectedLocation == null && autoVisitType.getCheckbox().getLocationPickList()) {
                        locationNoDataTextView.setEnabled(true);
                        card_location.setVisibility(View.VISIBLE);
                    }*/

                } else if (autoVisitType.getCheckbox().getProviderPickList()) {
                   /* if (isLocationOnTop && selectedLocation == null) {
                        providersNoDataTextView.setEnabled(false);
                    } else {
                        providersNoDataTextView.setEnabled(true);
                    }*/

                    lastProviderContainer.setVisibility(View.GONE);
                    providerContainer.setVisibility(View.VISIBLE);
                }
            }
            enableAvailableButton();
        }

        fragment.dismiss();
    }

    @Override
    public void onOptionSelected(VisitTypeQuestions visitTypeQuestion) {
        fragment.onVisitOptionSelected(visitTypeQuestion);
    }

    @Override
    public void onViewAnswerClicked() {
        ((IntelligentSchedulerFragment) fragment).showQuestions(false);
        List<SchedulerAnswerTally> answerTallyList = fragment.getAllQuestionsAnswers();
        QuestionAnswerTallyFragment answerTallyFragment = QuestionAnswerTallyFragment.newInstance(answerTallyList);
        showFragment(answerTallyFragment);
    }

    @Override
    public void fromView() {
        new Handler().postDelayed(() -> {
            ((IntelligentSchedulerFragment) fragment).showQuestions(true);
        }, 1000);
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
