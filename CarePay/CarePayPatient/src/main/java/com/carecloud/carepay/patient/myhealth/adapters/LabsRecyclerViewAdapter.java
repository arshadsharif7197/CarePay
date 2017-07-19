package com.carecloud.carepay.patient.myhealth.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.dtos.LabDto;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthDataInterface;

import java.util.List;

/**
 * @author pjohnson on 18/07/17.
 */

public class LabsRecyclerViewAdapter extends RecyclerView.Adapter<LabsRecyclerViewAdapter.ViewHolder> {


    private final List<LabDto> medications;
    private MyHealthDataInterface callback;

    public LabsRecyclerViewAdapter(List<LabDto> providers) {
        this.medications = providers;
    }

    public void setCallback(MyHealthDataInterface callback) {
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_condition, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LabDto lab = medications.get(position);
        holder.labNameTextView.setText(lab.getName());
        holder.dateTextView.setText(String.format("Performed on %s", lab.getCreatedAt()));
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onLabClicked(lab);
            }
        });

    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myHealthActionButton;
        TextView labNameTextView;
        TextView dateTextView;
        ViewGroup row;

        public ViewHolder(View itemView) {
            super(itemView);
            labNameTextView = (TextView) itemView.findViewById(R.id.conditionNameTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.practiceTextView);
            row = (ViewGroup) itemView.findViewById(R.id.row);
            myHealthActionButton = (TextView) itemView.findViewById(R.id.myHealthActionButton);
        }
    }
}
