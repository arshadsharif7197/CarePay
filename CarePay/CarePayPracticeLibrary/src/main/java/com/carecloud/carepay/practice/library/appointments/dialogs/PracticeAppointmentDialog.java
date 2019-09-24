package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.adhocforms.AdHocFormsListFragment;
import com.carecloud.carepay.practice.library.appointments.interfaces.PracticeAppointmentDialogListener;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.dobverification.DoBVerificationActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.interfaces.VideoAppointmentCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.base.models.UserAuthPermissions;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class PracticeAppointmentDialog extends BaseDialogFragment {

    public static final String COMMA = ",";

    private PracticeAppointmentDialogListener callback;
    private AppointmentDTO appointmentDTO;
    private UserAuthPermissions authPermissions;

    private int headerColor;
    private int timeColor;
    private String leftActionLabel;
    private String rightActionLabel;
    private String middleActionLabel;
    private AppointmentDisplayStyle style;
    private GradientDrawable drawable;

    private View headerView;
    private AppointmentsResultModel checkInDto;

    /**
     * @param style           dialog style to determine buttons and colors
     * @param appointmentDTO  appointment information
     * @param authPermissions auth permissions
     * @return instance of PracticeAppointmentDialog
     */
    public static PracticeAppointmentDialog newInstance(AppointmentDisplayStyle style,
                                                        AppointmentDTO appointmentDTO,
                                                        UserAuthPermissions authPermissions) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        args.putSerializable("style", style);
        DtoHelper.bundleDto(args, appointmentDTO);
        DtoHelper.bundleDto(args, authPermissions);

        PracticeAppointmentDialog dialog = new PracticeAppointmentDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (PracticeAppointmentDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PracticeAppointmentDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        checkInDto = (AppointmentsResultModel) callback.getDto();
        this.style = (AppointmentDisplayStyle) arguments.getSerializable("style");
        this.appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, arguments);
        this.authPermissions = DtoHelper.getConvertedDTO(UserAuthPermissions.class, arguments);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_practice_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        drawable = (GradientDrawable) headerView.getBackground();

        TextView apptTime = view.findViewById(R.id.appointment_start_time);
        apptTime.setTextColor(ContextCompat.getColor(getContext(), timeColor));
        (findViewById(R.id.closeViewLayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    @SuppressWarnings("deprecation")
    private void initializeViews(View view) {
        setupDialogStyle();

        headerView = view.findViewById(R.id.appointment_card_header);

        AppointmentsPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentPayloadDTO.getStartTime());

        String appointmentDateStr = dateUtil.getInstance().getDateAsWeekdayFullMonthDayYear(Label
                .getLabel("appointments_web_today_heading"), Label.getLabel("add_appointment_tomorrow"));

        setTextViewById(R.id.appointment_start_day, appointmentDateStr);
        setTextViewById(R.id.appointment_start_time, DateUtil.getInstance().getTime12Hour());
        setTextViewById(R.id.appointment_doctor, appointmentPayloadDTO.getProvider().getName());

        PatientModel patientDTO = appointmentDTO.getPayload().getPatient();
        if (!StringUtil.isNullOrEmpty(patientDTO.getProfilePhoto())) {
            initializeProfilePhotoView(view, patientDTO.getProfilePhoto());
        }

        String patientNameText;
        if (StringUtil.isNullOrEmpty(patientDTO.getPrimaryPhoneNumber())) {
            patientNameText = StringUtil.captialize(patientDTO.getFullName());
        } else {
            patientNameText = String.format("%s | %s", StringUtil.captialize(patientDTO.getFullName()),
                    StringUtil.formatPhoneNumber(patientDTO.getPrimaryPhoneNumber()));
        }
        setTextViewById(R.id.appointment_patient_name, patientNameText);
        setTextViewById(R.id.appointment_short_name, patientDTO.getShortName());
        setTextViewById(R.id.appointment_visit_type, StringUtil.captialize(appointmentPayloadDTO.getVisitType().getName()));
        setTextViewById(R.id.appointment_visit_type_label, Label.getLabel("visit_type_heading"));

        View videoVisitIndicator = findViewById(R.id.visit_type_video);
        videoVisitIndicator.setVisibility(appointmentPayloadDTO.getVisitType().hasVideoOption() ?
                View.VISIBLE : View.GONE);

        initializeButtons();
    }

    private void initializeProfilePhotoView(View view, String photoUrl) {
        ImageView profileImage = view.findViewById(R.id.appointment_patient_picture_image_view);

        Picasso.with(getActivity()).load(photoUrl).transform(new CircleImageTransform())
                .resize(58, 58).into(profileImage);
        profileImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        drawable.setColor(ContextCompat.getColor(getContext(), headerColor));
    }

    @Override
    public void onPause() {
        super.onPause();
        drawable.setColor(getResources().getColor(R.color.colorPrimary));
    }

    private void setupDialogStyle() {
        switch (this.style) {
            case PENDING:
                headerColor = R.color.dark_blue;
                timeColor = R.color.colorPrimary;
                leftActionLabel = Label.getLabel("cancel_appointment_short_label");
                if (appointmentDTO.getPayload().getVisitType().hasVideoOption()) {
                    rightActionLabel = Label.getLabel("appointment_video_visit_start_short");
                } else {
                    rightActionLabel = Label.getLabel("start_checkin_label");
                }
                middleActionLabel = Label.getLabel("adhoc_show_forms_button_label");
                break;
            case REQUESTED:
                headerColor = R.color.lightning_yellow;
                timeColor = R.color.transparent_70;
                leftActionLabel = Label.getLabel("reject_label");
                rightActionLabel = Label.getLabel("accept_label");
                break;
            case CHECKED_IN:
                headerColor = R.color.dark_blue;
                timeColor = R.color.colorPrimary;
                leftActionLabel = Label.getLabel("cancel_appointment_short_label");
                if (appointmentDTO.getPayload().getVisitType().hasVideoOption()) {
                    rightActionLabel = Label.getLabel("appointment_video_visit_start_short");
                } else {
                    rightActionLabel = Label.getLabel("start_checkout_label");
                }
                middleActionLabel = Label.getLabel("adhoc_show_forms_button_label");
                break;
            case MISSED:
                headerColor = R.color.dark_blue;
                timeColor = R.color.white;
                leftActionLabel = Label.getLabel("cancel_appointment_short_label");
                rightActionLabel = Label.getLabel("start_checkin_label");
                middleActionLabel = Label.getLabel("adhoc_show_forms_button_label");
            default:
                headerColor = R.color.dark_blue;
                timeColor = R.color.white;
                middleActionLabel = Label.getLabel("adhoc_show_forms_button_label");
        }
    }

    private void initializeButtons() {
        initializeButton(R.id.button_left_action, leftActionLabel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLeftActionTapped(appointmentDTO);
            }
        });

        initializeButton(R.id.button_right_action, rightActionLabel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != callback) {
                    if (appointmentDTO.getPayload().getVisitType().hasVideoOption()) {
                        ((VideoAppointmentCallback) callback).startVideoVisit(appointmentDTO);
                        dismiss();
                        return;
                    }
                }
                onRightActionTapped(appointmentDTO);
            }
        });

        initializeButton(R.id.button_middle_action, middleActionLabel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAdHocFormsDialog(appointmentDTO);
            }
        });


        switch (style) {
            case REQUESTED:
                if (!authPermissions.canAcceptAppointmentRequest) {
                    enableById(R.id.button_left_action, false);
                    enableById(R.id.button_right_action, false);
                }
                break;
            case PENDING:
            case CHECKED_IN:
                enableById(R.id.button_left_action, authPermissions.canCancelAppointment);
                break;
            case MISSED:
            default:
                enableById(R.id.button_left_action, authPermissions.canCancelAppointment);
                enableById(R.id.button_right_action, true);
        }
    }

    private void onLeftActionTapped(AppointmentDTO appointmentDTO) {
        AppointmentsPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();

        if (appointmentPayloadDTO.getAppointmentStatus().getCode().equals(CarePayConstants.REQUESTED)) {
            rejectAppointment(appointmentDTO);
        } else {
            CancelAppointmentConfirmDialogFragment fragment = CancelAppointmentConfirmDialogFragment
                    .newInstance(appointmentDTO);
            fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    showDialog();
                }
            });
            fragment.show(getFragmentManager(), fragment.getClass().getName());
            hideDialog();
        }
    }

    private void rejectAppointment(AppointmentDTO appointmentDTO) {
        TransitionDTO transitionDTO = checkInDto.getMetadata().getTransitions().getDismissAppointment();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appointment_id", appointmentDTO.getPayload().getId());
        getWorkflowServiceHelper().execute(transitionDTO, getAppointmentsServiceCallback(appointmentDTO,
                "appointment_rejection_success_message_HTML",
                getString(R.string.event_appointment_rejected)), queryMap);
    }

    private void onRightActionTapped(AppointmentDTO appointmentDTO) {
        if (appointmentDTO.getPayload().getAppointmentStatus().getCode().equals(CarePayConstants.REQUESTED)) {
            confirmAppointment(appointmentDTO);
        } else if (appointmentDTO.getPayload().canCheckIn()) {
            launchPatientModeCheckIn(appointmentDTO);
        } else if (appointmentDTO.getPayload().canCheckOut()) {
            launchPatientModeCheckOut(appointmentDTO);
        }
    }

    private void confirmAppointment(AppointmentDTO appointmentDTO) {
        TransitionDTO transitionDTO = checkInDto.getMetadata().getTransitions().getConfirmAppointment();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appointment_id", appointmentDTO.getPayload().getId());
        getWorkflowServiceHelper().execute(transitionDTO, getAppointmentsServiceCallback(appointmentDTO,
                "appointment_schedule_success_message_HTML",
                getString(R.string.event_appointment_accepted)), queryMap);
    }

    WorkflowServiceCallback getAppointmentsServiceCallback(final AppointmentDTO appointmentDTO,
                                                           final String successMessage,
                                                           final String eventName) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                SystemUtil.showSuccessToast(getContext(), Label.getLabel(successMessage));
                logMixPanelEvent(appointmentDTO, eventName);
                callback.refreshData();
                dismiss();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
            }

        };
    }

    private void logMixPanelEvent(AppointmentDTO appointmentDTO, String eventName) {
        String[] params = {getString(R.string.param_appointment_type),
                getString(R.string.param_practice_id),
                getString(R.string.param_practice_name),
                getString(R.string.param_patient_id),
                getString(R.string.param_provider_id),
                getString(R.string.param_location_id)};
        String[] values = {appointmentDTO.getPayload().getVisitType().getName(),
                getApplicationMode().getUserPracticeDTO().getPracticeId(),
                getApplicationMode().getUserPracticeDTO().getPracticeName(),
                appointmentDTO.getMetadata().getPatientId(),
                appointmentDTO.getPayload().getProvider().getGuid(),
                appointmentDTO.getPayload().getLocation().getGuid()};
        MixPanelUtil.logEvent(eventName, params, values);
    }

    private void launchPatientModeCheckIn(AppointmentDTO appointmentDTO) {
        getApplicationPreferences().setAppointmentNavigationOption(Defs.NAVIGATE_CHECKIN);

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appointment_id", appointmentDTO.getPayload().getId());
        TransitionDTO checkInPatientTransition = checkInDto.getMetadata().getTransitions().getCheckinPatientMode();
        getWorkflowServiceHelper().execute(checkInPatientTransition, getPatientModeCallback(appointmentDTO), queryMap);
    }

    private void launchPatientModeCheckOut(AppointmentDTO appointmentDTO) {
        getApplicationPreferences().setAppointmentNavigationOption(Defs.NAVIGATE_CHECKOUT);

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("appointment_id", appointmentDTO.getPayload().getId());

        TransitionDTO checkoutPatientTransition = checkInDto.getMetadata().getTransitions()
                .getCheckoutPatientMode();

        getWorkflowServiceHelper().execute(checkoutPatientTransition,
                getPatientModeCallback(appointmentDTO), queryMap);
    }

    private WorkflowServiceCallback getPatientModeCallback(final AppointmentDTO appointmentDTO) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                Bundle appointmentInfo = new Bundle();
                appointmentInfo.putString(CarePayConstants.APPOINTMENT_ID, appointmentDTO.getPayload().getId());

                if (workflowDTO.getState().equals("demographics_verify")) {
                    WorkFlowRecord workFlowRecord = new WorkFlowRecord(workflowDTO);
                    workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(getContext()));

                    Intent intent = new Intent(getContext(), DoBVerificationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong(WorkflowDTO.class.getName(), workFlowRecord.save(getContext()));
                    intent.putExtras(bundle);
                    intent.putExtra(NavigationStateConstants.EXTRA_INFO, appointmentInfo);
                    startActivity(intent);
                } else {
                    PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, appointmentInfo);
                }
                dismiss();
                getActivity().finish();

            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        };
    }

    protected void enableById(int id, boolean enabled) {
        View view = findViewById(id);
        if (view != null) {
            view.setEnabled(enabled);
        }
    }

    private void showAdHocFormsDialog(AppointmentDTO appointmentDTO) {
        String patientId = appointmentDTO.getMetadata().getPatientId();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", patientId);
        TransitionDTO adHocForms = checkInDto.getMetadata().getLinks().getAllPracticeForms();
        getWorkflowServiceHelper().execute(adHocForms, getAdHocServiceCallback(appointmentDTO), queryMap);
    }

    WorkflowServiceCallback getAdHocServiceCallback(final AppointmentDTO appointmentDTO) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                Gson gson = new Gson();
                AppointmentsResultModel appointmentsResultModel = gson
                        .fromJson(workflowDTO.toString(), AppointmentsResultModel.class);
                AdHocFormsListFragment fragment = AdHocFormsListFragment
                        .newInstance(appointmentsResultModel, appointmentDTO.getMetadata().getPatientId());
                fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        showDialog();
                    }
                });
                callback.displayDialogFragment(fragment, false);
                hideDialog();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
            }
        };
    }

    private void initializeButton(int id, String text, View.OnClickListener listener) {
        if (null == text) {
            disappearViewById(id);
        } else {
            TextView textView = setTextViewById(id, text);
            if (null != textView) {
                textView.setOnClickListener(listener);
            }
        }
    }

    private TextView setTextViewById(int id, String text) {
        View view = findViewById(id);
        if (!(view instanceof TextView)) {
            return null;
        }

        TextView textView = (TextView) view;
        textView.setText(text);

        return textView;
    }

}
