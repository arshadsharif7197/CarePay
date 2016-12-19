package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

public class ProvidersListAdapter extends RecyclerView.Adapter<ProvidersListAdapter.ProvidersListViewHolder> {

    private Context context;
    private List<AppointmentResourcesDTO> providersArrayList;
    private AppointmentsResultModel appointmentsResultModel;

    /**
     * This will create a list of appointments
     * @param context context
     * @param providersArrayList providersArrayList
     */
    public ProvidersListAdapter(Context context, List<AppointmentResourcesDTO> providersArrayList,
                                AppointmentsResultModel appointmentInfo) {

        this.context = context;
        this.providersArrayList = providersArrayList;
        this.appointmentsResultModel = appointmentInfo;
    }

    @Override
    public ProvidersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View providersListItemView = LayoutInflater.from(context).inflate(
                R.layout.cardview_provider_item, parent, false);
        return new ProvidersListViewHolder(providersListItemView);
    }

    @Override
    public void onBindViewHolder(final ProvidersListViewHolder holder, int position) {
        AppointmentResourcesDTO resource = providersArrayList.get(position);

        String providerName = resource.getResource().getProvider().getName();
        holder.shortName.setText(StringUtil.onShortDrName(providerName));
        holder.doctorName.setText(providerName);
        holder.doctorType.setText(resource.getResource().getProvider().getSpecialty().getName());

        holder.placeName.setText(StringUtil.getLabelForView(""));
        holder.address.setText(StringUtil.getLabelForView(""));

        AppointmentLabelDTO label = appointmentsResultModel.getMetadata().getLabel();
        holder.scheduleAppointment.setText(label.getProviderListScheduleAppointmentButton());
        holder.scheduleAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return providersArrayList.size();
    }

    class ProvidersListViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView shortName;
        private CarePayTextView doctorName;
        private CarePayTextView doctorType;
        private CarePayTextView placeName;
        private CarePayTextView address;
        private Button scheduleAppointment;

        ProvidersListViewHolder(View itemView) {
            super(itemView);

            shortName = (CarePayTextView) itemView.findViewById(R.id.provider_short_name);
            doctorName = (CarePayTextView) itemView.findViewById(R.id.provider_doctor_name);
            doctorType = (CarePayTextView) itemView.findViewById(R.id.provider_doctor_speciality);
            placeName = (CarePayTextView) itemView.findViewById(R.id.provider_place_name);
            address = (CarePayTextView) itemView.findViewById(R.id.provider_place_address);

            scheduleAppointment = (Button) itemView.findViewById(R.id.provider_schedule_appointment);
        }
    }
}
