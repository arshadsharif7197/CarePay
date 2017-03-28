package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;

import java.util.ArrayList;
import java.util.List;

public class PracticeAvailableLocationsAdapter extends RecyclerView.Adapter<PracticeAvailableLocationsAdapter.ViewHolderLocation> {

    private final SelectLocationCallback callback;
    private final Context context;

    // The items to display in your RecyclerView
    private List<FilterDataDTO> locations;
    private int totalSelected;

    public interface SelectLocationCallback {
        void onLocationSelected();
    }

    /**
     * Constructor.
     *
     * @param context  context
     * @param callback callback on select time slot
     */
    public PracticeAvailableLocationsAdapter(Context context, SelectLocationCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return locations == null ? 0 : locations.size();
    }

    @Override
    public ViewHolderLocation onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.apt_available_locations_item, viewGroup, false);

        return new ViewHolderLocation(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderLocation viewHolder, final int position) {
        FilterDataDTO filterDataDTO = locations.get(position);

        TextView locationTextView = viewHolder.getLocationTextView();
        locationTextView.setText(filterDataDTO.getDisplayText());
        locationTextView.setSelected(filterDataDTO.isChecked());
        locationTextView.setTag(position);

        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = view.isSelected();
                int position = (int) view.getTag();

                if (position == 0) {
                    if (!isSelected) {
                        selectAllLocations(!isSelected);
                    }
                } else {
                    FilterDataDTO all = locations.get(0);
                    if (all.isChecked()) {
                        selectAllLocations(false);
                        selectLocation(position, true);
                        totalSelected = 1;
                    } else {
                        if (!isSelected || (isSelected && totalSelected > 1)) {
                            selectLocation(position, !isSelected);
                            if (isSelected) {
                                totalSelected--;
                            } else {
                                totalSelected++;
                            }
                            if (totalSelected == locations.size() - 1) {
                                all.setChecked(true);
                            }
                        }
                    }
                }

                if (callback != null) {
                    callback.onLocationSelected();
                }

                notifyDataSetChanged();
            }
        });
    }

    private void selectAllLocations(boolean isSelected) {
        for (FilterDataDTO filterDataDTO : locations) {
            filterDataDTO.setChecked(isSelected);
        }

        totalSelected = isSelected ? locations.size() : 0;
    }

    private void selectLocation(int position, boolean isSelected) {
        FilterDataDTO filterDataDTO = locations.get(0);
        filterDataDTO.setChecked(false);

        filterDataDTO = locations.get(position);
        filterDataDTO.setChecked(isSelected);
    }

    /**
     * @param items to be shown in list
     */
    public void setItems(List<FilterDataDTO> items) {
        FilterDataDTO all = new FilterDataDTO("all", Label.getLabel("appointment_all_locations_item"), FilterDataDTO.FilterDataType.HEADER);
        all.setChecked(true);

        totalSelected = items.size() + 1;

        locations = new ArrayList<>(totalSelected);
        locations.add(all);
        locations.addAll(items);
    }

    static class ViewHolderLocation extends RecyclerView.ViewHolder {
        private TextView locationTextView;

        ViewHolderLocation(View itemView) {
            super(itemView);
            locationTextView = (TextView) itemView.findViewById(R.id.available_location);
        }

        TextView getLocationTextView() {
            return locationTextView;
        }
    }
}