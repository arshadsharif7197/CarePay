package com.carecloud.carepay.mini.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.models.response.LocationsDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 6/24/17
 */

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    public interface SelectLocationListener{
        void onLocationSelected(String locationID);
    }

    private Context context;
    private SelectLocationListener listener;
    private List<LocationsDTO> locations = new ArrayList<>();

    private View lastIndicator;

    public LocationsAdapter(Context context, List<LocationsDTO> locations, SelectLocationListener listener){
        this.context = context;
        this.locations = locations;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selection_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LocationsDTO locationsDTO = locations.get(position);

        holder.listItemText.setText(locationsDTO.getName());
        holder.listItemIndicator.setSelected(false);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLocationSelected(locationsDTO.getGuid());
                if(lastIndicator != null){
                    lastIndicator.setSelected(false);
                }
                holder.listItemIndicator.setSelected(true);
                lastIndicator = holder.listItemIndicator;
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView listItemText;
        View listItemIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            listItemText = (TextView) itemView.findViewById(R.id.list_item_text);
            listItemIndicator = itemView.findViewById(R.id.list_item_indicator);
        }

    }
}
