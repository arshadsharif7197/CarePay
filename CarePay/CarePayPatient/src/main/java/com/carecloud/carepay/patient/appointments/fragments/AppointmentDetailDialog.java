package com.carecloud.carepay.patient.appointments.fragments;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.AppointmentViewModel;
import com.carecloud.carepay.patient.appointments.PatientAppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.models.AppointmentCalendarEvent;
import com.carecloud.carepay.patient.db.BreezeDataBase;
import com.carecloud.carepay.patient.visitsummary.VisitSummaryDialogFragment;
import com.carecloud.carepay.patient.visitsummary.dto.VisitSummaryDTO;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayUtil;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.PortalSetting;
import com.carecloud.carepaylibray.appointments.models.PortalSettingDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.QueueDTO;
import com.carecloud.carepaylibray.appointments.models.QueueStatusPayloadDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayProgressButton;
import com.carecloud.carepaylibray.utils.CalendarUtil;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.FileDownloadUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.PermissionsUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Created by lmenendez on 5/9/17
 */

public class AppointmentDetailDialog extends BaseDialogFragment {
    private static final int MY_PERMISSIONS_VS_WRITE_EXTERNAL_STORAGE = 10;
    private static final int OPEN_CALENDAR_APP = 100;

    private AppointmentDTO appointmentDTO;

    private PatientAppointmentNavigationCallback callback;

    private View header;
    private View cancelAppointment;
    private TextView appointmentDateTextView;
    private TextView appointmentVisitTypeTextView;
    private TextView appointmentTimeTextView;
    private TextView providerInitials;
    private ImageView providerPhoto;
    private TextView providerName;
    private TextView providerSpecialty;
    private TextView locationName;
    private TextView locationAddress;
    private View mapButton;
    private View callButton;
    private View scheduleAppointmentButton;
    private TextView appointmentStatus;
    private View queueLayout;
    private TextView queueStatus;
    private View actionsLayout;
    private Button leftButton;
    private CarePayProgressButton rightButton;
    private View videoVisitIndicator;
    private CarePayButton joinVideoVisitBtn;
    private RelativeLayout joinVideoVisitLayout;

    private boolean isBreezePractice = true;
    private boolean isRescheduleEnabled = true;
    private Handler statusHandler;
    private long enqueueId;
    private int retryIntent = 0;
    private long lastEventId = -1;
    private AppointmentCalendarEvent calendarEvent;
    private boolean eventExists;
    private boolean isCalendarAvailable = false;
    private AppointmentsResultModel appointmentResultModel;
    private AppointmentViewModel viewModel;


    /**
     * Return a new instance of AppointmentDetailDialog and setup the arguments
     *
     * @param appointmentDTO appointment info
     * @return AppointmentDetailDialog
     */
    public static AppointmentDetailDialog newInstance(AppointmentDTO appointmentDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDTO);
        AppointmentDetailDialog detailDialog = new AppointmentDetailDialog();
        detailDialog.setArguments(args);
        return detailDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof AppointmentViewHandler) {
                callback = (PatientAppointmentNavigationCallback) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PatientAppointmentNavigationCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PatientAppointmentNavigationCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, getArguments());

        viewModel = ViewModelProviders.of(getActivity()).get(AppointmentViewModel.class);
        appointmentResultModel = viewModel.getAppointmentsDtoObservable().getValue();
        setUpViewModel();

        isBreezePractice = getPracticeInfo(appointmentResultModel.getPayload().getUserPractices(),
                appointmentDTO.getMetadata().getPracticeId()).isBreezePractice();
        isRescheduleEnabled = appointmentResultModel.getPayload()
                .isRescheduleEnabled(appointmentDTO.getMetadata().getPracticeId());

        Executors.newSingleThreadExecutor().execute(() -> {
            BreezeDataBase database = BreezeDataBase.getDatabase(getContext());
            calendarEvent = database.getCalendarEventDao()
                    .getAppointmentCalendarEvent(appointmentDTO.getPayload().getId());
            eventExists = false;
            if (calendarEvent != null) {
                checkIfEventExists();
                if (!eventExists) {
                    database.getCalendarEventDao().delete(calendarEvent);
                }
            }
        });
        isCalendarAvailable = getSaveCalendarEventIntent().resolveActivity(getActivity().getPackageManager()) != null;
    }

    private void setUpViewModel() {
        viewModel.getQueueStatusObservable().observe(getActivity(), queueStatusPayloadDTO -> {
            List<QueueDTO> queueList = queueStatusPayloadDTO.getQueueStatus().getQueueStatusInnerPayload().getQueueList();
            QueueDTO placeInQueue = findPlaceInQueue(queueList, appointmentDTO.getPayload().getId());
            if (placeInQueue != null) {
                String place = StringUtil.getOrdinal(ApplicationPreferences.getInstance().getUserLanguage(),
                        placeInQueue.getRank());
                queueLayout.setVisibility(View.VISIBLE);
                queueStatus.setText(getFormattedText(Label.getLabel("appointment_queue_status"), place));
            }
        });
    }

    private UserPracticeDTO getPracticeInfo(List<UserPracticeDTO> userPractices, String practiceId) {
        UserPracticeDTO userPractice = new UserPracticeDTO();
        for (UserPracticeDTO userPracticeDTO : userPractices) {
            if (userPracticeDTO.getPracticeId() != null && userPracticeDTO.getPracticeId().equals(practiceId)) {
                userPractice = userPracticeDTO;
                break;
            }
        }
        return userPractice;
    }

    private void checkIfEventExists() {
        String title = String.format(Label.getLabel("appointment.schedule.event.title.event"), StringUtil
                .capitalize(appointmentDTO.getPayload().getProvider().getFullName()));
        eventExists = CalendarUtil.eventExists(getContext(), calendarEvent.getEventId(), title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.dialog_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        initViews(view);
        setCommonValues();
        applyStyle();
    }


    private void initViews(View view) {
        View closeButton = view.findViewById(R.id.dialogAppointDismiss);
        closeButton.setOnClickListener(dismissDialogClick);

        cancelAppointment = view.findViewById(R.id.dialogCancelAppointTextView);
        cancelAppointment.setOnClickListener(cancelAppointmentClick);

        header = view.findViewById(R.id.dialogHeaderLayout);
        appointmentDateTextView = view.findViewById(R.id.appointDateTextView);
        appointmentTimeTextView = view.findViewById(R.id.appointTimeTextView);
        appointmentVisitTypeTextView = view.findViewById(R.id.appointmentVisitTypeTextView);

        providerInitials = view.findViewById(R.id.appointShortnameTextView);
        providerPhoto = view.findViewById(R.id.appointUserPicImageView);
        providerName = view.findViewById(R.id.providerName);
        providerSpecialty = view.findViewById(R.id.providerSpecialty);

        locationName = view.findViewById(R.id.appointAddressHeaderTextView);
        locationAddress = view.findViewById(R.id.appointAddressTextView);
        mapButton = view.findViewById(R.id.appointLocationImageView);
        mapButton.setOnClickListener(mapClick);
        callButton = view.findViewById(R.id.appointDailImageView);
        callButton.setOnClickListener(callClick);
        scheduleAppointmentButton = view.findViewById(R.id.scheduleAppointmentButton);
        scheduleAppointmentButton.setOnClickListener(scheduleAppointmentClick);
        scheduleAppointmentButton.setEnabled(false);

        appointmentStatus = view.findViewById(R.id.appointment_status);

        queueLayout = view.findViewById(R.id.queue_layout);
        queueStatus = view.findViewById(R.id.queue_status);

        actionsLayout = view.findViewById(R.id.appointment_actions_layout);
        leftButton = view.findViewById(R.id.appointment_button_left);
        rightButton = view.findViewById(R.id.appointment_button_right);

        videoVisitIndicator = view.findViewById(R.id.visit_type_video);
        joinVideoVisitLayout = view.findViewById(R.id.join_video_visit_layout);
        joinVideoVisitLayout.setOnClickListener(joinVideoVisitClick);
        joinVideoVisitBtn = view.findViewById(R.id.join_video_visit_btn);
    }

    private void setCommonValues() {
        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getStartTime());
        appointmentDateTextView.setText(dateUtil.getDateAsWeekdayMonthDayYear());
        appointmentTimeTextView.setText(dateUtil.getTime12Hour());
        appointmentVisitTypeTextView.setText(StringUtil.
                capitalize(appointmentDTO.getPayload().getVisitType().getName()));

        final ProviderDTO provider = appointmentDTO.getPayload().getProvider();
        providerInitials.setText(StringUtil.getShortName(provider.getName()));
        providerName.setText(provider.getName());
        providerSpecialty.setText(provider.getSpecialty().getName());
        callButton.setEnabled(!StringUtil.isNullOrEmpty(getPhoneNumber()));

        int size = getResources().getDimensionPixelSize(R.dimen.apt_dl_image_ht_wdh);
        Picasso.with(getContext())
                .load(provider.getPhoto())
                .resize(size, size)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(providerPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        providerInitials.setVisibility(View.GONE);
                        providerPhoto.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        providerPhoto.setVisibility(View.GONE);
                        providerInitials.setVisibility(View.VISIBLE);
                    }
                });

        LocationDTO location = appointmentDTO.getPayload().getLocation();
        locationName.setText(StringUtil.capitalize(location.getName()));
        locationAddress.setText(StringUtil
                .capitalize(location.getAddress().geAddressStringWithShortZipWOCounty().toLowerCase()));
        mapButton.setEnabled(!StringUtil.isNullOrEmpty(location.getAddress().geAddressStringWithShortZipWOCounty()));

        videoVisitIndicator.setVisibility(appointmentDTO.getPayload().getVisitType().hasVideoOption() ?
                View.VISIBLE : View.GONE);

        if (!appointmentDTO.getPayload().isAppointmentOver()) {
            joinVideoVisitLayout.setVisibility(appointmentDTO.getPayload().getVisitType().hasVideoOption() ?
                    View.VISIBLE : View.GONE);
            joinVideoVisitBtn.setText(Label.getLabel("appointment_video_visit_start"));
        }
    }

    private void applyStyle() {
        cleanupViews();
        if (appointmentDTO != null && appointmentDTO.getPayload().getDisplayStyle() != null) {
            Set<String> enabledLocations = ApplicationPreferences.getInstance()
                    .getPracticesWithBreezeEnabled(appointmentDTO.getMetadata().getPracticeId());
            AppointmentDisplayStyle style = AppointmentDisplayUtil.determineDisplayStyle(appointmentDTO.getPayload());
            switch (style) {
                case CHECKED_IN: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_green_bg);
                    appointmentDateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentTimeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentVisitTypeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

                    if (appointmentDTO.getPayload().isAppointmentToday()
                            || !appointmentDTO.getPayload().isAppointmentOver()) {
                        if (appointmentDTO.getPayload().getAppointmentStatus().getOriginalName() == null &&
                                !appointmentDTO.getPayload().getVisitType().hasVideoOption()) {
                            viewModel.getQueueStatus(appointmentDTO,
                                    appointmentResultModel.getMetadata().getLinks().getQueueStatus());
                        }
                        showCheckoutButton(enabledLocations);
                    } else if (appointmentDTO.getPayload().isAppointmentOver()) {
                        showCheckoutButton(enabledLocations);
                    }
                    scheduleAppointmentButton.setEnabled(isCalendarAvailable
                            && !appointmentDTO.getPayload().isAppointmentOver());

                    updateStartVideoVisitBtn(true);
                    break;
                }
                case PENDING: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_gray_bg);
                    appointmentDateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.textview_default_textcolor));
                    appointmentTimeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.slateGray));
                    appointmentVisitTypeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.slateGray));
                    actionsLayout.setVisibility(View.VISIBLE);
                    if (!appointmentDTO.getPayload().isAppointmentOver() && appointmentDTO.getPayload().isAppointmentToday()) {
                        if (shouldShowCancelButton(enabledLocations)) {
                            cancelAppointment.setVisibility(View.VISIBLE);
                        }
                        if (isLocationWithBreezeEnabled(enabledLocations)
                                && appointmentResultModel.getPayload()
                                .canCheckInCheckOut(appointmentDTO.getMetadata().getPracticeId())) {
                            leftButton.setVisibility(View.VISIBLE);
                            leftButton.setText(Label.getLabel("sigin_how_check_in_scan_qr_code"));
                            leftButton.setOnClickListener(scanClick);
                            rightButton.setVisibility(View.VISIBLE);
                            if (appointmentDTO.getPayload().canCheckInNow(callback
                                    .getAppointmentSettings(appointmentDTO.getMetadata().getPracticeId()))) {
                                rightButton.setText(Label.getLabel("appointments_check_in_now"));
                                rightButton.setContentDescription(getContext().getString(R.string.content_description_checkin_appointment_button));
                            } else {
                                rightButton.setText(Label.getLabel("appointments_check_in_early"));
                            }
                            rightButton.setOnClickListener(checkInClick);
                        }
                    }
                    scheduleAppointmentButton.setEnabled(isCalendarAvailable);
                    updateStartVideoVisitBtn(true);
                    break;
                }
                case REQUESTED_UPCOMING:
                case REQUESTED: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_yellow_bg);
                    appointmentDateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentTimeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentVisitTypeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentStatus.setVisibility(View.VISIBLE);
                    appointmentStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.lightning_yellow));
                    appointmentStatus.setText(Label.getLabel("appointments_request_pending_heading"));
                    scheduleAppointmentButton.setEnabled(isCalendarAvailable
                            && !appointmentDTO.getPayload().isAppointmentOver());
                    updateStartVideoVisitBtn(true);
                    break;
                }
                case MISSED: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_red_bg);
                    appointmentDateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentTimeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentVisitTypeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentStatus.setVisibility(View.VISIBLE);
                    appointmentStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.remove_red));
                    appointmentStatus.setText(Label.getLabel("appointments_missed_heading"));
                    actionsLayout.setVisibility(View.VISIBLE);
                    if (isRescheduleEnabled) {
                        leftButton.setVisibility(View.VISIBLE);
                        leftButton.setText(Label.getLabel("appointment_reschedule_button"));
                        leftButton.setOnClickListener(rescheduleClick);
                    }
                    break;
                }
                case CANCELED_UPCOMING:
                case CANCELED: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_med_gray_bg);
                    appointmentDateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentTimeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentVisitTypeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentStatus.setVisibility(View.VISIBLE);
                    appointmentStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.optional_gray));
                    appointmentStatus.setText(Label.getLabel("appointments_canceled_heading"));
                    break;
                }
                case PENDING_UPCOMING: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_gray_bg);
                    appointmentDateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.textview_default_textcolor));
                    appointmentTimeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.slateGray));
                    appointmentVisitTypeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.slateGray));
                    if (!appointmentDTO.getPayload().isAppointmentOver()) {
                        if (shouldShowCancelButton(enabledLocations)) {
                            cancelAppointment.setVisibility(View.VISIBLE);
                        }
                        if (isLocationWithBreezeEnabled(enabledLocations)
                                && appointmentResultModel.getPayload()
                                .canCheckInCheckOut(appointmentDTO.getMetadata().getPracticeId())) {
                            leftButton.setVisibility(View.VISIBLE);
                            leftButton.setText(Label.getLabel("sigin_how_check_in_scan_qr_code"));
                            leftButton.setOnClickListener(scanClick);
                            actionsLayout.setVisibility(View.VISIBLE);
                            rightButton.setVisibility(View.VISIBLE);
                            if (appointmentDTO.getPayload().canCheckInNow(callback
                                    .getAppointmentSettings(appointmentDTO.getMetadata().getPracticeId()))) {
                                rightButton.setText(Label.getLabel("appointments_check_in_now"));
                            } else {
                                rightButton.setText(Label.getLabel("appointments_check_in_early"));
                            }
                            rightButton.setOnClickListener(checkInClick);
                        }
                    }
                    scheduleAppointmentButton.setEnabled(isCalendarAvailable);
                    updateStartVideoVisitBtn(true);
                    break;
                }
                case CHECKED_OUT: {
                    header.setBackgroundResource(R.drawable.checked_out_appointment_card_background);
                    providerInitials.setBackgroundResource(R.drawable.round_list_tv_default);
                    appointmentDateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentTimeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentVisitTypeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentStatus.setVisibility(View.VISIBLE);
                    appointmentStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.grayRound));
                    appointmentStatus.setText(Label.getLabel("appointment_checked_out_label"));
                    if (isAPastAppointment()) {
                        showVisitSummaryButton();
                    }
                    updateStartVideoVisitBtn(true);
                    break;
                }
                case DENIED: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_red_bg);
                    appointmentDateTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentTimeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentVisitTypeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentStatus.setVisibility(View.VISIBLE);
                    appointmentStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.remove_red));
                    appointmentStatus.setText(Label.getLabel("notification_denied_appointment_status"));
                    break;
                }
                default: {
                    cleanupViews();
                }
            }
        }
    }

    private boolean isAPastAppointment() {
        return !appointmentDTO.getPayload().isAppointmentToday();
    }

    private void showCheckoutButton(Set<String> enabledLocations) {
        DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getEndTime());
        if (isLocationWithBreezeEnabled(enabledLocations)
                && appointmentDTO.getPayload().canCheckOut()
                && DateUtil.getInstance().isWithinHours(24)
                && appointmentResultModel.getPayload()
                .canCheckInCheckOut(appointmentDTO.getMetadata().getPracticeId())) {
            actionsLayout.setVisibility(View.VISIBLE);
            leftButton.setVisibility(View.VISIBLE);
            leftButton.setText(Label.getLabel("appointment_request_checkout_now"));
            leftButton.setContentDescription(getString(R.string.content_description_checkout_appointment_button));
            leftButton.setOnClickListener(checkOutClick);
        }
    }

    private void showVisitSummaryButton() {
        String code = appointmentDTO.getPayload().getAppointmentStatus().getOriginalCode();
        if ((CarePayConstants.BILLED.equals(code) || CarePayConstants.MANUALLY_BILLED.equals(code))
                && shouldShowVisitSummary()) {
            appointmentStatus.setVisibility(View.GONE);
            actionsLayout.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.VISIBLE);
            rightButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_selector));
            rightButton.setText(Label.getLabel("visitSummary.appointments.button.label.visitSummary"));
            rightButton.setOnClickListener(visitSummaryClick);
        }
    }

    private boolean shouldShowVisitSummary() {
        UserPracticeDTO appointmentPractice = null;
        for (UserPracticeDTO userPracticeDTO : appointmentResultModel.getPayload().getUserPractices()) {
            if (userPracticeDTO.getPracticeId().equals(appointmentDTO.getMetadata().getPracticeId())) {
                appointmentPractice = userPracticeDTO;
            }
        }
        if (appointmentPractice != null && appointmentPractice.isVisitSummaryEnabled()
                && appointmentResultModel.getPayload().canViewAndCreateVisitSummaries(appointmentPractice.getPracticeId())) {
            for (PortalSettingDTO portalSettingDTO : appointmentResultModel.getPayload().getPortalSettings()) {
                if (appointmentPractice.getPracticeId().equals(portalSettingDTO.getMetadata().getPracticeId())) {
                    for (PortalSetting portalSetting : portalSettingDTO.getPayload()) {
                        if (portalSetting.getName().toLowerCase().equals("visit summary")) {
                            return portalSetting.getStatus().toLowerCase().equals("a");
                        }
                    }
                    break;
                }
            }
        }
        return false;
    }

    private boolean shouldShowCancelButton(Set<String> enabledLocations) {
        return appointmentDTO.getPayload().isAppointmentCancellable(callback
                .getAppointmentSettings(appointmentDTO.getMetadata().getPracticeId()))
                && isLocationWithBreezeEnabled(enabledLocations)
                && appointmentResultModel.getPayload()
                .canScheduleAppointments(appointmentDTO.getMetadata().getPracticeId());
    }

    private boolean isLocationWithBreezeEnabled(Set<String> enabledLocations) {
        boolean isTheLocationWithBreezeEnabled = enabledLocations == null;
        if (enabledLocations != null) {
            for (String locationId : enabledLocations) {
                if (locationId.equals(appointmentDTO.getPayload().getLocation().getGuid())) {
                    isTheLocationWithBreezeEnabled = true;
                    break;
                }
            }
        }
        return isBreezePractice && isTheLocationWithBreezeEnabled;
    }

    private void cleanupViews() {
        actionsLayout.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);
        leftButton.setVisibility(View.GONE);
        cancelAppointment.setVisibility(View.GONE);
        queueLayout.setVisibility(View.GONE);
        appointmentStatus.setVisibility(View.GONE);
        updateStartVideoVisitBtn(false);
    }

    private WorkflowServiceCallback queueStatusCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            QueueStatusPayloadDTO queueStatusPayloadDTO = workflowDTO.getPayload(QueueStatusPayloadDTO.class);
            List<QueueDTO> queueList = queueStatusPayloadDTO.getQueueStatus().getQueueStatusInnerPayload().getQueueList();

            QueueDTO placeInQueue = findPlaceInQueue(queueList, appointmentDTO.getPayload().getId());
            String place = StringUtil.getOrdinal(ApplicationPreferences.getInstance().getUserLanguage(),
                    placeInQueue.getRank());

            queueLayout.setVisibility(View.VISIBLE);
            queueStatus.setText(getFormattedText(Label.getLabel("appointment_queue_status"), place));
        }

        @Override
        public void onFailure(String exceptionMessage) {
            queueLayout.setVisibility(View.GONE);
        }
    };

    private QueueDTO findPlaceInQueue(List<QueueDTO> queueDTOList, String appointmentId) {
        for (QueueDTO queueDTO : queueDTOList) {
            if (queueDTO.getAppointmentId().equals(appointmentId)) {
                return queueDTO;
            }
        }
        return null;
    }

    private String getFormattedText(String formatString, String... fields) {
        if (!formatString.contains("%s")) {
            return formatString;
        }
        return String.format(formatString, fields);
    }

    private String getPhoneNumber() {
        String phone = appointmentDTO.getPayload().getLocation().getPrimaryPhone();
        if (StringUtil.isNullOrEmpty(phone)) {
            for (LocationDTO.PhoneDTO phoneDTO : appointmentDTO.getPayload().getLocation().getPhoneDTOs()) {
                if (!phoneDTO.isPrimary()) {
                    phone = phoneDTO.getPhoneNumber();
                }
            }
        }

        if (StringUtil.isNullOrEmpty(phone)) {
            UserPracticeDTO practiceDTO = appointmentResultModel.getPayload()
                    .getPractice(appointmentDTO.getMetadata().getPracticeId());
            phone = practiceDTO.getPracticePhone();
        }

        return phone;
    }

    private void launchMapView(String address) {
        if (SystemUtil.isNotEmptyString(address)) {
            Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                SystemUtil.showErrorToast(getContext(), "Unable to launch maps on this device");
            }
        }
    }

    private void startPhoneCall(final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            SystemUtil.showErrorToast(getContext(), "Unable to start a call on this device");
        }
    }

    private View.OnClickListener dismissDialogClick = view -> dismiss();

    private View.OnClickListener cancelAppointmentClick = view -> {
        callback.onCancelAppointment(appointmentDTO);
        dismiss();
    };

    private View.OnClickListener mapClick = view -> {
        String address = appointmentDTO.getPayload().getLocation().getAddress().getPlaceAddressString();
        launchMapView(address);
    };

    private View.OnClickListener callClick = view -> {
        String phone = getPhoneNumber();
        if (!StringUtil.isNullOrEmpty(phone)) {
            startPhoneCall(phone);
        }
    };

    private View.OnClickListener scheduleAppointmentClick = view -> {
        if (calendarEvent == null && !eventExists
                && PermissionsUtil.checkPermissionReadCalendar(AppointmentDetailDialog.this)) {
            saveCalendarEvent();
        } else if (calendarEvent != null) {
            Toast.makeText(getContext(), Label.getLabel("appointment.schedule.event.message.repeated"),
                    Toast.LENGTH_LONG).show();
        }
    };

    private void saveCalendarEvent() {
        lastEventId = CalendarUtil.getNewEventId(getContext());
        Intent intent = getSaveCalendarEventIntent();
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, OPEN_CALENDAR_APP);
        } else {
            Toast.makeText(getContext(), Label.getLabel("appointment.schedule.alert.message.noCalendarApp"),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Intent getSaveCalendarEventIntent() {
        String title = String.format(Label.getLabel("appointment.schedule.event.title.event"), StringUtil
                .capitalize(appointmentDTO.getPayload().getProvider().getFullName()));
        return CalendarUtil.createSaveEventIntent(lastEventId, title, "",
                DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getStartTime()).getDate().getTime(),
                DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getEndTime()).getDate().getTime(),
                appointmentDTO.getPayload().getLocation().getAddress().getPlaceAddressString());
    }

    private View.OnClickListener checkInClick = view -> {
        callback.onCheckInStarted(appointmentDTO);
        dismiss();
    };

    private View.OnClickListener checkOutClick = view -> {
        callback.onCheckOutStarted(appointmentDTO);
        dismiss();
    };

    private View.OnClickListener visitSummaryClick = view -> {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PermissionChecker.PERMISSION_GRANTED && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_VS_WRITE_EXTERNAL_STORAGE);
        } else {
            callVisitSummaryService();
        }
    };

    private void callVisitSummaryService() {
        callback.callVisitSummaryService(appointmentDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                VisitSummaryDTO visitSummaryDTO = DtoHelper.getConvertedDTO(VisitSummaryDTO.class, workflowDTO);
                ((BaseActivity) getActivity()).hideProgressDialog();
                rightButton.setEnabled(false);
                rightButton.setText(Label.getLabel("visitSummary.createVisitSummary.button.label.processing"));
                callVisitSummaryStatusService(visitSummaryDTO.getPayload().getVisitSummaryRequest().getJobId());
            }

            @Override
            public void onFailure(String exceptionMessage) {
                Log.e("okHttp", exceptionMessage);
                hideProgressDialog();
                showErrorNotification(Label.getLabel("visitSummary.createVisitSummary.error.label.downloadError"));

            }
        });
    }

    private void callVisitSummaryStatusService(final String jobId) {
        callback.callVisitSummaryStatusService(jobId, appointmentDTO.getMetadata().getPracticeMgmt(),
                new WorkflowServiceCallback() {

                    @Override
                    public void onPreExecute() {
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        VisitSummaryDTO visitSummaryDTO = null;
                        try {
                            visitSummaryDTO = DtoHelper.getConvertedDTO(VisitSummaryDTO.class, workflowDTO);
                        } catch (Exception ex) {
                            onFailure("");
                        }
                        String status = visitSummaryDTO.getPayload().getVisitSummary().getStatus();
                        if (retryIntent > VisitSummaryDialogFragment.MAX_NUMBER_RETRIES) {
                            resetProcess();
                        } else if (status.equals("queued") || status.equals("working")) {
                            retryIntent++;
                            statusHandler = new Handler();
                            statusHandler.postDelayed(() -> callVisitSummaryStatusService(jobId),
                                    VisitSummaryDialogFragment.RETRY_TIME);
                        }
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        Log.e("OkHttp", exceptionMessage);
                        if (exceptionMessage.contains("JSON")) {
                            String title = String.format("%s - %s",
                                    appointmentDTO.getPayload().getProvider().getFullName(),
                                    DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getStartTime())
                                            .toStringWithFormatMmDashDdDashYyyy());
                            enqueueId = callback.downloadVisitSummaryFile(jobId,
                                    appointmentDTO.getMetadata().getPracticeMgmt(), title);
                        } else {
                            resetProcess();
                        }
                        rightButton.setEnabled(true);

                    }
                });
    }

    private void resetProcess() {
        retryIntent = 0;
        rightButton.setEnabled(true);
        rightButton.setText(Label.getLabel("visitSummary.appointments.button.label.visitSummary"));
        showErrorNotification(Label.getLabel("visitSummary.createVisitSummary.error.label.downloadError"));
    }

    private View.OnClickListener scanClick = view -> {
        callback.onCheckInOfficeStarted(appointmentDTO);
        //log event to mixpanel
        String[] params = {getString(R.string.param_appointment_type),
                getString(R.string.param_practice_id),
                getString(R.string.param_provider_id),
                getString(R.string.param_location_id),
                getString(R.string.param_patient_id)
        };
        Object[] values = {appointmentDTO.getPayload().getVisitType().getName(),
                appointmentDTO.getMetadata().getPracticeId(),
                appointmentDTO.getPayload().getProvider().getGuid(),
                appointmentDTO.getPayload().getLocation().getGuid(),
                appointmentDTO.getMetadata().getPatientId()
        };
        MixPanelUtil.logEvent(getString(R.string.event_qr_code), params, values);
        dismiss();
    };

    private View.OnClickListener rescheduleClick = view -> {
        dismiss();
        callback.rescheduleAppointment(appointmentDTO);
    };

    private View.OnClickListener joinVideoVisitClick = view -> {
        callback.startVideoVisit(appointmentDTO);
    };

    private void updateStartVideoVisitBtn(boolean isEnable) {
        if (isEnable) {
            joinVideoVisitLayout.setBackground(getResources().getDrawable(R.drawable.button_green_fill_background));
            joinVideoVisitLayout.setEnabled(true);
        } else {
            joinVideoVisitLayout.setBackground(getResources().getDrawable(R.drawable.button_light_gray_background));
            joinVideoVisitLayout.setEnabled(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        if (!eventExists) {
            saveEventOnLocalDB();
        }
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(receiver);
        if (statusHandler != null) {
            statusHandler.removeCallbacksAndMessages(null);
        }
        if (enqueueId > 0) {
            DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            dm.remove(enqueueId);
        }
        super.onStop();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                DownloadManager.Query query = new DownloadManager.Query();
                DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

                query.setFilterById(enqueueId);
                final Cursor cursor = dm.query(query);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
//                    int reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                    if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                        rightButton.setEnabled(true);
                        rightButton.setText(Label.getLabel("visitSummary.createVisitSummary.button.label.openFile"));
                        rightButton.setOnClickListener(v -> {
                            String downloadLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            if (downloadLocalUri != null) {
                                String downloadMimeType = cursor.getString(cursor
                                        .getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                                FileDownloadUtil.openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType);
                            }
                        });
                        enqueueId = -1;
                    }
                }
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_VS_WRITE_EXTERNAL_STORAGE
                && (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            callVisitSummaryService();
        } else if (requestCode == PermissionsUtil.MY_PERMISSIONS_READ_CALENDAR
                && (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            saveCalendarEvent();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_CALENDAR_APP) {
            saveEventOnLocalDB();

        }
    }

    private void saveEventOnLocalDB() {
        if (lastEventId > -1 && lastEventId != CalendarUtil.getNewEventId(getContext())) {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    AppointmentCalendarEvent appointmentCalendarEvent = new AppointmentCalendarEvent();
                    appointmentCalendarEvent.setAppointmentId(appointmentDTO.getPayload().getId());
                    appointmentCalendarEvent.setEventId(lastEventId);
                    BreezeDataBase database = BreezeDataBase.getDatabase(getContext());
                    database.getCalendarEventDao().insert(appointmentCalendarEvent);
                    calendarEvent = database.getCalendarEventDao()
                            .getAppointmentCalendarEvent(appointmentDTO.getPayload().getId());
                }
            });
            eventExists = true;
        }
    }
}
