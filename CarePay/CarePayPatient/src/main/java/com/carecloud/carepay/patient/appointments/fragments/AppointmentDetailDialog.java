package com.carecloud.carepay.patient.appointments.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.PatientAppointmentNavigationCallback;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by lmenendez on 5/9/17.
 */

public class AppointmentDetailDialog extends BaseDialogFragment {

    private AppointmentDTO appointmentDTO;

    private PatientAppointmentNavigationCallback callback;

    private View header;
    private View cancelAppointment;
    private TextView appointmentDate;
    private TextView appointmentTime;
    private TextView providerInitials;
    private ImageView providerPhoto;
    private TextView providerName;
    private TextView providerSpecialty;
    private TextView locationName;
    private TextView locationAddress;
    private View mapButton;
    private View callButton;
    private TextView appointmentStatus;
    private View queueLayout;
    private TextView queueStatus;
    private View actionsLayout;
    private Button leftButton;
    private Button rightButton;


    /**
     * Return a new instance of AppointmentDetailDialog and setup the arguments
     * @param appointmentDTO appointment info
     * @return AppointmentDetailDialog
     */
    public static AppointmentDetailDialog newInstance(AppointmentDTO appointmentDTO){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDTO);

        AppointmentDetailDialog detailDialog = new AppointmentDetailDialog();
        detailDialog.setArguments(args);
        return detailDialog;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (PatientAppointmentNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement AppointmentDetailsCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle args = getArguments();
        if(args!=null){
            appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, args);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.dialog_appointments, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initViews(view);
        setCommonValues();
        applyStyle();
    }


    private void initViews(View view){
        View closeButton = view.findViewById(R.id.dialogAppointDismiss);
        closeButton.setOnClickListener(dismissDialogClick);

        cancelAppointment = view.findViewById(R.id.dialogCancelAppointTextView);
        cancelAppointment.setOnClickListener(cancelAppointmentClick);

        header = view.findViewById(R.id.dialogHeaderLayout);
        appointmentDate = (TextView) view.findViewById(R.id.appointDateTextView);
        appointmentTime = (TextView) view.findViewById(R.id.appointTimeTextView);

        providerInitials = (TextView) view.findViewById(R.id.appointShortnameTextView);
        providerPhoto = (ImageView) view.findViewById(R.id.appointUserPicImageView);
        providerName = (TextView) view.findViewById(R.id.providerName);
        providerSpecialty = (TextView) view.findViewById(R.id.providerSpecialty);

        locationName = (TextView) view.findViewById(R.id.appointAddressHeaderTextView);
        locationAddress = (TextView) view.findViewById(R.id.appointAddressTextView);
        mapButton = view.findViewById(R.id.appointLocationImageView);
        mapButton.setOnClickListener(mapClick);
        callButton = view.findViewById(R.id.appointDailImageView);
        callButton.setOnClickListener(callClick);

        appointmentStatus = (TextView) view.findViewById(R.id.appointment_status);

        queueLayout = view.findViewById(R.id.queue_layout);
        queueStatus = (TextView) view.findViewById(R.id.queue_status);

        actionsLayout = view.findViewById(R.id.appointment_actions_layout);
        leftButton = (Button) view.findViewById(R.id.appointment_button_left);
        rightButton = (Button) view.findViewById(R.id.appointment_button_right);
    }

    private void setCommonValues(){
        if(appointmentDTO!=null){
            DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentDTO.getPayload().getStartTime());
            appointmentDate.setText(dateUtil.getDateAsDayShortMonthDayOrdinal());
            appointmentTime.setText(dateUtil.getTime12Hour());

            final ProviderDTO provider = appointmentDTO.getPayload().getProvider();
            providerInitials.setText(StringUtil.getShortName(provider.getName()));
            providerName.setText(provider.getName());
            providerSpecialty.setText(provider.getSpecialty().getName());
            callButton.setEnabled(!StringUtil.isNullOrEmpty(provider.getPhone()));

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
            locationName.setText(location.getName());
            locationAddress.setText(location.getAddress().getPlaceAddressString());
            mapButton.setEnabled(!StringUtil.isNullOrEmpty(location.getAddress().getPlaceAddressString()));
        }
    }

    private void applyStyle(){
        cleanupViews();
        if(appointmentDTO!=null && appointmentDTO.getPayload().getDisplayStyle()!=null){
            AppointmentDisplayStyle style = appointmentDTO.getPayload().getDisplayStyle();
            switch (style) {
                case CHECKED_IN: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_green_bg);
                    appointmentDate.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentTime.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    queueLayout.setVisibility(View.VISIBLE);
                    //// TODO: 5/9/17 make call to get queue status
                    actionsLayout.setVisibility(View.VISIBLE);
                    leftButton.setVisibility(View.VISIBLE);
                    leftButton.setText(Label.getLabel("appointment_request_checkout_now"));
                    leftButton.setOnClickListener(checkOutClick);
                    break;
                }
                case PENDING: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_gray_bg);
                    cancelAppointment.setVisibility(View.VISIBLE);
                    appointmentDate.setTextColor(ContextCompat.getColor(getContext(), R.color.textview_default_textcolor));
                    appointmentTime.setTextColor(ContextCompat.getColor(getContext(), R.color.slateGray));
                    actionsLayout.setVisibility(View.VISIBLE);
                    leftButton.setVisibility(View.VISIBLE);
                    leftButton.setText(Label.getLabel("appointments_check_in_at_office"));
                    leftButton.setOnClickListener(scanClick);
                    rightButton.setVisibility(View.VISIBLE);
                    rightButton.setText(Label.getLabel("appointments_check_in_now"));
                    rightButton.setOnClickListener(checkInClick);
                    break;
                }
                case REQUESTED_UPCOMING:
                case REQUESTED: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_yellow_bg);
                    appointmentDate.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentTime.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentStatus.setVisibility(View.VISIBLE);
                    appointmentStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.lightning_yellow));
                    appointmentStatus.setText(Label.getLabel("appointments_request_pending_heading"));
                    break;
                }
                case MISSED: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_red_bg);
                    appointmentDate.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentTime.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentStatus.setVisibility(View.VISIBLE);
                    appointmentStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.remove_red));
                    appointmentStatus.setText(Label.getLabel("appointments_missed_heading"));
                    break;
                }
                case CANCELED_UPCOMING:
                case CANCELED: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_med_gray_bg);
                    appointmentDate.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentTime.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentStatus.setVisibility(View.VISIBLE);
                    appointmentStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.optional_gray));
                    appointmentStatus.setText(Label.getLabel("appointments_canceled_heading"));
                    break;
                }
                case PENDING_UPCOMING: {
                    header.setBackgroundResource(R.drawable.appointment_dialog_gray_bg);
                    cancelAppointment.setVisibility(View.VISIBLE);
                    appointmentDate.setTextColor(ContextCompat.getColor(getContext(), R.color.textview_default_textcolor));
                    appointmentTime.setTextColor(ContextCompat.getColor(getContext(), R.color.slateGray));
                    actionsLayout.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.VISIBLE);
                    rightButton.setText(Label.getLabel("appointments_check_in_early"));
                    rightButton.setOnClickListener(checkInClick);
                    break;
                }
//                case REQUESTED_UPCOMING: {
//                    break;
//                }
//                case CANCELED_UPCOMING: {
//                    break;
//                }
                case CHECKED_OUT:{
                    header.setBackgroundResource(R.drawable.appointment_dialog_dark_gray_bg);
                    appointmentDate.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentTime.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    appointmentStatus.setVisibility(View.VISIBLE);
                    appointmentStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.grayRound));
                    appointmentStatus.setText(Label.getLabel("appointment_checked_out_label"));
                    break;                }
                default: {
                    cleanupViews();
                }
            }
        }
    }

    private void cleanupViews(){
        actionsLayout.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);
        leftButton.setVisibility(View.GONE);
        cancelAppointment.setVisibility(View.GONE);
        queueLayout.setVisibility(View.GONE);
        appointmentStatus.setVisibility(View.GONE);
    }

    private void launchMapView(String address) {
        if (SystemUtil.isNotEmptyString(address)) {
            Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            if(mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(mapIntent);
            }else{
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

    private View.OnClickListener dismissDialogClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    private View.OnClickListener cancelAppointmentClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.onCancelAppointment(appointmentDTO);
            dismiss();
        }
    };

    private View.OnClickListener mapClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String address = appointmentDTO.getPayload().getLocation().getAddress().getPlaceAddressString();
            launchMapView(address);
        }
    };

    private View.OnClickListener callClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String phone = appointmentDTO.getPayload().getProvider().getPhone();
            startPhoneCall(phone);
        }
    };

    private View.OnClickListener checkInClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.onCheckInStarted(appointmentDTO);
            dismiss();
        }
    };

    private View.OnClickListener checkOutClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.onCheckOutStarted(appointmentDTO);
        }
    };

    private View.OnClickListener scanClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.onCheckInOfficeStarted(appointmentDTO);
            dismiss();
        }
    };

    private View.OnClickListener rescheduleClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.rescheduleAppointment(appointmentDTO);
            dismiss();
        }
    };

}
