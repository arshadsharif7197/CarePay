package com.carecloud.carepaylibray.demographics.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.payload.PhysicianDto;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 14/11/17.
 */

public class PhysicianAdapter extends RecyclerView.Adapter<PhysicianAdapter.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_DATA = 1;

    private final PhysicianSelectedInterface callback;
    private List<PhysicianDto> physicians;
    private boolean maximumReached;

    public PhysicianAdapter(ArrayList<PhysicianDto> physicianDtos, PhysicianSelectedInterface callback) {
        this.physicians = physicianDtos;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_LOADING) {
            return new ViewHolder(inflater.inflate(R.layout.item_progress_bar, parent, false));
        }
        return new ViewHolder(inflater.inflate(R.layout.item_physician, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            // display the loading view
            getFooterView(position, holder);
            return;
        }
        final PhysicianDto physician = physicians.get(position);
        holder.physicianNameTextView.setText(physician.getFullName());
        if (physician.getSpeciality() != null) {
            holder.physicianSpecialityTextView.setText(physician.getSpeciality());
        } else {
            holder.physicianSpecialityTextView.setVisibility(View.GONE);
        }
        if (physician.getAddress() != null) {
            holder.physicianPhoneTextView.setText(StringUtil.formatPhoneNumber(physician.getAddress().getPhoneNumber()));
            String address = physician.getAddress().getAddress1();
            if (physician.getAddress().getAddress2() != null) {
                address += " " + physician.getAddress().getAddress2();
            }
            holder.physicianAddressTextView.setText(address);
            holder.physicianZipCodeTextView.setText(StringUtil.formatZipCode(physician.getAddress().getZipcode()));
            holder.physicianCityTextView.setText(physician.getAddress().getCity()
                    + ", " + physician.getAddress().getState());
            if (physician.getAddress().getFaxNumber() != null) {
                String fax = StringUtil.formatPhoneNumber(physician.getAddress().getFaxNumber()) + " (Fax)";
                holder.physicianFaxTextView.setText(fax);
            }
        } else {
            holder.physicianAddressContainer.setVisibility(View.GONE);
        }
        holder.physicianContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPhysicianSelected(physician);
            }
        });
    }

    @Override
    public int getItemCount() {
        return physicians.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position >= physicians.size()) ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
    }

    private void getFooterView(int position, ViewHolder holder) {
        if (position >= physicians.size() && physicians.size() > 0 && !maximumReached) {
            holder.progressBar.setVisibility(View.VISIBLE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * reset Data when a new search is made
     */
    public void resetData() {
        physicians.clear();
        maximumReached = false;
        notifyDataSetChanged();
    }

    /**
     * set the new Data
     *
     * @param physicians list of physicians
     */
    public void setData(List<PhysicianDto> physicians) {
        this.physicians.addAll(physicians);
        notifyDataSetChanged();
    }

    /**
     * set maximumReached as true to avoid showing th progress view
     */
    public void maximumNumberOfItemsReached() {
        maximumReached = true;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView physicianNameTextView;
        TextView physicianSpecialityTextView;
        TextView physicianPhoneTextView;
        TextView physicianAddressTextView;
        TextView physicianFaxTextView;
        TextView physicianZipCodeTextView;
        TextView physicianCityTextView;
        View physicianAddressContainer;
        View physicianContainer;
        View progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            physicianNameTextView = (TextView) itemView.findViewById(R.id.physicianNameTextView);
            physicianSpecialityTextView = (TextView) itemView.findViewById(R.id.physicianSpecialityTextView);
            physicianPhoneTextView = (TextView) itemView.findViewById(R.id.physicianPhoneTextView);
            physicianAddressTextView = (TextView) itemView.findViewById(R.id.physicianAddressTextView);
            physicianFaxTextView = (TextView) itemView.findViewById(R.id.physicianFaxTextView);
            physicianZipCodeTextView = (TextView) itemView.findViewById(R.id.physicianZipCodeTextView);
            physicianCityTextView = (TextView) itemView.findViewById(R.id.physicianCityTextView);
            physicianAddressContainer = itemView.findViewById(R.id.physicianAddressContainer);
            physicianContainer = itemView.findViewById(R.id.physicianContainer);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public interface PhysicianSelectedInterface {
        void onPhysicianSelected(PhysicianDto physician);
    }
}
