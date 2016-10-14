package com.carecloud.carepaylibray.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentProviderDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {

    private Context context;
    private OnAllListItemClickListener listener;
    private ArrayList<AppointmentDTO> appointmentArrayList;

    /**
     * Constructor.
     * @param context context
     * @param appointmentArrayList list of appointments
     * @param listener Onclick listener
     */
    public ProviderAdapter(Context context, ArrayList<AppointmentDTO> appointmentArrayList,
                           OnAllListItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.appointmentArrayList = appointmentArrayList;
    }

    @Override
    public ProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View chooseProviderListItemView = LayoutInflater.from(context).inflate(
                R.layout.choose_provider_list_item, parent, false);
        return new ProviderViewHolder(chooseProviderListItemView);
    }

    @Override
    public void onBindViewHolder(final ProviderViewHolder holder, int position) {

        if (appointmentArrayList != null) {

            AppointmentsPayloadDTO payload = appointmentArrayList.get(position).getPayload();
            if (payload != null) {

                AppointmentProviderDTO provider = payload.getProvider();
                holder.doctorName.setText(provider.getName());
                holder.doctorType.setText(provider.getSpecialty());
                holder.shortName.setText(StringUtil.onShortDrName(provider.getName()));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onAllListItemClickListener(holder.getAdapterPosition());
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return appointmentArrayList.size();
    }

    static class ProviderViewHolder extends RecyclerView.ViewHolder {

        private CustomGothamRoundedMediumLabel shortName;
        private CustomProxyNovaSemiBoldLabel doctorName;
        private CustomProxyNovaRegularLabel doctorType;

        ProviderViewHolder(View itemView) {
            super(itemView);

            // set doctor name
            doctorName = (CustomProxyNovaSemiBoldLabel) itemView.findViewById(R.id.doctor_name);
            // set doctor type
            doctorType = (CustomProxyNovaRegularLabel) itemView.findViewById(R.id.doctor_type);
            // set doctor short name
            shortName = (CustomGothamRoundedMediumLabel) itemView.findViewById(R.id.avatarTextView);
        }
    }

    public interface OnAllListItemClickListener {
        void onAllListItemClickListener(int position);
    }
}
