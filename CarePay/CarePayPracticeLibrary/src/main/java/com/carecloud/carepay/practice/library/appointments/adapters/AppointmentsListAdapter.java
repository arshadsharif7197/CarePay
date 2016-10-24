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
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedBoldLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaExtraBold;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;

import java.util.List;

/**
 * Created by harshal_patil on 10/19/2016.
 */

public class AppointmentsListAdapter extends RecyclerView.Adapter<AppointmentsListAdapter.AppointmentsListViewHolder> {

    private Context context;
    private List<com.carecloud.carepaylibray.appointments.models.AppointmentDTO> appointmentsArrayList;

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
        holder.doctorName.setText(R.string.not_defined);
        holder.doctorType.setText(R.string.not_defined);
        holder.appointmentLocation.setText(R.string.not_defined);
        holder.appointmentDate.setText(R.string.not_defined);
        holder.appointmentTime.setText(R.string.not_defined);
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

