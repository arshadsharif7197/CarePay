package com.carecloud.carepay.patient.appointments.adapters;

import android.content.Context;
import android.view.View;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;

import java.util.List;

/**
 * @author pjohnson on 19/10/18.
 */
public class AppointmentHistoricAdapter extends BaseAppointmentAdapter {

    private final SelectAppointmentCallback callback;
    private boolean isLoading;

    public AppointmentHistoricAdapter(Context context,
                                      List<AppointmentDTO> appointments,
                                      SelectAppointmentCallback callback) {
        this.context = context;
        this.sortedAppointments = appointments;
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
        bindView(holder, appointmentsPayload, false);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onItemTapped(sortedAppointments.get(holder.getAdapterPosition()));
                }
            }
        });

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

    public interface SelectAppointmentCallback {
        void onItemTapped(AppointmentDTO appointmentDTO);
    }
}
