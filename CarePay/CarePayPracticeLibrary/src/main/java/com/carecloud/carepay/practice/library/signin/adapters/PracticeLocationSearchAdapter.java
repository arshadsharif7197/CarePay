package com.carecloud.carepay.practice.library.signin.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pjohnson on 02/21/18.
 */
public class PracticeLocationSearchAdapter extends RecyclerView.Adapter<PracticeLocationSearchAdapter.PracticeViewHolder> {

    public interface SelectPracticeLocationAdapterCallback {
        void onSelectPracticeLocation(LocationDTO location);
    }

    private List<LocationDTO> locationList = new ArrayList<>();
    private SelectPracticeLocationAdapterCallback callback;

    private LocationDTO selectedLocation;

    /**
     * Constructor
     *
     * @param locationList list of practices
     * @param callback     select practice callback
     */
    public PracticeLocationSearchAdapter(List<LocationDTO> locationList,
                                         SelectPracticeLocationAdapterCallback callback) {
        this.locationList = locationList;
        this.callback = callback;
    }


    @Override
    public PracticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_practice_list_row, parent, false);
        return new PracticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PracticeViewHolder holder, int position) {
        final LocationDTO location = locationList.get(position);

        holder.locaationName.setText(location.getName());
        if (selectedLocation == null || !location.getId().equals(selectedLocation.getId())) {
            holder.check.setSelected(false);
        } else {
            holder.check.setSelected(true);
        }
        holder.check.setSelected(location == selectedLocation);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedLocation = location;
                if (callback != null) {
                    callback.onSelectPracticeLocation(location);
                }
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public void setSelectedLocation(LocationDTO selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public void setLocationList(List<LocationDTO> locationList) {
        this.locationList = locationList;
    }


    class PracticeViewHolder extends RecyclerView.ViewHolder {

        TextView locaationName;
        ImageView check;
        View item;

        public PracticeViewHolder(View itemView) {
            super(itemView);
            locaationName = (TextView) itemView.findViewById(R.id.practice_name);
            check = (ImageView) itemView.findViewById(R.id.practice_check);
            item = itemView;
        }
    }

}
