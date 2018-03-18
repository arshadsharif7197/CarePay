package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.view.View;

import com.carecloud.carepaylibray.appointments.models.LocationDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/15/17.
 */

public class PopupPickLocationAdapter extends PopupPickerAdapter {

    private List<LocationDTO> appointmentLocations = new ArrayList<>();
    private PopupPickCallback callback;

    /**
     * Constructor
     * @param context context
     * @param appointmentLocations list of locations
     * @param callback callback
     */
    public PopupPickLocationAdapter(Context context, List<LocationDTO> appointmentLocations, PopupPickCallback callback){
        super(context);
        this.appointmentLocations = appointmentLocations;
        this.callback = callback;
    }

    @Override
    public void onBindViewHolder(PopupListViewHolder holder, int position) {
        final LocationDTO location = appointmentLocations.get(position);

        holder.getName().setText(location.getName());
        holder.getName().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.pickLocation(location, selectedBalanceItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentLocations.size();
    }
}
