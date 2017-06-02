package com.carecloud.carepay.patient.checkout;


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

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.fragments.AvailableHoursFragment;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.customdialogs.VisitTypeFragmentDialog;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NextAppointmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NextAppointmentFragment extends BaseFragment {

    private CheckOutInterface callback;
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentDTO selectedAppointment;
    private TextView visitTypeTextView;
    private TextView visitTimeTextView;
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
        args.putSerializable(NextAppointmentActivity.APPOINTMENT_ID, appointmentId);
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
        TextView title = (TextView) toolbar.findViewById(com.carecloud.carepaylibrary.R.id.respons_toolbar_title);
        title.setText(Label.getLabel("next_appointment_toolbar_title"));
    }

    private void setUpUi(View view) {
        final ImageView providerPicImageView = (ImageView) view.findViewById(R.id.providerPicImageView);
        final TextView providerInitials = (TextView) view.findViewById(R.id.providerInitials);
        providerInitials.setText(StringUtil.getShortName(selectedAppointment.getPayload()
                .getProvider().getName()));
        Picasso.with(getContext()).load(selectedAppointment.getPayload().getProvider().getPhoto())
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
                showAvailableHoursFragment();
            }
        });

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
    }

    private void showVisitTypeFragment() {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", selectedAppointment.getMetadata().getPracticeMgmt());
        queries.put("practice_id", selectedAppointment.getMetadata().getPracticeId());
        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getLinks().getResourcesToSchedule();
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
        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(transitionDTO, continueCallback, queryMap, header);
    }

    private void scheduleAppointment() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", selectedAppointment.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", selectedAppointment.getMetadata().getPracticeId());
        queryMap.put("appointment_id", selectedAppointment.getMetadata().getAppointmentId());
        queryMap.put("patient_id", selectedAppointment.getMetadata().getPatientId());

        JsonObject patientJSONObj = new JsonObject();
        patientJSONObj.addProperty("id", selectedAppointment.getMetadata().getPatientId());

        JsonObject appointmentJSONObj = new JsonObject();
        appointmentJSONObj.addProperty("start_time", appointmentSlot.getStartTime());
        appointmentJSONObj.addProperty("end_time", appointmentSlot.getEndTime());
        appointmentJSONObj.addProperty("appointment_status_id", "5");
        appointmentJSONObj.addProperty("location_id", appointmentSlot.getLocation().getId());
        appointmentJSONObj.addProperty("provider_id", appointmentResourceDTO.getResource().getProvider().getId());
        appointmentJSONObj.addProperty("resource_id", appointmentResourceDTO.getResource().getId());
        appointmentJSONObj.addProperty("visit_reason_id", visitType.getId());
        appointmentJSONObj.addProperty("chief_complaint", visitType.getName());
        appointmentJSONObj.addProperty("comments", "");

        appointmentJSONObj.add("patient", patientJSONObj);

        JsonObject makeAppointmentJSONObj = new JsonObject();
        makeAppointmentJSONObj.add("appointment", appointmentJSONObj);

        Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
        header.put("transition", "true");

        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getMakeAppointment();
        getWorkflowServiceHelper().execute(transitionDTO, makeAppointmentCallback,
                makeAppointmentJSONObj.toString(), queryMap, header);
    }

    /**
     * show available hours fragment
     */
    public void showAvailableHoursFragment() {
        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment
                .newInstance(appointmentsResultModel, appointmentResourceDTO.getResource(),
                        null, null, visitType);
        callback.addFragment(availableHoursFragment, true);
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
            if (NavigationStateConstants.APPOINTMENTS.equals(workflowDTO.getState())) {
                callback.showAllDoneFragment(workflowDTO);
            } else {
                PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
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
            if (NavigationStateConstants.APPOINTMENTS.equals(workflowDTO.getState())) {
                callback.showAllDoneFragment(workflowDTO);
            } else {
                PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
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
        Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
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
            String resourcesToScheduleString = gson.toJson(workflowDTO);
            appointmentsResultModel = gson.fromJson(resourcesToScheduleString,
                    AppointmentsResultModel.class);
            appointmentResourceDTO = getResourceFromModel(appointmentsResultModel, selectedAppointment);

            VisitTypeFragmentDialog fragmentDialog = VisitTypeFragmentDialog.newInstance(appointmentResourceDTO, appointmentsResultModel);
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
        String selectedAppointmentId = getArguments().getString(NextAppointmentActivity.APPOINTMENT_ID);
        for (AppointmentDTO appointmentDTO : appointmentsResultModel.getPayload().getAppointments()) {
            if (appointmentDTO.getPayload().getId().equals(selectedAppointmentId)) {
                return appointmentDTO;
            }
        }
        return null;
    }

    /**
     * @param visitTypeDTO the visit type
     */
    public void setVisitType(VisitTypeDTO visitTypeDTO) {
        visitType = visitTypeDTO;
        visitTypeTextView.setText(visitTypeDTO.getName());
        visitTimeTextView.setEnabled(true);
    }

    /**
     * @param appointmentsSlot the location and time of the appointment
     */
    public void setLocationAndTime(AppointmentsSlotsDTO appointmentsSlot) {
        this.appointmentSlot = appointmentsSlot;
        String nextAppointmentDate = getNextAppointmentDate(appointmentsSlot.getStartTime());
        visitTimeTextView.setText(nextAppointmentDate);
        visitTypeTextView.setText(visitType.getName());

        findViewById(R.id.providerMessageHeader).setSelected(true);
        TextView providerMessage = (TextView) findViewById(R.id.providerMessage);
        providerMessage.setText(String.format(Label.getLabel("next_appointment_provider_message"),
                nextAppointmentDate));
        scheduleAppointmentButton.setEnabled(true);
    }

    private String getNextAppointmentDate(String time) {
        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(time);
        return dateUtil.getDateAsDayMonthDayOrdinal();
    }
}
