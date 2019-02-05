package com.carecloud.carepaylibray.checkout;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.createappointment.CreateAppointmentFragmentInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.translation.TranslatableFragment;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseNextAppointmentFragment extends BaseFragment
        implements TranslatableFragment, CreateAppointmentFragmentInterface {

    protected CheckOutInterface callback;
    protected AppointmentsResultModel appointmentsResultModel;
    protected VisitTypeDTO selectedVisitType;
    protected AppointmentResourcesItemDTO selectedResource;
    protected LocationDTO selectedLocation;
    protected AppointmentsSlotsDTO selectedTimeSlot;
    protected UserPracticeDTO selectedPractice;
    private AppointmentDTO selectedAppointment;
    private TextView providerMessage;
    private TextInputLayout providerTextInputLayout;
    private TextView chooseProviderTextView;
    private TextInputLayout visitTypeTextInputLayout;
    private TextView visitTypeTextView;
    private TextInputLayout visitTimeTextInputLayout;
    private TextView visitTimeTextView;
    private TextInputLayout locationTextInputLayout;
    private TextView locationTextView;
    private Button scheduleAppointmentButton;
    private EditText reasonForVisitEditText;
    private View providerResetImage;
    private View locationResetImage;
    private View visitTypeResetImage;
    private View visitTimeResetImage;

    /**
     * new Instance of BaseNextAppointmentFragment
     */
    public BaseNextAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (CheckOutInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement CheckOutInterface");
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appointmentsResultModel = (AppointmentsResultModel) callback.getDto();
        selectedAppointment = getAppointmentSelected();
        selectedResource = new AppointmentResourcesItemDTO();
        selectedResource.setId(selectedAppointment.getPayload().getResourceId());
        selectedResource.setProvider(selectedAppointment.getPayload().getProvider());
        selectedLocation = selectedAppointment.getPayload().getLocation();
        selectedPractice = new UserPracticeDTO();
        selectedPractice.setPracticeId(selectedAppointment.getMetadata().getPracticeId());
        selectedPractice.setPracticeMgmt(selectedAppointment.getMetadata().getPracticeMgmt());
        setUpToolbar(view);
        setUpUI(view);
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

    private void setUpUI(final View view) {
        setUpProviderMessage(view, selectedResource.getProvider());

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

        setUpProviderField(view);
        setUpLocationField(view);
        setUpVisitTypeField(view);
        setUpVisitTimeField(view);

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

    private void setUpProviderField(View view) {
        providerTextInputLayout = view.findViewById(R.id.providerTextInputLayout);
        chooseProviderTextView = view.findViewById(R.id.providerTextView);
        chooseProviderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseProviderFragment();
            }
        });
        chooseProviderTextView.setText(selectedResource.getProvider().getName());
        chooseProviderTextView.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(providerTextInputLayout, null));
        chooseProviderTextView.getOnFocusChangeListener().onFocusChange(chooseProviderTextView,
                !StringUtil.isNullOrEmpty(chooseProviderTextView.getText().toString().trim()));

        providerResetImage = view.findViewById(R.id.providerResetImage);
        providerResetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedResource = null;
                chooseProviderTextView.setText(null);
                setHint(chooseProviderTextView, providerTextInputLayout, null);
                providerResetImage.setVisibility(View.GONE);
                chooseProviderTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(R.drawable.icon_drop_down), null);
                enableTimeSlotField();
                enableScheduleAppointmentButton();
            }
        });
    }

    private void setUpLocationField(View view) {
        locationTextInputLayout = view.findViewById(R.id.locationTextInputLayout);
        locationTextView = view.findViewById(R.id.locationTextView);
        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseLocationFragment();
            }
        });
        locationTextView.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(locationTextInputLayout, null));
        locationTextView.setText(StringUtil.capitalize(selectedLocation.getName()));
        locationTextView.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(locationTextInputLayout, null));
        locationTextView.getOnFocusChangeListener().onFocusChange(locationTextView,
                !StringUtil.isNullOrEmpty(locationTextView.getText().toString().trim()));

        locationResetImage = view.findViewById(R.id.locationResetImage);
        locationResetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLocation = null;
                locationTextView.setText(null);
                setHint(locationTextView, locationTextInputLayout, null);
                locationResetImage.setVisibility(View.GONE);
                locationTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(R.drawable.icon_drop_down), null);
                enableTimeSlotField();
                enableScheduleAppointmentButton();
            }
        });

    }

    private void setUpVisitTypeField(View view) {
        visitTypeTextInputLayout = view.findViewById(R.id.visitTypeTextInputLayout);
        visitTypeTextView = view.findViewById(R.id.visitTypeTextView);
        visitTypeTextView.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(visitTypeTextInputLayout, null));
        visitTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVisitTypeFragment();
            }
        });
        visitTypeTextView.getOnFocusChangeListener().onFocusChange(visitTypeTextView,
                !StringUtil.isNullOrEmpty(visitTypeTextView.getText().toString().trim()));

        visitTypeResetImage = view.findViewById(R.id.visitTypeResetImage);
        visitTypeResetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedVisitType = null;
                visitTypeTextView.setText(null);
                setHint(visitTypeTextView, visitTypeTextInputLayout, null);
                visitTypeResetImage.setVisibility(View.GONE);
                visitTypeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(R.drawable.icon_drop_down), null);
                enableTimeSlotField();
                enableScheduleAppointmentButton();
            }
        });
    }

    private void setUpVisitTimeField(View view) {
        visitTimeTextInputLayout = view.findViewById(R.id.visitTimeTextInputLayout);
        visitTimeTextView = view.findViewById(R.id.visitTimeTextView);
        visitTimeTextView.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(visitTimeTextInputLayout, null));
        visitTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAvailabilityFragment();
            }
        });
        visitTimeTextView.getOnFocusChangeListener().onFocusChange(visitTimeTextView,
                !StringUtil.isNullOrEmpty(visitTimeTextView.getText().toString().trim()));

        visitTimeResetImage = view.findViewById(R.id.visitTimeResetImage);
        visitTimeResetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTimeSlot = null;
                visitTimeTextView.setText(null);
                setHint(visitTimeTextView, visitTimeTextInputLayout, null);
                visitTimeResetImage.setVisibility(View.GONE);
                visitTimeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(R.drawable.icon_drop_down), null);
                setDefaultMessage();
                enableScheduleAppointmentButton();
            }
        });
    }

    private void setUpProviderMessage(View view, ProviderDTO provider) {
        final ImageView providerPicImageView = view.findViewById(R.id.providerPicImageView);
        final TextView providerInitials = view.findViewById(R.id.providerInitials);
        providerInitials.setText(StringUtil.getShortName(provider.getName()));
        if (!StringUtil.isNullOrEmpty(provider.getPhoto())) {
            PicassoHelper.get().loadImage(getContext(), providerPicImageView, providerInitials, provider.getPhoto());
        }
        providerMessage = (TextView) findViewById(R.id.providerMessage);
        setDefaultMessage();
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
        getWorkflowServiceHelper().execute(transitionDTO, getContinueCallback(false), queryMap, header);
    }

    private void scheduleAppointment() {
        ScheduleAppointmentRequestDTO scheduleAppointmentRequestDTO = new ScheduleAppointmentRequestDTO();
        ScheduleAppointmentRequestDTO.Appointment appointment = scheduleAppointmentRequestDTO
                .getAppointment();
        appointment.setProviderId(selectedResource.getProvider().getId());
        appointment.setProviderGuid(selectedResource.getProvider().getGuid());
        appointment.setVisitReasonId(selectedVisitType.getId());
        appointment.setComplaint(reasonForVisitEditText.getText().toString());
        appointment.setStartTime(selectedTimeSlot.getStartTime());
        appointment.setEndTime(selectedTimeSlot.getEndTime());
        appointment.setLocationId(selectedLocation.getId());
        appointment.setLocationGuid(selectedLocation.getGuid());
        appointment.setComments(selectedVisitType.getName());
        if (selectedResource != null) {
            appointment.setResourceId(selectedResource.getId());
        }
        appointment.getPatient().setId(selectedAppointment.getMetadata().getPatientId());

        if (selectedVisitType.getAmount() > 0) {
            callback.startPrepaymentProcess(scheduleAppointmentRequestDTO,
                    selectedVisitType.getAmount(), selectedPractice.getPracticeId());
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
            getWorkflowServiceHelper().execute(transitionDTO, getContinueCallback(true),
                    gson.toJson(scheduleAppointmentRequestDTO), queryMap, header);
        }
    }

    private void onAppointmentRequestSuccess() {
        getFragmentManager().popBackStack();
        String appointmentRequestSuccessMessage = Label.getLabel("appointment_request_success_message_HTML");
        SystemUtil.showSuccessToast(getContext(), appointmentRequestSuccessMessage);
    }

    WorkflowServiceCallback getContinueCallback(final boolean appointmentMade) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                if (appointmentMade) {
                    onAppointmentRequestSuccess();
                    MixPanelUtil.endTimer(getString(R.string.timer_next_appt));
                }
                String state = workflowDTO.getState();
                if (NavigationStateConstants.APPOINTMENTS.equals(state)
                        || NavigationStateConstants.PATIENT_HOME.equals(state)) {
                    callback.showAllDone(workflowDTO);
                } else if (NavigationStateConstants.SURVEYS_CHECKOUT.equals(state)) {
                    callback.startSurveyFlow(workflowDTO);
                } else {
                    callback.navigateToWorkflow(workflowDTO);
                }

                boolean surveyAvailable = NavigationStateConstants.SURVEYS_CHECKOUT.equals(workflowDTO.getState());
                if (!workflowDTO.getState().contains("checkout") || surveyAvailable) {
                    callback.completeCheckout(false, 0D, surveyAvailable, false);
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e("Server Error", exceptionMessage);
            }
        };
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

    @Override
    public void setResourceProvider(AppointmentResourcesItemDTO resource) {
        selectedResource = resource;
        providerResetImage.setVisibility(View.VISIBLE);
        chooseProviderTextView.setCompoundDrawables(null, null, null, null);
        selectedAppointment.getPayload().setProvider(resource.getProvider());
        chooseProviderTextView.setText(selectedResource.getProvider().getName());
        setUpProviderMessage(getView(), selectedResource.getProvider());
        selectedTimeSlot = null;
        visitTimeTextView.setText(null);
        setHint(chooseProviderTextView, providerTextInputLayout, chooseProviderTextView.getText().toString());
        enableScheduleAppointmentButton();
        enableTimeSlotField();
    }

    @Override
    public void setLocation(LocationDTO locationDTO) {
        selectedLocation = locationDTO;
        locationResetImage.setVisibility(View.VISIBLE);
        locationTextView.setCompoundDrawables(null, null, null, null);
        locationTextView.setText(StringUtil.capitalize(locationDTO.getName()));
        setHint(locationTextView, locationTextInputLayout, locationTextView.getText().toString());
        enableTimeSlotField();
        enableScheduleAppointmentButton();
    }

    @Override
    public void setVisitType(VisitTypeDTO visitType) {
        selectedVisitType = visitType;
        visitTypeResetImage.setVisibility(View.VISIBLE);
        visitTypeTextView.setCompoundDrawables(null, null, null, null);
        visitTypeTextView.setText(StringUtil.capitalize(visitType.getName()));
        setHint(visitTypeTextView, visitTypeTextInputLayout, StringUtil.capitalize(visitType.getName()));

        setDefaultMessage();
        selectedTimeSlot = null;
        setHint(visitTimeTextView, visitTimeTextInputLayout, null);
        enableTimeSlotField();
        enableScheduleAppointmentButton();
    }

    @Override
    public void setAppointmentSlot(AppointmentsSlotsDTO slot) {
        selectedTimeSlot = slot;
        visitTimeResetImage.setVisibility(View.VISIBLE);
        visitTimeTextView.setCompoundDrawables(null, null, null, null);
        String nextAppointmentDate = getNextAppointmentDate(slot.getStartTime());
        visitTimeTextView.setText(nextAppointmentDate);
        setHint(visitTimeTextView, visitTimeTextInputLayout, nextAppointmentDate);
        providerMessage.setText(String.format(Label.getLabel("next_appointment_provider_message"),
                nextAppointmentDate));
        enableScheduleAppointmentButton();
    }

    private void enableScheduleAppointmentButton() {
        boolean enabled = selectedResource != null && selectedVisitType != null
                && selectedLocation != null && selectedTimeSlot != null;
        scheduleAppointmentButton.setEnabled(enabled);
        findViewById(R.id.providerMessageHeader).setSelected(enabled);
    }

    private void enableTimeSlotField() {
        boolean enabled = selectedResource != null && selectedVisitType != null
                && selectedLocation != null;
        if (!enabled) {
            visitTimeResetImage.setVisibility(View.GONE);
            visitTimeTextView.setText(null);
            selectedTimeSlot = null;
            visitTimeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.icon_drop_down), null);
            setHint(visitTimeTextView, visitTimeTextInputLayout, null);
        }
        visitTimeTextView.setEnabled(enabled);
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
        bundle.putString("appointmentResource", gson.toJson(selectedResource));
        bundle.putString("selectedVisitType", gson.toJson(selectedVisitType));
        bundle.putString("location", gson.toJson(selectedLocation));
        bundle.putString("visitTime", gson.toJson(selectedTimeSlot));
        bundle.putBoolean("shouldReload", true);
        return bundle;
    }

    @Override
    public void restoreInstanceForTranslation(Bundle bundle) {
        Gson gson = new Gson();
        selectedResource = gson.fromJson(bundle.getString("appointmentResource"), AppointmentResourcesItemDTO.class);
        if (selectedResource != null) {
            setResourceProvider(selectedResource);
        }
        VisitTypeDTO visitType = gson.fromJson(bundle.getString("selectedVisitType"), VisitTypeDTO.class);
        if (visitType != null) {
            setVisitType(visitType);
        }
        LocationDTO location = gson.fromJson(bundle.getString("location"), LocationDTO.class);
        if (visitType != null) {
            setLocation(location);
        }
        AppointmentsSlotsDTO visitTime = gson.fromJson(bundle.getString("visitTime"),
                AppointmentsSlotsDTO.class);
        if (visitTime != null) {
            setAppointmentSlot(visitTime);
        }
    }

    protected abstract void showChooseLocationFragment();

    protected abstract void showChooseProviderFragment();

    protected abstract void showVisitTypeFragment();

    protected abstract void showAvailabilityFragment();
}
