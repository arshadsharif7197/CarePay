package com.carecloud.carepaylibray.demographics.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.payload.PhysicianDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 14/11/17.
 */

public class PhysicianAdapter extends RecyclerView.Adapter<PhysicianAdapter.ViewHolder> {

    private final PhysicianSelectedInterface callback;
    private List<PhysicianDto> physicians;

    public PhysicianAdapter(ArrayList<PhysicianDto> physicianDtos, PhysicianSelectedInterface callback) {
        this.physicians = physicianDtos;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_physician, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PhysicianDto physician = physicians.get(position);
        holder.physicianNameTextView.setText(physician.getFullName());
        if (physician.getSpeciality() != null) {
            holder.physicianSpecialityTextView.setText(physician.getSpeciality());
        } else {
            holder.physicianSpecialityTextView.setVisibility(View.GONE);
        }
        if (physician.getAddress() != null) {
            holder.physicianPhoneTextView.setText(physician.getAddress().getPhone());
            holder.physicianAddressTextView.setText(physician.getAddress().getAddress1()
                    + " " + physician.getAddress().getAddress2());
            holder.physicianFaxTextView.setText(physician.getSpeciality());
            holder.physicianZipCodeTextView.setText(physician.getAddress().getZipcode());
            holder.physicianCityTextView.setText(physician.getAddress().getCity()
                    + " " + physician.getAddress().getState());
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
        return physicians.size();
    }

    public void setData(List<PhysicianDto> physicians) {
        this.physicians = physicians;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView physicianNameTextView;
        TextView physicianSpecialityTextView;
        TextView physicianPhoneTextView;
        TextView physicianAddressTextView;
        TextView physicianFaxTextView;
        TextView physicianZipCodeTextView;
        TextView physicianCityTextView;
        View physicianAddressContainer;
        View physicianContainer;

        public ViewHolder(View itemView) {
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
        }
    }

    public interface PhysicianSelectedInterface {
        void onPhysicianSelected(PhysicianDto physician);
    }
}
