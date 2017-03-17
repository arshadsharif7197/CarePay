package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.view.View;

import com.carecloud.carepaylibray.appointments.models.ProviderDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/15/17.
 */

public class PopupPickProviderAdapter extends PopupPickerAdapter {

    private List<ProviderDTO> appointmentProvider = new ArrayList<>();
    private PopupPickCallback callback;

    /**
     * Contructor
     * @param context context
     * @param appointmentProvider list of providers
     * @param callback callback
     */
    public PopupPickProviderAdapter(Context context, List<ProviderDTO> appointmentProvider, PopupPickCallback callback){
        super(context);
        this.appointmentProvider = appointmentProvider;
        this.callback = callback;
    }

    @Override
    public void onBindViewHolder(PopupListViewHolder holder, int position) {
        final ProviderDTO provider = appointmentProvider.get(position);

        holder.getName().setText(provider.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.pickProvider(provider, selectedBalanceItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentProvider.size();
    }
}
