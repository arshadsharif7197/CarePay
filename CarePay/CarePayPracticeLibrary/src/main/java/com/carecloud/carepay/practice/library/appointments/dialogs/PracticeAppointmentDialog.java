package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customdialogs.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class PracticeAppointmentDialog extends BaseDialogFragment {

    public static final String COMMA = ",";

    private PracticeAppointmentDialogListener callback;
    private AppointmentDTO appointmentDTO;
    private int headerColor;
    private int timeColor;
    private String leftAction;
    private String rightAction;
    private String photoUrl;
    private AppointmentDialogStyle style;

    private View view;
    private View headerView;

    public interface PracticeAppointmentDialogListener {
        void onLeftActionTapped(AppointmentDTO appointmentDTO);

        void onRightActionTapped(AppointmentDTO appointmentDTO);
    }

    public enum AppointmentDialogStyle implements Serializable {
        PENDING, REQUESTED, CHECKED_IN, DEFAULT;
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
     * @param style dialog style to determine buttons and colors
     * @param appointmentDTO appointment information
     * @param callback listener
     * @return new instance of PracticeAppointmentDialog 
     */
    public static PracticeAppointmentDialog newInstance(AppointmentDialogStyle style,
                                                        AppointmentDTO appointmentDTO,
                                                        PracticeAppointmentDialogListener callback) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        args.putSerializable("style", style);
        DtoHelper.bundleDto(args, appointmentDTO);

        PracticeAppointmentDialog dialog = new PracticeAppointmentDialog();
        dialog.setArguments(args);
        dialog.setCallback(callback);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        this.style = (AppointmentDialogStyle) arguments.getSerializable("style");
        this.appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, arguments);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    @SuppressWarnings("deprecation")
    private void initializeViews() {
        setupDialogStyle();

        headerView = view.findViewById(R.id.appointment_card_header);

        AppointmentPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();

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
        photoUrl = patientDTO.getProfilePhoto();
        if(photoUrl!=null){
            initializeProfilePhotoView();
        }

        setTextViewById(R.id.appointment_patient_name, StringUtil.captialize(patientDTO.getFullName()));

        String primaryPhoneNumber = patientDTO.getPrimaryPhoneNumber();
        if (null == primaryPhoneNumber) {
            hideViewById(R.id.appointment_patient_phone);
        } else {
            setTextViewById(R.id.appointment_patient_phone, PhoneNumberUtils.formatNumber(primaryPhoneNumber));
        }

        setTextViewById(R.id.appointment_short_name, patientDTO.getShortName());

        setTextViewById(R.id.appointment_visit_type, StringUtil.captialize(appointmentPayloadDTO.getVisitReason().getName()));
        setTextViewById(R.id.appointment_visit_type_label, Label.getLabel("visit_type_heading"));

        initializeButtons();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews();
        GradientDrawable drawable = (GradientDrawable) headerView.getBackground();
        drawable.setColor(ContextCompat.getColor(getContext(), headerColor));

        TextView apptTime = (TextView) view.findViewById(R.id.appointment_start_time);
        apptTime.setTextColor(ContextCompat.getColor(getContext(), timeColor));

    }

    private void initializeHeaderBackgroundView() {
        final ImageView imageView = (ImageView) view.findViewById(R.id.appointment_card_header_background);

        Picasso.with(getActivity()).load(photoUrl).resize(510, 510).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                imageView.setVisibility(View.VISIBLE);

                if (isAdded()) {
                    int trueHeaderColor = getResources().getColor(headerColor);
                    headerView.setBackgroundColor(Color.argb(
                            230,
                            Color.red(trueHeaderColor),
                            Color.green(trueHeaderColor),
                            Color.blue(trueHeaderColor)
                    ));
                }
            }

            @Override
            public void onError() {
                headerView.setBackgroundResource(headerColor);
            }
        });
    }

    private void initializeProfilePhotoView() {
        ImageView profileImage = (ImageView) view.findViewById(R.id.appointment_patient_picture_image_view);

        Picasso.with(getActivity()).load(photoUrl).transform(new CircleImageTransform())
                .resize(58, 58).into(profileImage);
        profileImage.setVisibility(View.VISIBLE);
    }

    private void setupDialogStyle(){
        switch (this.style){
            case PENDING:
                headerColor = R.color.dark_blue;
                timeColor = R.color.colorPrimary;
                leftAction = Label.getLabel("cancel_appointment_short_label");
                rightAction = Label.getLabel("start_checkin_label");

                break;
            case REQUESTED:
                headerColor = R.color.lightning_yellow;
                timeColor = R.color.transparent_70;
                leftAction = Label.getLabel("reject_label");
                rightAction = Label.getLabel("accept_label");

                break;
            case CHECKED_IN:
                headerColor = R.color.dark_blue;
                timeColor = R.color.colorPrimary;
                leftAction = Label.getLabel("cancel_appointment_label");
                break;
            default:
                headerColor = R.color.colorPrimary;
                timeColor = R.color.white;
        }
    }

    private void initializeButtons() {
        initializeButton(R.id.button_left_action, leftAction, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != callback) {
                    callback.onLeftActionTapped(appointmentDTO);
                }

                dismiss();
            }
        });

        initializeButton(R.id.button_right_action, rightAction, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != callback) {
                    callback.onRightActionTapped(appointmentDTO);
                }

                dismiss();
            }
        });
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

    @Override
    protected String getCancelString() {
        return null;
    }

    @Override
    protected int getCancelImageResource() {
        return R.drawable.icn_close;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.dialog_practice_appointment;
    }

    @Override
    protected boolean getCancelable() {
        return true;
    }
}
