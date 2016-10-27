package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedBoldLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaExtraBold;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.List;

import org.apache.commons.lang3.StringUtils;



/**
 * Created by harshal_patil on 10/19/2016.
 */

public class AppointmentsListAdapter extends RecyclerView.Adapter<AppointmentsListAdapter.AppointmentsListViewHolder> {

    private Context context;
    private List<com.carecloud.carepaylibray.appointments.models.AppointmentDTO> appointmentsArrayList;

    /**
     * This will create a list of appointments
     * @param context context
     * @param appointmentsArrayList appointmentsArrayList
     */
    public AppointmentsListAdapter(Context context, List<com.carecloud.carepaylibray.appointments.models.AppointmentDTO> appointmentsArrayList) {

        this.context = context;
        this.appointmentsArrayList = appointmentsArrayList;
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
        holder.doctorName.setText(item.getProvider().getName());
        holder.doctorType.setText(item.getProvider().getSpecialty().getName());
        holder.appointmentLocation.setText(item.getLocation().getName());
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
        DateUtil.getInstance().setDateRaw(item.getStartTime());
        holder.appointmentDate.setText(DateUtil.getInstance().getDateAsDayMonthDayOrdinal());
        String startDay = StringUtils.substringBefore(DateUtil.getInstance().getDateAsDayMonthDayOrdinal(), ",");
        String endDay =DateUtil.getInstance().getDateAsDayMonthDayOrdinal()
                .substring(DateUtil.getInstance().getDateAsDayMonthDayOrdinal().indexOf(","));
        String strToday = startDay.replace(startDay, "Today")+ endDay ;
        holder.appointmentDate.setText(strToday);
        holder.appointmentTime.setText(DateUtil.getInstance().getTime12Hour());
        holder.startCheckInTextview.setText(R.string.not_defined);
    }

    @Override
    public int getItemCount() {
        return appointmentsArrayList.size();
    }

    class AppointmentsListViewHolder extends RecyclerView.ViewHolder {

        private TextView shortNameTextview;
        private TextView startCheckInTextview;
        private CustomProxyNovaSemiBoldLabel doctorName;
        private CustomProxyNovaRegularLabel doctorType;
        private CustomGothamRoundedBoldLabel appointmentLocation;
        private CustomProxyNovaExtraBold appointmentDate;
        private CustomGothamRoundedBoldLabel appointmentTime;

        AppointmentsListViewHolder(View itemView) {
            super(itemView);

            doctorName = (CustomProxyNovaSemiBoldLabel) itemView.findViewById(R.id.appointmentNameTextView);
            doctorType = (CustomProxyNovaRegularLabel) itemView.findViewById(R.id.appointmentTypeTextView);
            shortNameTextview = (TextView) itemView.findViewById(R.id.appointmentShortnameTextView);
            startCheckInTextview = (TextView) itemView.findViewById(R.id.checkInTextview);
            appointmentLocation = (CustomGothamRoundedBoldLabel) itemView.findViewById(R.id.appointmentLocationTextview);
            appointmentDate = (CustomProxyNovaExtraBold) itemView.findViewById(R.id.appointmentDateTextView);
            appointmentTime = (CustomGothamRoundedBoldLabel) itemView.findViewById(R.id.appointmentTimeTextView);
        }
    }

}

