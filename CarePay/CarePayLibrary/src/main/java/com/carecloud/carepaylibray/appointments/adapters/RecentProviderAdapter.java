package com.carecloud.carepaylibray.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaSemiBoldLabel;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;

public class RecentProviderAdapter extends RecyclerView.Adapter<RecentProviderAdapter.ProviderViewHolder> {

    private Context context;
    private OnRecentListItemClickListener listener;
    private ArrayList<AppointmentModel> providerItems;

    public RecentProviderAdapter(Context context, ArrayList<AppointmentModel> providerItems,
                                 OnRecentListItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.providerItems = providerItems;
    }

    @Override
    public ProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_provider_list_item, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProviderViewHolder holder, int position) {
        final AppointmentModel item = providerItems.get(position);
        holder.doctorName.setText(item.getDoctorName());
        holder.doctorType.setText(item.getAppointmentType());

        SystemUtil.setGothamRoundedMediumTypeface(context, holder.shortName);
        holder.shortName.setText(SystemUtil.onShortDrName(item.getDoctorName()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecentListItemClickListener(holder.getAdapterPosition());
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

            // Set doctor name
            doctorName = (CustomProxyNovaSemiBoldLabel) itemView.findViewById(R.id.doctor_name);
            // Set doctor type
            doctorType = (CustomProxyNovaRegularLabel) itemView.findViewById(R.id.doctor_type);
            // set doctor short name
            shortName = (CustomGothamRoundedMediumLabel) itemView.findViewById(R.id.avatarTextView);
        }
    }

    public interface OnRecentListItemClickListener {
        void onRecentListItemClickListener(int position);
    }
}
