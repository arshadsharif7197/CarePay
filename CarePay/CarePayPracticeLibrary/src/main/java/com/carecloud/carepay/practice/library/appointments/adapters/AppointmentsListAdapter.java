package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by harshal_patil on 10/19/2016
 */
public class AppointmentsListAdapter extends RecyclerView.Adapter<AppointmentsListAdapter.AppointmentsListViewHolder> {

    private Context context;
    private List<AppointmentDTO> appointmentsArrayList;
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentsAdapterStartCheckInListener listener;
    private @Defs.AppointmentNavigationTypeDef int appointmentNavigationType;


    /**
     * This will create a list of appointments
     *
     * @param context               context
     * @param appointmentsArrayList appointmentsArrayList
     */
    public AppointmentsListAdapter(Context context, List<AppointmentDTO> appointmentsArrayList,
                                   AppointmentsResultModel appointmentInfo, @Defs.AppointmentNavigationTypeDef int appointmentNavigationType) {
        this.context = context;
        this.appointmentsArrayList = appointmentsArrayList;
        this.appointmentsResultModel = appointmentInfo;
        this.appointmentNavigationType = appointmentNavigationType;
    }

    @Override
    public AppointmentsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View appointmentsListItemView = LayoutInflater.from(context).inflate(
                R.layout.cardview_appointments_item, parent, false);
        return new AppointmentsListViewHolder(appointmentsListItemView);
    }

    @Override
    public void onBindViewHolder(final AppointmentsListViewHolder holder, int position) {
        AppointmentsPayloadDTO payload = appointmentsArrayList.get(position).getPayload();
        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(payload.getStartTime());

        holder.setProvider(payload.getProvider());
        holder.setDateTime(dateUtil);
        holder.setLocation(payload.getLocation());
        holder.setStatus(payload, dateUtil.isToday());

        holder.setCheckInButton(position);
    }

    public void setList(List<AppointmentDTO> appointmentsArrayList) {
        this.appointmentsArrayList = appointmentsArrayList;
    }

    @Override
    public int getItemCount() {
        return appointmentsArrayList.size();
    }

    class AppointmentsListViewHolder extends RecyclerView.ViewHolder {

        private final View appointmentCardHeader;
        private final View appointmentLocationView;
        private final ImageView profileImage;
        private final CarePayTextView appointmentStatusMissed;
        private final CarePayTextView appointmentStatusCheckedIn;
        private final CarePayTextView appointmentStatusRequested;
        private final CarePayTextView appointmentStatusCanceled;
        private final CarePayTextView appointmentStatusCheckedOut;
        private final CarePayTextView appointmentPlaceName;
        private final CarePayTextView appointmentPlaceAddress;
        private CarePayTextView shortName;
        private CarePayTextView doctorName;
        private CarePayTextView doctorType;
        private CarePayTextView appointmentDate;
        private CarePayTextView appointmentTime;
        private Button startCheckIn;

        AppointmentsListViewHolder(View view) {
            super(view);
            startCheckIn = (Button) view.findViewById(R.id.appointment_check_in);
            appointmentCardHeader = view.findViewById(R.id.appointment_card_header);
            appointmentLocationView = view.findViewById(R.id.appointment_location_view);
            shortName = (CarePayTextView) view.findViewById(R.id.appointment_short_name);
            doctorName = (CarePayTextView) view.findViewById(R.id.appointment_doctor_name);
            doctorType = (CarePayTextView) view.findViewById(R.id.appointment_doctor_speciality);
            appointmentDate = (CarePayTextView) view.findViewById(R.id.appointment_start_day);
            appointmentTime = (CarePayTextView) view.findViewById(R.id.appointment_start_time);
            appointmentStatusMissed = (CarePayTextView) view.findViewById(R.id.appointment_status_missed);
            appointmentStatusCheckedIn = (CarePayTextView) view.findViewById(R.id.appointment_status_checked_in);
            appointmentStatusRequested = (CarePayTextView) view.findViewById(R.id.appointment_status_requested);
            appointmentStatusCanceled = (CarePayTextView) view.findViewById(R.id.appointment_status_canceled);
            appointmentStatusCheckedOut = (CarePayTextView) view.findViewById(R.id.appointment_status_checked_out);
            appointmentPlaceName = (CarePayTextView) view.findViewById(R.id.appointment_place_name);
            appointmentPlaceAddress = (CarePayTextView) view.findViewById(R.id.appointment_place_address);
            profileImage = (ImageView) view.findViewById(R.id.appointment_picture_image_view);
        }

        void setStatus(AppointmentsPayloadDTO payloadDTO, boolean isToday) {

            startCheckIn.setVisibility(View.GONE);
            if(appointmentNavigationType == Defs.NAVIGATE_CHECKOUT){
                startCheckIn.setText(Label.getLabel("practice_app_check_out_text"));
            }

            appointmentLocationView.setVisibility(View.GONE);
            appointmentStatusMissed.setVisibility(View.GONE);
            appointmentStatusCheckedIn.setVisibility(View.GONE);
            appointmentStatusRequested.setVisibility(View.GONE);
            appointmentStatusCanceled.setVisibility(View.GONE);

            GradientDrawable headerBackground = (GradientDrawable) appointmentCardHeader.getBackground();

            String statusCode = payloadDTO.getAppointmentStatus().getCode();

            switch (statusCode) {
                case CarePayConstants.IN_PROGRESS_IN_ROOM:
                case CarePayConstants.IN_PROGRESS_OUT_ROOM:
                case CarePayConstants.CHECKED_IN:
                    if(appointmentNavigationType == Defs.NAVIGATE_CHECKOUT){
                        headerBackground.setColor(context.getResources().getColor(R.color.colorPrimary));
                        startCheckIn.setVisibility(View.VISIBLE);
                        startCheckIn.setClickable(true);
                        startCheckIn.setEnabled(true);
                    }else {
                        headerBackground.setColor(context.getResources().getColor(R.color.yellowGreen));
                        appointmentStatusCheckedIn.setVisibility(View.VISIBLE);
                    }
                    break;

                case CarePayConstants.CANCELLED:
                    headerBackground.setColor(context.getResources().getColor(R.color.harvard_crimson));
                    appointmentStatusCanceled.setVisibility(View.VISIBLE);

                    break;

                case CarePayConstants.REQUESTED:
                    headerBackground.setColor(context.getResources().getColor(R.color.colorPrimary));
                    appointmentStatusRequested.setVisibility(View.VISIBLE);

                    break;

                case CarePayConstants.BILLED:
                case CarePayConstants.MANUALLY_BILLED:
                case CarePayConstants.CHECKED_OUT:
                    headerBackground.setColor(context.getResources().getColor(R.color.slateGray));
                    appointmentStatusCheckedOut.setVisibility(View.VISIBLE);

                    break;

                default:

                    // Missed Appointment
                    if (payloadDTO.isAppointmentOver()) {

                        headerBackground.setColor(context.getResources().getColor(R.color.lightning_yellow));

                        appointmentStatusMissed.setVisibility(View.VISIBLE);

                    } else if (payloadDTO.canCheckInNow(appointmentsResultModel)) {

                        headerBackground.setColor(context.getResources().getColor(R.color.colorPrimary));
                        startCheckIn.setVisibility(View.VISIBLE);
                        startCheckIn.setClickable(true);
                        startCheckIn.setEnabled(true);

                    } else if (isToday) {

                        headerBackground.setColor(context.getResources().getColor(R.color.colorPrimary));
                        startCheckIn.setVisibility(View.VISIBLE);
                        startCheckIn.setClickable(false);
                        startCheckIn.setEnabled(false);

                    } else {

                        headerBackground.setColor(context.getResources().getColor(R.color.textview_default_textcolor));
                        appointmentLocationView.setVisibility(View.VISIBLE);
                    }
            }
        }

        private void setProvider(ProviderDTO provider) {
            doctorName.setText(provider.getName());
            doctorType.setText(provider.getSpecialty().getName());
            shortName.setText(StringUtil.getShortName(provider.getName()));

            String photoUrl = provider.getPhoto();
            if (TextUtils.isEmpty(photoUrl)) {
                setPhotoView(photoUrl);
                System.out.println(provider.getName() + " - " + photoUrl);
            }
        }

        private void setPhotoView(String photoUrl) {
            Callback callback = new Callback() {
                @Override
                public void onSuccess() {
                    profileImage.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                }
            };

            Picasso.with(context).load(photoUrl).transform(new CircleImageTransform())
                    .resize(58, 58).into(profileImage, callback);
        }

        private void setCheckInButton(int position) {
            startCheckIn.setTag(position);
            startCheckIn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();
                    AppointmentDTO appointmentDTO = appointmentsArrayList.get(position);
                    if (listener != null) {
                        if(appointmentNavigationType == Defs.NAVIGATE_CHECKOUT){
                            listener.onStartCheckOut(appointmentDTO);
                        }else {
                            listener.onStartCheckIn(appointmentDTO);
                        }
                    }
                }
            });
        }

        private void setDateTime(DateUtil dateUtil) {
            appointmentDate.setText(dateUtil.getDateAsDayMonthDayOrdinalYear(Label.getLabel("appointments_web_today_heading")));
            appointmentTime.setText(dateUtil.getTime12Hour());
        }

        void setLocation(LocationDTO location) {
            appointmentPlaceName.setText(location.getName());
            appointmentPlaceAddress.setText(location.getAddress().toString());
        }
    }

    public interface AppointmentsAdapterStartCheckInListener {
        void onStartCheckIn(AppointmentDTO appointmentDTO);

        void onStartCheckOut(AppointmentDTO appointmentDTO);
    }

    public void setListener(AppointmentsAdapterStartCheckInListener listener) {
        this.listener = listener;
    }
}

