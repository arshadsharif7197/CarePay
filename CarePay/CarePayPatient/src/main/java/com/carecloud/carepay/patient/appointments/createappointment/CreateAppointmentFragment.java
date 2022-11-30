package com.carecloud.carepay.patient.appointments.createappointment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.AppointmentViewModel;
import com.carecloud.carepay.patient.appointments.activities.IntelligentSchedulerActivity;
import com.carecloud.carepay.patient.appointments.adapters.PracticesAdapter;
import com.carecloud.carepay.patient.patientsplash.SplashActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.BaseCreateAppointmentFragment;
import com.carecloud.carepaylibray.appointments.createappointment.CreateAppointmentFragmentInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPopUpDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.GetPatientTypeResponse;
import com.carecloud.carepaylibray.appointments.models.IntelligentSchedulerDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.customdialogs.ExitAlertDialog;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 1/15/19.
 */
public class CreateAppointmentFragment extends BaseCreateAppointmentFragment implements CreateAppointmentFragmentInterface {

    public static CreateAppointmentFragment fragment;
    private AppointmentViewModel appointmentViewModel;

    public static CreateAppointmentFragment newInstance() {
        return new CreateAppointmentFragment();
    }

    public static CreateAppointmentFragment newInstance(UserPracticeDTO userPracticeDTO,
                                                        AppointmentResourcesItemDTO selectedResource,
                                                        VisitTypeDTO selectedVisitTypeDTO,
                                                        LocationDTO selectedLocation) {
        Bundle args = new Bundle();
        args.putBoolean("isReschedule", true);
        DtoHelper.bundleDto(args, userPracticeDTO);gi
        DtoHelper.bundleDto(args, selectedResource);
        DtoHelper.bundleDto(args, selectedVisitTypeDTO);
        DtoHelper.bundleDto(args, selectedLocation);
        fragment = new CreateAppointmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CreateAppointmentFragment getInstance() {
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        initData();
        showPracticeList(view);
    }

    private void initData() {
        appointmentViewModel = new ViewModelProvider(requireActivity()).get(AppointmentViewModel.class);
        appointmentViewModel.getAutoScheduleVisitTypeObservable().observe(requireActivity(), autoVisitType -> {
            if (autoVisitType != null) {

                // selectedVisitType = autoVisitType;
                selectedVisitType = autoVisitType.getVisittype();
                // tvAutoVisitType.setText(autoVisitType.getName());
                tvAutoVisitType.setText(autoVisitType.getVisittype().getName());
                autoVisitTypeContainer.setVisibility(View.VISIBLE);
                visitTypeCard.setVisibility(View.GONE);
                //auto location and provider setup
                if (autoVisitType.getCheckbox() != null) {
                    if (autoVisitType.getCheckbox().getLocationSameAsLast()&&getPatientTypeResponse!=null&&getPatientTypeResponse.getLastAppointment()!=null) {

                        selectedLocation = getPatientTypeResponse.getLastAppointment().getPayload().getLocation();
                        auto_location_container.setVisibility(View.VISIBLE);
                        card_location.setVisibility(View.GONE);
                        location_container1.setVisibility(View.GONE);
                        setCardViewContent(autoLocationContainerData, selectedLocation.getName(), selectedLocation.getAddress().geAddressStringWithShortZipWOCounty(), false, null);
                        //  setLocation(selectedLocation,true);
                        // auto_location_container.findViewById(titleTextView)

                    }else if (autoVisitType.getCheckbox().getLocationPickList()){
                        locationNoDataTextView.setEnabled(true);
                    }

                    if (autoVisitType.getCheckbox().getProviderSameAsLast()&&getPatientTypeResponse!=null&&getPatientTypeResponse.getLastAppointment()!=null) {
                        selectedResource = getPatientTypeResponse.getLastAppointment().getPayload().getProvider();
                        card_provider.setVisibility(View.GONE);
                        auto_provider_container.setVisibility(View.VISIBLE);
                        String providerName = selectedResource.getName();
                        String speciality = selectedResource.getProvider().getSpecialityName();
                        //TextView titleTextview= autoProviderContainerData.findViewById(R.id.titleTextView);
                        // TextView subTitleTextview= autoProviderContainerData.findViewById(R.id.subtitleTextView);
                        // titleTextview.setText(providerName);
                        // subTitleTextview.setText(speciality);
                        setCardViewContent(autoProviderContainerData, providerName, speciality, true, selectedResource.getProvider().getPhoto());
                        //setResourceProvider(selectedResource);


                    }else if (autoVisitType.getCheckbox().getProviderPickList()){
                        providersNoDataTextView.setEnabled(true);
                    }
                    checkIfButtonEnabled();
                }


                // if (appointmentsModelDto.getPayload().getIntelligent_scheduler().get(0).getEstablishedPatientIntelligentSchedulerQuestions().get(0).)
                // Disable Location selection
                if (!isLocationOnTop&&!isSchedulerEnabled) {
                    providersNoDataTextView.setEnabled(true);
                    locationNoDataTextView.setEnabled(false);
                }
            } else {
                autoVisitTypeContainer.setVisibility(View.GONE);
                visitTypeCard.setVisibility(View.VISIBLE);
                if (isSchedulerEnabled)
                    onBackPressed();
            }
        });
        appointmentViewModel.setAutoScheduleVisitTypeObservable(null);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> onBackPressed());
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("appointments_heading"));
        callback.displayToolbar(false, null);
    }


    private void showPracticeList(View view) {
        RecyclerView practicesRecyclerView = view.findViewById(R.id.practicesRecyclerView);
        List<UserPracticeDTO> filteredList = filterPracticesList(appointmentsModelDto.getPayload().getUserPractices());
        selectedPractice = filteredList.get(0);
        if (filteredList.size() > 1 && !isReschedule) {
            // by default no practice will be selected
            selectedPractice = null;
            practicesRecyclerView.setVisibility(View.VISIBLE);
            practicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            PracticesAdapter adapter = new PracticesAdapter(filteredList);
            adapter.setCallback((userPracticeDTO, position) -> {

                isSchedulerEnabled = false;
                if (selectedPractice != null && !selectedPractice.getPracticeId().equals(userPracticeDTO.getPracticeId())) {
                    resetForm();
                    if ((!appointmentsModelDto.getPayload().getIntelligent_scheduler().get(0).isSchedulerEnabled() &&
                            appointmentsModelDto.getPayload().getIntelligent_scheduler().get(0).getIntelligent_scheduler_questions() == null)
                            ||(!appointmentsModelDto.getPayload().getIntelligent_scheduler().get(0).isEstablishedPatientSchedulerEnabled() &&
                            appointmentsModelDto.getPayload().getIntelligent_scheduler().get(0).getEstablishedPatientIntelligentSchedulerQuestions()==null)){

                        if (appointmentsModelDto.getPayload().getAppointmentsSettings().get(position).getScheduleResourceOrder().getOrder().startsWith("location")) {

                            isLocationOnTop = true;
                            setLocationVisibility(isLocationOnTop);
                        }
                        if (appointmentsModelDto.getPayload().getAppointmentsSettings().get(position).getScheduleResourceOrder().getOrder().startsWith("provider")) {
                            isLocationOnTop = false;
                            setLocationVisibility(isLocationOnTop);
                        }
                    }



                }
                selectedPractice = userPracticeDTO;


                AppointmentsPopUpDTO appointmentsPopUpDTO = appointmentsModelDto.getPayload().getAppointmentsSetting(selectedPractice.getPracticeId()).getAppointmentsPopUpDTO();
                if (appointmentsPopUpDTO != null && appointmentsPopUpDTO.isEnabled()) {
                    ExitAlertDialog selfPayAlertDialog = ExitAlertDialog.
                            newInstance(appointmentsPopUpDTO.getText(),
                                    Label.getLabel("ok"), Label.getLabel("button_no"));
                    // Intelligent Scheduler flow
                    // selfPayAlertDialog.setOnDismissListener(dialogInterface -> startIntelligentScheduler());  old
                    selfPayAlertDialog.setOnDismissListener(dialogInterface -> getPatientType());
                    selfPayAlertDialog.show(requireActivity().getSupportFragmentManager(), selfPayAlertDialog.getClass().getName());
                } else {
                    // Intelligent Scheduler flow
                    //startIntelligentScheduler(); old
                    getPatientType();

                }
            });
            practicesRecyclerView.setAdapter(adapter);
        } else {
            practicesRecyclerView.setVisibility(View.GONE);
            // Intelligent Scheduler flow
            // startIntelligentScheduler(); old
            getPatientType();

        }
    }

    private void getPatientType() {

        Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
        Map<String, String> query = new HashMap<>();
        query.put("practice_mgmt", selectedPractice.getPracticeMgmt());
        query.put("practice_id", selectedPractice.getPracticeId());
        query.put("patient_id", getPatientId(appointmentsModelDto, selectedPractice));
        getWorkflowServiceHelper().execute(appointmentsModelDto.getMetadata().getLinks().getGetPatientType(),
                getPatientTypeCallback, null, query, header);
    }

    private String getPatientId(AppointmentsResultModel appointmentsModelDto, UserPracticeDTO selectedPractice) {
        String patientId = "";
        for (PracticePatientIdsDTO practicePatientIdsDTO : appointmentsModelDto.getPayload().getPracticePatientIds()) {
            if (practicePatientIdsDTO.getPracticeId().equals(selectedPractice.getPracticeId())) {
                patientId = practicePatientIdsDTO.getPatientId();
                break;
            }
        }
        return patientId;
    }

    private void startIntelligentScheduler() {
        IntelligentSchedulerDTO intelligentSchedulerDTO = getIntelligentScheduler(selectedPractice);
        //old logic
       /* if (selectedPractice != null &&
                intelligentSchedulerDTO != null &&
                intelligentSchedulerDTO.isSchedulerEnabled() &&
                intelligentSchedulerDTO.getIntelligent_scheduler_questions() != null &&
                isNewUser(appointmentsModelDto.getPayload().getAppointments())) {

            isSchedulerEnabled = true;
            requireActivity().startActivityForResult(new Intent(requireContext(),
                    IntelligentSchedulerActivity.class).putExtra(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY,
                    new Gson().toJson(intelligentSchedulerDTO.getIntelligent_scheduler_questions().get(0))), CarePayConstants.INTELLIGENT_SCHEDULER_REQUEST);
        }*/
        //new logic

        if (selectedPractice != null && intelligentSchedulerDTO != null) {
            if (intelligentSchedulerDTO.isSchedulerEnabled() &&
                    intelligentSchedulerDTO.getIntelligent_scheduler_questions() != null && getPatientTypeResponse.getPatientType().getNewPatient()) {
                isSchedulerEnabled = true;
                isLocationOnTop=false;
               // resetForm();


                requireActivity().startActivityForResult(new Intent(requireContext(),
                        IntelligentSchedulerActivity.class).putExtra(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY,
                        new Gson().toJson(intelligentSchedulerDTO.getIntelligent_scheduler_questions().get(0))), CarePayConstants.INTELLIGENT_SCHEDULER_REQUEST);
            } else if (intelligentSchedulerDTO.isEstablishedPatientSchedulerEnabled() &&
                    intelligentSchedulerDTO.getEstablishedPatientIntelligentSchedulerQuestions() != null && !getPatientTypeResponse.getPatientType().getNewPatient()) {

                isSchedulerEnabled = true;
                isLocationOnTop=false;
               // resetForm();


                requireActivity().startActivityForResult(new Intent(requireContext(),
                        IntelligentSchedulerActivity.class).putExtra(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY,
                        new Gson().toJson(intelligentSchedulerDTO.getEstablishedPatientIntelligentSchedulerQuestions().get(0))), CarePayConstants.INTELLIGENT_SCHEDULER_REQUEST);
            }
        }


/*        isSchedulerStarted = true; // For local testing
        requireActivity().startActivityForResult(new Intent(requireContext(), IntelligentSchedulerActivity.class)
                        .putExtra(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY, CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS),
                CarePayConstants.INTELLIGENT_SCHEDULER_REQUEST);*/
    }

    private IntelligentSchedulerDTO getIntelligentScheduler(UserPracticeDTO selectedPractice) {
        for (IntelligentSchedulerDTO intelligentSchedulerDTO : appointmentsModelDto.getPayload().getIntelligent_scheduler()) {
            if (intelligentSchedulerDTO.getPractice_id().equals(selectedPractice.getPracticeId())) {
                return intelligentSchedulerDTO;
            }
        }
        return null;
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

    private List<UserPracticeDTO> filterPracticesList(List<UserPracticeDTO> userPractices) {
        List<UserPracticeDTO> filteredList = new ArrayList<>();
        for (UserPracticeDTO practiceDTO : userPractices) {
            if (appointmentsModelDto.getPayload().canScheduleAppointments(practiceDTO.getPracticeId())) {
                filteredList.add(practiceDTO);
            }
        }
        return filteredList;
    }

    @Override
    protected void showAvailabilityFragment() {
        AvailabilityHourFragment availabilityHourFragment = AvailabilityHourFragment.newInstance(AvailabilityHourFragment.SCHEDULE_MODE);
        availabilityHourFragment.setOnBackPressedListener(new OnBackPressedInterface() {
            @Override
            public void onBackPressed() {
                callback.displayToolbar(false, null);
            }
        });
        callback.showFragment(availabilityHourFragment);
    }

    protected void showLocationList(UserPracticeDTO selectedPractice,
                                    AppointmentResourcesItemDTO selectedProvider,
                                    VisitTypeDTO selectedVisitType) {
        LocationListFragment fragment = LocationListFragment
                .newInstance(selectedPractice, selectedVisitType, selectedProvider);
        callback.showFragment(fragment);
    }

    protected void showVisitTypeList(UserPracticeDTO selectedPractice,
                                     AppointmentResourcesItemDTO selectedProvider,
                                     LocationDTO selectedLocation) {
        VisitTypeListFragment fragment = VisitTypeListFragment
                .newInstance(selectedPractice, selectedLocation, selectedProvider);
        callback.showFragment(fragment);
    }

    protected void showProviderList(UserPracticeDTO selectedPractice,
                                    VisitTypeDTO selectedVisitType,
                                    LocationDTO selectedLocation) {
        ProviderListFragment fragment = ProviderListFragment
                .newInstance(selectedPractice, selectedVisitType, selectedLocation);
        callback.showFragment(fragment);
    }


    @Override
    public void setAppointmentSlot(AppointmentsSlotsDTO slot) {
        //NA for this flow
    }

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

}
