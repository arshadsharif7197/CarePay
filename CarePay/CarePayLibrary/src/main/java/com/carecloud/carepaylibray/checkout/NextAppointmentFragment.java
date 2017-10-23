package com.carecloud.carepaylibray.checkout;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.customdialogs.VisitTypeFragmentDialog;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NextAppointmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NextAppointmentFragment extends BaseFragment implements NextAppointmentFragmentInterface {

    private CheckOutInterface callback;
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentDTO selectedAppointment;
    private ProviderDTO selectedProvider;
    private TextView visitTypeTextView;
    private TextView visitTimeTextView;
    private TextView chooseProviderTextView;
    private TextView providerMessage;
    private VisitTypeDTO visitType;
    private AppointmentResourcesDTO appointmentResourceDTO;
    private Button scheduleAppointmentButton;
    private AppointmentsSlotsDTO appointmentSlot;

    /**
     * new Instance of NextAppointmentFragment
     */
    public NextAppointmentFragment() {
        // Required empty public constructor
    }

    /**
     * @param appointmentId the appointment id to checkout
     * @return a new instance of NextAppointmentFragment
     */
    public static NextAppointmentFragment newInstance(String appointmentId) {
        NextAppointmentFragment fragment = new NextAppointmentFragment();
        Bundle args = new Bundle();
        args.putSerializable(CarePayConstants.APPOINTMENT_ID, appointmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (CheckOutInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement ResponsibilityActionCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appointmentsResultModel = (AppointmentsResultModel) callback.getDto();
        selectedAppointment = getAppointmentSelected();
        selectedProvider = selectedAppointment.getPayload().getProvider();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_next_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        setUpUi(getView());
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        callback.setToolbar(toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("next_appointment_toolbar_title"));

        if (!callback.shouldAllowNavigateBack()) {
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(null);
        }
    }

    private void setUpUi(View view) {
        setUpProviderMessage(view, selectedProvider);

        Button scheduleLaterButton = (Button) view.findViewById(R.id.scheduleLaterButton);
        scheduleLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleAppointmentLater();
            }
        });

        scheduleAppointmentButton = (Button) getView().findViewById(R.id.scheduleAppointmentButton);
        scheduleAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleAppointment();
            }
        });

        chooseProviderTextView = (TextView) view.findViewById(R.id.providerTextView);
        chooseProviderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseProviderFragment();
            }
        });
        if (selectedProvider != null) {
            chooseProviderTextView.setText(selectedProvider.getFullName());
        }

        visitTypeTextView = (TextView) view.findViewById(R.id.visitTypeTextView);
        visitTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVisitTypeFragment();
            }
        });

        visitTimeTextView = (TextView) view.findViewById(R.id.visitTimeTextView);
        visitTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date start = null;
                Date end = null;
                if (appointmentSlot != null) {
                    start = DateUtil.getInstance().setDateRaw(appointmentSlot.getStartTime()).getDate();
                    end = DateUtil.getInstance().setDateRaw(appointmentSlot.getEndTime()).getDate();
                }
                callback.showAvailableHoursFragment(start, end, appointmentsResultModel,
                        appointmentResourceDTO.getResource(), visitType);
            }
        });
    }

    private void setUpProviderMessage(View view, ProviderDTO provider) {
        final ImageView providerPicImageView = (ImageView) view.findViewById(R.id.providerPicImageView);
        final TextView providerInitials = (TextView) view.findViewById(R.id.providerInitials);
        providerInitials.setText(StringUtil.getShortName(provider.getName()));
        Picasso.with(getContext()).load(provider.getPhoto())
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(providerPicImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        providerPicImageView.setVisibility(View.VISIBLE);
                        providerInitials.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        providerPicImageView.setVisibility(View.GONE);
                        providerInitials.setVisibility(View.VISIBLE);
                    }
                });

        providerMessage = (TextView) findViewById(R.id.providerMessage);
        setDefaultMessage();
    }

    private void showChooseProviderFragment() {
        callback.showChooseProviderFragment();
    }

    private void showVisitTypeFragment() {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", selectedAppointment.getMetadata().getPracticeMgmt());
        queries.put("practice_id", selectedAppointment.getMetadata().getPracticeId());
        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata()
                .getLinks().getResourcesToSchedule();
        Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
        getWorkflowServiceHelper().execute(transitionDTO, resourcesToScheduleCallback, queries, header);
    }

    private void scheduleAppointmentLater() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", selectedAppointment.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", selectedAppointment.getMetadata().getPracticeId());
        queryMap.put("appointment_id", selectedAppointment.getMetadata().getAppointmentId());
        queryMap.put("patient_id", selectedAppointment.getMetadata().getPatientId());
        Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
        header.put("transition", "true");
        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata()
                .getTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(transitionDTO, continueCallback, queryMap, header);
    }

    private void scheduleAppointment() {
        ScheduleAppointmentRequestDTO scheduleAppointmentRequestDTO = new ScheduleAppointmentRequestDTO();
        ScheduleAppointmentRequestDTO.Appointment appointment = scheduleAppointmentRequestDTO
                .getAppointment();
        appointment.setStartTime(appointmentSlot.getStartTime());
        appointment.setEndTime(appointmentSlot.getEndTime());
        appointment.setLocationId(appointmentSlot.getLocation().getId());
        appointment.setLocationGuid(appointmentSlot.getLocation().getGuid());
        appointment.setProviderId(appointmentResourceDTO.getResource().getProvider().getId());
        appointment.setProviderGuid(appointmentResourceDTO.getResource().getProvider().getGuid());
        appointment.setVisitReasonId(visitType.getId());
        appointment.setResourceId(appointmentResourceDTO.getResource().getId());
        appointment.setComplaint(visitType.getName());
        appointment.setComments("");
        appointment.getPatient().setId(selectedAppointment.getMetadata().getPatientId());

        if (visitType.getAmount() > 0) {
            callback.startPrepaymentProcess(scheduleAppointmentRequestDTO, appointmentSlot,
                    visitType.getAmount());
        } else {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("practice_mgmt", selectedAppointment.getMetadata().getPracticeMgmt());
            queryMap.put("practice_id", selectedAppointment.getMetadata().getPracticeId());
            queryMap.put("appointment_id", selectedAppointment.getMetadata().getAppointmentId());
            queryMap.put("patient_id", selectedAppointment.getMetadata().getPatientId());

            Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
            header.put("transition", "true");

            TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions()
                    .getMakeAppointment();
            Gson gson = new Gson();
            getWorkflowServiceHelper().execute(transitionDTO, makeAppointmentCallback,
                    gson.toJson(scheduleAppointmentRequestDTO), queryMap, header);
        }
    }

    private WorkflowServiceCallback makeAppointmentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            onAppointmentRequestSuccess();
            String state = workflowDTO.getState();
            if (NavigationStateConstants.APPOINTMENTS.equals(state)
                    || NavigationStateConstants.PATIENT_HOME.equals(state)) {
                callback.showAllDone(workflowDTO);
            } else {
                callback.navigateToWorkflow(workflowDTO);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            onFailureCallback(exceptionMessage);
        }
    };

    private void onAppointmentRequestSuccess() {
        getFragmentManager().popBackStack();
        showAppointmentConfirmation();
    }

    private void showAppointmentConfirmation() {
        String appointmentRequestSuccessMessage = Label.getLabel("appointment_request_success_message_HTML");
        SystemUtil.showSuccessToast(getContext(), appointmentRequestSuccessMessage);
    }

    WorkflowServiceCallback continueCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            String state = workflowDTO.getState();
            if (NavigationStateConstants.APPOINTMENTS.equals(state)
                    || NavigationStateConstants.PATIENT_HOME.equals(state)) {
                callback.showAllDone(workflowDTO);
            } else {
                callback.navigateToWorkflow(workflowDTO);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            onFailureCallback(exceptionMessage);
        }
    };

    private void onFailureCallback(String exceptionMessage) {
        hideProgressDialog();
        showErrorNotification(exceptionMessage);
        Log.e("Server Error", exceptionMessage);
    }

    WorkflowServiceCallback resourcesToScheduleCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Gson gson = new Gson();
            List<AppointmentsSettingDTO> appointmentsSettingDTOs = appointmentsResultModel
                    .getPayload().getAppointmentsSettings();
            String resourcesToScheduleString = gson.toJson(workflowDTO);
            appointmentsResultModel = gson.fromJson(resourcesToScheduleString,
                    AppointmentsResultModel.class);
            appointmentsResultModel.getPayload().setAppointmentsSettings(appointmentsSettingDTOs);
            appointmentResourceDTO = getResourceFromModel(appointmentsResultModel, selectedAppointment);

            VisitTypeFragmentDialog fragmentDialog = VisitTypeFragmentDialog
                    .newInstance(appointmentResourceDTO, appointmentsResultModel, getPracticeSettings());
            callback.displayDialogFragment(fragmentDialog, false);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            onFailureCallback(exceptionMessage);
        }
    };

    private AppointmentResourcesDTO getResourceFromModel(AppointmentsResultModel appointmentsResultModel,
                                                         AppointmentDTO selectedAppointment) {
        for (AppointmentResourcesDTO appointmentResourcesDTO : appointmentsResultModel.getPayload()
                .getResourcesToSchedule().get(0).getResources()) {
            if (appointmentResourcesDTO.getResource().getProvider().getId().equals(selectedAppointment
                    .getPayload().getProvider().getId())) {
                return appointmentResourcesDTO;
            }
        }
        return null;
    }

    private AppointmentDTO getAppointmentSelected() {
        if (appointmentsResultModel.getPayload().getAppointments().size() == 1) {
            return appointmentsResultModel.getPayload().getAppointments().get(0);
        }
        String selectedAppointmentId = getArguments().getString(CarePayConstants.APPOINTMENT_ID);
        for (AppointmentDTO appointmentDTO : appointmentsResultModel.getPayload().getAppointments()) {
            if (appointmentDTO.getPayload().getId().equals(selectedAppointmentId)) {
                return appointmentDTO;
            }
        }
        return null;
    }

    @Override
    public void setSelectedProvider(ProviderDTO provider) {
        selectedProvider = provider;
        if (selectedProvider != null) {
            selectedAppointment.getPayload().setProvider(provider);
            chooseProviderTextView.setText(selectedProvider.getFullName());
            setUpProviderMessage(getView(), selectedProvider);
            showVisitTypeFragment();
            appointmentSlot = null;
            visitType = null;
            visitTimeTextView.setText(null);
            visitTypeTextView.setText(null);
            scheduleAppointmentButton.setEnabled(false);
            findViewById(R.id.providerMessageHeader).setSelected(false);
        }
    }

    /**
     * @param visitTypeDTO the visit type
     */
    @Override
    public boolean setVisitType(VisitTypeDTO visitTypeDTO) {
        visitType = visitTypeDTO;
        visitTypeTextView.setText(visitTypeDTO.getName());
        visitTimeTextView.setEnabled(true);
        scheduleAppointmentButton.setEnabled(false);
        appointmentSlot = null;
        setDefaultMessage();
        findViewById(R.id.providerMessageHeader).setSelected(false);
        return true;
    }

    /**
     * @param appointmentsSlot the location and time of the appointment
     */
    @Override
    public void setLocationAndTime(AppointmentsSlotsDTO appointmentsSlot) {
        this.appointmentSlot = appointmentsSlot;
        String nextAppointmentDate = getNextAppointmentDate(appointmentsSlot.getStartTime());
        visitTimeTextView.setText(nextAppointmentDate);
        visitTypeTextView.setText(visitType.getName());

        findViewById(R.id.providerMessageHeader).setSelected(true);
        providerMessage.setText(String.format(Label.getLabel("next_appointment_provider_message"),
                nextAppointmentDate));
        scheduleAppointmentButton.setEnabled(true);
    }

    private String getNextAppointmentDate(String time) {
        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(time);
        return dateUtil.getDateAsDayMonthDayOrdinal();
    }

    private void setDefaultMessage() {
        String label = Label.getLabel("next_appointment_default_provider_message");
        if (label.contains("%s")) {//check if its "format-able"
            label = String.format(label, selectedAppointment.getPayload().getProvider().getName());
        }
        providerMessage.setText(label);
    }

    private AppointmentsSettingDTO getPracticeSettings() {
        List<AppointmentsSettingDTO> appointmentsSettingsList = appointmentsResultModel
                .getPayload().getAppointmentsSettings();
        String practiceId = selectedAppointment.getMetadata().getPracticeId();
        if (practiceId != null) {
            for (AppointmentsSettingDTO appointmentsSettingDTO : appointmentsSettingsList) {
                if (appointmentsSettingDTO.getPracticeId().equals(practiceId)) {
                    return appointmentsSettingDTO;
                }
            }
        }
        if (appointmentsSettingsList.isEmpty()) {
            return new AppointmentsSettingDTO();
        }
        return appointmentsSettingsList.get(0);
    }

}
