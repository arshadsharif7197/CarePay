package com.carecloud.carepaylibray.appointments.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;

public class AllProviderAdapter extends RecyclerView.Adapter<AllProviderAdapter.ProviderViewHolder> {

    private Context context;
    private OnAllListItemClickListener listener;
    private ArrayList<AppointmentModel> providerItems;

    public AllProviderAdapter(Context context, ArrayList<AppointmentModel> providerItems,
                              OnAllListItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.providerItems = providerItems;
    }

    @Override
    public ProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View chooseProviderListItemView = LayoutInflater.from(context).inflate(
                R.layout.choose_provider_list_item, parent, false);
        return new ProviderViewHolder(chooseProviderListItemView);
    }

    @Override
    public void onBindViewHolder(final ProviderViewHolder holder, int position) {
        final AppointmentModel item = providerItems.get(position);

        holder.doctorName.setText(item.getDoctorName());
        holder.doctorType.setText(item.getAppointmentType());
        holder.shortName.setText(StringUtil.onShortDrName(item.getDoctorName()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAllListItemClickListener(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return providerItems.size();
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
