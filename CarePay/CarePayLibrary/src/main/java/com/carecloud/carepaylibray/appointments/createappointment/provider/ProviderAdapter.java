package com.carecloud.carepaylibray.appointments.createappointment.provider;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

/**
 * @author pjohnson on 1/15/19.
 */
public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ViewHolder> {

    private final List<AppointmentResourcesItemDTO> resources;
    private final OnProviderListItemClickListener listener;

    public ProviderAdapter(List<AppointmentResourcesItemDTO> resources, OnProviderListItemClickListener listener) {
        this.resources = resources;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_provider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AppointmentResourcesItemDTO resource = resources.get(position);
        holder.doctorNameTextView.setText(StringUtil.capitalize(resource.getProvider().getFullName()));
        holder.shortNameTextView.setText(StringUtil.getShortName(resource.getProvider().getName()));
        holder.doctorTypeTextView.setText(resource.getProvider().getSpecialty().getName());
        PicassoHelper.get().loadImage(holder.providerPicImageView.getContext(), holder.providerPicImageView,
                holder.shortNameTextView, resource.getProvider().getPhoto());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProviderListItemClickListener(resource);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    public interface OnProviderListItemClickListener {
        void onProviderListItemClickListener(AppointmentResourcesItemDTO position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView shortNameTextView;
        private CarePayTextView doctorNameTextView;
        private CarePayTextView doctorTypeTextView;
        private ImageView providerPicImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            doctorNameTextView = itemView.findViewById(R.id.doctor_name);
            doctorTypeTextView = itemView.findViewById(R.id.doctor_type);
            shortNameTextView = itemView.findViewById(R.id.avatarTextView);
            providerPicImageView = itemView.findViewById(R.id.providerPicImageView);
        }
    }
}
