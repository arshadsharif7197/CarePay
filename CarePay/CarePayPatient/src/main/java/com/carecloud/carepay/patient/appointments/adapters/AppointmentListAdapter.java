package com.carecloud.carepay.patient.appointments.adapters;

import android.content.Context;
import android.view.View;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayUtil;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lmenendez on 5/2/17
 */

public class AppointmentListAdapter extends BaseAppointmentAdapter {

    private List<AppointmentDTO> appointmentItems;

    /**
     * Constructor
     *
     * @param context          context
     * @param appointmentItems initial appt list
     * @param callback         select appt callback
     */
    public AppointmentListAdapter(Context context, List<AppointmentDTO> appointmentItems,
                                  SelectAppointmentCallback callback,
                                  List<UserPracticeDTO> userPracticeDTOs,
                                  Map<String, Set<String>> enabledPracticeLocations) {
        this.context = context;
        this.appointmentItems = appointmentItems;
        this.callback = callback;
        this.userPracticeDTOs = userPracticeDTOs;
        this.enabledPracticeLocations = enabledPracticeLocations;

        sortAppointments();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AppointmentDTO appointmentDTO = sortedAppointments.get(position);
        AppointmentsPayloadDTO appointmentsPayload = appointmentDTO.getPayload();

        //Header
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            holder.sectionHeaderTitle.setText(appointmentsPayload.getId());
            return;
        }

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
                    callback.onItemTapped(sortedAppointments.get(position));
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

    /**
     * Update appt list
     *
     * @param appointmentItems new appt list
     */
    public void setAppointmentItems(List<AppointmentDTO> appointmentItems) {
        this.appointmentItems = appointmentItems;
        sortAppointments();
        notifyDataSetChanged();
    }

    private void sortAppointments() {
        Collections.sort(appointmentItems, new Comparator<AppointmentDTO>() {
            @Override
            public int compare(AppointmentDTO left, AppointmentDTO right) {
                AppointmentDisplayStyle leftStyle = getDisplayStyle(left.getPayload());
                AppointmentDisplayStyle rightStyle = getDisplayStyle(right.getPayload());

                Date leftDate = DateUtil.getInstance().setDateRaw(left.getPayload().getStartTime()).getDate();
                Date rightDate = DateUtil.getInstance().setDateRaw(right.getPayload().getStartTime()).getDate();

                //Check-in should go on top
                if (leftStyle == AppointmentDisplayStyle.CHECKED_IN
                        || rightStyle == AppointmentDisplayStyle.CHECKED_IN) {
                    if (leftStyle != rightStyle) {
                        if (leftStyle == AppointmentDisplayStyle.CHECKED_IN) {//left should come first
                            return -1;
                        } else {//right should come first
                            return 1;
                        }
                    } else {
                        //compare the dates
                        return leftDate.compareTo(rightDate);
                    }
                }
                return leftDate.compareTo(rightDate);
            }

        });

        addHeaders();
    }

    private void addHeaders() {
        sortedAppointments.clear();

        boolean checkinHeader = false;
        boolean todayHeader = false;
        boolean upcomingHeader = false;
        AppointmentDTO headerTemplate;

        for (AppointmentDTO appointmentDTO : appointmentItems) {
            //required here to handle the case where user has only 1 appt and it was not run through comparator
            if (appointmentDTO.getPayload().getDisplayStyle() == null) {
                appointmentDTO.getPayload().setDisplayStyle(AppointmentDisplayUtil
                        .determineDisplayStyle(appointmentDTO.getPayload()));
            }

            //check if we need to add a checked in header
            if (!checkinHeader && appointmentDTO.getPayload().getDisplayStyle() == AppointmentDisplayStyle.CHECKED_IN) {
                headerTemplate = new AppointmentDTO();
                headerTemplate.getPayload().setDisplayStyle(AppointmentDisplayStyle.HEADER);
                headerTemplate.getPayload().setId(Label.getLabel("appointments_checked_in_label"));
                sortedAppointments.add(headerTemplate);
                checkinHeader = true;
            }

            //check if we need a today header
            if (!todayHeader && appointmentDTO.getPayload().getDisplayStyle()
                    != AppointmentDisplayStyle.CHECKED_IN && appointmentDTO.getPayload().isAppointmentToday()) {
                headerTemplate = new AppointmentDTO();
                headerTemplate.getPayload().setDisplayStyle(AppointmentDisplayStyle.HEADER);
                headerTemplate.getPayload().setId(Label.getLabel("today_appointments_heading"));
                sortedAppointments.add(headerTemplate);
                todayHeader = true;
            }

            //check if we need an upcoming header
            if (!upcomingHeader && appointmentDTO.getPayload().getDisplayStyle() != AppointmentDisplayStyle.CHECKED_IN
                    && !appointmentDTO.getPayload().isAppointmentToday()) {
                headerTemplate = new AppointmentDTO();
                headerTemplate.getPayload().setDisplayStyle(AppointmentDisplayStyle.HEADER);
                headerTemplate.getPayload().setId(Label.getLabel("upcoming_appointments_heading"));
                sortedAppointments.add(headerTemplate);
                upcomingHeader = true;
            }

            sortedAppointments.add(appointmentDTO);
        }
    }


}
