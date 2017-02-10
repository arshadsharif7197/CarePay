package com.carecloud.carepay.patient.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentLocationsDTO;

import java.util.List;

public class AvailableLocationsAdapter extends RecyclerView.Adapter<AvailableLocationsAdapter.ViewHolderLocation> {

    public interface SelectLocationCallback{
        void onSelectLocation(AppointmentLocationsDTO appointmentLocationsDTO);
    }

    // The items to display in your RecyclerView
    private List<AppointmentLocationsDTO> items;
    private Context context;
    private SelectLocationCallback selectLocationCallback;
    private String allButtonText;

    /**
     * Constructor.
     * @param context context
     * @param items list of occurrence
     * @param callback callback on select time slot
     */
    public AvailableLocationsAdapter(Context context, List<AppointmentLocationsDTO> items, SelectLocationCallback callback, String allButtonText) {
        this.context = context;
        this.items = items;
        this.selectLocationCallback = callback;
        this.allButtonText = allButtonText;
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size()+1;//need to account for the All
    }


    @Override
    public ViewHolderLocation onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.apt_available_locations_item, viewGroup, false);

        return new ViewHolderLocation(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderLocation viewHolder, final int position) {
        final AppointmentLocationsDTO appointmentLocations = position>0?items.get(position-1):null;

        if(position == 0) {
            viewHolder.getLocationTextView().setText(allButtonText);
        }else{
            viewHolder.getLocationTextView().setText(appointmentLocations.getName());
        }

        viewHolder.getLocationTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectLocationCallback!=null)
                    selectLocationCallback.onSelectLocation(appointmentLocations);
            }
        });
    }

    public void setItems(List<AppointmentLocationsDTO> items){
        this.items = items;
    }

    public static class ViewHolderLocation extends RecyclerView.ViewHolder{
        private TextView locationTextView;

        public ViewHolderLocation(View itemView) {
            super(itemView);
            locationTextView = (TextView) itemView.findViewById(R.id.available_location);
        }

        public TextView getLocationTextView() {
            return locationTextView;
        }
    }
}