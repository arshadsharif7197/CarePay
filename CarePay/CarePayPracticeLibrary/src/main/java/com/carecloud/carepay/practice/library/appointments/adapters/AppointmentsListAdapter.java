package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by harshal_patil on 10/19/2016.
 */
public class AppointmentsListAdapter extends RecyclerView.Adapter<AppointmentsListAdapter.AppointmentsListViewHolder> {

    private Context context;
    private List<AppointmentDTO> appointmentsArrayList;
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentsAdapterStartCheckInListener listener;

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
        final AppointmentDTO selectedAppointment = appointmentsArrayList.get(position);
        final AppointmentsPayloadDTO item = selectedAppointment.getPayload();

        holder.doctorName.setText(item.getProvider().getName());
        holder.doctorType.setText(item.getProvider().getSpecialty().getName());

        DateUtil.getInstance().setDateRaw(item.getStartTime());
        holder.appointmentDate.setText(DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal());

        String strToday;
        String startDay = StringUtils.substringBefore(DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal(), ",");
        String endDay = DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal()
                .substring(DateUtil.getInstance().getDateAsDayMonthDayOrdinal().indexOf(","));
        boolean isToday = DateUtil.getInstance().isToday();
        if (isToday) {
            strToday = startDay.replace(startDay, Label.getLabel("appointments_web_today_heading") + endDay);
        } else {
            strToday = startDay + endDay;
        }
        holder.appointmentDate.setText(strToday);

        holder.appointmentTime.setText(DateUtil.getInstance().getTime12Hour());

        Date appointmentTime = DateUtil.getInstance().setDateRaw(item.getEndTime()).getDate();
        // Get current date/time in required format
        Date currentDate = DateUtil.getInstance().setToCurrent().getDate();
        boolean isMissed = false;
        long differenceInMilli = appointmentTime.getTime() - currentDate.getTime();
        long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMilli);
        if (differenceInMinutes < 0) {
            isMissed = true;
        }

        boolean allowEarlyCheckin = appointmentsResultModel.getPayload().getAppointmentsSettings().get(0).getCheckin().getAllowEarlyCheckin();
        String allowEarlyCheckinPeriodStr = appointmentsResultModel.getPayload().getAppointmentsSettings().get(0).getCheckin().getEarlyCheckinPeriod();
        long allowEarlyCheckinPeriod = Long.parseLong(allowEarlyCheckinPeriodStr);
        // Get current date/time in required format
        boolean isPending = item.getAppointmentStatusModel().getCode().equalsIgnoreCase(CarePayConstants.PENDING);

        if (isPending && isToday && !isMissed) {
            holder.startCheckIn.setClickable(true);
            holder.startCheckIn.setEnabled(true);
        } else if (isPending && !isMissed && allowEarlyCheckin && (differenceInMinutes < allowEarlyCheckinPeriod)) {
            holder.startCheckIn.setClickable(true);
            holder.startCheckIn.setEnabled(true);
        } else {
            holder.startCheckIn.setClickable(false);
            holder.startCheckIn.setEnabled(false);
        }

        String photoUrl = item.getProvider().getPhoto();
        if (TextUtils.isEmpty(photoUrl)) {
            holder.shortName.setText(StringUtil.getShortName(item.getProvider().getName()));
        } else {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    holder.shortName.setText(StringUtil.getShortName(item.getProvider().getName()));
                }
            });

        }

        holder.startCheckIn.setTag(position);
        holder.startCheckIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                listener.onStartCheckIn(selectedAppointment);
            }
        });
    }

    public void setList(List<AppointmentDTO> appointmentsArrayList) {
        this.appointmentsArrayList = appointmentsArrayList;
    }

    @Override
    public int getItemCount() {
        return appointmentsArrayList.size();
    }

    class AppointmentsListViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView shortName;
        private CarePayTextView doctorName;
        private CarePayTextView doctorType;
        private CarePayTextView appointmentDate;
        private CarePayTextView appointmentTime;
        private Button startCheckIn;

        AppointmentsListViewHolder(View itemView) {
            super(itemView);
            shortName = (CarePayTextView) itemView.findViewById(R.id.appointment_short_name);
            doctorName = (CarePayTextView) itemView.findViewById(R.id.appointment_doctor_name);
            doctorType = (CarePayTextView) itemView.findViewById(R.id.appointment_doctor_speciality);
            startCheckIn = (Button) itemView.findViewById(R.id.appointment_check_in);
            appointmentDate = (CarePayTextView) itemView.findViewById(R.id.appointment_start_day);
            appointmentTime = (CarePayTextView) itemView.findViewById(R.id.appointment_start_time);
        }
    }

    public interface AppointmentsAdapterStartCheckInListener {
        void onStartCheckIn(AppointmentDTO appointmentDTO);
    }

    public void setListener(AppointmentsAdapterStartCheckInListener listener) {
        this.listener = listener;
    }
}

