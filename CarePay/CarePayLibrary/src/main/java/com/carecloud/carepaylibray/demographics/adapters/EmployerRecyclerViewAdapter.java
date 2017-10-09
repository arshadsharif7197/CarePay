package com.carecloud.carepaylibray.demographics.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerDto;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerWrapperDto;

import java.util.List;

/**
 * @author pjohnson on 4/10/17.
 */

public class EmployerRecyclerViewAdapter extends RecyclerView
        .Adapter<EmployerRecyclerViewAdapter.ViewHolder> {

    private List<EmployerWrapperDto> employers;
    private EmployerAdapterInterface callback;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_employer, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final EmployerDto employer = employers.get(position).getEmployer();
        holder.employerNameTextView.setText(employer.getName());
        if (employer.getAddress() != null) {
            holder.address1TextView.setText(employer.getAddress().getAddress1());
            holder.address2TextView.setText(employer.getAddress().getAddress2());
            holder.addressCityTextView.setText(employer.getAddress().getCity()
                    + " " + employer.getAddress().getState());
            holder.phoneTextView.setText(employer.getAddress().getPhone());
        }
        holder.employerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onEmployerClicked(employer);
            }
        });

    }

    @Override
    public int getItemCount() {
        return employers != null ? employers.size() : 0;
    }

    public void setData(List<EmployerWrapperDto> data) {
        this.employers = data;
    }

    public void setCallback(EmployerAdapterInterface callback) {
        this.callback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView employerNameTextView;
        TextView address1TextView;
        TextView address2TextView;
        TextView addressCityTextView;
        TextView phoneTextView;
        View employerContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            employerNameTextView = (TextView) itemView.findViewById(R.id.employerNameTextView);
            address1TextView = (TextView) itemView.findViewById(R.id.address1TextView);
            address2TextView = (TextView) itemView.findViewById(R.id.address2TextView);
            addressCityTextView = (TextView) itemView.findViewById(R.id.addressCityTextView);
            phoneTextView = (TextView) itemView.findViewById(R.id.phoneTextView);
            employerContainer = itemView.findViewById(R.id.employerContainer);
        }
    }

    public interface EmployerAdapterInterface{
        void onEmployerClicked(EmployerDto employer);
    }
}
