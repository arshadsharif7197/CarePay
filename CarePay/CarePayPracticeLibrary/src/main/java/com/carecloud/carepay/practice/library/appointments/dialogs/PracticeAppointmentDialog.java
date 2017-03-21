package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customdialogs.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.lang3.StringUtils;

public class PracticeAppointmentDialog extends BaseDialogFragment {

    public static final String COMMA = ",";

    private PracticeAppointmentDialogListener callback;
    private AppointmentDTO appointmentDTO;
    private int headerColor;
    private String todayLabel;
    private String cancelString;
    private String leftAction;
    private String rightAction;
    private String visitTypeLabel;
    private String photoUrl;

    private View view;
    private LinearLayout headerView;

    public interface PracticeAppointmentDialogListener {
        void onLeftActionTapped(AppointmentDTO appointmentDTO);

        void onRightActionTapped(AppointmentDTO appointmentDTO);
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
     * @param headerColor color of header
     * @param closeText label below the close icon
     * @param todayLabel today in current language
     * @param visitTypeLabel visit type label
     * @param leftAction label for left button. If empty, then no left button
     * @param rightAction label for right button. If empty, then no right button
     * @param appointmentDTO appointment information
     * @param callback listener
     * @return new instance of PracticeAppointmentDialog 
     */
    public static PracticeAppointmentDialog newInstance(int headerColor,
                                                        String closeText,
                                                        String todayLabel,
                                                        String visitTypeLabel,
                                                        String leftAction,
                                                        String rightAction,
                                                        AppointmentDTO appointmentDTO,
                                                        PracticeAppointmentDialogListener callback) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        args.putInt("headerColor", headerColor);
        args.putString("todayLabel", todayLabel);
        args.putString("cancelString", closeText);
        args.putString("leftAction", leftAction);
        args.putString("rightAction", rightAction);
        args.putString("visitTypeLabel", visitTypeLabel);

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
        this.headerColor = arguments.getInt("headerColor");
        this.todayLabel = arguments.getString("todayLabel");
        this.cancelString = arguments.getString("cancelString");
        this.leftAction = arguments.getString("leftAction");
        this.rightAction = arguments.getString("rightAction");
        this.visitTypeLabel = arguments.getString("visitTypeLabel");
        this.appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    @SuppressWarnings("deprecation")
    private void initializeViews() {
        headerView = (LinearLayout) view.findViewById(R.id.appointment_card_header);

        AppointmentPayloadDTO appointmentPayloadDTO = appointmentDTO.getPayload();

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentPayloadDTO.getStartTime());

        String appointmentDateStr = DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal();

        if (dateUtil.isToday()) {
            String dayLiteral = StringUtils.substringBefore(appointmentDateStr, COMMA);
            appointmentDateStr = appointmentDateStr.replace(dayLiteral, todayLabel);
        }

        setTextViewById(R.id.appointment_start_day, appointmentDateStr);
        setTextViewById(R.id.appointment_start_time, DateUtil.getInstance().getTime12Hour());

        setTextViewById(R.id.appointment_doctor, appointmentPayloadDTO.getProvider().getName());

        PatientModel patientDTO = appointmentDTO.getPayload().getPatient();
        photoUrl = patientDTO.getProfilePhoto();
        setTextViewById(R.id.appointment_patient_name, patientDTO.getFullName());

        String primaryPhoneNumber = patientDTO.getPrimaryPhoneNumber();
        if (null == primaryPhoneNumber) {
            hideViewById(R.id.appointment_patient_phone);
        } else {
            setTextViewById(R.id.appointment_patient_phone, PhoneNumberUtils.formatNumber(primaryPhoneNumber));
        }

        setTextViewById(R.id.appointment_short_name, patientDTO.getShortName());

        setTextViewById(R.id.appointment_visit_type, appointmentPayloadDTO.getVisitReason().getName());
        setTextViewById(R.id.appointment_visit_type_label, visitTypeLabel);

        initializeButtons();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews();

        if (!TextUtils.isEmpty(photoUrl)) {
            initializeHeaderBackgroundView();
        } else {
            headerView.setBackgroundResource(headerColor);
        }
    }

    private void initializeHeaderBackgroundView() {
        final ImageView imageView = (ImageView) view.findViewById(R.id.appointment_card_header_background);

        Picasso.with(getActivity()).load(photoUrl).resize(510, 510).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                imageView.setVisibility(View.VISIBLE);
                initializeProfilePhotoView();

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

    @NonNull
    private Target getHeaderTarget() {
        return new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                headerView.setBackground(new BitmapDrawable(getResources(), bitmap));

                initializeProfilePhotoView();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                headerView.setBackgroundResource(headerColor);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
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
        return cancelString;
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
