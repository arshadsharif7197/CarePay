package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harshal_patil on 10/19/2016.
 */

public class AppointmentsListAdapter extends RecyclerView.Adapter<AppointmentsListAdapter.AppointmentsListViewHolder> {

    private Context context;
    private List<com.carecloud.carepaylibray.appointments.models.AppointmentDTO> appointmentsArrayList;
    private AppointmentLabelDTO appointmentLabels;
    private AppointmentsResultModel appointmentsResultModel;

    /**
     * This will create a list of appointments
     * @param context context
     * @param appointmentsArrayList appointmentsArrayList
     */
    public AppointmentsListAdapter(Context context, List<com.carecloud.carepaylibray.appointments.models.AppointmentDTO> appointmentsArrayList, AppointmentsResultModel appointmentInfo) {

        this.context = context;
        this.appointmentsArrayList = appointmentsArrayList;
        this.appointmentsResultModel = appointmentInfo;
    }

    @Override
    public AppointmentsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View appointmentsListItemView = LayoutInflater.from(context).inflate(
                R.layout.appointment_card, parent, false);
        return new AppointmentsListViewHolder(appointmentsListItemView);
    }

    @Override
    public void onBindViewHolder(final AppointmentsListViewHolder holder, int position) {
        final Object object = appointmentsArrayList.get(position);
        final AppointmentsPayloadDTO item = ((AppointmentDTO) object).getPayload();
        appointmentLabels = appointmentsResultModel.getMetadata().getLabel();
        holder.doctorName.setText(item.getProvider().getName());
        holder.doctorType.setText(item.getProvider().getSpecialty().getName());
        holder.appointmentLocation.setText(item.getLocation().getName());
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
        DateUtil.getInstance().setDateRaw(item.getStartTime());
        holder.appointmentDate.setText(DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal());
        String startDay = StringUtils.substringBefore(DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal(), ",");
        String endDay =DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal()
                .substring(DateUtil.getInstance().getDateAsDayMonthDayOrdinal().indexOf(","));
        String strToday;
        if(DateUtil.getInstance().isToday()) {
            strToday = startDay.replace(startDay, "Today") + endDay;
        }else{
            strToday = startDay + endDay;
        }
        holder.appointmentDate.setText(strToday);

        holder.appointmentTime.setText(DateUtil.getInstance().getTime12Hour());
        holder.startCheckInTextview.setText(StringUtil.getLabelForView(
                appointmentLabels.getAppointmentsPracticeCheckin()));
        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius(50.0f);

        if (!item.getAppointmentStatusModel().getName().equals("Pending")) {
            shape.setColor(Color.LTGRAY);
            holder.startCheckInTextview.setEnabled(false);

        } else {
            int color = Color.parseColor("#7ED321");
            shape.setColor(color);
            holder.startCheckInTextview.setClickable(true);
        }

        if (Build.VERSION.SDK_INT >= 16) {
            holder.startCheckInTextview.setBackground(shape);
        } else {
            holder.startCheckInTextview.setBackgroundDrawable(shape);
        }

        holder.startCheckInTextview.setBackground(shape);
        String photoUrl = item.getProvider().getPhoto();
        if (TextUtils.isEmpty(photoUrl)) {
            holder.shortNameTextview.setText(StringUtil.onShortDrName(item.getProvider().getName()));
        } else {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    holder.shortNameTextview.setText(StringUtil.onShortDrName(item.getProvider().getName()));
                }
            });

            builder.build().load(photoUrl).transform(new CircleImageTransform())
                    .resize(58, 58).into(holder.profileImage);
            holder.profileImage.setVisibility(View.VISIBLE);
        }
        holder.startCheckInTextview.setTag(position);
        holder.startCheckInTextview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView startCheckInTextview=((TextView)v);
                int pos = (Integer) startCheckInTextview.getTag();
                Map<String, String> queries = new HashMap<>();
                queries.put("practice_mgmt", ((AppointmentDTO) object).getMetadata().getPracticeMgmt());
                queries.put("practice_id", ((AppointmentDTO) object).getMetadata().getPracticeId());
                queries.put("appointment_id", ((AppointmentDTO) object).getMetadata().getAppointmentId());

                Map<String, String> header = new HashMap<>();
                header.put("transition", "true");

                TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getCheckin();
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
        }
    };

    @Override
    public int getItemCount() {
        return appointmentsArrayList.size();
    }

    class AppointmentsListViewHolder extends RecyclerView.ViewHolder {

        private TextView shortNameTextview;
        private TextView startCheckInTextview;
        private CarePayTextView doctorName;
        private CarePayTextView doctorType;
        private CarePayTextView appointmentLocation;
        private CarePayTextView appointmentDate;
        private CarePayTextView appointmentTime;
        //private ImageView cellAvatar;
        private ImageView profileImage;

        AppointmentsListViewHolder(View itemView) {
            super(itemView);

            doctorName = (CarePayTextView) itemView.findViewById(R.id.appointmentNameTextView);
            doctorType = (CarePayTextView) itemView.findViewById(R.id.appointmentTypeTextView);
            shortNameTextview = (TextView) itemView.findViewById(R.id.appointmentShortnameTextView);
            startCheckInTextview = (TextView) itemView.findViewById(R.id.checkInTextview);
            appointmentLocation = (CarePayTextView) itemView.findViewById(R.id.appointmentLocationTextview);
            appointmentDate = (CarePayTextView) itemView.findViewById(R.id.appointmentDateTextView);
            appointmentTime = (CarePayTextView) itemView.findViewById(R.id.appointmentTimeTextView);
            //cellAvatar = (ImageView) itemView.findViewById(R.id.cellAvatarImageView);
            profileImage = (ImageView) itemView.findViewById(R.id.appointUserPicImageView);

        }
    }

}

