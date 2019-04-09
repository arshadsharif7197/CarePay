package com.carecloud.carepay.patient.appointments.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pjohnson on 19/10/18.
 */
public class AppointmentHistoricAdapter extends BaseAppointmentAdapter {

    private boolean isLoading;

    public AppointmentHistoricAdapter(Context context,
                                      List<AppointmentDTO> appointments,
                                      List<UserPracticeDTO> userPracticeDTOs,
                                      Map<String, Set<String>> enabledPracticeLocations,
                                      SelectAppointmentCallback callback) {
        this.context = context;
        this.sortedAppointments = appointments;
        this.userPracticeDTOs = userPracticeDTOs;
        this.enabledPracticeLocations = enabledPracticeLocations;
        this.callback = callback;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position >= sortedAppointments.size()) {
            return;
        }

        AppointmentDTO appointmentDTO = sortedAppointments.get(position);
        AppointmentsPayloadDTO appointmentsPayload = appointmentDTO.getPayload();

        //cleanup
        cleanupViews(holder);

        boolean isBreezePractice = isBreezePractice(appointmentDTO.getMetadata().getPracticeId());
        Set<String> enabledLocations = enabledPracticeLocations.get(appointmentDTO.getMetadata()
                .getPracticeId());
        boolean shouldShowCheckoutButton = shouldShowCheckOutButton(appointmentDTO, enabledLocations,
                isBreezePractice);
        bindView(holder, appointmentsPayload, shouldShowCheckoutButton);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onItemTapped(sortedAppointments.get(holder.getAdapterPosition()));
                }
            }
        });
        holder.checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onCheckoutTapped(sortedAppointments.get(position));
            }
        });
    }

    @Override
    protected void bindView(final ViewHolder holder, AppointmentsPayloadDTO appointmentsPayload,
                            boolean shouldShowCheckoutButton) {
        super.bindView(holder, appointmentsPayload, shouldShowCheckoutButton);
        AppointmentDisplayStyle style = getDisplayStyle(appointmentsPayload);
        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentsPayload.getStartTime());
        switch (style) {
            case MISSED:
                holder.upcomingDateLayout.setVisibility(View.VISIBLE);
                holder.upcomingDateTextView.setText(dateUtil.getDayLiteralAbbr());
                holder.upcomingMonthTextView.setText(dateUtil.getDateAsMonthLiteralDay());
                holder.upcomingTimeTextView.setText(dateUtil.getTime12Hour());
                holder.todayTimeMessage.setVisibility(View.GONE);
                holder.todayTimeLayout.setVisibility(View.GONE);
            case CANCELED_UPCOMING:
            case CANCELED: {
                holder.doctorName.setTextColor(ContextCompat.getColor(context, R.color.payne_gray));
                holder.initials.setBackgroundResource(R.drawable.round_list_tv_charcoal);
                holder.initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;
            }
            case CHECKED_IN:
                holder.checkOutButton.setVisibility(View.GONE);
                holder.checkedOutLabel.setVisibility(View.GONE);
                holder.upcomingDateLayout.setVisibility(View.VISIBLE);
                holder.upcomingDateTextView.setText(dateUtil.getDayLiteralAbbr());
                holder.upcomingMonthTextView.setText(dateUtil.getDateAsMonthLiteralDay());
                holder.upcomingTimeTextView.setText(dateUtil.getTime12Hour());
            case CHECKED_OUT:
                holder.checkedOutLabel.setVisibility(View.GONE);
                holder.upcomingDateLayout.setVisibility(View.VISIBLE);
                holder.upcomingDateTextView.setText(dateUtil.getDayLiteralAbbr());
                holder.upcomingMonthTextView.setText(dateUtil.getDateAsMonthLiteralDay());
                holder.upcomingTimeTextView.setText(dateUtil.getTime12Hour());
        }
        DateUtil.getInstance().setDateRaw(appointmentsPayload.getEndTime());
        if (style.equals(AppointmentDisplayStyle.CHECKED_IN) && DateUtil.getInstance().isWithinHours(24) && shouldShowCheckoutButton){
            holder.checkOutButton.setVisibility(View.VISIBLE);
            holder.upcomingDateLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (isLoading && !sortedAppointments.isEmpty()) {
            return sortedAppointments.size() + 1;
        }
        return sortedAppointments.size();
    }

    public void setData(List<AppointmentDTO> appointments, boolean refresh) {
        if (refresh) {
            sortedAppointments.clear();
        }
        this.sortedAppointments.addAll(appointments);
        notifyDataSetChanged();
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }
}
