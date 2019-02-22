package com.carecloud.carepaylibray.appointments.createappointment.location;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

/**
 * @author pjohnson on 1/16/19.
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private final List<LocationDTO> locations;
    private final OnLocationListItemClickListener listener;

    public LocationAdapter(List<LocationDTO> locations, OnLocationListItemClickListener listener) {
        this.locations = locations;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final LocationDTO locationDTO = locations.get(position);
        holder.locationNameTextView.setText(StringUtil.capitalize(locationDTO.getName()));
        String address = locationDTO.getAddress().geAddressStringWithShortZipWOCounty2Lines();
        if (!StringUtil.isNullOrEmpty(address)) {
            holder.locationAddressTextView.setText(address);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLocationListItemClickListener(locationDTO);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public interface OnLocationListItemClickListener {
        void onLocationListItemClickListener(LocationDTO locationDTO);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView locationInitialsTextView;
        TextView locationNameTextView;
        TextView locationAddressTextView;
        ImageView locationPicImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            locationInitialsTextView = itemView.findViewById(R.id.locationInitialsTextView);
            locationNameTextView = itemView.findViewById(R.id.locationNameTextView);
            locationAddressTextView = itemView.findViewById(R.id.locationAddressTextView);
            locationPicImageView = itemView.findViewById(R.id.locationPicImageView);
        }
    }
}
