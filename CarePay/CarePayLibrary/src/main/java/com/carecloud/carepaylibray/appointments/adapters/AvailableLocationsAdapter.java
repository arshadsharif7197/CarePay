package com.carecloud.carepaylibray.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailableLocationsAdapter extends RecyclerView.Adapter<AvailableLocationsAdapter.ViewHolderLocation> {

    public interface SelectLocationCallback {
        void onSelectLocation(LocationDTO locationDTO);
    }

    // The items to display in your RecyclerView
    private List<LocationDTO> items;
    private Context context;
    private SelectLocationCallback selectLocationCallback;
    private String allButtonText;
    private Map<String, LocationDTO> selectedLocations = new HashMap<>();

    /**
     * Constructor.
     *
     * @param context  context
     * @param items    list of occurrence
     * @param callback callback on select time slot
     */
    public AvailableLocationsAdapter(Context context, List<LocationDTO> items,
                                     SelectLocationCallback callback,
                                     String allButtonText) {
        this.context = context;
        this.items = items;
        this.selectLocationCallback = callback;
        this.allButtonText = allButtonText;
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size() + 1;//need to account for the All
    }


    @Override
    public ViewHolderLocation onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.apt_available_locations_item, viewGroup, false);
        return new ViewHolderLocation(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderLocation viewHolder, final int position) {
        final LocationDTO appointmentLocations = position > 0 ? items.get(position - 1) : null;
        TextView locationTextView = viewHolder.getLocationTextView();
        if (position == 0) {
            locationTextView.setText(allButtonText);
            locationTextView.setSelected(selectedLocations.isEmpty());

        } else {
            locationTextView.setText(appointmentLocations.getName());
            locationTextView.setSelected(selectedLocations.isEmpty() || selectedLocations.containsKey(appointmentLocations.getGuid()));
        }

        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectLocationCallback != null) {
                    selectLocationCallback.onSelectLocation(appointmentLocations);
                }
                view.setSelected(!view.isSelected());//toggle selection status
            }
        });
    }

    /**
     * Refresh locations and reset selections if required
     *
     * @param clearAll true to cause reset of all selections
     */
    public void resetLocationsSelected(boolean clearAll) {
        if (clearAll) {
            selectedLocations.clear();
        }
        notifyDataSetChanged();
    }

    public void updateSelectedLocations(Map<String, LocationDTO> selectedLocations) {
        this.selectedLocations = selectedLocations;
    }

    public void setItems(List<LocationDTO> items) {
        this.items = items;
    }

    public static class ViewHolderLocation extends RecyclerView.ViewHolder {
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