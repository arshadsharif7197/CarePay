package com.carecloud.carepay.practice.library.checkin.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.adapters.CheckedInAppointmentAdapter;
import com.carecloud.carepay.practice.library.checkin.adapters.PagePickerAdapter;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerWindow;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionFragment;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.CheckinStatusDTO;
import com.carecloud.carepaylibray.appointments.models.QueueDTO;
import com.carecloud.carepaylibray.appointments.models.QueueStatusPayloadDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayTypefaceSpan;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by sudhir_pingale on 10/26/2016
 */
public class AppointmentDetailDialog extends BaseDialogFragment implements PagePickerAdapter.PagePickerCallback,
        PaymentLineItemsListAdapter.PaymentLineItemCallback {

    private static final String TAG = "AppointmentDetailDialog";

    private CheckInDTO checkInDTO;
    private AppointmentsPayloadDTO appointmentPayloadDTO;
    private PendingBalanceDTO pendingBalanceDTO;

    private TextView checkingInLabel;
    private TextView hourLabel;
    private TextView patientNameLabel;
    private TextView doctorNameLabel;
    private TextView balanceValueLabel;
    private CheckBox demographicsCheckbox;
    private CheckBox consentFormsCheckbox;
    private CheckBox medicationsCheckbox;
    private CheckBox intakeCheckbox;
    private CheckBox responsibilityCheckbox;
    private CarePayButton paymentButton;
    private CarePayButton assistButton;
    private CarePayButton pageButton;
    private ImageView profilePhoto;
    private ImageView bgImage;
    private TextView shortName;
    private View checkboxLayout;
    private View queueTextLayout;
    private View patientBalancesLayout;
    private RecyclerView patientBalancesRecycler;
    private TextView queueText;

    private int theRoom;
    private Vector<CheckBox> checkBoxes = new Vector<>();

    private ISession sessionHandler;

    private FragmentActivityInterface callback;
    private PopupPickerWindow pickerWindow;
    private String pushUserId;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

    private Handler handler;
    private PaymentsModel paymentDetailsModel;
    private View spacerView;
    private String practiceManagement;

    /**
     * Constructor.
     */
    public static AppointmentDetailDialog newInstance(CheckInDTO checkInDTO, PendingBalanceDTO pendingBalanceDTO,
                                                      AppointmentsPayloadDTO payloadDTO, int theRoom) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, checkInDTO);
        DtoHelper.bundleDto(args, pendingBalanceDTO);
        DtoHelper.bundleDto(args, payloadDTO);
        args.putInt("theRoom", theRoom);
        AppointmentDetailDialog dialog = new AppointmentDetailDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (FragmentActivityInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PatientAppointmentNavigationCallback");
        }
    }

    /**
     * for initialization UI .
     *
     * @param savedInstanceState for saving state
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHandlersAndListeners();
        Bundle args = getArguments();
        handler = new Handler();
        if (args != null) {
            checkInDTO = DtoHelper.getConvertedDTO(CheckInDTO.class, args);
            pendingBalanceDTO = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, args);
            appointmentPayloadDTO = DtoHelper.getConvertedDTO(AppointmentsPayloadDTO.class, args);
            theRoom = args.getInt("theRoom");
        }
        practiceManagement = ((IApplicationSession) getActivity().getApplicationContext()).getApplicationPreferences().getStartPracticeManagement();

    }

    private void setHandlersAndListeners() {
        try {
            sessionHandler = (ISession) getContext();
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must be an instance of ISession");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitialization();
        callGetCheckInStatusAPI(); //API call for getting check-in status
        onSetValuesFromDTO();

        if (getPatientBalance() == 0) {
            paymentButton.setEnabled(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_checkin_detail, container, false);
    }

    /**
     * for initialization UI components  .
     */
    private void onInitialization() {
        checkingInLabel = (TextView) findViewById(R.id.checkingInLabel);
        hourLabel = (TextView) findViewById(R.id.hourLabel);
        patientNameLabel = (TextView) findViewById(R.id.patientNameLabel);
        doctorNameLabel = (TextView) findViewById(R.id.doctorNameLabel);
        balanceValueLabel = (TextView) findViewById(R.id.balanceValueLabel);

        demographicsCheckbox = (CheckBox) findViewById(R.id.demographicsCheckbox);
        consentFormsCheckbox = (CheckBox) findViewById(R.id.consentFormsCheckbox);
        medicationsCheckbox = (CheckBox) findViewById(R.id.medsAllergiesCheckbox);
        intakeCheckbox = (CheckBox) findViewById(R.id.intakeFormsCheckbox);
        responsibilityCheckbox = (CheckBox) findViewById(R.id.responsibilityCheckbox);

        paymentButton = (CarePayButton) findViewById(R.id.paymentButton);
        assistButton = (CarePayButton) findViewById(R.id.assistButton);
        pageButton = (CarePayButton) findViewById(R.id.pageButton);

        paymentButton.setOnClickListener(paymentActionListener);
        assistButton.setOnClickListener(assistActionListener);
        pageButton.setOnClickListener(pageActionListener);

        profilePhoto = (ImageView) findViewById(R.id.patient_profile_photo);
        bgImage = (ImageView) findViewById(R.id.profile_bg_image);
        shortName = (TextView) findViewById(R.id.patientNameLabelShort);

        checkboxLayout = findViewById(R.id.checkbox_layout);
        spacerView = findViewById(R.id.spacer);
        queueTextLayout = findViewById(R.id.queue_text_layout);
        queueText = (TextView) findViewById(R.id.queue_text);
        patientBalancesLayout = findViewById(R.id.patientBalancesContainer);
        patientBalancesRecycler = (RecyclerView) findViewById(R.id.patientBalancesRecycler);
        patientBalancesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        pickerWindow = new PopupPickerWindow(getContext());
        pickerWindow.flipPopup(true);
        pickerWindow.setAdapter(new PagePickerAdapter(getContext(), checkInDTO.getPayload().getPageMessages(), this));

        if (!canSendPage()) {
            pageButton.setEnabled(false);
        } else {
            View mainView = findViewById(R.id.dialog_checkin_main);
            mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickerWindow.dismiss();
                }
            });
        }

    }


    /**
     * for setting values to UI Component from DTO .
     */
    private void onSetValuesFromDTO() {
        Date date = DateUtil.getInstance().setDateRaw(appointmentPayloadDTO.getStartTime()).getDate();
        hourLabel.setText(dateFormat.format(date));
        setHourLabelBackground(date);

        String title = "";
        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentPayloadDTO.getAppointmentStatus().getLastUpdated());
        if (theRoom == CheckedInAppointmentAdapter.CHECKING_IN) {
            demographicsCheckbox.setText(Label.getLabel("practice_checkin_detail_dialog_demographics"));
            consentFormsCheckbox.setText(Label.getLabel("practice_checkin_detail_dialog_consent_forms"));
            medicationsCheckbox.setText(Label.getLabel("practice_checkin_detail_dialog_medications"));
            intakeCheckbox.setText(Label.getLabel("practice_checkin_detail_dialog_intake"));
            responsibilityCheckbox.setText(Label.getLabel("practice_checkin_detail_dialog_responsibility"));
            title = String.format(Label.getLabel("practice_checkin_started_elapsed"),
                    DateUtil.getContextualTimeElapsed(dateUtil.getDate(), new Date()));

            // Hide intakeCheckbox for talkEHR Practices on Phase 1 integration
            if (practiceManagement.equalsIgnoreCase(Defs.START_PM_TALKEHR))
                intakeCheckbox.setVisibility(View.GONE);


        } else if (theRoom == CheckedInAppointmentAdapter.CHECKED_IN) {
            checkboxLayout.setVisibility(View.INVISIBLE);
            checkBoxes.add(demographicsCheckbox);
            checkBoxes.add(consentFormsCheckbox);
            checkBoxes.add(intakeCheckbox);
            checkBoxes.add(responsibilityCheckbox);
            checkBoxes.add(medicationsCheckbox);
            medicationsCheckbox.setVisibility(View.GONE);
            title = String.format(Label.getLabel("practice_checkin_complete_elapsed"),
                    DateUtil.getContextualTimeElapsed(dateUtil.getDate(), new Date()));
        } else if (theRoom == CheckedInAppointmentAdapter.CHECKING_OUT) {
            hourLabel.setBackgroundResource(R.drawable.right_rounded_background_light_gray);
            demographicsCheckbox.setText(Label.getLabel("next_appointment_title"));
            consentFormsCheckbox.setVisibility(View.INVISIBLE);
            medicationsCheckbox.setText(Label.getLabel("practice_checkin_detail_dialog_consent_forms"));
            intakeCheckbox.setVisibility(View.INVISIBLE);
            responsibilityCheckbox.setText(Label.getLabel("practice_checkin_detail_dialog_payment"));
            title = String.format(Label.getLabel("practice_checkout_started_elapsed"),
                    DateUtil.getContextualTimeElapsed(dateUtil.getDate(), new Date()));
        } else if (theRoom == CheckedInAppointmentAdapter.CHECKED_OUT) {
            hourLabel.setBackgroundResource(R.drawable.right_rounded_background_light_gray);
            checkboxLayout.setVisibility(View.INVISIBLE);
            title = String.format(Label.getLabel("practice_checkout_complete_elapsed"), dateUtil.getTime12Hour());
        }

        checkingInLabel.setText(title);

        balanceValueLabel.setText(StringUtil.getFormattedBalanceAmount(getPatientBalance()));
        patientNameLabel.setText(StringUtil.capitalize(StringUtil.getFormatedLabel(getContext(), appointmentPayloadDTO.getPatient().getFullName())));
        doctorNameLabel.setText(StringUtil.getFormatedLabel(getContext(), appointmentPayloadDTO.getProvider().getName()));


        findViewById(R.id.checkin_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        String photoUrl = appointmentPayloadDTO.getPatient().getProfilePhoto();
        if (!TextUtils.isEmpty(photoUrl)) {
            Picasso.with(getContext()).load(photoUrl)
                    .transform(new CircleImageTransform())
                    .resize(88, 88)
                    .into(profilePhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            profilePhoto.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            shortName.setText(appointmentPayloadDTO.getPatient().getShortName());
                        }
                    });
            Picasso.with(getContext()).load(photoUrl)
                    .fit()
                    .into(bgImage);

            bgImage.setScaleX(5);
            bgImage.setScaleY(5);
        } else {
            shortName.setText(appointmentPayloadDTO.getPatient().getShortName());
        }

        checkingInLabel.bringToFront();
        hourLabel.bringToFront();

        enableActionItems();
    }

    /**
     * Sets hour label background.
     * If the appointment start time has passed curent time, then show the appointment time label background in red.
     * If the appointment start time is in future, then show the appointment time label background in green.
     */
    private void setHourLabelBackground(Date appointmentDateTime) {

        if (appointmentDateTime.before(new Date())) {
            hourLabel.setBackgroundResource(R.drawable.right_rounded_background_red);
        } else {
            hourLabel.setBackgroundResource(R.drawable.right_rounded_background_green);
        }
    }

    private void enableActionItems() {
        assistButton.setEnabled(checkInDTO.getMetadata().hasAssistEnabled());
    }

    private View.OnClickListener paymentActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            pickerWindow.dismiss();
            getPatientBalanceDetails(false);
        }
    };

    private View.OnClickListener assistActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener pageActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (pickerWindow.isShowing()) {
                pickerWindow.dismiss();
            } else {
                int offset = view.getWidth() / 2 - pickerWindow.getWidth() / 2;
                pickerWindow.showAsDropDown(view, offset, 10);
            }
        }
    };

    /**
     * Method to get checkin status API
     */
    private void callGetCheckInStatusAPI() {
        if (checkInDTO != null && checkInDTO.getMetadata().getLinks() != null &&
                checkInDTO.getMetadata().getLinks().getCheckinStatus() != null) {

            TransitionDTO transition = null;
            Map<String, String> queryMap = new HashMap<>();
            WorkflowServiceCallback callback = null;
            queryMap.put("appointment_id", appointmentPayloadDTO.getId());

            if (theRoom == CheckedInAppointmentAdapter.CHECKING_IN) {
                updateCheckinStatus(appointmentPayloadDTO.getAppointmentStatus().getCheckinStatusDTO());
                transition = checkInDTO.getMetadata().getLinks().getAppointmentStatus();
                callback = getAppointmentStatusCallback(true);
            } else if (theRoom == CheckedInAppointmentAdapter.CHECKED_IN) {
                queryMap.put("patient_id", appointmentPayloadDTO.getPatient().getPatientId());
                transition = checkInDTO.getMetadata().getLinks().getQueueStatus();
                callback = getQueueCallBack;
            } else if (theRoom == CheckedInAppointmentAdapter.CHECKING_OUT) {
                updateCheckoutStatus(appointmentPayloadDTO.getAppointmentStatus().getCheckinStatusDTO());
                transition = checkInDTO.getMetadata().getLinks().getAppointmentStatus();
                callback = getAppointmentStatusCallback(false);
            } else if (theRoom == CheckedInAppointmentAdapter.CHECKED_OUT) {
                getPatientBalanceDetails(true);
            }

            if (transition != null && callback != null) {
                ((ISession) getContext()).getWorkflowServiceHelper().execute(transition, callback, queryMap);
            }
        }
    }


    private WorkflowServiceCallback getQueueCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            updatePageOptions(workflowDTO);
            if (appointmentPayloadDTO.getAppointmentStatus().getOriginalName() == null) {
                updateQueueStatus(workflowDTO);
            } else {
                queueTextLayout.setVisibility(View.GONE);
                checkboxLayout.setVisibility(View.GONE);
                queueText.setVisibility(View.GONE);

                View roomLayout = findViewById(R.id.roomStatusLayout);
                roomLayout.setVisibility(View.VISIBLE);

                TextView roomTitleTextView = (TextView) findViewById(R.id.roomStatusTitle);
                TextView roomMessageTextView = (TextView) findViewById(R.id.roomStatusMessage);

                Date checkinTime = DateUtil.getInstance().setDateRaw(appointmentPayloadDTO.getAppointmentStatus()
                        .getLastUpdated().replaceAll("\\.\\d\\d\\dZ", "-00:00")).getDate();
                String roomMessage;
                if (appointmentPayloadDTO.getAppointmentStatus().getOriginalCode() != null)
                    switch (appointmentPayloadDTO.getAppointmentStatus().getOriginalCode()) {
                        case CarePayConstants.IN_PROGRESS_IN_ROOM:
                        default:
                            roomMessage = String.format(Label.getLabel("appointments_queue_in_room_message"),
                                    appointmentPayloadDTO.getPatient().getFirstName(),
                                    DateUtil.getContextualTimeElapsed(checkinTime, new Date()));
                            roomTitleTextView.setText(Label.getLabel("appointments_queue_in_room_title"));
                            roomMessageTextView.setText(roomMessage);
                            break;
                        case CarePayConstants.IN_PROGRESS_OUT_ROOM:
                            roomMessage = String.format(Label.getLabel("appointments_queue_out_room_message"),
                                    appointmentPayloadDTO.getPatient().getFirstName(),
                                    DateUtil.getContextualTimeElapsed(checkinTime, new Date()));
                            roomTitleTextView.setText(Label.getLabel("appointments_queue_out_room_title"));
                            roomMessageTextView.setText(roomMessage);
                            break;
                    }
                else
                    roomMessage = String.format(Label.getLabel("appointments_queue_in_room_message"),
                            appointmentPayloadDTO.getPatient().getFirstName(),
                            DateUtil.getContextualTimeElapsed(checkinTime, new Date()));
                roomTitleTextView.setText(Label.getLabel("appointments_queue_in_room_title"));
                roomMessageTextView.setText(roomMessage);


            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(exceptionMessage);
            if (getActivity() != null) {
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
            }
        }
    };


    private WorkflowServiceCallback getAppointmentStatusCallback(final boolean isCheckIn) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                AppointmentDTO appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, workflowDTO);
                if (isCheckIn) {
                    updateCheckinStatus(appointmentDTO.getPayload().getAppointmentStatus().getCheckinStatusDTO());
                } else {
                    updateCheckoutStatus(appointmentDTO.getPayload().getAppointmentStatus().getCheckinStatusDTO());
                }
                updatePageOptions(workflowDTO);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
            }
        };
    }

    /**
     * @param workflowDTO workflow model returned by server.
     */
    private void updateQueueStatus(WorkflowDTO workflowDTO) {
        QueueStatusPayloadDTO queueStatusPayloadDTO = workflowDTO.getPayload(QueueStatusPayloadDTO.class);
        List<QueueDTO> queueList = queueStatusPayloadDTO.getQueueStatus().getQueueStatusInnerPayload().getQueueList();

        Map<Integer, QueueDTO> queueMap = new HashMap<>();
        QueueDTO placeInQueue = findPlaceInQueue(queueList, appointmentPayloadDTO.getId(), queueMap);

        final Typeface bold = Typeface.createFromAsset(getContext().getAssets(), CustomAssetStyleable.FONT_PROXIMA_NOVA_EXTRA_BOLD);

        String[] sufixes = getOrdinalSufix();
        String language = ApplicationPreferences.getInstance().getUserLanguage();
        if (placeInQueue != null) {
            String place = StringUtil.getOrdinal(language, placeInQueue.getRank()) + " "
                    + Label.getLabel("practice_checkin_detail_dialog_in_queue");//ordinal(, sufixes)
            int rank = placeInQueue.getRank();
            switch (rank) {
                case 1: {
                    //show single place in queue
                    queueTextLayout.setVisibility(View.GONE);
                    spacerView.setVisibility(View.GONE);
                    checkboxLayout.setVisibility(View.VISIBLE);
                    checkBoxes.get(1).setVisibility(View.GONE);
                    checkBoxes.get(2).setVisibility(View.GONE);
                    checkBoxes.get(3).setVisibility(View.GONE);
                    checkBoxes.get(4).setVisibility(View.GONE);
                    checkBoxes.get(0).setSelected(true);
                    SpannableString spannableString = new SpannableString(place);
                    spannableString.setSpan(new CarePayTypefaceSpan(bold), 0, place.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    checkBoxes.get(0).setText(spannableString);
                    break;
                }
                case 2: {
                    queueTextLayout.setVisibility(View.GONE);
                    checkboxLayout.setVisibility(View.VISIBLE);

                    //current user
                    CheckBox checkBox = checkBoxes.get(3);
                    checkBox.setSelected(true);
                    checkBox.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

                    SpannableString spannableString = new SpannableString(place);
                    spannableString.setSpan(new CarePayTypefaceSpan(bold), 0, place.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    checkBox.setText(spannableString);

                    //first user
                    checkBox = checkBoxes.get(0);
                    checkBox.setTextColor(ContextCompat.getColor(getContext(), R.color.textview_default_textcolor));
                    placeInQueue = queueMap.get(1);
                    place = StringUtil.getOrdinal(language, 1) + "\n" + StringUtil.captialize(placeInQueue.getFirstName());

                    spannableString = new SpannableString(place);
                    spannableString.setSpan(new CarePayTypefaceSpan(bold), 0, place.indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    checkBox.setText(spannableString);

                    //hide other checkboxes
                    checkBoxes.get(1).setVisibility(View.INVISIBLE);
                    checkBoxes.get(2).setVisibility(View.INVISIBLE);
                    break;
                }
                case 3: {
                    queueTextLayout.setVisibility(View.GONE);
                    checkboxLayout.setVisibility(View.VISIBLE);

                    //current user
                    CheckBox checkBox = checkBoxes.get(3);
                    checkBox.setSelected(true);
                    checkBox.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

                    SpannableString spannableString = new SpannableString(place);
                    spannableString.setSpan(new CarePayTypefaceSpan(bold), 0, place.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    checkBox.setText(spannableString);

                    //first user
                    checkBox = checkBoxes.get(0);
                    checkBox.setTextColor(ContextCompat.getColor(getContext(), R.color.textview_default_textcolor));
                    placeInQueue = queueMap.get(1);
                    place = StringUtil.getOrdinal(language, 1) + "\n" + StringUtil.captialize(placeInQueue.getFirstName());

                    spannableString = new SpannableString(place);
                    spannableString.setSpan(new CarePayTypefaceSpan(bold), 0, place.indexOf("\n"),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    checkBox.setText(spannableString);

                    //second user
                    checkBox = checkBoxes.get(4);
                    checkBox.setTextColor(ContextCompat.getColor(getContext(), R.color.textview_default_textcolor));
                    placeInQueue = queueMap.get(2);
                    place = StringUtil.getOrdinal(language, 2) + "\n" + StringUtil.captialize(placeInQueue.getFirstName());

                    spannableString = new SpannableString(place);
                    spannableString.setSpan(new CarePayTypefaceSpan(bold), 0, place.indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    checkBox.setText(spannableString);


                    //hide other checkboxes
                    checkBoxes.get(1).setVisibility(View.INVISIBLE);
                    checkBoxes.get(2).setVisibility(View.INVISIBLE);
                    checkBoxes.get(4).setVisibility(View.VISIBLE);

                    break;
                }

                default: {
                    //show checkbox bar
                    queueTextLayout.setVisibility(View.GONE);
                    checkboxLayout.setVisibility(View.VISIBLE);
                    int counter = 3;
                    CheckBox checkBox = checkBoxes.get(counter);
                    checkBox.setSelected(true);
                    checkBox.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

                    SpannableString spannableString = new SpannableString(place);
                    spannableString.setSpan(new CarePayTypefaceSpan(bold), 0,
                            place.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    checkBox.setText(spannableString);

                    counter--;
                    rank--;
                    while (counter > -1) {
                        placeInQueue = queueMap.get(rank);
                        place = StringUtil.getOrdinal(language, rank) + "\n" + StringUtil
                                .captialize(placeInQueue.getFirstName());
                        checkBox = checkBoxes.get(counter);
                        checkBox.setTextColor(ContextCompat.getColor(getContext(), R.color.textview_default_textcolor));

                        spannableString = new SpannableString(place);
                        spannableString.setSpan(new CarePayTypefaceSpan(bold), 0,
                                place.indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        checkBox.setText(spannableString);

                        counter--;
                        rank--;
                    }
                }
            }
        }
    }


    private void updateCheckinStatus(CheckinStatusDTO checkinStatusDTO) {
        final boolean demographicsComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                .equalsIgnoreCase(checkinStatusDTO.getDemographicsStatus());
        final boolean consentComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                .equalsIgnoreCase(checkinStatusDTO.getConsentFormsStatus());
        final boolean medicationsComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                .equalsIgnoreCase(checkinStatusDTO.getMedicationsStatus());
        final boolean intakeComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                .equalsIgnoreCase(checkinStatusDTO.getIntakeStatus());

        if (demographicsComplete) {
            demographicsCheckbox.setChecked(true);
        } else {
            demographicsCheckbox.setChecked(false);
            demographicsCheckbox.setSelected(true);
        }
        if (consentComplete) {
            consentFormsCheckbox.setChecked(true);
        } else {
            consentFormsCheckbox.setChecked(false);
            if (demographicsComplete) {
                consentFormsCheckbox.setSelected(true);
            }
        }
        if (medicationsComplete) {
            medicationsCheckbox.setChecked(true);
        } else {
            medicationsCheckbox.setChecked(false);
            if (consentComplete) {
                medicationsCheckbox.setSelected(true);
            }
        }
        if (intakeComplete) {
            intakeCheckbox.setChecked(true);
        } else {
            intakeCheckbox.setChecked(false);
            if (medicationsComplete) {
                intakeCheckbox.setSelected(true);
            }
        }

        //checking in will never display payment as completed because by this point
        // it will be considered checked in
        responsibilityCheckbox.setChecked(false);
        if (intakeComplete) {
            responsibilityCheckbox.setSelected(true);
        }

    }

    private void updateCheckoutStatus(CheckinStatusDTO checkinStatusDTO) {
        final boolean appointmentsComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                .equalsIgnoreCase(checkinStatusDTO.getCheckoutAppointmentStatus());
        final boolean formsComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                .equalsIgnoreCase(checkinStatusDTO.getCheckoutFormsStatus());
        final boolean paymentComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                .equalsIgnoreCase(checkinStatusDTO.getCheckoutPaymentStatus());

        if (appointmentsComplete) {
            demographicsCheckbox.setChecked(true);
        } else {
            demographicsCheckbox.setChecked(false);
            demographicsCheckbox.setSelected(true);
        }
        if (formsComplete) {
            medicationsCheckbox.setChecked(true);
        } else {
            medicationsCheckbox.setChecked(false);
            if (appointmentsComplete) {
                medicationsCheckbox.setSelected(true);
            }
        }
        if (paymentComplete) {
            responsibilityCheckbox.setChecked(true);
        } else {
            responsibilityCheckbox.setChecked(false);
            if (formsComplete) {
                responsibilityCheckbox.setSelected(true);
            }
        }

    }

    private void updatePatientBalanceStatus(PaymentsModel paymentsModel) {
        List<PendingBalanceDTO> pendingBalances = paymentsModel.getPaymentPayload()
                .getPatientBalances().get(0).getBalances();
        if (!pendingBalances.isEmpty()) {
            patientBalancesLayout.setVisibility(View.VISIBLE);
            PaymentLineItemsListAdapter adapter = new PaymentLineItemsListAdapter(getContext(),
                    getAllPendingBalancePayloads(pendingBalances), this, true);
            patientBalancesRecycler.setAdapter(adapter);
        }
    }

    protected List<PendingBalancePayloadDTO> getAllPendingBalancePayloads(List<PendingBalanceDTO> pendingBalances) {
        List<PendingBalancePayloadDTO> pendingBalancePayloads = new ArrayList<>();
        for (PendingBalanceDTO pendingBalance : pendingBalances) {
            pendingBalancePayloads.addAll(pendingBalance.getPayload());
        }
        return pendingBalancePayloads;
    }


    private double getPatientBalance() {
        if (pendingBalanceDTO != null && !pendingBalanceDTO.getPayload().isEmpty()) {
            return pendingBalanceDTO.getPayload().get(0).getAmount();
        }

        return 0;
    }

    private String[] getOrdinalSufix() {
        String th = Label.getLabel("practice_checkin_detail_dialog_ordinal_th");
        String st = Label.getLabel("practice_checkin_detail_dialog_ordinal_st");
        String nd = Label.getLabel("practice_checkin_detail_dialog_ordinal_nd");
        String rd = Label.getLabel("practice_checkin_detail_dialog_ordinal_rd");
        return new String[]{th, st, nd, rd, th, th, th, th, th, th};
    }

    private QueueDTO findPlaceInQueue(List<QueueDTO> queueDTOList, String appointmentId, Map<Integer, QueueDTO> placeMap) {
        QueueDTO patientQueueDTO = null;
        for (QueueDTO queueDTO : queueDTOList) {
            placeMap.put(queueDTO.getRank(), queueDTO);
            if (queueDTO.getAppointmentId().equals(appointmentId)) {
                patientQueueDTO = queueDTO;
            }
        }
        return patientQueueDTO;
    }

    private void updatePageOptions(final WorkflowDTO workflowDTO) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
                pageButton.setEnabled(paymentsModel.getPaymentPayload().getPatientBalances().get(0)
                        .getDemographics().getPayload().getNotificationOptions().hasPushNotification());
            }
        });
    }

    private void getPatientBalanceDetails(boolean showInline) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", appointmentPayloadDTO.getPatient().getPatientId());

        TransitionDTO transitionDTO = checkInDTO.getMetadata().getLinks().getPatientBalances();
//        getWorkflowServiceHelper().interrupt();
        getWorkflowServiceHelper().execute(transitionDTO, getPatientBalancesCallback(showInline), queryMap);

    }

    private boolean canSendPage() {
        if (pendingBalanceDTO != null) {
            String userId = pendingBalanceDTO.getMetadata().getUserId();
            String checkUserId;
            for (PatientBalanceDTO patientBalanceDTO : checkInDTO.getPayload().getPatientBalances()) {
                checkUserId = patientBalanceDTO.getDemographics().getMetadata().getUserId();
                if (userId.equals(checkUserId)) {
                    pushUserId = checkUserId;
                    return patientBalanceDTO.getDemographics().getPayload().getNotificationOptions()
                            .hasPushNotification();
                }
            }
        }
        return false;
    }


    private WorkflowServiceCallback getPatientBalancesCallback(final boolean showInline) {
        return new WorkflowServiceCallback() {

            @Override
            public void onPreExecute() {
                if (!showInline) {
                    paymentButton.setEnabled(false);
                    showProgressDialog();
                }
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                sessionHandler.hideProgressDialog();
                PaymentsModel patientDetails = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO.toString());
                if (showInline) {
                    paymentDetailsModel = patientDetails;
                    updatePatientBalanceStatus(patientDetails);
                    pageButton.setEnabled(patientDetails.getPaymentPayload().getPatientBalances()
                            .get(0).getDemographics().getPayload().getNotificationOptions().hasPushNotification());
                    return;
                }
                if (patientDetails != null && !patientDetails.getPaymentPayload().getPatientBalances().isEmpty()) {

                    PatientBalanceDTO patientBalanceDTO = patientDetails.getPaymentPayload().getPatientBalances().get(0);
                    if (patientBalanceDTO.getBalances().get(0).getPayload().isEmpty()) {
                        Toast.makeText(getContext(), "Patient has no balance", Toast.LENGTH_LONG).show();
                    } else {
                        dismiss();
                        patientDetails.getPaymentPayload().setLocations(checkInDTO.getPayload().getLocations());
                        patientDetails.getPaymentPayload().setLocationIndex(checkInDTO.getPayload().getLocationIndex());
                        patientDetails.getPaymentPayload().setProviders(checkInDTO.getPayload().getProviders());
                        patientDetails.getPaymentPayload().setProviderIndex(checkInDTO.getPayload().getProviderIndex());
                        showPaymentDistributionDialog(patientDetails);
                    }
                } else {
                    Toast.makeText(getContext(), "Patient has no balance", Toast.LENGTH_LONG).show();
                }
                paymentButton.setEnabled(true);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(TAG, exceptionMessage);
                paymentButton.setEnabled(true);
            }
        };
    }

    private void showPaymentDistributionDialog(PaymentsModel paymentsModelValue) {
        patientResponsibilityViewModel.setPaymentsModel(paymentsModelValue);
        PaymentDistributionFragment fragment = PaymentDistributionFragment.newInstance(paymentsModel);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }

    @Override
    public void sendMessage(String message) {
        pickerWindow.dismiss();

        TransitionDTO transitionDTO = checkInDTO.getMetadata().getLinks().getPagePatient();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("user_id", pushUserId);
        queryMap.put("message", message);

        sessionHandler.getWorkflowServiceHelper().execute(transitionDTO, pushNotificationCallback, queryMap);

    }

    private WorkflowServiceCallback pushNotificationCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("push_notification_sent"));
        }

        @Override
        public void onFailure(String exceptionMessage) {
            sessionHandler.showErrorNotification(Label.getLabel("push_notification_failed"));
        }
    };

    @Override
    public void onDetailItemClick(PendingBalancePayloadDTO paymentLineItem) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentDetailsModel, paymentLineItem, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                showDialog();
            }
        });
        callback.displayDialogFragment(dialog, false);
        hideDialog();
    }
}