package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by harshal_patil on 10/19/2016.
 */
public class AppointmentsListAdapter extends RecyclerView.Adapter<AppointmentsListAdapter.AppointmentsListViewHolder> {

    private Context context;
    private List<AppointmentDTO> appointmentsArrayList;
    private AppointmentsResultModel appointmentsResultModel;

    /**
     * This will create a list of appointments
     *
     * @param context               context
     * @param appointmentsArrayList appointmentsArrayList
     */
    public AppointmentsListAdapter(Context context, List<AppointmentDTO> appointmentsArrayList,
                                   AppointmentsResultModel appointmentInfo) {

        this.context = context;
        this.appointmentsArrayList = appointmentsArrayList;
        this.appointmentsResultModel = appointmentInfo;
    }

    @Override
    public AppointmentsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View appointmentsListItemView = LayoutInflater.from(context).inflate(
                R.layout.cardview_appointments_item, parent, false);
        return new AppointmentsListViewHolder(appointmentsListItemView);
    }

    @Override
    public void onBindViewHolder(final AppointmentsListViewHolder holder, int position) {
        final Object object = appointmentsArrayList.get(position);
        final AppointmentsPayloadDTO item = ((AppointmentDTO) object).getPayload();
        AppointmentLabelDTO appointmentLabels = appointmentsResultModel.getMetadata().getLabel();

        holder.doctorName.setText(item.getProvider().getName());
        holder.doctorType.setText(item.getProvider().getSpecialty().getName());

//        holder.placeName.setText("Place Name");
//        holder.placeAddress.setText("2645 SW 37th Ave #502, Miami, FL 33133, USA");

        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
        DateUtil.getInstance().setDateRaw(item.getStartTime());
        holder.appointmentDate.setText(DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal());

        String strToday;
        String startDay = StringUtils.substringBefore(DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal(), ",");
        String endDay = DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal()
                .substring(DateUtil.getInstance().getDateAsDayMonthDayOrdinal().indexOf(","));

        if (DateUtil.getInstance().isToday()) {
            strToday = startDay.replace(startDay, appointmentLabels.getAppointmentsTodayHeadingSmall()) + endDay;
        } else {
            strToday = startDay + endDay;
        }
        holder.appointmentDate.setText(strToday);

        holder.appointmentTime.setText(DateUtil.getInstance().getTime12Hour());
        holder.startCheckIn.setText(appointmentLabels.getAppointmentsPracticeCheckin());

        boolean isPending = item.getAppointmentStatusModel().getId() == 1;
        if (!isPending) {
            holder.startCheckIn.setClickable(false);
            holder.startCheckIn.setEnabled(false);
        } else {
            holder.startCheckIn.setClickable(true);
            holder.startCheckIn.setEnabled(true);
        }
        holder.startCheckIn.setClickable(true);
        holder.startCheckIn.setEnabled(true);

//        GradientDrawable bgShape = (GradientDrawable) holder.headerView.getBackground();
//        bgShape.setColor(context.getResources().getColor(R.color.cardview_header_bg));

        String photoUrl = item.getProvider().getPhoto();
        if (TextUtils.isEmpty(photoUrl)) {
            holder.shortName.setText(StringUtil.onShortDrName(item.getProvider().getName()));
        } else {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    holder.shortName.setText(StringUtil.onShortDrName(item.getProvider().getName()));
                }
            });

//            builder.build().load(photoUrl).transform(new CircleImageTransform())
//                    .resize(58, 58).into(holder.profileImage);
//            holder.profileImage.setVisibility(View.VISIBLE);
        }

        holder.startCheckIn.setTag(position);
        holder.startCheckIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Map<String, String> queries = new HashMap<>();
                queries.put("practice_mgmt", ((AppointmentDTO) object).getMetadata().getPracticeMgmt());
                queries.put("practice_id", ((AppointmentDTO) object).getMetadata().getPracticeId());
                queries.put("appointment_id", ((AppointmentDTO) object).getMetadata().getAppointmentId());

                Map<String, String> header = new HashMap<>();
                header.put("transition", "true");

                TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getCheckingIn();
                WorkflowServiceHelper.getInstance().execute(transitionDTO, transitionToDemographicsVerifyCallback, queries, header);
            }
        });
    }

    private WorkflowServiceCallback transitionToDemographicsVerifyCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PracticeNavigationHelper.getInstance().navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(context);
            Log.e(context.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public int getItemCount() {
        return appointmentsArrayList.size();
    }

    class AppointmentsListViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView shortName;
        private CarePayTextView doctorName;
        private CarePayTextView doctorType;

        private CarePayTextView placeName;
        private CarePayTextView placeAddress;
        private CarePayTextView appointmentDate;
        private CarePayTextView appointmentTime;
        private Button startCheckIn;
        private LinearLayout headerView;
//        private ImageView profileImage;

        AppointmentsListViewHolder(View itemView) {
            super(itemView);

            shortName = (CarePayTextView) itemView.findViewById(R.id.appointment_short_name);
            doctorName = (CarePayTextView) itemView.findViewById(R.id.appointment_doctor_name);
            doctorType = (CarePayTextView) itemView.findViewById(R.id.appointment_doctor_speciality);

            placeName = (CarePayTextView) itemView.findViewById(R.id.appointment_place_name);
            placeAddress = (CarePayTextView) itemView.findViewById(R.id.appointment_place_address);
            startCheckIn = (Button) itemView.findViewById(R.id.appointment_check_in);

            appointmentDate = (CarePayTextView) itemView.findViewById(R.id.appointment_start_day);
            appointmentTime = (CarePayTextView) itemView.findViewById(R.id.appointment_start_time);
            headerView = (LinearLayout) itemView.findViewById(R.id.appointment_card_header);
//            profileImage = (ImageView) itemView.findViewById(R.id.appointUserPicImageView);
        }
    }
}

