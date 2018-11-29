package com.carecloud.carepaylibray.checkout;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
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
import com.carecloud.carepaylibray.translation.TranslatableFragment;
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
public class NextAppointmentFragment extends BaseFragment implements NextAppointmentFragmentInterface,
        TranslatableFragment {

    private CheckOutInterface callback;
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentDTO selectedAppointment;
    private ProviderDTO selectedProvider;
    private VisitTypeDTO visitType;
    private AppointmentsSlotsDTO visitTime;
    private TextView providerMessage;
    private TextView chooseProviderTextView;
    private TextInputLayout visitTypeTextInputLayout;
    private TextView visitTypeTextView;
    private TextInputLayout visitTimeTextInputLayout;
    private TextView visitTimeTextView;
    private Button scheduleAppointmentButton;
    private AppointmentResourcesDTO appointmentResourcesDTO;
    private EditText reasonForVisitEditText;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_next_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appointmentsResultModel = (AppointmentsResultModel) callback.getDto();
        selectedAppointment = getAppointmentSelected();
        selectedProvider = selectedAppointment.getPayload().getProvider();
        setUpToolbar(view);
        setUpUi(getView());
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        callback.setToolbar(toolbar);
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("next_appointment_toolbar_title"));

        if (!callback.shouldAllowNavigateBack()) {
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(null);
        }
    }

    private void setUpUi(final View view) {
        setUpProviderMessage(view, selectedProvider);

        Button scheduleLaterButton = view.findViewById(R.id.scheduleLaterButton);
        scheduleLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleAppointmentLater();
            }
        });

        scheduleAppointmentButton = getView().findViewById(R.id.scheduleAppointmentButton);
        scheduleAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleAppointment();
            }
        });

        chooseProviderTextView = view.findViewById(R.id.providerTextView);
        chooseProviderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseProviderFragment();
            }
        });
        if (selectedProvider != null) {
            chooseProviderTextView.setText(selectedProvider.getFullName());
        }

        visitTypeTextInputLayout = view.findViewById(R.id.visitTypeTextInputLayout);
        visitTypeTextView = view.findViewById(R.id.visitTypeTextView);
        visitTypeTextView.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(visitTypeTextInputLayout, null));
        visitTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVisitTypeFragment();
            }
        });
        visitTypeTextView.getOnFocusChangeListener().onFocusChange(visitTypeTextView,
                !StringUtil.isNullOrEmpty(visitTypeTextView.getText().toString().trim()));

        visitTimeTextInputLayout = view.findViewById(R.id.visitTimeTextInputLayout);
        visitTimeTextView = view.findViewById(R.id.visitTimeTextView);
        visitTimeTextView.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(visitTimeTextInputLayout, null));
        visitTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date start = null;
                Date end = null;
                if (visitTime != null) {
                    start = DateUtil.getInstance().setDateRaw(visitTime.getStartTime()).getDate();
                    end = DateUtil.getInstance().setDateRaw(visitTime.getEndTime()).getDate();
                }
                callback.showAvailableHoursFragment(start, end, appointmentsResultModel,
                        appointmentResourcesDTO.getResource(), visitType);
            }
        });
        visitTimeTextView.getOnFocusChangeListener().onFocusChange(visitTimeTextView,
                !StringUtil.isNullOrEmpty(visitTimeTextView.getText().toString().trim()));

        final ScrollView scrollContainer = view.findViewById(R.id.scrollContainer);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                SystemUtil.hideSoftKeyboard(getContext(), view);
                if (scrollContainer != null) {
                    scrollContainer.fullScroll(View.FOCUS_UP);
                }
            }
        }, 300);

        reasonForVisitEditText = view.findViewById(R.id.reasonForVisitEditText);
    }

    private void setUpProviderMessage(View view, ProviderDTO provider) {
        final ImageView providerPicImageView = view.findViewById(R.id.providerPicImageView);
        final TextView providerInitials = view.findViewById(R.id.providerInitials);
        providerInitials.setText(StringUtil.getShortName(provider.getName()));
        int size = getResources().getDimensionPixelSize(R.dimen.nextAppointmentProviderPicImageSize);
        Picasso.with(getContext()).load(provider.getPhoto())
                .resize(size,size)
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
        appointment.setProviderId(selectedProvider.getId());
        appointment.setProviderGuid(selectedProvider.getGuid());
        appointment.setVisitReasonId(visitType.getId());
        appointment.setComplaint(reasonForVisitEditText.getText().toString());
        appointment.setStartTime(visitTime.getStartTime());
        appointment.setEndTime(visitTime.getEndTime());
        appointment.setLocationId(visitTime.getLocation().getId());
        appointment.setLocationGuid(visitTime.getLocation().getGuid());
        appointment.setComments(visitType.getName());
        if (appointmentResourcesDTO != null) {
            appointment.setResourceId(appointmentResourcesDTO.getResource().getId());
        }
        appointment.getPatient().setId(selectedAppointment.getMetadata().getPatientId());

        if (visitType.getAmount() > 0) {
            callback.startPrepaymentProcess(scheduleAppointmentRequestDTO, visitTime,
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
            if (!workflowDTO.getState().contains("checkout")) {
                callback.completeCheckout();
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
            if (!workflowDTO.getState().contains("checkout")) {
                callback.completeCheckout();
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
            appointmentResourcesDTO = getResourceFromModel(appointmentsResultModel, selectedAppointment);

            VisitTypeFragmentDialog fragmentDialog = VisitTypeFragmentDialog
                    .newInstance(appointmentResourcesDTO, appointmentsResultModel, getPracticeSettings());
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
    public void setSelectedProvider(ProviderDTO provider, boolean showNextDialogs) {
        selectedProvider = provider;
        if (selectedProvider != null) {
            selectedAppointment.getPayload().setProvider(provider);
            chooseProviderTextView.setText(selectedProvider.getFullName());
            setUpProviderMessage(getView(), selectedProvider);
            if (showNextDialogs) {
                showVisitTypeFragment();
            }

            visitTypeTextView.setText(null);
            setHint(visitTypeTextView, visitTypeTextInputLayout, null);
            visitType = null;

            visitTimeTextView.setText(null);
            setHint(visitTimeTextView, visitTimeTextInputLayout, null);
            visitTimeTextView.setEnabled(false);
            visitTime = null;
            scheduleAppointmentButton.setEnabled(false);
            findViewById(R.id.providerMessageHeader).setSelected(false);
        }
    }

    /**
     * @param visitTypeDTO    the visit type
     * @param showNextDialogs
     */
    @Override
    public boolean setVisitType(VisitTypeDTO visitTypeDTO, boolean showNextDialogs) {
        visitType = visitTypeDTO;
        visitTypeTextView.setText(visitTypeDTO.getName());
        setHint(visitTypeTextView, visitTypeTextInputLayout, visitTypeDTO.getName());
        visitTimeTextView.setEnabled(true);

        visitTimeTextView.setText(null);
        setHint(visitTimeTextView, visitTimeTextInputLayout, null);
        visitTime = null;

        findViewById(R.id.providerMessageHeader).setSelected(false);
        setDefaultMessage();
        scheduleAppointmentButton.setEnabled(false);
        return showNextDialogs;
    }

    /**
     * @param appointmentsSlot the location and time of the appointment
     * @param showNextDialogs
     */
    @Override
    public void setLocationAndTime(AppointmentsSlotsDTO appointmentsSlot, boolean showNextDialogs) {
        this.visitTime = appointmentsSlot;
        String nextAppointmentDate = getNextAppointmentDate(appointmentsSlot.getStartTime());
        visitTimeTextView.setText(nextAppointmentDate);
        setHint(visitTimeTextView, visitTimeTextInputLayout, nextAppointmentDate);

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

    private void setHint(TextView textView, TextInputLayout inputLayout, String name) {
        String[] tags = (String[]) textView.getTag();
        if (name == null) {
            inputLayout.setHint(tags[1]);
        } else {
            inputLayout.setHint(tags[0]);
        }
    }

    @Override
    public Bundle saveInstanceForTranslation(Bundle bundle) {
        Gson gson = new Gson();
        bundle.putString("provider", gson.toJson(selectedProvider));
        bundle.putString("visitType", gson.toJson(visitType));
        bundle.putString("visitTime", gson.toJson(visitTime));
        bundle.putString("appointmentResource", gson.toJson(appointmentResourcesDTO));
        bundle.putBoolean("shouldReload", true);
        return bundle;
    }

    @Override
    public void restoreInstanceForTranslation(Bundle bundle) {
        Gson gson = new Gson();
        ProviderDTO provider = gson.fromJson(bundle.getString("provider"), ProviderDTO.class);
        if (provider != null) {
            setSelectedProvider(provider, false);
        }
        VisitTypeDTO visitType = gson.fromJson(bundle.getString("visitType"), VisitTypeDTO.class);
        if (visitType != null) {
            setVisitType(visitType, false);
        }
        AppointmentsSlotsDTO visitTime = gson.fromJson(bundle.getString("visitTime"),
                AppointmentsSlotsDTO.class);
        if (visitTime != null) {
            setLocationAndTime(visitTime, false);
        }
        appointmentResourcesDTO = gson.fromJson(bundle.getString("appointmentResource"), AppointmentResourcesDTO.class);
    }
}
