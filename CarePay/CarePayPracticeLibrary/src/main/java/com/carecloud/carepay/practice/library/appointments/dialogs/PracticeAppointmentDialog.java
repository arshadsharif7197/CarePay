package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.base.models.UserAuthPermissions;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

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

    public interface PracticeAppointmentDialogListener {
        void onLeftActionTapped(AppointmentDTO appointmentDTO);

        void onRightActionTapped(AppointmentDTO appointmentDTO);

        void onMiddleActionTapped(AppointmentDTO appointmentDTO);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            if (null == callback) {
                callback = (PracticeAppointmentDialogListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement PracticeAppointmentDialogListener");
        }
    }

    /**
     * @param style           dialog style to determine buttons and colors
     * @param appointmentDTO  appointment information
     * @param authPermissions auth permissions
     * @param callback        listener
     * @return instance of PracticeAppointmentDialog
     */
    public static PracticeAppointmentDialog newInstance(AppointmentDisplayStyle style,
                                                        AppointmentDTO appointmentDTO,
                                                        UserAuthPermissions authPermissions,
                                                        PracticeAppointmentDialogListener callback) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        args.putSerializable("style", style);
        DtoHelper.bundleDto(args, appointmentDTO);
        DtoHelper.bundleDto(args, authPermissions);

        PracticeAppointmentDialog dialog = new PracticeAppointmentDialog();
        dialog.setArguments(args);
        dialog.setCallback(callback);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
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

        String appointmentDateStr = DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal();

        if (dateUtil.isToday()) {
            String dayLiteral = StringUtils.substringBefore(appointmentDateStr, COMMA);
            appointmentDateStr = appointmentDateStr.replace(dayLiteral, Label.getLabel("today_label"));
        }

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
                rightActionLabel = Label.getLabel("start_checkin_label");
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
                rightActionLabel = Label.getLabel("start_checkout_label");
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
                if (null != callback) {
                    callback.onLeftActionTapped(appointmentDTO);
                }
                dismiss();
            }
        });

        initializeButton(R.id.button_right_action, rightActionLabel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != callback) {
                    callback.onRightActionTapped(appointmentDTO);
                }
                dismiss();
            }
        });

        initializeButton(R.id.button_middle_action, middleActionLabel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != callback) {
                    callback.onMiddleActionTapped(appointmentDTO);
                }
                dismiss();
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
                if (!authPermissions.canCancelAppointment) {
                    enableById(R.id.button_left_action, false);
                }
                break;
            case MISSED:
            default:
                enableById(R.id.button_left_action, true);
                enableById(R.id.button_right_action, true);
        }
    }

    protected void enableById(int id, boolean enabled) {
        View view = findViewById(id);
        if (view != null) {
            view.setEnabled(enabled);
        }
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

    public void setCallback(PracticeAppointmentDialogListener callback) {
        this.callback = callback;
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
